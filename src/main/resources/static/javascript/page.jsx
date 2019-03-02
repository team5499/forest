import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

class WidgetAdder extends React.Component {
    constructor(props) {
        super(props);
    }

    getWidgetList() {
        let widgetClasses = PageUtils.getWidgetClasses();
        let widgetList = [];
        for(var i in widgetClasses) {
            widgetList.push(<a key={i} className="dropdown-item" href="#">{i}</a>);
        }
        return widgetList;
    }

    render() {
        return (
            <ul className="navbar-nav ml-auto">
                <li className="nav-item dropleft">
                    <a className="nav-link p-0" href="#" role="button" data-toggle="dropdown" style={{fontSize: "2rem", lineHeight: "2rem"}} title="Add a widget to the dashboard">+</a>
                    <div className="dropdown-menu">
                        {this.getWidgetList()}
                    </div>
                </li>
            </ul>
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
            <WidgetAdder />,
            $('#widgetadder')[ 0 ]
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
