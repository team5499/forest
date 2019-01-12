class RawVarEditor extends React.Component {
    render() {
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_var_display'} placeholder='value' />
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'}>Submit</button>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' name='TEST' defaultValue='test value' />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.WidgetClasses['RawVarEditor'] = RawVarEditor
