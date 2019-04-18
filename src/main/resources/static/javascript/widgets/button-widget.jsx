import PageUtils from 'page-utils';
import Widget from 'widget';

class ButtonWidget {}

ButtonWidget.Body = class extends Widget.Body {
    init() {
        let newValue = (this.getBoolean(this.widgetConfig.variables.target) === undefined) ? "" : this.getBoolean(this.widgetConfig.variables.target);
        this.state = {
            targetName: this.widgetConfig.variables.target,
            targetValue: newValue
        };
        this.callbackId = this.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value});
    }

    settingsSave(newConfig) {
        this.removeVarListener(this.state.targetName, this.callbackId);
        let newValue = (this.getBoolean(newConfig.variables.target) === undefined) ? "" : this.getBoolean(newConfig.variables.target);
        this.setState({
            targetName: newConfig.variables.target,
            targetValue: newValue
        });
        this.callbackId = this.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    toogleVar(){
        this.setBoolean(!this.state.targetValue);
    }

    onFieldEdit(e) {
        let newValue = (e.target.value === "") ? "" : parseInt(e.target.value);
        this.updateState(this.state.targetName, newValue);
    }

    render() {
        return (
            <div>
                <label class="switch">
                    <input type="checkbox"></input>
                    <span class="slider round"></span>
                </label>
            </div>
        );
    }
}

ButtonWidget.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            targetName: this.widgetConfig.variables.target
        };
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.variables.target = this.state.targetName;
        return newConfig;
    }

    onSettingsEdit(e) {
        this.setState({targetName: e.target.value});
    }

    render() {
        return (
            <input className='form-control mb-2' type='text' placeholder="variable" value={this.state.targetName} onChange={(e) => this.onSettingsEdit(e)} />
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('ButtonWidget', ButtonWidget);
