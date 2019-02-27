import PageUtils from 'page-utils';
import Widget from 'widget';

class StringEditor {}

StringEditor.Body = class extends Widget.Body {
    init() {
        console.log(this.constructor.name)
        let newValue = (this.getString(this.widgetConfig.variables.target) === undefined) ? '' : this.getString(this.widgetConfig.variables.target);
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
        this.removeVarListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        let newValue = (this.getString(newConfig.variables.target) === undefined) ? '' : this.getString(newConfig.variables.target);
        this.setState({
            targetName: newConfig.variables.target,
            targetValue: newValue
        });
        this.callbackId = this.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.setString(this.state.targetName, this.state.targetValue);
    }

    onFieldEdit(e) {
        this.updateState(this.state.targetName, e.target.value);
    }

    render() {
        return (
            <div>
                <input className='form-control mb-2' type='text' placeholder='value' value={this.state.targetValue} onChange={(e) => this.onFieldEdit(e)} />
                <button className='btn btn-primary' onClick={() => this.onVarSave()}>Submit</button>
            </div>
        );
    }
}

StringEditor.Settings = class extends Widget.Settings {
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
            <input className='form-control mb-2' type='text' placeholder='variable' value={this.state.targetName} onChange={(e) => this.onSettingsEdit(e)} />
        );
    }
}

export default StringEditor;

// make sure to do this for every widget
PageUtils.addWidgetClass('StringEditor', StringEditor);
