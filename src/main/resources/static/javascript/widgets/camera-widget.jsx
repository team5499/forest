import PageUtils from 'page-utils';
import Widget from 'widget';

class CameraWidget {};

CameraWidget.Body = class extends Widget.Body {
    init() {
        let src = this.widgetConfig.kwargs.src;
        this.state = {
            src: src,
            height: this.widgetConfig.height,
            imgStart: this.widgetConfig.kwargs.imgStart,
            imgSize: this.widgetConfig.kwargs.imgSize
        }
        this.divImg = React.createRef();
        this.imgDim = {x: 1, y: 1};
    }

    settingsSave(newConfig) {
        let newSrc = newConfig.kwargs.src;
        this.setState({
            src: newSrc,
            height: newConfig.height,
            imgStart: newConfig.kwargs.imgStart,
            imgSize: newConfig.kwargs.imgSize
        });
        this.forceUpdate();
    }

    componentDidMount() {
        this.getMeta(this.state.src).then((dim) => {
            this.imgDim = dim;

            let divWidth = $(this.divImg.current).width();
            let divHeight = $(this.divImg.current).height();
            let imgWidth = this.imgDim.x * (divWidth / this.state.imgSize.x);
            let imgHeight = this.imgDim.y * (divHeight / this.state.imgSize.y);
            let imgOffsetX = -this.state.imgStart.x * (divWidth / this.state.imgSize.x);
            let imgOffsetY = -this.state.imgStart.y * (divHeight / this.state.imgSize.y);

            $(this.divImg.current).css({backgroundSize: imgWidth + "px " + imgHeight + "px", backgroundPosition: imgOffsetX + "px " + imgOffsetY + "px"});
        });
    }

    componentDidUpdate() {
        let divWidth = $(this.divImg.current).width();
        let divHeight = $(this.divImg.current).height();
        let imgWidth = this.imgDim.x * (divWidth / this.state.imgSize.x);
        let imgHeight = this.imgDim.y * (divHeight / this.state.imgSize.y);
        let imgOffsetX = -this.state.imgStart.x * (divWidth / this.state.imgSize.x);
        let imgOffsetY = -this.state.imgStart.y * (divHeight / this.state.imgSize.y);

        $(this.divImg.current).css({backgroundSize: imgWidth + "px " + imgHeight + "px", backgroundPosition: imgOffsetX + "px " + imgOffsetY + "px"});
    }

    getMeta(url){
        return new Promise((resolve, reject) => {
            var img = new Image();
            window.setTimeout(() => {
                reject();
            }, 3000);
            img.addEventListener("load", function(){
                resolve({x: this.naturalWidth, y: this.naturalHeight});
                img.src = "";
            });
            img.src = url;
        });
    }

    render() {
        let heightPrefix = this.state.height.substr(parseFloat(this.state.height).toString().length, this.state.height.length);
        let imgWidth = this.state.imgSize[0]
        return <div ref={this.divImg} style={{background: "url(\"" + this.state.src + "\")", width: "100%", height: (parseFloat(this.state.height) - 60) + heightPrefix, overflow: "hidden"}} />;
    }
}

CameraWidget.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            src: this.widgetConfig.kwargs.src,
            imgStart: this.widgetConfig.kwargs.imgStart,
            imgSize: this.widgetConfig.kwargs.imgSize
        }
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.kwargs.src = this.state.src;
        newConfig.kwargs.imgStart = this.state.imgStart;
        newConfig.kwargs.imgSize = this.state.imgSize;
        return newConfig;
    }

    onSrcEdit(e) {
        this.setState({
            src: e.target.value
        });
    }

    onStartXEdit(e) {
        let newImgStart = this.state.imgStart;
        newImgStart.x = (e.target.value === "") ? 0 : parseInt(e.target.value);
        this.setState({imgStart: newImgStart});
    }

    onStartYEdit(e) {
        let newImgStart = this.state.imgStart;
        newImgStart.y = (e.target.value === "") ? 0 : parseInt(e.target.value);
        this.setState({imgStart: newImgStart});
    }

    onSizeXEdit(e) {
        let newImgSize = this.state.imgSize;
        newImgSize.x = (e.target.value === "") ? 0 : parseInt(e.target.value);
        this.setState({imgSize: newImgSize});
    }

    onSizeYEdit(e) {
        let newImgSize = this.state.imgSize;
        newImgSize.y = (e.target.value === "") ? 0 : parseInt(e.target.value);
        this.setState({imgSize: newImgSize});
    }

    render() {
        return [
            <input key="src" className='form-control mb-2' type='text' placeholder='img source' value={this.state.src} onChange={(e) => this.onSrcEdit(e)} />,
            <input key="startx" className='form-control mb-2' type='number' step='1' placeholder='image start x' value={this.state.imgStart.x} onChange={(e) => this.onStartXEdit(e)} />,
            <input key="starty" className='form-control mb-2' type='number' step='1' placeholder='image start y' value={this.state.imgStart.y} onChange={(e) => this.onStartYEdit(e)} />,
            <input key="sizex" className='form-control mb-2' type='number' step='1' placeholder='image size x' value={this.state.imgSize.x} onChange={(e) => this.onSizeXEdit(e)} />,
            <input key="sizey" className='form-control mb-2' type='number' step='1' placeholder='image size y' value={this.state.imgSize.y} onChange={(e) => this.onSizeYEdit(e)} />
        ];
    }
}

export default CameraWidget;

// make sure to do this for every widget
PageUtils.addWidgetClass('CameraWidget', CameraWidget);
