package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.staticfiles.MimeType

import com.hubspot.jinjava.loader.ClasspathResourceLocator
import com.hubspot.jinjava.loader.FileLocator
import com.hubspot.jinjava.JinjavaConfig

import org.json.JSONObject

import java.io.File

typealias VariableCallback = (String, Any?) -> Unit

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
@SuppressWarnings("ReturnCount", "TooManyFunctions")
object Dashboard {
    private var config: Config = Config()
    public var callbacks: HashMap<String, MutableList<VariableCallback>> = HashMap()
        private set
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

        // register mime types for javascript, so that it isn't application/octet-stream
        MimeType.register("jsx", "application/javascript")
        MimeType.register("mjs", "application/javascript")

        Spark.port(port)
        Spark.webSocket("/socket", SocketHandler::class.java)
        if (config.devMode) {
            val projectDir: String = System.getProperty("user.dir")
            val staticDir: String = "/src/main/resources/static"
            Spark.staticFiles.externalLocation(projectDir + staticDir)
        } else {
            Spark.staticFiles.location("/static")
        }

        Spark.get("/", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = config.getBaseAttributes()
            renderWithJinjava(attributes, "home.html")
        })

        Spark.get("/page/:name", {
            request: Request, response: Response ->
            val requestedPageName: String = request.params(":name")
            if (!config.hasPageWithName(requestedPageName)) {
                response.redirect("/")
            }
            val attributes: HashMap<String, Any> = config.getPageAttributes(requestedPageName)
            renderWithJinjava(attributes, "page.html")
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
            renderWithJinjava(attributes, "newpage.html")
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

    fun setVariable(key: String, value: Any) {
        variableUpdates.put(key, value)
    }

    fun <T> getVariable(key: String): T {
        if ((!variables.has(key)) && (!variableUpdates.has(key))) {
            throw DashboardException("The variable with name " + key + " was not found.")
        } else if (variableUpdates.has(key)) {
            return variableUpdates.get(key) as T
        } else {
            return variables.get(key) as T
        }
    }

    fun getInt(key: String): Int {
        val rawValue = getVariable<Any>(key)
        if (rawValue is Double) {
            return (rawValue as Double).toInt()
        } else if (rawValue is String) {
            return (rawValue as String).toInt()
        } else {
            return rawValue as Int
        }
    }

    fun getDouble(key: String): Double {
        val rawValue = getVariable<Any>(key)
        if (rawValue is Int) {
            return (rawValue as Int).toDouble()
        } else if (rawValue is String) {
            return (rawValue as String).toDouble()
        } else {
            return rawValue as Double
        }
    }

    fun getString(key: String): String {
        val rawValue = getVariable<Any>(key)
        if (rawValue is Int) {
            return (rawValue as Int).toString()
        } else if (rawValue is String) {
            return (rawValue as Double).toString()
        } else {
            return rawValue as String
        }
    }

    fun getBoolean(key: String): Boolean {
        val rawValue = getVariable<Any>(key)
        return rawValue as Boolean
    }

    fun addVarListener(key: String, callback: (String, Any?) -> Unit): Int {
        if (callbacks.contains(key)) {
            val tmp = callbacks.get(key)
            tmp!!.add(callback)
            return callbacks.put(key, tmp)!!.size
        } else {
            callbacks.put(key, mutableListOf(callback))
            return 0
        }
    }

    fun removeVarListener(key: String, callbackId: Int): Boolean {
        if (callbacks.contains(key)) {
            val tmp = callbacks.get(key)
            if (tmp!!.size > callbackId) {
                tmp!!.removeAt(callbackId)
                return true
            }
        }
        return false
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

    fun renderWithJinjava(attributes: HashMap<String, Any>, path: String): String {
        if (config.devMode) {
            val projectDir: String = System.getProperty("user.dir")
            val staticDir: String = "/src/main/resources/"
            return ModifiedJinjavaEngine(JinjavaConfig(), FileLocator(File(projectDir + staticDir))).render(
                ModelAndView(attributes, path)
            )
        } else {
            return ModifiedJinjavaEngine(JinjavaConfig(), ClasspathResourceLocator()).render(
                ModelAndView(attributes, path)
            )
        }
    }
}
