class WidgetContainer extends React.Component {
    render() {
        // we need IDs for Bootstrap
        return (
            <div class="card m-1" style="width: {this.props.width}; height: {this.props.height};" id="{this.props.id}_card">
                <div class="card-header p-1">
                    <h4 class="m-0 d-inline">{this.props.title}</h4>
                    <a class="btn btn-light float-right d-inline p-1" role="button" data-toggle="collapse" href=".{this.props.id}_collapse"><h5 class="fas fa-cog m-0"></h5></a>
                </div>
                <div class="card-body p-2 collapse show {this.props.id}_collapse" id="{this.props.id}_widget">

                Main widget

                </div>
                <div class="card-body p-2 collapse {this.props.id}_collapse" id="{this.props.id}_settings">

                Settings for widget

                </div>
            </div>
        );
    }
}


var config = {}

$(function() { // runs when document finishes loading
    $.getJSON( "/config", function( data ) {
        console.log("config:")
        console.log(data)
        config = data
    }).done(function () {

    }).fail(function () {
        // figure out how to deal with JSON request failing
    }).always(function () {
        // leave this here, just in case its useful later
    });

    ReactDOM.render(
        <WidgetContainer title="Test Variable" width="12rem" height="auto" id="testVarEditor"/>,
        $("#reactapp")[ 0 ]
    );
});

class PageUtils {
    static getPageJSON(json, page) {
        return json["pages"][page]
    }

    static getPageWidgets(json, page) {
        return getPageJSON(json, page)["widgets"]
    }
}
