class WidgetContainer extends React.Component {
    render() {
        return (
            <div className='card m-1' style={{width: this.props.width, height: this.props.height, display:'inline-block'}} id={this.props.id + '_card'}>
                <div className='card-header p-1'>
                    <h4 className='m-0 d-inline'>{this.props.title}</h4>
                    <button className='btn btn-light float-right d-inline p-0 m-1' type='button' data-toggle='modal' data-target={'#' + this.props.id + '_modal'}><h5 className='fas fa-cog m-0'></h5></button>
                </div>
                {this.props.children}
            </div>
        );
    }
}

class WidgetBody extends React.Component {
    render() {
        return (
            <div className={'card-body p-2 show'} id={this.props.id + '_widget'}>
                {this.props.children}
            </div>
        );
    }
}

class WidgetSettings extends React.Component {
    render() {
        return (
            <div className='modal fade' id={this.props.id + '_modal'} tabIndex='-1' role='dialog' aria-hidden='true'>
                <div className='modal-dialog modal-dialog-centered' role='document'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id={this.props.id + '_modal_title'}>{this.props.title} Settings</h5>
                            <button type='button' className='close' data-dismiss='modal' aria-label='Close'>
                                <span aria-hidden='true'>&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            {this.props.children}
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className='btn btn-secondary' data-dismiss='modal'>Close</button>
                            <button type='submit' className='btn btn-primary' onClick={() => this.props.onSave()} data-dismiss='modal'>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class VariableAutofill extends React.Component {
    constructor(props) {
        super(props)
        this.inputRef = React.createRef();
        this.state = {
            variable: ""
        };
    }

    onEdit(e) {
        this.setState({variable: e.target.value});
    }

    componentDidMount() {
        var availableTags = [
            "PID",
            "DEEK",
            "TEST"
        ];
        console.log("called");
        console.log($(this.inputRef.current));
        $(this.inputRef.current).autocomplete({
            source: availableTags
        });
    }
    render() {
        return <div id="ui-widget"><input className='' type='text' ref={this.inputRef} placeholder="variable" value={this.state.variable} onChange={(e) => this.onEdit(e)} /></div>;

    }
}

$(function() { // runs when document finishes loading
    if(PageUtils.loadPageConfig()) {
        SocketHandler.connect(PageUtils.getWebSocketPageAddress());
        ReactDOM.render(
            <div>
                {PageUtils.renderWidgets()}
            </div>,
            $('#reactapp')[ 0 ]
        );
    } else {
        let err = textStatus + ', ' + error;
        ReactDOM.render(
        <div>
            <div class='alert alert-danger p-2 show' role='alert'>
                There was an error loading the JSON config from the robot:
                <br />
                <b>{err}</b>
            </div>
        </div>,
        $('#reactapp')[ 0 ]);
    }
});

class PageUtils {
    static WidgetClasses = {};
    static Config = {};

    static addWidgetClass(classname, widgetclass) {
        PageUtils.WidgetClasses[classname] = widgetclass;
    }

    static loadPageConfig() {
        var newConfig = {};
        let success = false;
        $.getJSON({
            url: '/config',
            async: false
        }).done(function( data ) {
            newConfig = data;
            PageUtils.setPageConfig(newConfig);
            success = true;
        });
        return success;
    }

    static sendPageConfig() {
        let success = false;
        $.ajax({
            async: false,
            method: 'POST',
            url: '/config',
            contentType: 'application/json',
            data: JSON.stringify(PageUtils.getPageConfig()),
            success: function () {
                success = true;
            },
            error: function (jqxhr, status, error) {

                console.warn('error sending config json: ' + status + ' : ' + error);
            }
        });
        return success;
    }

    static getPageConfig() {
        return PageUtils.Config;
    }

    static setPageConfig(json) {
        PageUtils.Config = json;
    }

    static getPageName() {
        let pathname = window.location.pathname;
        return pathname.substring(pathname.lastIndexOf('/') + 1, pathname.length);
    }

    static getWebSocketPageAddress() {
        return 'ws://' + window.location.host + '/socket';
    }

    static getPageJSON() {
        return PageUtils.Config['pages'][PageUtils.getPageName()];
    }

    static getPageWidgets() {
        return PageUtils.getPageJSON()['widgets'];
    }

    static getPageWidgetIndex(id) {
        const widgets = PageUtils.getPageWidgets();
        for(var i in widgets) {
            if(widgets[i]['id'] === id) {
                return i;
            }
        }
        return -1;
    }

    static getPageWidget(id) {
        return PageUtils.getPageWidgets()[PageUtils.getPageWidgetIndex(id)];
    }

    static setPageWidget(id, json) {
        const index = PageUtils.getPageWidgetIndex(id);
        let newConfig = PageUtils.Config;
        newConfig.pages[PageUtils.getPageName()].widgets[index] = json;
        PageUtils.setPageConfig(newConfig);
        return PageUtils.sendPageConfig();
    }

    static getWidgetTag(widget) {
        return `${widget.type}`;
    }

    static renderWidgets() {
        let widgetsJson = PageUtils.getPageWidgets();
        let widgets = [];
        for(var i in widgetsJson) {
            let widget = widgetsJson[i];
            const GenericWidget = PageUtils.getWidgetTag(widget);
            widgets.push(React.createElement(PageUtils.WidgetClasses[GenericWidget], {key: widget.id, title: widget.title, id: widget.id, width: widget.width, height: widget.height, variables: widget.variables, kwargs: widget.kwargs}, null));
            //widgets.push(<GenericWidget key={i.id} title={i.title} id={i.id} width={i.width} height={i.height} variables={i.variables} kwargs={i.kwargs} />);
        }
        return widgets;
    }
}

class SocketHandler {
    static BROADCAST_INTERVAL = 40.0; // Hertz
    static socket = {};
    static variables = {};
    static variableUpdates = {};
    static callbacks = {};
    static isConnected = false;
    static broadcastInterval = 0;

    static connect(address) {
        SocketHandler.socket = new WebSocket(address);
        SocketHandler.socket.onopen = SocketHandler.onopen
        SocketHandler.socket.onclose = SocketHandler.onclose
        SocketHandler.socket.onmessage = SocketHandler.onmessage

        SocketHandler.broadcastInterval = window.setInterval(function() {
            // if changes, broadcast them
            if (Object.keys(SocketHandler.variableUpdates).length > 0) {
                SocketHandler.socket.send(JSON.stringify(SocketHandler.variableUpdates));
                for(var i in SocketHandler.variableUpdates) {
                    SocketHandler.variables[i] = SocketHandler.variableUpdates[i]
                }
                SocketHandler.variableUpdates = {};
            }

        }, 1000.0 / SocketHandler.BROADCAST_INTERVAL);
    }

    static onopen(event) {
        SocketHandler.isConnected = true;
        console.warn("Robot connected!")
    }

    static onclose(event) {
        SocketHandler.isConnected = false;
        console.warn("Robot disconnected!");
        // stop the websocket sender loop
        window.clearInterval(SocketHandler.broadcastInterval);
        // maybe reconnect?
    }

    static onmessage(event) {
        // update page
        // update variables
        let prefix = event.data.substring(0, event.data.indexOf(":"));
        let updates = JSON.parse(event.data.substring(event.data.indexOf(":") + 1, event.data.length));
        if(prefix === "variables") {
            SocketHandler.variables = updates;
            for(var u in updates) {
                for(var c in SocketHandler.callbacks[u]) {
                    SocketHandler.callbacks[u][c](updates[u]);
                }
            }
        } else if(prefix === "updates") {
            for(var u in updates) {
                SocketHandler.variables[u] = updates[u];
                for(var c in SocketHandler.callbacks[u]) {
                    SocketHandler.callbacks[u][c](updates[u]);
                }
            }
        }
    }

    static addVariableListener(key, callback) {
        if(!Array.isArray(SocketHandler.callbacks[key])) {
            SocketHandler.callbacks[key] = [];
        }
        SocketHandler.callbacks[key].push(callback);
        return SocketHandler.callbacks[key].length - 1;
    }

    static removeVariableListener(key, id) {
        SocketHandler.callbacks[key].splice(id, 1);
    }

    static getVariable(key) {
        if((!(key in SocketHandler.variables)) && (!(key in SocketHandler.variableUpdates))) {
            console.warn("variable " + key + " not found!");
            return undefined;
        } else if(key in SocketHandler.variableUpdates) {
            return SocketHandler.variableUpdates[key];
        } else {
            return SocketHandler.variables[key];
        }
    }

    static setVariable(key, value) {
        SocketHandler.variableUpdates[key] = value;
    }
}
