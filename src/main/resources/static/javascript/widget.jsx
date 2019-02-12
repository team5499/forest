var Widget = {};

/**
 * Base class that represents the body of a widget
 *
 * Any class that extends this class should define the following functions:
 * @param render() - should return the html to render
 * @param settingsSave(newConfig) - should update the state to reflect the new settings
 */
Widget.Body = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsSaveCallback((newConfig) => this.settingsSave(newConfig));
        this.init(); // extended class should define this
    }

    getInt(key) {
        return this.props.getInt(key);
    }

    getDouble(key) {
        return this.props.getDouble(key);
    }

    getString(key) {
        return this.props.getString(key);
    }

    getBoolean(key) {
        return this.props.getBoolean(key);
    }

    setInt(key, value) {
        return this.props.setInt(key, value);
    }

    setDouble(key, value) {
        return this.props.setDouble(key, value);
    }

    setString(key, value) {
        return this.props.setString(key, value);
    }

    setBoolean(key, value) {
        return this.props.setBoolean(key, value);
    }

    registerVarListener(key, callback) {
        return this.props.registerVarListener(key, callback)
    }

    removeVarListener(key, id) {
        return this.props.removeVarListener(key, id)
    }

}

/**
 * Base class that represents the settings of a widget
 *
 * Any class that extends this class should define the following functions:
 * @param render() - should return the html to render
 * @param settingsData(config) - should return the config object with modified settings
 */
Widget.Settings = class extends React.Component {
    constructor(props) {
        super(props);
        this.props.setSettingsDataCallback((config) => this.settingsData(config));
        this.init(); // extended class should define this
    }

    getInt(key) {
        return this.props.getInt(key);
    }

    getDouble(key) {
        return this.props.getDouble(key);
    }

    getString(key) {
        return this.props.getString(key);
    }

    getBoolean(key) {
        return this.props.getBoolean(key);
    }

    setInt(key, value) {
        return this.props.setInt(key, value);
    }

    setDouble(key, value) {
        return this.props.setDouble(key, value);
    }

    setString(key, value) {
        return this.props.setString(key, value);
    }

    setBoolean(key, value) {
        return this.props.setBoolean(key, value);
    }

    registerVarListener(key, callback) {
        return this.props.registerVarListener(key, callback)
    }

    removeVarListener(key, id) {
        return this.props.removeVarListener(key, id)
    }

}

export default Widget;
