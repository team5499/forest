import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

class Grid extends React.Component {
    constructor(props) {
        super(props);
        this.PIXELS_PER_UNIT = 35.0;
        this.state = {
            gridWidth: this.getGridWidth(),
            gridHeight: this.getGridHeight()
        }

        $(window).on('resize', () => {
            this.setState({
                gridWidth: this.getGridWidth(),
                gridHeight: this.getGridHeight()
            });
        });
    }

    updateCss() {
        console.log("update css");
        let width = this.getGridWidth();
        let percent = 100.0 / parseFloat(width);
        let rules = $('#gridheights')[0].cssRules || $('#gridheights')[0].rules;
        console.log(document.styleSheets);

        for(let i = 0; i < width + 1; i++) {
            $('.grid-stack > .grid-stack-item[data-gs-width=\'' + i + '\']').css({width: (percent * i) + '%'});
            $('.grid-stack > .grid-stack-item[data-gs-x=\'' + i + '\']').css({left: (percent * i) + '%'});
            $('.grid-stack > .grid-stack-item[data-gs-min-width=\'' + i + '\']').css({minWidth: (percent * i) + '%'});
            $('.grid-stack > .grid-stack-item[data-gs-max-width=\'' + i + '\']').css({maxWidth: (percent * i) + '%'});
        }

        for(let i = 1; i < width + 1; i++) {
            console.log('width: ' + $('.grid-stack > .grid-stack-item[data-gs-width=\'' + i + '\']').css('width'));
        }
    }

    getGridWidth() {
        return Math.floor($(window).width() / this.PIXELS_PER_UNIT);
    }

    getGridHeight() {
        return Math.floor($(window).height() / this.PIXELS_PER_UNIT);
    }

    loadGrid() {
        $(".grid-stack").gridstack({
            width: this.getGridWidth(),
            height: this.getGridHeight(),
            cellWidth: this.PIXELS_PER_UNIT,
            cellHeight: this.PIXELS_PER_UNIT,
            auto: true,
            float: true,
            resizable: { autoHide: true, handles: "se" },
            animate: true,
            horizontalMargin: 1,
            placeholderClass: "grid-stack-placeholder",
            draggable: {handle: '.ui-draggable-handle' }
        });
        $('.grid-stack').on('change', (event, items) => {
            console.log('change');
            for(var i in items) {
                let newX = items[i].x;
                let newY = items[i].y;
                let newWidth = items[i].width;
                let newHeight = items[i].height;
                let id = $(items[i].el).data('widget-id');
                console.log(id);
                let newConfig = PageUtils.getPageWidget(id);
                newConfig.x = newX.toString();
                newConfig.y = newY.toString();
                newConfig.width = newWidth.toString();
                newConfig.height = newHeight.toString();
                PageUtils.setPageWidget(id, newConfig);
            }
        });
    }

    componentDidMount() {
        console.log('mount');
        console.log(`${this.getGridWidth()} : ${this.getGridHeight()}`);
        this.loadGrid();
        // this.updateCss();
    }

    componentDidUpdate() {
        console.log('update');
        // this.loadGrid();
        // this.updateCss();
        console.log($(this.grid).data('gridstack'));
    }

    render() {
        console.log(`render:${this.state.gridWidth} : ${this.state.gridHeight}`);
        return (
            <div className="grid-stack" ref={(item) => {this.grid = item}} data-gs-width={`${this.state.gridWidth}`} data-gs-height={`${this.state.gridHeight}`}>
                {PageUtils.renderWidgets()}
            </div>
        );
    }
}

$(function() { // runs when document finishes loading
    if(PageUtils.loadPageConfig()) {
        SocketHandler.connect(PageUtils.getWebSocketPageAddress());
        ReactDOM.render(
            <Grid />,
            $('#reactapp')[ 0 ]
        );
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
