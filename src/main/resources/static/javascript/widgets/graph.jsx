import {WidgetContainer, WidgetBody, WidgetSettings} from "widget-components";
import PageUtils from "page-utils";
import SocketHandler from "socket-handler";

export default class Graph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            depVar: this.props.variables.dep,
            // updateName: this.props.variables.inDep,
        };

        // this.xVarCallbackIDs = []
        // this.state.depVars.forEach((v) => {
        //     console.log(v)
        //     this.xVarCallbackIDs.push(SocketHandler.addVariableListener(v, (value, v) => this.updateState(value, v)));
        // });
        this.varCallbackID = SocketHandler.addVariableListener(this.state.depVar, (value) => this.updateState(value));
        this.chartRef = React.createRef();
        this.chartConfig = this.props.kwargs;
        this.timer;
        this.chart;
        this.range;
    }

    //runs when componet is renderd into DOM
    componentDidMount(){
        let ctx = this.chartRef.current.getContext("2d");
        console.log(this.chartConfig);
        this.chart = new Chart(ctx, this.chartConfig);
        let chart = this.chart
        let chartConfig = this.chartConfig;
        let label1 = $("#" + this.props.id + "_label1");
        let label2 = $("#" + this.props.id + "_label2");
        let times = [];
        this.range = $( "#" + this.props.id + "_range" ).slider({
            range: true,
            animate: "fast",
            min: 0,
            max: 150,
            values: [ 0, 150 ],
            slide: function( event, ui ) {
                chartConfig.options.scales.xAxes[0].ticks.max = ui.values[1];
                chartConfig.options.scales.xAxes[0].ticks.min = ui.values[0];
                label1.text(ui.values[0]);
                label1.css("margin-left", (ui.values[0])/(150)*100+"%");
                label1.css("left", "-50px");
                label2.text(ui.values[1]);
                label2.css("margin-left", (ui.values[1])/(150)*100+"%");
                label2.css("left", "-50px");
                chart.update();
            }
          });
        for(let i=0;i<=150;i+=1){
            times.push(Math.round(Math.round(i*10)*10)/100);
            console.log(i);
        }
        this.chartConfig.data.labels = times;
        this.chart.update();
        $('#' + this.props.id + '_reset').click(() => {
            time = 0;
            this.chartConfig.data.labels = [];
            this.chart.update();
        });
    }

    updateState(value) {
        this.chartConfig.data.datasets.push(value);
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
        this.chart.update()
    }

    render() {
        return ([
            <WidgetContainer key={this.props.id + '_main'} title={this.props.title} width={this.props.width} height={this.props.height} id={this.props.id}>
                <WidgetBody title={this.props.title} id={this.props.id}>
                    <canvas id={this.props.id + '_canvas'} ref={this.chartRef} className='chart'></canvas>
                    <div style={{marginLeft:'6%', marginRight:'1%'}}>
                        <div id={this.props.id + '_range'}></div>
                        <div id={this.props.id + '_labels'} className='labelWrapper'>
                            <span id={this.props.id + '_label1'} className='label'></span>
                            <span id={this.props.id + '_label2'} className='label'></span>
                        </div>
                    </div>
                </WidgetBody>
            </WidgetContainer>,
            <WidgetSettings key={this.props.id + '_settings'} title={this.props.title} id={this.props.id} onSave={() => this.onSettingsSave()}>
                <input className='form-control mb-2' type='text' id={this.props.id + '_settings_variable'} placeholder="variable" value={this.state.updateName} onChange={(e) => this.onSettingsEdit(e)} />
                <button className='btn btn-light form-control mb-2' id={this.props.id + '_reset'}>Reset X-Axes</button>
                <input className='form-control mb-2'></input>
            </WidgetSettings>
        ]);
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('Graph', Graph);
