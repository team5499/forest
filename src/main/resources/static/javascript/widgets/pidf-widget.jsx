import PageUtils from 'page-utils';
import Widget from 'widget';

class PIDFWidget {}

PIDFWidget.Body = class extends Widget.Body {
    init() {
        let newPValue = (this.getString(this.widgetConfig.variables.ptarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.ptarget);
        let newIValue = (this.getString(this.widgetConfig.variables.itarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.itarget);
        let newDValue = (this.getString(this.widgetConfig.variables.dtarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.dtarget);
        let newFValue = (this.getString(this.widgetConfig.variables.ftarget) === undefined) ? '' : this.getString(this.widgetConfig.variables.ftarget);
        this.state = {
            pTargetName: this.widgetConfig.variables.ptarget,
            iTargetName: this.widgetConfig.variables.itarget,
            dTargetName: this.widgetConfig.variables.dtarget,
            fTargetName: this.widgetConfig.variables.ftarget,
            pTargetValue: newPValue,
            iTargetValue: newIValue,
            dTargetValue: newDValue,
            fTargetValue: newFValue

        };
        this.pCallbackId = SocketHandler.addVariableListener(this.state.pTargetName, (key, value) => this.updateState(key, value));
        this.iCallbackId = SocketHandler.addVariableListener(this.state.iTargetName, (key, value) => this.updateState(key, value));
        this.dCallbackId = SocketHandler.addVariableListener(this.state.dTargetName, (key, value) => this.updateState(key, value));
        this.fCallbackId = SocketHandler.addVariableListener(this.state.fTargetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        let prefix = key.substring(0, 1);
        let newState = {};
        newState[prefix + 'TargetValue'] = value;
        this.setState(newState);
    }

    settingsSave(newConfig) {
        this.removeVarListener(this.state.pTargetName, this.pCallbackId);
        this.removeVarListener(this.state.iTargetName, this.iCallbackId);
        this.removeVarListener(this.state.dTargetName, this.dCallbackId);
        this.removeVarListener(this.state.fTargetName, this.fCallbackId);
        let newPValue = (this.getString(newConfig.variables.ptarget) === undefined) ? '' : this.getString(newConfig.variables.ptarget);
        let newIValue = (this.getString(newConfig.variables.itarget) === undefined) ? '' : this.getString(newConfig.variables.itarget);
        let newDValue = (this.getString(newConfig.variables.dtarget) === undefined) ? '' : this.getString(newConfig.variables.dtarget);
        let newFValue = (this.getString(newConfig.variables.ftarget) === undefined) ? '' : this.getString(newConfig.variables.ftarget);
        this.setState({
            pTargetName: newConfig.variables.ptarget,
            iTargetName: newConfig.variables.itarget,
            dTargetName: newConfig.variables.dtarget,
            fTargetName: newConfig.variables.ftarget,
            pTargetValue: newPValue,
            iTargetValue: newIValue,
            dTargetValue: newDValue,
            fTargetValue: newFValue
        });
        this.pCallbackId = SocketHandler.addVariableListener(newConfig.variables.ptarget, (key, value) => this.updateState(key, value));
        this.iCallbackId = SocketHandler.addVariableListener(newConfig.variables.itarget, (key, value) => this.updateState(key, value));
        this.dCallbackId = SocketHandler.addVariableListener(newConfig.variables.dtarget, (key, value) => this.updateState(key, value));
        this.fCallbackId = SocketHandler.addVariableListener(newConfig.variables.ftarget, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.setString(this.state.pTargetName, this.state.pTargetValue);
        this.setString(this.state.iTargetName, this.state.iTargetValue);
        this.setString(this.state.dTargetName, this.state.dTargetValue);
        this.setString(this.state.fTargetName, this.state.fTargetValue);
    }

    onFieldEdit(e) {
        let newValue = (e.target.value === "") ? "" : parseFloat(e.target.value);
        this.updateState(e.target.name, newValue);
    }

    render() {
        return (
            <div>
                <input className='form-control mb-2' name='p' type='number' placeholder='value' value={this.state.pTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <input className='form-control mb-2' name='i' type='number' placeholder='value' value={this.state.iTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <input className='form-control mb-2' name='d' type='number' placeholder='value' value={this.state.dTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <input className='form-control mb-2' name='f' type='number' placeholder='value' value={this.state.fTargetValue} onChange={(e) => this.onFieldEdit(e)} />
                <button className='btn btn-primary' onClick={() => this.onVarSave()}>Submit</button>
            </div>
        );
    }
}

PIDFWidget.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            pTargetName: this.widgetConfig.variables.ptarget,
            iTargetName: this.widgetConfig.variables.itarget,
            dTargetName: this.widgetConfig.variables.dtarget,
            fTargetName: this.widgetConfig.variables.ftarget
        };
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.variables.ptarget = this.state.pTargetName;
        newConfig.variables.itarget = this.state.iTargetName;
        newConfig.variables.dtarget = this.state.dTargetName;
        newConfig.variables.ftarget = this.state.fTargetName;
        return newConfig;
    }

    onSettingsEdit(e) {
        let newState = {};
        newState[e.target.name + 'TargetName'] = e.target.value;
        this.setState(newState);
    }

    render() {
        return (
            <input className='form-control mb-2' type='text' name='p' placeholder='variable' value={this.state.pTargetName} onChange={(e) => this.onSettingsEdit(e)} />
            <input className='form-control mb-2' type='text' name='i' placeholder='variable' value={this.state.iTargetName} onChange={(e) => this.onSettingsEdit(e)} />
            <input className='form-control mb-2' type='text' name='d' placeholder='variable' value={this.state.dTargetName} onChange={(e) => this.onSettingsEdit(e)} />
            <input className='form-control mb-2' type='text' name='f' placeholder='variable' value={this.state.fTargetName} onChange={(e) => this.onSettingsEdit(e)} />
        );
    }
}

export default PIDFWidget;

// make sure to do this for every widget
PageUtils.addWidgetClass('PIDFWidget', PIDFWidget);
