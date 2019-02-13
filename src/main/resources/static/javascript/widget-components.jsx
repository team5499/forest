import SocketHandler from 'socket-handler';

export class WidgetContainer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            settingsSave: () => console.error("No settings save callback set!"),
            settingsData: () => console.error("No settings data callback set!"),
            title: this.props.widgetConfig.title
        }
    }

    /**
    * This sets the callback that will get called with the current settings
    * when the settings are updated.
    *
    * @param {function} callback the callback to call
    */
    setSettingsSaveCallback(callback) {
        this.setState({
            settingsSave: (newConfig) => callback(newConfig)
        });
    }

    /**
    * This sets the callback that will get the current
    * settings data when the save settings button is pressed.
    *
    * @param {function} callback the callback to call
    */
    setSettingsDataCallback(callback) {
        this.setState({
            settingsData: (config) => callback(config)
        });
    }

    /**
     * This function gets called when the settings save button is pressed,
     * and sends the updated settings to the body of the widget.
     */
    onSettingsSave() {
        let currentWidgetConfig = this.props.getWidgetConfig();
        let newConfig = this.state.settingsData(currentWidgetConfig);

        this.setState({
            title: newConfig.variables.target
        });

        this.state.settingsSave(this.state.settingsData(currentWidgetConfig));
    }

    render() {
        return (
            <div className='card m-1' style={{width: this.props.widgetConfig.width, height: this.props.widgetConfig.height, display:'inline-block'}} id={this.props.widgetConfig.id + '_card'}>
                <div className='card-header p-1'>
                    <h4 className='m-0 d-inline'>{this.state.title}</h4>
                    <button className='btn btn-light float-right d-inline p-0 m-1' type='button' data-toggle='modal' data-target={'#' + this.props.widgetConfig.id + '_modal'}><h5 className='fas fa-cog m-0'></h5></button>
                </div>
                <WidgetBody title={this.props.widgetConfig.title} id={this.props.widgetConfig.id}>
                    {React.createElement(this.props.widgetClass.Body,
                        {registerVarListener: (variable, callback) => SocketHandler.addVariableListener(variable, callback),
                            removeVarListener: (variable, id) => SocketHandler.removeVariableListener(variable, id),
                            setInt: (key, value) => SocketHandler.setInt(key, value),
                            setDouble: (key, value) => SocketHandler.setDouble(key, value),
                            setString: (key, value) => SocketHandler.setString(key, value),
                            setBoolean: (key, value) => SocketHandler.setBoolean(key, value),
                            getInt: (key) => SocketHandler.getInt(key),
                            getDouble: (key) => SocketHandler.getDouble(key),
                            getString: (key) => SocketHandler.getString(key),
                            getBoolean: (key) => SocketHandler.getBoolean(key),

                            setSettingsSaveCallback: (callback) => this.setSettingsSaveCallback(callback),

                            widgetConfig: this.props.widgetConfig}, null)}
                </WidgetBody>
                <WidgetSettings title={this.props.widgetConfig.title} id={this.props.widgetConfig.id} onSave={() => this.onSettingsSave()}>
                    {React.createElement(this.props.widgetClass.Settings,
                        {registerVarListener: (variable, callback) => SocketHandler.addVariableListener(variable, callback),
                            removeVarListener: (variable, id) => SocketHandler.removeVariableListener(variable, id),
                            setInt: (key, value) => SocketHandler.setInt(key, value),
                            setDouble: (key, value) => SocketHandler.setDouble(key, value),
                            setString: (key, value) => SocketHandler.setString(key, value),
                            setBoolean: (key, value) => SocketHandler.setBoolean(key, value),
                            getInt: (key) => SocketHandler.getInt(key),
                            getDouble: (key) => SocketHandler.getDouble(key),
                            getString: (key) => SocketHandler.getString(key),
                            getBoolean: (key) => SocketHandler.getBoolean(key),

                            setSettingsDataCallback: (callback) => this.setSettingsDataCallback(callback),

                            widgetConfig: this.props.widgetConfig}, null)}
                </WidgetSettings>
            </div>
        );
    }
}

export class WidgetBody extends React.Component {
    render() {
        return (
            <div className={'card-body p-2 show'} id={this.props.id + '_widget'}>
                {this.props.children}
            </div>
        );
    }
}

export class WidgetSettings extends React.Component {
    render() {
        return (
            <div className='modal fade' id={this.props.id + '_modal'} tabIndex='-1' role='dialog' aria-hidden='true'>
                <div className='modal-dialog modal-dialog-centered' role='document'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id={this.props.id + '_modal_title'}>{this.props.title} Settings</h5>
                            <button type='button' className='close' data-dismiss='modal' aria-label='Close'>
                                <span aria-hidden='true'>&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            {this.props.children}
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className='btn btn-secondary' data-dismiss='modal'>Close</button>
                            <button type='submit' className='btn btn-primary' onClick={() => this.props.onSave()} data-dismiss='modal'>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
