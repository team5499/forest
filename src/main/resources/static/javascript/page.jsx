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
                            <button type='button' className='btn btn-primary'>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}




var config = {};
var pagename = '';

$(function() { // runs when document finishes loading
    let pathname = window.location.pathname;
    pagename = pathname.substring(pathname.lastIndexOf('/') + 1, pathname.length);
    console.log(pagename);

    $.getJSON( '/config', function( data ) {
        console.log('config:');
        console.log(data);
        config = data;
        let TestElement = 'WidgetContainer';
        console.log(TestElement);
        ReactDOM.render(
            <div>
                {PageUtils.renderWidgets(config, pagename)}
            </div>,
            $('#reactapp')[ 0 ]
        );
    }).done(function () {
    }).fail(function (jqxhr, textStatus, error) {
        // display error on page
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
    }).always(function () {
    });
});

class PageUtils {
    static WidgetClasses = {};

    static getPageJSON(json, page) {
        return json['pages'][page];
    }

    static getPageWidgets(json, page) {
        return PageUtils.getPageJSON(json, page)['widgets'];
    }

    static getWidgetTag(widget) {
        return `${widget.type}`;
    }

    static renderWidgets(json, page) {
        let widgetsJson = PageUtils.getPageWidgets(json, page);
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
