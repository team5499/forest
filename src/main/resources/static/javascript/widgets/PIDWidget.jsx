class PIDWidget extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            pTargetName: this.props.variables.ptarget,
            pUpdateName: this.props.variables.ptarget,
            iTargetName: this.props.variables.itarget,
            iUpdateName: this.props.variables.itarget,
            dTargetName: this.props.variables.dtarget,
            dUpdateName: this.props.variables.dtarget,
            fTargetName: this.props.variables.ftarget,
            fUpdateName: this.props.variables.ftarget,
            pTargetValue: SocketHandler.getVariable(this.props.variables.ptarget) || "",
            iTargetValue: SocketHandler.getVariable(this.props.variables.itarget) || "",
            dTargetValue: SocketHandler.getVariable(this.props.variables.dtarget) || "",
            fTargetValue: SocketHandler.getVariable(this.props.variables.ftarget) || ""

        };
        this.pCallbackId = SocketHandler.addVariableListener(this.state.pTargetName, (value) => this.updatePState(value));
        this.iCallbackId = SocketHandler.addVariableListener(this.state.iTargetName, (value) => this.updateIState(value));
        this.dCallbackId = SocketHandler.addVariableListener(this.state.dTargetName, (value) => this.updateDState(value));
        this.fCallbackId = SocketHandler.addVariableListener(this.state.fTargetName, (value) => this.updateFState(value));
    }

    updatePState(value) {
        this.setState({pTargetValue: value || ""});
    }
    updateIState(value) {
        this.setState({iTargetValue: value || ""});
    }
    updateDState(value) {
        this.setState({dTargetValue: value || ""});
    }
    updateFState(value) {
        this.setState({fTargetValue: value || ""});
    }

    onSettingsSave() {
        let widget = PageUtils.getPageWidget(this.props.id);
        let newPVar = $('#' + this.props.id + 'p_settings_variable').val();
        let newIVar = $('#' + this.props.id + 'i_settings_variable').val();
        let newDVar = $('#' + this.props.id + 'd_settings_variable').val();
        let newFVar = $('#' + this.props.id + 'f_settings_variable').val();
        widget.variables.ptarget = newPVar;
        widget.variables.itarget = newIVar;
        widget.variables.dtarget = newDVar;
        widget.variables.ftarget = newFVar;
        let success = PageUtils.setPageWidget(this.props.id, widget);
        SocketHandler.removeVariableListener(this.state.pTargetName);
        SocketHandler.removeVariableListener(this.state.iTargetName);
        SocketHandler.removeVariableListener(this.state.dTargetName);
        SocketHandler.removeVariableListener(this.state.fTargetName);
        // do something with success, and also update event listener for variable changes
        this.setState({
            pTargetName: newPVar,
            pUpdateName: newPVar,
            pTargetValue: SocketHandler.getVariable(newPVar),
            iTargetName: newIVar,
            iUpdateName: newIVar,
            iTargetValue: SocketHandler.getVariable(newIVar),
            dTargetName: newDVar,
            dUpdateName: newDVar,
            dTargetValue: SocketHandler.getVariable(newDVar),
            fTargetName: newFVar,
            fUpdateName: newFVar,
            fTargetValue: SocketHandler.getVariable(newFVar)
        });
        this.pCallbackId = SocketHandler.addVariableListener(this.state.pUpdateName, (value) => this.updateState(value));
        this.pCallbackId = SocketHandler.addVariableListener(this.state.iUpdateName, (value) => this.updateState(value));
        this.pCallbackId = SocketHandler.addVariableListener(this.state.dUpdateName, (value) => this.updateState(value));
        this.pCallbackId = SocketHandler.addVariableListener(this.state.fUpdateName, (value) => this.updateState(value));

    }

    onPVarSave() {
        let newPVal = $('#' + this.props.id + 'p_var_display').val();
        SocketHandler.setVariable(this.state.pTargetName, newPVal);
    }
    onIVarSave() {
        let newIVal = $('#' + this.props.id + 'i_var_display').val();
        SocketHandler.setVariable(this.state.iTargetName, newIVal);
    }
    onDVarSave() {
        let newDVal = $('#' + this.props.id + 'd_var_display').val();
        SocketHandler.setVariable(this.state.dTargetName, newDVal);
    }
    onFVarSave() {
        let newFVal = $('#' + this.props.id + 'f_var_display').val();
        SocketHandler.setVariable(this.state.fTargetName, newFVal);
    }

    onPFieldEdit(e) {
        this.setState({pTargetValue: e.ptarget.value});
    }
    onIFieldEdit(e) {
        this.setState({iTargetValue: e.itarget.value});
    }
    onDFieldEdit(e) {
        this.setState({dTargetValue: e.dtarget.value});
    }
    onFFieldEdit(e) {
        this.setState({fTargetValue: e.ftarget.value});
    }

    onPSettingsEdit(e) {
        this.setState({updateName: e.ptarget.value});
    }
    onISettingsEdit(e) {
        this.setState({updateName: e.itarget.value});
    }
    onDSettingsEdit(e) {
        this.setState({updateName: e.dtarget.value});
    }
    onFSettingsEdit(e) {
        this.setState({updateName: e.ftarget.value});
    }

    render() {
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + 'p_var_display'} placeholder="value1" value={this.state.pTargetValue} onChange={(e) => this.onPFieldEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'i_var_display'} placeholder="value2" value={this.state.iTargetValue} onChange={(e) => this.onIFieldEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'd_var_display'} placeholder="value3" value={this.state.dTargetValue} onChange={(e) => this.onDFieldEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'f_var_display'} placeholder="value4" value={this.state.fTargetValue} onChange={(e) => this.onFFieldEdit(e)} />
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'} onClick={() => this.onVarSave()} >Submit</button>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                    <input className='form-control mb-2' type='text' id={this.props.id + 'p_settings_variable'} placeholder="variable" value={this.state.pUpdateName} onChange={(e) => this.onSettingsEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'i_settings_variable'} placeholder="variable" value={this.state.iUpdateName} onChange={(e) => this.onSettingsEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'd_settings_variable'} placeholder="variable" value={this.state.dUpdateName} onChange={(e) => this.onSettingsEdit(e)} />
                    <input className='form-control mb-2' type='text' id={this.props.id + 'f_settings_variable'} placeholder="variable" value={this.state.fUpdateName} onChange={(e) => this.onSettingsEdit(e)} />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('PIDWidget', PIDWidget);
