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

    var configString: String = Config.EMPTY_CONFIG_STRING
        private set
    var configObject: JSONObject = JSONObject(configString)
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
        try {
            this.configString = file.bufferedReader().readText()
        } catch (ioe: IOException) {
            println("Config file could not be read!")
            println("Creating empty configuration")
            this.configString = Config.EMPTY_CONFIG_STRING
        }
        try {
            this.configObject = JSONObject(this.configString)
        } catch (je: JSONException) {
            println("Config file is improperly formatted!")
            println("Creating empty configuration")
            this.configString = Config.EMPTY_CONFIG_STRING
            this.configObject = JSONObject(this.configString)
        }
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
        this.configString = json
        try {
            this.configObject = JSONObject(this.configString)
        } catch (je: JSONException) {
            println("Config file is improperly formatted!")
            println("Creating empty configuration")
            this.configString = Config.EMPTY_CONFIG_STRING
            this.configObject = JSONObject(this.configString)
        }
    }

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
    fun getPageAttributes(pageId: String): HashMap<String, Any> {

        return HashMap<String, Any>()
    }

    // config util functions
}
