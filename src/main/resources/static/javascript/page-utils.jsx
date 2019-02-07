import {WidgetContainer, WidgetBody, WidgetSettings} from "widget-components";

let widgetClasses = {};
let config = {};

export default class PageUtils {
    static addWidgetClass(classname, widgetclass) {
        widgetClasses[classname] = widgetclass;
    }

    static loadPageConfig() {
        var newconfig = {};
        let success = false;
        $.getJSON({
            url: '/config',
            async: false
        }).done(function( data ) {
            newconfig = data;
            PageUtils.setPageConfig(newconfig);
            success = true;
        });
        return success;
    }

    static sendPageConfig() {
        let success = false;
        $.ajax({
            async: false,
            method: 'POST',
            url: '/config',
            contentType: 'application/json',
            data: JSON.stringify(PageUtils.getPageConfig()),
            success: function () {
                success = true;
            },
            error: function (jqxhr, status, error) {

                console.warn('error sending config json: ' + status + ' : ' + error);
            }
        });
        return success;
    }

    static getPageConfig() {
        return config;
    }

    static setPageConfig(json) {
        config = json;
    }

    static getPageName() {
        let pathname = window.location.pathname;
        return pathname.substring(pathname.lastIndexOf('/') + 1, pathname.length);
    }

    static getWebSocketPageAddress() {
        return 'ws://' + window.location.host + '/socket';
    }

    static getPageJSON() {
        return config['pages'][PageUtils.getPageName()];
    }

    static getPageWidgets() {
        return PageUtils.getPageJSON()['widgets'];
    }

    static getPageWidgetIndex(id) {
        const widgets = PageUtils.getPageWidgets();
        for(var i in widgets) {
            if(widgets[i]['id'] === id) {
                return i;
            }
        }
        return -1;
    }

    static getPageWidget(id) {
        return PageUtils.getPageWidgets()[PageUtils.getPageWidgetIndex(id)];
    }

    static setPageWidget(id, json) {
        const index = PageUtils.getPageWidgetIndex(id);
        let newconfig = config;
        newconfig.pages[PageUtils.getPageName()].widgets[index] = json;
        PageUtils.setPageConfig(newconfig);
        return PageUtils.sendPageConfig();
    }

    static getWidgetTag(widget) {
        return `${widget.type}`;
    }

    static renderWidgets() {
        let widgetsJson = PageUtils.getPageWidgets();
        let widgets = [];
        for(var i in widgetsJson) {
            let widget = widgetsJson[i];
            const GenericWidget = PageUtils.getWidgetTag(widget);
            widgets.push(
                <WidgetContainer key={widget.id} title={widget.title} width={widget.width} height={widget.height} id={widget.id}>
                    <WidgetBody title={widget.title} id={widget.id}>
                        {React.createElement(widgetClasses[GenericWidget].Body, {key: widget.id, title: widget.title, id: widget.id, width: widget.width, height: widget.height, variables: widget.variables, kwargs: widget.kwargs}, null)}
                    </WidgetBody>
                    <WidgetSettings title={widget.title} id={widget.id} onSave={() => console.log("save settings!")}>
                        {React.createElement(widgetClasses[GenericWidget].Settings, {key: widget.id, title: widget.title, id: widget.id, width: widget.width, height: widget.height, variables: widget.variables, kwargs: widget.kwargs}, null)}
                    </WidgetSettings>
                </WidgetContainer>
            );
            //widgets.push(<GenericWidget key={i.id} title={i.title} id={i.id} width={i.width} height={i.height} variables={i.variables} kwargs={i.kwargs} />);
        }
        return widgets;
    }
}
