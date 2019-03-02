import PageUtils from 'page-utils';
import Widget from 'widget';

class StringChooser {}

StringChooser.Body = class extends Widget.Body {
    init() {
        console.log(this.constructor.name)
        let newValue = (this.getVariable(this.widgetConfig.variables.target) === undefined) ? {options: [''], selected: ''} : this.getVariable(this.widgetConfig.variables.target);
        this.state = {
            targetName: this.widgetConfig.variables.target,
            targetValue: newValue
        };
        this.callbackId = this.registerVarListener(this.state.targetName, (key, value) => this.updateState(key, value));
    }

    updateState(key, value) {
        this.setState({targetValue: value});
    }

    settingsSave(newConfig) {
        this.removeVarListener(this.state.targetName, this.callbackId);
        // do something with success, and also update event listener for variable changes
        let newValue = (this.getString(newConfig.variables.target) === undefined) ? '' : this.getString(newConfig.variables.target);
        this.setState({
            targetName: newConfig.variables.target,
            targetValue: newValue
        });
        this.callbackId = this.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    getOptions() {
        let options = [];
        for(let i in this.state.targetValue.options) {
            options.push(<a className="dropdown-item" onClick={() => this.updateSelectedValue(this.state.targetValue.options[i])} key={this.state.targetValue.options[i]} href="#">{this.state.targetValue.options[i]}</a>);
        }
        return options;
    }

    updateSelectedValue(select) {
        let newValue = this.state.targetValue;
        newValue.selected = select;
        this.setState({targetValue: newValue});
        this.setVariable(this.state.targetName, newValue);
    }

    render() {
        let buttonId = this.widgetConfig.id + "dropdownButton";
        return (
            <div className="dropdown show">
                <a className="btn btn-primary dropdown-toggle" href="#" role="button" id={buttonId} data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    {this.state.targetValue.selected}
                </a>

                <div className="dropdown-menu" aria-labelledby={buttonId}>
                    {this.getOptions()}
                </div>
            </div>
        );
    }
}

StringChooser.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            targetName: this.widgetConfig.variables.target
        };
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.variables.target = this.state.targetName;
        return newConfig;
    }

    onSettingsEdit(e) {
        this.setState({targetName: e.target.value});
    }

    render() {
        return (
            <input className='form-control mb-2' type='text' placeholder='variable' value={this.state.targetName} onChange={(e) => this.onSettingsEdit(e)} />
        );
    }
}

export default StringChooser;

// make sure to do this for every widget
PageUtils.addWidgetClass('StringChooser', StringChooser);
