package org.team5499.dashboard

import org.json.JSONObject
import org.json.JSONArray

@Suppress("SpreadOperator")
class StringChooser(val dashboardName: String, val default: String, vararg initialOptions: String) : DashboardType {
    public var options: MutableList<String>
    public var selected: String
        get() {
            return Dashboard.getVariable<JSONObject>(dashboardName).getString("selected")
        }
        set(value) {
            Dashboard.setVariable(dashboardName, getBroadcastObject(value))
        }

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

    @Deprecated("Renamed to `addConcurrentListener`")
    fun addVarListener(callback: VariableCallback<String>): Long {
        return addConcurrentListener(callback)
    }

    fun addConcurrentListener(callback: VariableCallback<String>): Long {
        return Dashboard.addConcurrentListener(dashboardName) {
            key: String, value: JSONObject? ->
            callback(key, value?.get("selected") as? String)
        }
    }

    @Deprecated("Renamed to `removeConcurrentListener`")
    fun removeVarListener(id: Long): Boolean {
        return removeConcurrentListener(id)
    }

    fun removeConcurrentListener(id: Long): Boolean {
        return Dashboard.removeConcurrentListener(dashboardName, id)
    }

    fun runIfUpdate(callback: VariableCallback<String>): Boolean {
        return Dashboard.runIfUpdate(dashboardName) {
            key: String, value: JSONObject? ->
            callback(key, value?.get("selected") as? String)
        }
    }

    fun addInlineListener(callback: VariableCallback<String>): Long {
        return Dashboard.addInlineListener(dashboardName) {
            key: String, value: JSONObject? ->
            callback(key, value?.get("selected") as? String)
        }
    }

    fun removeInlineListener(id: Long): Boolean {
        return Dashboard.removeInlineListener(dashboardName, id)
    }
}
