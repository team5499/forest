import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

class RawVarEditor {}

RawVarEditor.Body = class extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            targetName: this.props.variables.target,
            targetValue: SocketHandler.getVariable(this.props.variables.target) || ""
        };
        this.callbackId = SocketHandler.addVariableListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value || ""});
    }

    onSettingsChange(settings) {
        let widget = PageUtils.getPageWidget(this.props.id);
        SocketHandler.removeVariableListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        this.setState({
            targetName: settings.target,
            targetValue: SocketHandler.getVariable(settings.target) || ""
        });
        this.callbackId = SocketHandler.addVariableListener(this.state.updateName, (value) => this.updateValue(value));
    }

    onVarSave() {
        SocketHandler.setVariable(this.state.targetName, this.state.targetValue);
    }

    onFieldEdit(e) {
        this.updateValue(e.target.value)
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
RawVarEditor.Settings = class extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            targetName: this.props.variables.target
        };
    }

    onSettingsSave() {
        console.log("save settings with target: " + this.state.targetName)
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

export default RawVarEditor;

// make sure to do this for every widget
PageUtils.addWidgetClass('RawVarEditor', RawVarEditor);
