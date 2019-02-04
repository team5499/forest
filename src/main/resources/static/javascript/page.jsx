class WidgetAdder extends React.Component {
    render() {
        let widgets = [];
        for(var i in PageUtils.WidgetClasses) {
            widgets.push(<a className='dropdown-item' href='#' key={i}>{i}</a>);
        }

        return (
            <li className='nav-item dropleft'>
                <a className='nav-link p-0' href='#' role='button' data-toggle='dropdown' style={{fontSize: '2rem', lineHeight: '2rem'}} title='Add a card to the dashboard'>+</a>
                <div className='dropdown-menu'>
                    {widgets}
                </div>
            </li>
        );
    }
}

class PageDelete extends React.Component {
    onClick() {
        if(window.confirm('Are you sure you want to delete this page?')) {
            $('#deletepageform').submit();
        }
    }

    render() {
        return (
            <li className='nav-item dropleft'>
                <a className='nav-link p-0' href='#' role='button' style={{fontSize: '2rem', lineHeight: '2rem'}} title='Delete page' onClick={() => this.onClick()}><h5 className='fas fa-trash m-0'></h5></a>
                <form id='deletepageform' method='post' action='/actions/deletepage' style={{display: 'none'}}>
                    <input type='hidden' name='pagename' value={PageUtils.getPageName()} />
                </form>
            </li>
        );
    }
}

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
                            <button type='submit' className='btn btn-primary' onClick={() => this.props.onSave()} id={this.props.id + '_modal_save'} data-dismiss='modal'>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        );
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

        ReactDOM.render(
            [
                <WidgetAdder key='widgetadder' />,
                <PageDelete key='pagedelete' />
            ],
            $('#reactnavbar')[ 0 ]
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
        console.warn('Robot connected!')
    }

    static onclose(event) {
        SocketHandler.isConnected = false;
        console.warn('Robot disconnected!');
        // stop the websocket sender loop
        window.clearInterval(SocketHandler.broadcastInterval);
        // maybe reconnect?
    }

    static onmessage(event) {
        // update page
        // update variables
        let prefix = event.data.substring(0, event.data.indexOf(':'));
        let updates = JSON.parse(event.data.substring(event.data.indexOf(':') + 1, event.data.length));
        if(prefix === 'variables') {
            SocketHandler.variables = updates;
            for(var u in updates) {
                for(var c in SocketHandler.callbacks[u]) {
                    SocketHandler.callbacks[u][c](updates[u]);
                }
            }
        } else if(prefix === 'updates') {
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

    static getVariableType(key) {
        return typeof SocketHandler.variables[key]
    }

    static hasVariable(key) {
        return (key in SocketHandler.variableUpdates) || (key in SocketHandler.variables);
    }

    static getString(key) {
        if(key in SocketHandler.variableUpdates) {
            if(typeof SocketHandler.variableUpdates[key] !== 'string') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not string`);
                return undefined;
            }
            return SocketHandler.variableUpdates[key];
        } else if(key in SocketHandler.variables) {
            if(typeof SocketHandler.variables[key] !== 'string') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not string`);
                return undefined;
            }
            return SocketHandler.variables[key];
        } else {
            console.error('variable ' + key + ' of type string not found!');
            return undefined;
        }
    }

    static getNumber(key) {
        if(key in SocketHandler.variableUpdates) {
            if(typeof SocketHandler.variableUpdates[key] !== 'number') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not number`);
                return undefined;
            }
            return SocketHandler.variableUpdates[key];
        } else if(key in SocketHandler.variables) {
            if(typeof SocketHandler.variables[key] !== 'number') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not number`);
                return undefined;
            }
            return SocketHandler.variables[key];
        } else {
            console.error('variable ' + key + ' of type number not found!');
            return undefined;
        }
    }

    static getBoolean(key) {
        if(key in SocketHandler.variableUpdates) {
            if(typeof SocketHandler.variableUpdates[key] !== 'boolean') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not boolean`);
                return undefined;
            }
            return SocketHandler.variableUpdates[key];
        } else if(key in SocketHandler.variables) {
            if(typeof SocketHandler.variables[key] !== 'boolean') {
                console.error(`variable ' + key + ' of type ${typeof SocketHandler.variableUpdates[key]} is not boolean`);
                return undefined;
            }
            return SocketHandler.variables[key];
        } else {
            console.error('variable ' + key + ' of type boolean not found!');
            return undefined;
        }
    }

    static setString(key, value) {
        if(typeof SocketHandler.variableUpdates[key] !== 'string') {
            console.error(`cannot set variable ${key} of type ${typeof SocketHandler.variableUpdates[key]} to string`);
        } else {
            SocketHandler.variableUpdates[key] = value;
        }
    }

    static setNumber(key, value) {
        if(typeof SocketHandler.variableUpdates[key] !== 'number') {
            console.error(`cannot set variable ${key} of type ${typeof SocketHandler.variableUpdates[key]} to number`);
        } else {
            SocketHandler.variableUpdates[key] = value;
        }
    }

    static setBoolean(key, value) {
        if(typeof SocketHandler.variableUpdates[key] !== 'boolean') {
            console.error(`cannot set variable ${key} of type ${typeof SocketHandler.variableUpdates[key]} to boolean`);
        } else {
            SocketHandler.variableUpdates[key] = value;
        }
    }
}
