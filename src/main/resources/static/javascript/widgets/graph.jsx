import {WidgetContainer, WidgetBody, WidgetSettings} from "widget-components";
import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

export default class Graph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            depVars: [this.props.variables.deps],
            // updateName: this.props.variables.inDep,
            indepVar: this.props.variables.inDep,
        };
        this.xVarCallbackIDs = []
        this.state.depVars.forEach((v) => {
            this.xVarCallbackIDs.push(SocketHandler.addVariableListener(v, (value) => this.updateState(value)));
        });
        if(!this.state.inDepVar == "time"){
            this.yVarCallbackID = SocketHandler.addVariableListener(this.state.inDepVar, (value) => this.updateState(value));
        }
        this.chartRef = React.createRef();
        this.chartConfig = this.props.kwargs;
        this.timer;
        this.chart;
    }

    //runs when componet is renderd into DOM
    componentDidMount(){
        let ctx = this.chartRef.current.getContext("2d");
        console.log(this.chartConfig);
        this.chart = new Chart(ctx, this.chartConfig);
        if(this.state.indepVar == "time"){
            let maxTime = 60.0;
            let minTime = 0.0;
            let time = 0.0;
            this.timer = setInterval(() => {
                console.log(time);
                time += 0.025;
                if(time>10){
                    maxTime += 0.025,
                    minTime += 0.025
                }
                this.chartConfig.options.scales.xAxes[0].ticks.max = Math.round(maxTime*10)/10;
                this.chartConfig.options.scales.xAxes[0].ticks.min = Math.round(minTime*10)/10;
                this.chartConfig.data.labels.push(Math.round(time*10)/10);
                this.chart.update()
            }, 25);
        }
    }

    updateState() {
        this.chartConfig.data.datasets
        this.chart.update()
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
            //updateName: newVar,
            targetValue: SocketHandler.getVariable(newVar)
        });
        // this.callbackId = SocketHandler.addVariableListener(this.state.updateName, (value) => this.updateState(value));
    }

    onFieldEdit(e) {
        this.setState({targetValue: e.target.value});
    }

    onsettingsEdit(e) {
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
