import PageUtils from 'page-utils';

class IntEditor {}

IntEditor.Body = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsCallback((settings) => this.onSettingsChange(settings));
        let newValue = (this.props.getInt(this.props.variables.target) === undefined) ? "" : this.props.getInt(this.props.variables.target);
        this.state = {
            targetName: this.props.variables.target,
            targetValue: newValue
        };
        this.callbackId = this.props.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value});
    }

    onSettingsChange(settings) {
        this.props.removeVarListener(this.state.targetName);
        let newValue = (this.props.getInt(settings.target) === undefined) ? "" : this.props.getInt(settings.target);
        this.setState({
            targetName: settings.target,
            targetValue: newValue
        });
        this.callbackId = this.props.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    onVarSave() {
        this.props.setInt(this.state.targetName, this.state.targetValue);
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

IntEditor.Settings = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsSaveCallback(() => this.settingsData())
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

export default IntEditor;

// make sure to do this for every widget
PageUtils.addWidgetClass('IntEditor', IntEditor);
