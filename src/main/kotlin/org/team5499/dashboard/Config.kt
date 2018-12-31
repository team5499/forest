package org.team5499.dashboard

import java.io.File
import java.io.IOException

/**
 * A configuration of the smart dashboard layout
 *
 * This class contains the configuration that determines the layout of the dashboard. A few
 * convenience functions are provided for use by the Dashboard object. Other functions are
 * available to edit the configuration and save it as JSON.
 *
 * @property path the absolute path to the JSON config file
 * @constructor Creates a config from the saved JSON object
 */
class Config(path: String) {
    init {
        try {
            val configFile = File(path)
        } catch (ioe: IOException) {
            println("there was an error loading the config file $path")
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
}
