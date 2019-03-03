import PageUtils from 'page-utils';
import Widget from 'widget';

class Graph {}
Graph.Body = class extends Widget.Body {
    init() {
        this.state = {
            depVar: this.widgetConfig.variables.dep,
            max: 150
        };

        // this.xVarCallbackIDs = []
        // this.state.depVars.forEach((v) => {
        //     console.log(v)
        //     this.xVarCallbackIDs.push(SocketHandler.addVariableListener(v, (value, v) => this.updateState(value, v)));
        // });

        this.varCallbackID = this.registerVarListener(this.state.depVar, (value) => this.updateState(value));
        this.chartRef = React.createRef();
        this.chartConfig = this.widgetConfig.kwargs;
        this.time = 0;
        this.chart;
        this.range;
    }

    //runs when componet is renderd into DOM
    componentDidMount(){
        let ctx = this.chartRef.current.getContext("2d");
        let parent = this;
        this.chart = new Chart(ctx, this.chartConfig);
        let chart = this.chart
        let chartConfig = this.chartConfig;
        let isPaused = true;
        this.range = $( "#" + this.widgetConfig.id + "_range" ).slider({
            range: true,
            animate: "fast",
            min: 0,
            max: 150,
            values: [ 0, 150 ],
            slide: function( event, ui ) {
                parent.chartConfig.options.scales.xAxes[0].ticks.max = ui.values[1];
                parent.chartConfig.options.scales.xAxes[0].ticks.min = ui.values[0];
                parent.chart.update();
                $("#" + parent.widgetConfig.id + "_label1").css("margin-left", (ui.values[0])/(parent.state.max)*100+"%");
                $("#" + parent.widgetConfig.id + "_label2").css("margin-left", (ui.values[1])/(parent.state.max)*100+"%");
                $("#" + parent.widgetConfig.id + "_label1").css("left", "-50px");
                $("#" + parent.widgetConfig.id + "_label2").css("left", "-50px");
                $("#" + parent.widgetConfig.id + "_label1").text(ui.values[0]);
                $("#" + parent.widgetConfig.id + "_label2").text(ui.values[1]);

            }
          });
        for(let i=0;i<=150;i++){
            this.chartConfig.data.labels.push(i);
        }
        console.log(this.chartConfig)
        this.chart.update();
        $('#' + this.widgetConfig.id + '_reset').click(() => {
            this.chartConfig.data.datasets[0].data = [];
            this.time = 0
            this.chart.update();
        });
        $('#' + this.widgetConfig.id + '_play').click(() => {
            console.log('pause')
            isPaused = !isPaused;
            $('#' + this.widgetConfig.id + '_play').toggleClass('fa-play')
            $('#' + this.widgetConfig.id + '_play').toggleClass('fa-pause')
        });
        let timer = setInterval(() => {
            if(!isPaused){
                this.updateState(Math.random()*50);
                this.time = parseFloat((this.time + 1).toFixed(3));
              }
        }, 1000)
    }

    updateState(value) {
        this.chartConfig.data.datasets[0].data.push({
            x: this.time,
            y: value
        });
        if(this.time > this.chartConfig.data.labels[this.chartConfig.data.labels.length-1]){
            this.setMax(this.chartConfig.data.labels[this.chartConfig.data.labels.length-1] + 10)
        }
        this.chart.update()
    }

    onFieldEdit(e) {
        this.setState({targetValue: e.target.value});
    }

    onsettingsEdit(e) {
        this.setState({updateName: e.target.value});
        this.chart.update()
    }

    setMax(max){
        this.state.max = max;
        let oldMax = this.chartConfig.data.labels[this.chartConfig.data.labels.length-1];
        $("#" + parent.widgetConfig.id + "_label2").text(max);
        for(let i=oldMax+1;i<=max;i++){
            this.chartConfig.data.labels.push(i);
        }
        this.range.slider("option", "max", max);
        if (this.range.slider("values", 1) == oldMax){
            this.range.slider("values", 1, max);
            this.chartConfig.options.scales.xAxes[0].ticks.max = max;
        }
    }

    getMax() {
        return this.state.max;
    }

    render() {
        return (
            <div>
                <canvas id={this.widgetConfig.id + '_canvas'} ref={this.chartRef} className='chart'></canvas>
                <div style={{marginLeft:'6%', marginRight:'1%'}}>
                    <div id={this.widgetConfig.id + '_range'}></div>
                    <div id={this.widgetConfig.id + '_labels'} className='labelWrapper'>
                        <span id={this.widgetConfig.id + '_label1'} className='label'></span>
                        <span id={this.widgetConfig.id + '_label2'} className='label'></span>
                    </div>
                </div>
            </div>
        );
    }
}

Graph.Settings = class extends Widget.Settings{
    init() {
        this.state = {
            updateName: this.widgetConfig.variables.dep,
            setMax: Graph.Body.setMax
        }
    }

    onSettingsEdit(state) {
        let newState = {};
        newState[e.target.name + 'TargetName'] = e.target.value;
        this.setState(newState);
    }

    onSettingsSave() {

    }

    render() {
        return (
            <div>
                <input className='form-control mb-2' type='text' id={this.widgetConfig.id + '_settings_variable'} placeholder="variable" value={this.state.updateName} onChange={(e) => this.onSettingsEdit(e)} />
                <input className='form-control mb-2' type='number' id={this.widgetConfig.id + '_max'} placeholder='max' />
                <button className='btn btn-light form-control mb-2' id={this.widgetConfig.id + '_reset'}>Clear data</button>
                <button id={this.widgetConfig.id + '_play'} className='btn btn-light form-control mb-2 fas fa-play'/>
            </div>
        );
    }
}

// make sure to do this for every widget
PageUtils.addWidgetClass('Graph', Graph);
