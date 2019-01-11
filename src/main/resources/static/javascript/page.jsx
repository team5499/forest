class WidgetContainer extends React.Component {
    render() {
        return (
            <div className='card m-1' style={{width: this.props.width, height: this.props.height, display:'inline-block'}} id={this.props.id + '_card'}>
                <div className='card-header p-1'>
                    <h4 className='m-0 d-inline'>{this.props.title}</h4>
                    <a className='btn btn-light float-right d-inline p-0 m-1' role='button' data-toggle='collapse' href={'.' + this.props.id + '_collapse'}><h5 className='fas fa-cog m-0'></h5></a>
                </div>
                {this.props.children}
            </div>
        );
    }
}

class WidgetBody extends React.Component {
    render() {
        return (
            <div className={'card-body p-2 collapse show ' + this.props.id + '_collapse'} id={this.props.id + '_widget'}>
                {this.props.children}
            </div>
        );
    }
}

class WidgetSettings extends React.Component {
    render() {
        return (
            <div className={'card-body p-2 collapse ' + this.props.id + '_collapse'} id={this.props.id + '_settings'}>
                {this.props.children}
            </div>
        );
    }
}




var config = {}

$(function() { // runs when document finishes loading
    $.getJSON( '/config', function( data ) {
        console.log('config:')
        console.log(data)
        config = data
    }).done(function () {

    }).fail(function () {
        // figure out how to deal with JSON request failing
    }).always(function () {
        // leave this here, just in case its useful later
    });

    ReactDOM.render(
        <div>
            <RawVarEditor title='Editor 1' id='editor1' width='12rem' height='auto' />
            <RawVarEditor title='Editor 2' id='editor2' width='12rem' height='auto' />
            <RawVarEditor title='Editor 3' id='editor3' width='12rem' height='auto' />
            <RawVarEditor title='Editor 4' id='editor4' width='12rem' height='auto' />
            <RawVarEditor title='Editor 5' id='editor5' width='12rem' height='auto' />
            <RawVarEditor title='Editor 6' id='editor6' width='12rem' height='auto' />
            <RawVarEditor title='Editor 7' id='editor7' width='12rem' height='auto' />
            <RawVarEditor title='Editor 8' id='editor8' width='12rem' height='auto' />
            <RawVarEditor title='Editor 9' id='editor9' width='12rem' height='auto' />
            <RawVarEditor title='Editor 10' id='editor10' width='12rem' height='auto' />
            <RawVarEditor title='Editor 11' id='editor11' width='12rem' height='auto' />
        </div>,
        $('#reactapp')[ 0 ]
    );
});

class PageUtils {
    static getPageJSON(json, page) {
        return json['pages'][page]
    }

    static getPageWidgets(json, page) {
        return getPageJSON(json, page)['widgets']
    }
}
