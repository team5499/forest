import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

function loadGrid(w) {
    $(".grid-stack").gridstack({
        width: w,
        float: true,
        resizable: { autoHide: true, handles: "se" },
        animate: true,
        horizontalMargin: 1,
        placeholderClass: "grid-stack-placeholder",
        draggable: {handle: '.ui-draggable-handle' }
    });
}
$(function() { // runs when document finishes loading
    if(PageUtils.loadPageConfig()) {
        SocketHandler.connect(PageUtils.getWebSocketPageAddress());
        ReactDOM.render(
            <div className="grid-stack" id="gridstack" data-gs-width="35">
                {PageUtils.renderWidgets()}
            </div>,
            $('#reactapp')[ 0 ]
        );
        loadGrid(35);
        // let maxWidth = $('#gridstack').width()
        // $(window).resize(() => {
        //     let width = Math.floor(35 * ($('#gridstack').width()/maxWidth));
        //     console.log(width);
        //     loadGrid(width);
        // })
    } else {
        let err = textStatus + ', ' + error;
        ReactDOM.render(
        <div>
            <div className='alert alert-danger p-2 show' role='alert'>
                There was an error loading the JSON config from the robot:
                <br/>
                <b>{err}</b>
            </div>
        </div>,
        $('#reactapp')[ 0 ]);
    }
});
