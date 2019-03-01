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
    }

    settingsSave(newConfig) {
        let newSrc = newConfig.kwargs.src;
        this.setState({
            src: newSrc,
            height: newConfig.height,
            imgStart: newConfig.kwargs.imgStart,
            imgSize: newConfig.kwargs.imgSize
        });
    }

    componentDidMount() {
        let imgWidth = this.state.imgSize[0]
        this.getMeta(this.state.src).then((x, y) => {

        });
    }

    componentDidUpdate() {
        let imgWidth = this.state.imgSize[0]
    }

    getMeta(url){
        return new Promise((resolve, reject) => {
            var img = new Image();
            img.addEventListener("load", function(){
                alert( this.naturalWidth +' '+ this.naturalHeight );
            });
            img.src = url;
        });
    }

    render() {
        let heightPrefix = this.state.height.substr(parseFloat(this.state.height).toString().length, this.state.height.length);
        let imgWidth = this.state.imgSize[0]
        return <div style={{background: "url(\"" + this.state.src + "\")", width: "100%", height: (parseFloat(this.state.height) - 60) + heightPrefix, overflow: "hidden"}} />;
    }
}

CameraWidget.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            src: this.widgetConfig.kwargs.src
        }
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.kwargs.src = this.state.src;
        return newConfig;
    }

    onSettingsEdit(e) {
        this.setState({
            src: e.target.value
        });
    }

    render() {
        return <input className='form-control mb-2' type='text' placeholder='img source' value={this.state.src} onChange={(e) => this.onSettingsEdit(e)} />;
    }
}

export default CameraWidget;

// make sure to do this for every widget
PageUtils.addWidgetClass('CameraWidget', CameraWidget);
