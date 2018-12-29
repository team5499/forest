package org.team5499.dashboard

import java.io.File
import java.io.IOException

import org.json.JSONObject
import org.json.JSONException

/**
 * A configuration of the smart dashboard layout
 *
 * This class contains the configuration that determines the layout of the dashboard. A few
 * convenience functions are provided for use by the Dashboard object. Other functions are
 * available to edit the configuration and save it as JSON.
 *
 * @constructor Creates an empty config object
 */
class Config() {

    companion object {
        const val EMPTY_CONFIG_STRING: String = "{\"pages\":[]}"
        var widgets: String = File(this::class.java.getResource("/widgets.json").path).bufferedReader().readText()
            private set
    }

    var configObject: JSONObject = JSONObject(Config.EMPTY_CONFIG_STRING)
        private set

    /**
     * Construct a configuration with the specified JSON file
     *
     * The json file must
     * 1) be correctly formatted json =)
     * 2) follow the format of a dashboard config file
     *
     * @property file the json file (java.io.File)
     * @constructor creates a config from the speficied JSON file
     */
    constructor(file: File) : this() {
        var configString: String
        try {
            configString = file.bufferedReader().readText()
        } catch (ioe: IOException) {
            println("Config file could not be read!")
            println("Creating empty configuration")
            configString = Config.EMPTY_CONFIG_STRING
        }
        this.configObject = this.makeConfigJSONObject(configString)
    }

    /**
     * Construct a configuration with the specified JSON string
     *
     * The json string must
     * 1) be correctly formatted json =)
     * 2) follow the format of a dashboard config string
     *
     * @property json the json string
     * @constructor creates a config from the speficied JSON string
     */
    constructor(json: String) : this() {
        this.configObject = this.makeConfigJSONObject(json)
    }

    /**
     * create a json config object from the specified string
     *
     * If the specified string contains errors, an empty configuration is returned
     */
    private fun makeConfigJSONObject(json: String): JSONObject {
        var configObject: JSONObject
        // check if string contains correctly formatted json
        try {
            configObject = JSONObject(json)
        } catch (je: JSONException) {
            println("Config json is improperly formatted!")
            println("Creating empty configuration")
            configObject = JSONObject(Config.EMPTY_CONFIG_STRING)
        }
        // check if json contains "pages" element
        if (!configObject.has("pages")) {
            println("Config json does not contain \"pages\" element!")
            println("Creating empty configuration")
            configObject = JSONObject(Config.EMPTY_CONFIG_STRING)
        }
        return configObject
    }

    /**
     * Fail with warning and set current object to empty config
     */
    private fun fail() {
        println("resetting to empty config")
        this.configObject = JSONObject(Config.EMPTY_CONFIG_STRING)
    }

    /**
     * get the specified key as the specified type
     *
     * if the specified key is not found, a warning is printed and
     * fail() is called
     *
     * @param key the key associated with the requested value
     * @param T the type to return
     */
    @Suppress("ReturnCount")
    private inline fun <reified T> JSONObject.getOrFail(key: String): T? {
        if (!this.has(key)) {
            println("Could not get key $key from JSONObject!")
            fail()
            return null
        } else if (this.get(key) !is T) {
            println("Could not get key $key from JSONObject as type ${T::class}!")
            fail()
            return null
        }
        return this.get(key) as T
    }

    // config util functions
    /**
     * Returns the attributes for constructing the specified page
     *
     * Jinjava attributes:
     * - nav bar links
     * - static links
     * - page title
     *
     * @param pageId the id of the requested page
     * @return the jinjava attributes for the page
     */
    fun getPageAttributes(pageName: String): HashMap<String, Any> {
        // try {
        //     configObject.get()
        // }
        return HashMap<String, Any>()
    }

    /**
     * Get a list of the defined page names
     *
     * @return an Array of strings
     */
    fun getPageNames(): Array<String> {
        return configObject.getJSONObject("pages").keySet().toTypedArray()
    }
}
