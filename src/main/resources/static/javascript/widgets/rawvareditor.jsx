class RawVarEditor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            targetName: this.props.variables.target,
            updateName: this.props.variables.target,
            targetValue: undefined
        };
        this.callbackId = SocketHandler.addVariableListener(this.state.targetName, (value) => this.updateState(value));
    }

    updateState(value) {
        this.setState({targetValue: value || ''});
    }

    onSettingsSave() {
        let newVar = $('#' + this.props.id + '_settings_variable').val();
        if(!SocketHandler.hasVariable(newVar)) {
            this.setState({
                updateName: this.state.targetName
            });
            return;
        } else {
            let widget = PageUtils.getPageWidget(this.props.id);
            widget.variables.target = newVar;
            let success = PageUtils.setPageWidget(this.props.id, widget);
            SocketHandler.removeVariableListener(this.state.targetName);
            // do something with success, and also update event listener for variable changes
            this.setState({
                targetName: newVar,
                updateName: newVar,
                targetValue: SocketHandler.getVariable(newVar)
            });
            this.callbackId = SocketHandler.addVariableListener(this.state.updateName, (value) => this.updateState(value));
        }
    }

    onVarSave() {
        // yeet this
        // SocketHandler.setVariable(this.state.targetName, this.state.targetValue);
    }

    onFieldEdit(e) {
        if(e.target.type === 'number') {
            this.setState({targetValue: parseFloat(e.target.value)});
        } else if(e.target.type === 'checkbox') {
            this.setState({targetValue: e.target.checked});
        } else {
            this.setState({targetValue: e.target.value});
        }
    }

    onSettingsEdit(e) {
        this.setState({updateName: e.target.value});
        if(!SocketHandler.hasVariable(e.target.value)) {
            $('#' + this.props.id + '_modal_save').addClass('disabled');
        } else {
            $('#' + this.props.id + '_modal_save').removeClass('disabled');
        }
    }

    getInputElement() {
        if(typeof this.state.targetValue === 'string') {
            return <input className='form-control mb-2' type='text' id={this.props.id + '_var_display'} placeholder='value' value={this.state.targetValue} onChange={(e) => this.onFieldEdit(e)} />;
        } else if(typeof this.state.targetValue === 'number') {
            return <input className='form-control mb-2' type='number' id={this.props.id + '_var_display'} placeholder='0.0' value={this.state.targetValue} onChange={(e) => this.onFieldEdit(e)} />;
        } else if(typeof this.state.targetValue === 'boolean') {
            return <input className='form-check-input' type='checkbox' id={this.props.id + '_var_display'} checked={this.state.targetValue} onClick={(e) => this.onFieldEdit(e)} />;
        } else {
            return (
                <div className='alert alert-primary' role='alert'>
                    The variable type {typeof this.state.targetValue} is not supported for this widget.
                </div>
            );
        }
    }

    render() {
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    {this.getInputElement()}
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'} onClick={() => this.onVarSave()} >Submit</button>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} placeholder='variable' value={this.state.updateName} onChange={(e) => this.onSettingsEdit(e)} />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('RawVarEditor', RawVarEditor);
