let BROADCAST_INTERVAL = 40.0; // Hertz
let socket = {};
let variables = {};
let variableUpdates = {};
let callbacks = {};
let isConnected = false;
let broadcastInterval = 0;

export default class SocketHandler {
    static connect(address) {
        socket = new WebSocket(address);
        socket.onopen = SocketHandler.onopen
        socket.onclose = SocketHandler.onclose
        socket.onmessage = SocketHandler.onmessage

        broadcastInterval = window.setInterval(function() {
            // if changes, broadcast them
            if (Object.keys(variableUpdates).length > 0) {
                socket.send(JSON.stringify(variableUpdates));
                for(var i in variableUpdates) {
                    variables[i] = variableUpdates[i]
                }
                variableUpdates = {};
            }

        }, 1000.0 / BROADCAST_INTERVAL);
    }

    static isConnected() {
        return isConnected;
    }

    static onopen(event) {
        isConnected = true;
        console.warn("Robot connected! Event: " + event)
    }

    static onclose(event) {
        isConnected = false;
        console.warn("Robot disconnected! Event: " + event);
        // stop the websocket sender loop
        window.clearInterval(broadcastInterval);
        // maybe reconnect?
    }

    static onmessage(event) {
        // update page
        // update variables
        let prefix = event.data.substring(0, event.data.indexOf(":"));
        let updates = JSON.parse(event.data.substring(event.data.indexOf(":") + 1, event.data.length));
        if(prefix === "variables") {
            variables = updates;
            SocketHandler.callCallbacks(updates);
        } else if(prefix === "updates") {
            SocketHandler.callCallbacks(updates);
        }
    }

    static callCallbacks(updates) {
        for(var u in updates) {
            variables[u] = updates[u];
            for(var c in callbacks[u]) {
                callbacks[u][c](updates[u]);
            }
        }
    }

    static addVariableListener(key, callback) {
        if(!Array.isArray(callbacks[key])) {
            callbacks[key] = [];
        }
        callbacks[key].push(callback);
        return callbacks[key].length - 1;
    }

    static removeVariableListener(key, id) {
        callbacks[key].splice(id, 1);
    }

    static _getVariable(key) {
        if((!(key in variables)) && (!(key in variableUpdates))) {
            console.warn("variable " + key + " not found!");
            return undefined;
        } else if(key in variableUpdates) {
            return variableUpdates[key];
        } else {
            return variables[key];
        }
    }

    static _setVariable(key, value) {
        variableUpdates[key] = value;
    }

    static setInt(key, value) {
        let newValue = parseInt(value);
        SocketHandler._setVariable(key, newValue);
    }

    static setDouble(key, value) {
        let newValue = parseFloat(value);
        SocketHandler._setVariable(key, newValue);
    }

    static setString(key, value) {
        let newValue = String(value);
        SocketHandler._setVariable(key, newValue);
    }

    static setBoolean(key, value) {
        let newValue = Boolean(value);
        SocketHandler._setVariable(key, newValue);
    }

    static getInt(key) {
        return parseInt(SocketHandler._getVariable(key));
    }

    static getDouble(key) {
        return parseFloat(SocketHandler._getVariable(key));
    }

    static getString(key) {
        return String(SocketHandler._getVariable(key));
    }

    static getBoolean(key) {
        return Boolean(SocketHandler._getVariable(key));
    }
}
