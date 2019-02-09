import PageUtils from 'page-utils';

class StringEditor {}

StringEditor.Body = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsSaveCallback((settings) => this.settingsSave(settings));
        let newValue = (this.props.getString(this.props.variables.target) === undefined) ? "" : this.props.getString(this.props.variables.target);
        this.state = {
            targetName: this.props.variables.target,
            targetValue: newValue
        };
        this.callbackId = this.props.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value});
    }

    settingsSave(settings) {
        this.props.removeVarListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        let newValue = (this.props.getString(settings.target) === undefined) ? "" : this.props.getString(settings.target);
        this.setState({
            targetName: settings.target,
            targetValue: newValue
        });
        this.callbackId = this.props.registerVarListener(settings.target, (key, value) => this.updateState(key, value));
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
                <input className='form-control mb-2' type='text' placeholder="value" value={this.state.targetValue} onChange={(e) => this.onFieldEdit(e)} />
                <button className='btn btn-primary' onClick={() => this.onVarSave()}>Submit</button>
            </div>
        );
    }
}

StringEditor.Settings = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsDataCallback(() => this.settingsData())
        this.state = {
            targetName: this.props.variables.target
        };
    }

    settingsData() {
        console.log("save settings with target: " + this.state.targetName)
        let settings = {
            target: this.state.targetName
        };
        return settings;
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

export default StringEditor;

// make sure to do this for every widget
PageUtils.addWidgetClass('StringEditor', StringEditor);
