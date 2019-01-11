class RawVarEditor extends React.Component {
    render() {
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_var_display'} placeholder='value' />
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'}>Submit</button>
                </WidgetBody>
                <WidgetSettings id={this.props.id}>
                    <input className='form-control mb-2' type='text' name='TEST' defaultValue='test value' />
                    <button className='btn btn-primary' id={this.props.id + '_settings_submit'}>Submit</button>
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}
