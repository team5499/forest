package org.team5499.dashboard

import org.json.JSONObject
import org.json.JSONArray

class StringChooser(val dashboardName: String, val default: String, vararg initialOptions: String) : DashboardType {
    public var options: MutableList<String>
    public var selected: String
        get() {
            return Dashboard.getVariable<JSONObject>(dashboardName).getString("selected")
        }
        set(value) {
            Dashboard.setVariable(dashboardName, getBroadcastObject(value))
        }

    @Suppress("SpreadOperator")
    init {
        options = mutableListOf(*initialOptions)
        selected = default
    }

    private fun getBroadcastObject(value: String): JSONObject {
        val newJSON = JSONObject()
        val optionsJSON = JSONArray()
        options.forEach({
            optionsJSON.put(it)
        })
        newJSON.put("options", optionsJSON)
        newJSON.put("selected", value)
        newJSON.put("vartype", "StringChooser")
        return newJSON
    }

    fun addVarListener(callback: VariableCallback): Int {
        return Dashboard.addVarListener(dashboardName) {
            key: String, value: Any? ->
            callback(key, (value as? JSONObject)!!.get("selected") as? Any)
        }
    }

    fun removeVarListener(id: Int): Boolean {
        return Dashboard.removeVarListener(dashboardName, id)
    }

    fun runIfUpdate(callback: VariableCallback): Boolean {
        return Dashboard.runIfUpdate(dashboardName) {
            key: String, value: Any? ->
            callback(key, (value as? JSONObject)!!.get("selected") as? Any)
        }
    }

    fun addInlineListener(callback: VariableCallback): Int {
        return Dashboard.addInlineListener(dashboardName) {
            key: String, value: Any? ->
            callback(key, (value as? JSONObject)!!.get("selected") as? Any)
        }
    }

    fun removeInlineListener(id: Int): Boolean {
        return Dashboard.removeInlineListener(dashboardName, id)
    }
}
