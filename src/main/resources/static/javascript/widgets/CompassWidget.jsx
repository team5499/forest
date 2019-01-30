class CompassWidget extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            targetValue: SocketHandler.getVariable(this.props.variables.target) || ""
        };
        this.callbackId = SocketHandler.addVariableListener((value) => this.updateState(value));
    }
    updateState(value) {
        this.setState({targetValue: value || ""});
    }


    render() {
        return (
            <WidgetContainer className = ".card-img-top" title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <h1>{this.state.targetValue}</h1>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} placeholder="variable" />
                </WidgetSettings>
            </WidgetContainer>
        );
    }

}

// make sure to do this for every widget
PageUtils.addWidgetClass('CompassWidget', CompassWidget);
