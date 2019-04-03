import PageUtils from 'page-utils';
import Widget from 'widget';

class Button {}

Button.Body = class extends Widget.Body {
    init() {
        this.state = {
            task: this.widgetConfig.kwargs.task
        };
    }

    // updateState(key, value) {
    //     this.setState({targetValue: value});
    // }

    settingsSave(newConfig) {
        this.setState({
            task: newConfig.task
        });
        this.callbackId = this.registerVarListener(newConfig.variables.target, (key, value) => this.updateState(key, value));
    }

    // onVarSave() {
    //     this.setInt(this.state.targetName, this.state.targetValue);
    // }

    // onFieldEdit(e) {
    //     let newValue = (e.target.value === "") ? "" : parseInt(e.target.value);
    //     this.updateState(this.state.targetName, newValue);
    // }

    render() {
        return (
            <div>
                <button className='btn btn-primary' onClick={() => this.click()}>Submit</button>
            </div>
        );
    }
}

Button.Settings = class extends Widget.Settings {
    init() {
        this.state = {
            task: this.widgetConfig.kwargs.task
        };
    }

    settingsData(config) {
        let newConfig = config;
        newConfig.task = this.state.kwargs.task;
        return newConfig;
    }

    // onSettingsEdit(e) {
    //     this.setState({targetName: e.target.value});
    // }

    render() {
        return (
            <div>
                <label>Operation</label>
                <select>
                    <option value="Zero Hatch Mech"></option>
                    <option value="Zero Elevator"></option>
                </select>
            </div>
        );
    }
}

console.log(Button);

// make sure to do this for every widget
PageUtils.addWidgetClass('Button', Button);
