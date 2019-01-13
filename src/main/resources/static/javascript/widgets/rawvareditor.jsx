class RawVarEditor extends React.Component {
    onSettingsSave() {
        let widget = PageUtils.getPageWidget(this.props.id);
        widget.variables.target = $('#' + this.props.id + '_settings_variable').val();
        let success = PageUtils.setPageWidget(this.props.id, widget);
        console.log(success);

        // do something with success, and also update event listener for variable changes
    }

    render() {
        return (
            <WidgetContainer title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_var_display'} placeholder='value' />
                    <button className='btn btn-primary' id={this.props.id + '_body_submit'}>Submit</button>
                </WidgetBody>
                <WidgetSettings title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                    <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} defaultValue={this.props.variables.target} />
                </WidgetSettings>
            </WidgetContainer>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('RawVarEditor', RawVarEditor);
