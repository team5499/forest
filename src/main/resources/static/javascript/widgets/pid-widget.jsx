import PageUtils from 'page-utils';
import Widget from 'widget';

class PIDWidget {}

PIDWidget.Body = class extends Widget.Body {
    init() {
        let newPValue = (this.getString(this.widgetConfig.variables.ptarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.ptarget);
        let newIValue = (this.getString(this.widgetConfig.variables.itarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.itarget);
        let newDValue = (this.getString(this.widgetConfig.variables.dtarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.dtarget);
        this.state = {
            pTargetName: this.widgetConfig.variables.ptarget,
            iTargetName: this.widgetConfig.variables.itarget,
            dTargetName: this.widgetConfig.variables.dtarget,
            pTargetValue: newPValue,
            iTargetValue: newIValue,
            dTargetValue: newDValue

        };
        this.pCallbackId = this.registerVarListener(this.state.pTargetName, (key, value) => this.updateState(key, value));
        this.iCallbackId = this.registerVarListener(this.state.iTargetName, (key, value) => this.updateState(key, value));
        this.dCallbackId = this.registerVarListener(this.state.dTargetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        let newState = {};
        for(var i in this.state) {
            if(this.state[i] === key) {
                newState[i.substring(0, 1) + 'TargetValue'] = value;
                break;
            }
        }
        this.setState(newState);
    }

    settingsSave(newConfig) {
        this.removeVarListener(this.state.pTargetName, this.pCallbackId);
        this.removeVarListener(this.state.iTargetName, this.iCallbackId);
        this.removeVarListener(this.state.dTargetName, this.dCallbackId);
        let newPValue = (this.getString(newConfig.variables.ptarget) === undefined) ? '' : this.getString(newConfig.variables.ptarget);
        let newIValue = (this.getString(newConfig.variables.itarget) === undefined) ? '' : this.getString(newConfig.variables.itarget);
        let newDValue = (this.getString(newConfig.variables.dtarget) === undefined) ? '' : this.getString(newConfig.variables.dtarget);
        this.setState({
            pTargetName: newConfig.variables.ptarget,
            iTargetName: newConfig.variables.itarget,
            dTargetName: newConfig.variables.dtarget,
            pTargetValue: newPValue,
            iTargetValue: newIValue,
            dTargetValue: newDValue
        });
        this.pCallbackId = this.registerVarListener(newConfig.variables.ptarget, (key, value) => this.updateState(key, value));
        this.iCallbackId = this.registerVarListener(newConfig.variables.itarget, (key, value) => this.updateState(key, value));
        this.dCallbackId = this.registerVarListener(newConfig.variables.dtarget, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.setDouble(this.state.pTargetName, this.state.pTargetValue);
        this.setDouble(this.state.iTargetName, this.state.iTargetValue);
        this.setDouble(this.state.dTargetName, this.state.dTargetValue);
    }

    onFieldEdit(e) {
        let newValue = (e.target.value === "") ? "" : parseFloat(e.target.value);
        let newState = {};
        newState[e.target.name + 'TargetValue'] = newValue;
        this.setState(newState);
    }

    render() {
        return (
            <div>
                <input className='form-control mb-2' name='p' type='number' placeholder='value' value={this.state.pTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <input className='form-control mb-2' name='i' type='number' placeholder='value' value={this.state.iTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <input className='form-control mb-2' name='d' type='number' placeholder='value' value={this.state.dTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <button className='btn btn-primary' onClick={() => this.onVarSave()}>Submit</button>
            </div>
        );
    }
}

PIDWidget.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            pTargetName: this.widgetConfig.variables.ptarget,
            iTargetName: this.widgetConfig.variables.itarget,
            dTargetName: this.widgetConfig.variables.dtarget
        };
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.variables.ptarget = this.state.pTargetName;
        newConfig.variables.itarget = this.state.iTargetName;
        newConfig.variables.dtarget = this.state.dTargetName;
        return newConfig;
    }

    onSettingsEdit(e) {
        let newState = {};
        newState[e.target.name + 'TargetName'] = e.target.value;
        this.setState(newState);
    }

    render() {
        return [
            <input className='form-control mb-2' key='p' type='text' name='p' placeholder='variable' value={this.state.pTargetName} onChange={(e) => this.onSettingsEdit(e)} />,
            <input className='form-control mb-2' key='i' type='text' name='i' placeholder='variable' value={this.state.iTargetName} onChange={(e) => this.onSettingsEdit(e)} />,
            <input className='form-control mb-2' key='d' type='text' name='d' placeholder='variable' value={this.state.dTargetName} onChange={(e) => this.onSettingsEdit(e)} />
        ];
    }
}

export default PIDWidget;

// make sure to do this for every widget
PageUtils.addWidgetClass('PIDWidget', PIDWidget);
