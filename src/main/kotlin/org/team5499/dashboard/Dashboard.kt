package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine

import com.hubspot.jinjava.loader.ClasspathResourceLocator
import com.hubspot.jinjava.JinjavaConfig

import org.json.JSONObject

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
@Suppress("TooManyFunctions")
object Dashboard {

    private var config: Config = Config()
    private val pageSource: String = Utils.readResourceAsString(this, "page.html")
    public var variables: JSONObject = JSONObject()
        get() {
            synchronized(field) {
                return field
            }
        }
        private set(value) {
            synchronized(field) {
                field = value
            }
        }
    public var variableUpdates: JSONObject = JSONObject()
        get() {
            synchronized(field) {
                return field
            }
        }
        private set(value) {
            synchronized(field) {
                field = value
            }
        }

    /**
     * Start the dashboard server with a custom port and specified config file
     *
     * Open ports on the FMS are 5800 - 5810
     *
     * @param obj the object that this function is being called from
     * @param path the relative path to the JSON config file
     * @param port the port to host the dashboard on
     */
    fun start(obj: Any, path: String, port: Int = 5800) {
        config = Config(Utils.readResourceAsString(obj, path))

        Spark.port(port)
        Spark.webSocket("/socket", SocketHandler::class.java)
        Spark.staticFiles.location("/static")

        Spark.get("/", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = config.getBaseAttributes()
            JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator()).render(
                ModelAndView(attributes, "home.html")
            )
        })

        Spark.get("/page/:name", {
            request: Request, response: Response ->
            val requestedPageName: String = request.params(":name")
            if (!config.hasPageWithName(requestedPageName)) {
                response.redirect("/")
            }
            val attributes: HashMap<String, Any> = config.getPageAttributes(requestedPageName)
            JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator()).render(
                ModelAndView(attributes, "page.html")
            )
        })

        Spark.get("/config", {
            request: Request, response: Response ->
            response.type("text/json")
            @Suppress("MagicNumber")
            config.configObject.toString(4)
        })

        Spark.post("/config", {
            request: Request, response: Response ->
            config.setConfigJSON(request.body())
        })

        // Utils
        Spark.get("/utils/newpage", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = config.getBaseAttributes()
            if (request.queryParams("pageexists") == "true") {
                attributes.put("pageExistsError", true)
            }
            JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator()).render(
                ModelAndView(attributes, "newpage.html")
            )
        })

        // Actions
        Spark.post("/actions/newpage", {
            request: Request, response: Response ->
            val pagename: String = request.queryParams("pagename")
            val pagetitle: String = request.queryParams("pagetitle")
            println("Attempting to create page with name: $pagename")
            if (config.hasPageWithName(pagename)) {
                response.redirect("/utils/newpage?pageexists=true")
            } else {
                // make the new page and redirect to it
                config.addPage(pagename, pagetitle)
                response.redirect("/page/$pagename")
            }
            null
        })

        SocketHandler.startBroadcastThread() // start broadcasting data
    }

    fun setString(key: String, value: String) {
        variableUpdates.put(key, value)
    }

    fun setInt(key: String, value: Int) {
        variableUpdates.put(key, value)
    }

    fun setBool(key: String, value: Boolean) {
        variableUpdates.put(key, value)
    }

    fun setDouble(key: String, value: Double) {
        variableUpdates.put(key, value)
    }

    fun getString(key: String): String {
        if ((!variables.has(key)) && (!variableUpdates.has(key))) {
            throw DashboardException("The variable with name " + key + " was not found.")
        } else if (variableUpdates.has(key)) {
            return variableUpdates.getString(key)
        } else {
            return variables.getString(key)
        }
    }

    fun getInt(key: String): Int {
        if ((!variables.has(key)) && (!variableUpdates.has(key))) {
            throw DashboardException("The variable with name " + key + " was not found.")
        } else if (variableUpdates.has(key)) {
            return variableUpdates.getInt(key)
        } else {
            return variables.getInt(key)
        }
    }

    fun getBool(key: String): Boolean {
        if ((!variables.has(key)) && (!variableUpdates.has(key))) {
            throw DashboardException("The variable with name " + key + " was not found.")
        } else if (variableUpdates.has(key)) {
            return variableUpdates.getBoolean(key)
        } else {
            return variables.getBoolean(key)
        }
    }

    fun getDouble(key: String): Double {
        if ((!variables.has(key)) && (!variableUpdates.has(key))) {
            throw DashboardException("The variable with name " + key + " was not found.")
        } else if (variableUpdates.has(key)) {
            return variableUpdates.getDouble(key)
        } else {
            return variables.getDouble(key)
        }
    }

    fun mergeVariableUpdates() {
        for (u in variableUpdates.keys()) {
            variables.put(u, variableUpdates.get(u))
        }
        variableUpdates = JSONObject()
    }

    fun mergeVariableUpdates(json: JSONObject) {
        for (u in json.keys()) {
            variables.put(u, json.get(u))
        }
    }
}
