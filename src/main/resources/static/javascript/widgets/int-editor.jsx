import PageUtils from 'page-utils';
import Widget from 'widget';

class IntEditor {}

IntEditor.Body = class extends Widget.Body {
    init() {
        let newValue = (this.getInt(this.widgetConfig.variables.target) === undefined) ? "" : this.getInt(this.widgetConfig.variables.target);
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
        let newValue = (this.getInt(newConfig.variables.target) === undefined) ? "" : this.getInt(newConfig.variables.target);
        this.setState({
            targetName: newConfig.variables.target,
            targetValue: newValue
        });
        this.callbackId = this.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.setInt(this.state.targetName, this.state.targetValue);
    }

    onFieldEdit(e) {
        let newValue = (e.target.value === "") ? "" : parseInt(e.target.value);
        this.updateState(this.state.targetName, newValue);
    }

    render() {
        return (
            <div>
                <input className='form-control mb-2' type='number' step='1' placeholder='value' value={this.state.targetValue} onChange={(e) => this.onFieldEdit(e)} />
                <button className='btn btn-primary' onClick={() => this.onVarSave()}>Submit</button>
            </div>
        );
    }
}

IntEditor.Settings = class extends Widget.Settings {
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
PageUtils.addWidgetClass('IntEditor', IntEditor);
