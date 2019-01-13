class RawVarEditor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            targetName: this.props.variables.target,
            targetValue: SocketHandler.getVariable(this.props.variables.target) || ""
        };
        this.callbackId = SocketHandler.addVariableListener(this.state.targetName, (value) => this.updateState(value));
    }

    updateState(value) {
        console.log("callback called");
        this.setState({targetName: this.state.targetName, targetValue: value});
    }

    onSettingsSave() {
        let widget = PageUtils.getPageWidget(this.props.id);
        let newVar = $('#' + this.props.id + '_settings_variable').val();
        widget.variables.target = newVar;
        let success = PageUtils.setPageWidget(this.props.id, widget);
        console.log(success);
        SocketHandler.removeVariableListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        this.setState({
            targetName: newVar,
            targetValue: SocketHandler.getVariable(newVar)
        });
        this.callbackId = SocketHandler.addVariableListener(this.state.targetName, function(value) {
            this.setState({targetName: this.state.targetName, targetValue: value});
        });

    }

    onVarSave() {
        console.log("save this shit!");
    }

    render() {
        console.log("render:" + this.state.targetValue);
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_var_display'} value={this.state.targetValue} onChange={() => console.log("change!")} />
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'} onClick={() => this.onVarSave()} >Submit</button>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} value={this.state.targetName} onChange={() => console.log("change!")} />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('RawVarEditor', RawVarEditor);
