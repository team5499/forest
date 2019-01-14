package org.team5499.dashboard

import org.json.JSONObject
import org.json.JSONArray
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
@Suppress("TooManyFunctions")
class Config() {

    companion object {
        const val EMPTY_CONFIG_STRING: String = "{\"pages\":[]}"
        var widgets: String = Utils.readResourceAsString(this, "widgets.json")
            private set
    }

    var configObject: JSONObject = JSONObject(Config.EMPTY_CONFIG_STRING)
        private set

    private var pages: JSONObject
        get() {
            synchronized(configObject) {
                return configObject.getJSONObject("pages")
            }
        }
        set(value) {
            synchronized(configObject) {
                configObject.put("pages", value)
            }
        }

    public var devMode: Boolean = false

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
        if (this.configObject.opt("devMode") is Boolean) {
            this.devMode = this.configObject.getBoolean("devMode")
        }
    }

    /**
     * create a json config object from the specified string
     *
     * If the specified string contains errors, an empty configuration is returned
     */
    private fun makeConfigJSONObject(json: String): JSONObject {
        var newConfigObject: JSONObject
        // check if string contains correctly formatted json
        try {
            newConfigObject = JSONObject(json)
        } catch (je: JSONException) {
            throw ConfigException("Improperly formatted JSON was detected when reading the dashboard configuration!")
        }
        // check if json contains "pages" element
        if (!newConfigObject.has("pages")) {
            println("Config json does not contain \"pages\" element!")
            fail()
            synchronized(configObject) {
                return configObject
            }
        }
        return newConfigObject
    }

    /**
     * Fail with warning and set current object to empty config
     */
    private fun fail() {
        println("resetting to empty config")
        synchronized(configObject) {
            configObject = JSONObject(Config.EMPTY_CONFIG_STRING)
        }
    }

    // config util functions
    /**
     * get the specified key as the specified type
     *
     * if the specified key is not found, a ConfigException is thrown
     *
     * @param key the key associated with the requested value
     * @param T the type to return
     */
    private inline fun <reified T> JSONObject.getOrFail(key: String): T {
        if (!this.has(key)) {
            throw ConfigException("Key $key was not found in specified JSONObject!")
        } else if (this.get(key) !is T) {
            throw ConfigException("Could not get key $key from JSONObject as type ${T::class}!")
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
        var attributes: HashMap<String, Any> = HashMap<String, Any>()
        val page: JSONObject
        try {
            page = pages.getJSONObject(pageName)
        } catch (je: JSONException) {
            throw ConfigException("Could not get configuration for page with name $pageName")
        }

        val title: String = page.getOrFail<String>("title")

        attributes.put("pageTitle", title)
        attributes.put("activePage", pageName)
        attributes.putAll(getBaseAttributes())

        return attributes
    }

    fun getBaseAttributes(): HashMap<String, Any> {
        var attributes: HashMap<String, Any> = HashMap()
        var navbar: HashMap<String, Any> = HashMap()
        for (i in getPageNamesInNavBarOrder()) {
            val tmpTitle: String = pages.getJSONObject(i).getOrFail<String>("title")
            navbar.put(i, tmpTitle)
        }
        attributes.put("navbar", navbar)
        attributes.put("developmentEnvironment", devMode)
        return attributes
    }

    /**
     * Get an array of the defined page names
     *
     * @return an Array of strings
     */
    fun getPageNames(): Array<String> {
        return pages.keySet().toTypedArray()
    }

    /**
     * Get an array of the defined page names in the order that they should appear on the nav bar
     *
     * @return an Array of strings
     */
    fun getPageNamesInNavBarOrder(): Array<String> {
        val unorderedNames: Array<String> = getPageNames()
        val orderedNames: Array<String> = unorderedNames.copyOf()
        for (n in unorderedNames) {
            val order = pages.getJSONObject(n).getOrFail<Int>("navbarOrder")
            orderedNames[order] = n
        }
        return orderedNames
    }

    fun getNumberOfPages(): Int {
        return getPageNames().size
    }

    fun hasPageWithName(name: String): Boolean {
        return getPageNames().contains(name)
    }

    fun setConfigJSON(json: String) {
        synchronized(configObject) {
            configObject = makeConfigJSONObject(json)
        }
    }

    fun addPage(name: String, title: String) {
        var page: JSONObject = JSONObject()
        page.put("navbarOrder", getNumberOfPages())
        page.put("title", title)
        page.put("widgets", JSONArray())
        pages.put(name, page)
    }
}
