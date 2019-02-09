import PageUtils from 'page-utils';

class StringEditor {}

StringEditor.Body = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsSaveCallback((newConfig) => this.settingsSave(newConfig));
        let newValue = (this.props.getString(this.props.variables.target) === undefined) ? '' : this.props.getString(this.props.variables.target);
        this.state = {
            targetName: this.props.variables.target,
            targetValue: newValue
        };
        this.callbackId = this.props.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value});
    }

    settingsSave(newConfig) {
        this.props.removeVarListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        let newValue = (this.props.getString(newConfig.variables.target) === undefined) ? '' : this.props.getString(newConfig.variables.target);
        this.setState({
            targetName: newConfig.variables.target,
            targetValue: newValue
        });
        this.callbackId = this.props.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.props.setString(this.state.targetName, this.state.targetValue);
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

StringEditor.Settings = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsDataCallback((config) => this.settingsData(config));
        this.state = {
            targetName: this.props.variables.target
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
