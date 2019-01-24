class LimeLightVideoFeed extends React.Component {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <WidgetContainer className = ".card-img-top" title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <img src="http://limelight.local:5800/" width="320" height="240"></img>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} placeholder="variable" />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('LimeLightVideoFeed', LimeLightVideoFeed);
