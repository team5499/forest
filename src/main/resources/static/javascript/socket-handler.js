let BROADCAST_INTERVAL = 40.0; // Hertz
let socket = {};
let variables = {};
let variableUpdates = {};
let callbacks = {};
let isConnected = false;
let broadcastInterval = 0;
let wsAddress = "";

export default class SocketHandler {

    static connect(address) {
        wsAddress = address;
        socket = new WebSocket(wsAddress);
        socket.onopen = SocketHandler.onopen
        socket.onclose = SocketHandler.onclose
        socket.onmessage = SocketHandler.onmessage
    }

    static isConnected() {
        return isConnected;
    }

    static onopen(event) {
        isConnected = true;
        console.warn("Robot connected! Event: " + event)
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

    static onclose(event) {
        isConnected = false;
        console.warn("Robot disconnected! Event: " + event);
        // stop the websocket sender loop
        window.clearInterval(broadcastInterval);
        // maybe reconnect?
        new Promise((resolve) => {
            window.setInterval(() => {
                socket = new WebSocket(wsAddress);
                socket.onopen = () => {
                    resolve();
                }
            }, 3000);

        }).then(() => {
            window.location.reload(true);
        });
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
                callbacks[u][c](u, updates[u]);
            }
        }
    }

    static addVariableListener(key, callback) {
        if(callbacks[key] === undefined) {
            callbacks[key] = {};
        }
        let uid = key + Date.now().toString();
        callbacks[key][uid] = callback;
        return uid;
    }

    static removeVariableListener(key, id) {
        return delete callbacks[key][id];
    }

    static getVariable(key) {
        if((!(key in variables)) && (!(key in variableUpdates))) {
            console.warn("variable " + key + " not found!");
            return undefined;
        } else if(key in variableUpdates) {
            return variableUpdates[key];
        } else {
            return variables[key];
        }
    }

    static setVariable(key, value) {
        variableUpdates[key] = value;
    }

    static setInt(key, value) {
        let newValue = parseInt(value);
        SocketHandler.setVariable(key, newValue);
    }

    static setDouble(key, value) {
        let newValue = parseFloat(value);
        SocketHandler.setVariable(key, newValue);
    }

    static setString(key, value) {
        let newValue = String(value);
        SocketHandler.setVariable(key, newValue);
    }

    static setBoolean(key, value) {
        let newValue = Boolean(value);
        SocketHandler.setVariable(key, newValue);
    }

    static getInt(key) {
        let value = SocketHandler.getVariable(key);
        if(value === undefined) {
            return undefined;
        }
        return parseInt(value);
    }

    static getDouble(key) {
        let value = SocketHandler.getVariable(key);
        if(value === undefined) {
            return undefined;
        }
        return parseFloat(value);
    }

    static getString(key) {
        let value = SocketHandler.getVariable(key);
        if(value === undefined) {
            return undefined;
        }
        return String(value);
    }

    static getBoolean(key) {
        let value = SocketHandler.getVariable(key);
        if(value === undefined) {
            return undefined;
        }
        return Boolean(value);
    }
}
