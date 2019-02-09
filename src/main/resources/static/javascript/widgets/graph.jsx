import {WidgetContainer, WidgetBody, WidgetSettings} from "widget-components";
import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

export default class Graph extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            targetName: this.props.variables.target,
            updateName: this.props.variables.target,
            targetValue: SocketHandler.getVariable(this.props.variables.target) || ""
        };
        this.callbackId = SocketHandler.addVariableListener(this.state.targetName, (value) => this.updateState(value));
        this.chartRef = React.createRef();
    }

    componentDidMount(){
        let ctx = this.chartRef.current.getContext("2d");
        let chart = new Chart(ctx, this.props.kwargs);
        console.log(chart);
    }

    updateState(xValues, datasets) {
        config.labels = xValues
        config.datasets = datasets
        chart.update()
    }

    onSettingsSave() {
        let widget = PageUtils.getPageWidget(this.props.id);
        let newVar = $('#' + this.props.id + '_settings_variable').val();
        widget.variables.target = newVar;
        let success = PageUtils.setPageWidget(this.props.id, widget);
        SocketHandler.removeVariableListener(this.state.targetName);
        // do something with success, and also update event listener for variable changes
        this.setState({
            targetName: newVar,
            updateName: newVar,
            targetValue: SocketHandler.getVariable(newVar)
        });
        this.callbackId = SocketHandler.addVariableListener(this.state.updateName, (value) => this.updateState(value));
    }

    onFieldEdit(e) {
        this.setState({targetValue: e.target.value});
    }

    onSettingsEdit(e) {
        this.setState({updateName: e.target.value});
    }

    render() {
        return ([
            <WidgetContainer key={this.props.id + '_main'} title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <canvas id={this.props.id + '_canvas'} ref={this.chartRef} className='chart'></canvas>
                </WidgetBody>
            </WidgetContainer>,
            <WidgetSettings key={this.props.id + '_settings'} title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} placeholder="variable" value={this.state.updateName} onChange={(e) => this.onSettingsEdit(e)} />
            </WidgetSettings>
        ]);
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('Graph', Graph);
