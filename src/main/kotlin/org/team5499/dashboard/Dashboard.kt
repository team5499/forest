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

import java.util.concurrent.ConcurrentHashMap

import java.util.concurrent.locks.ReentrantLock

import java.io.File

typealias VariableCallback = (String, Any?) -> Unit

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
@SuppressWarnings("ReturnCount", "TooManyFunctions", "LargeClass")
object Dashboard {
    private var config: Config = Config()
    public val concurrentCallbacks: ConcurrentHashMap<String, MutableList<VariableCallback>> = ConcurrentHashMap()
    public var inlineCallbacks: List<String> = listOf()
    public var inlineCallbackUpdates: MutableList<String> = mutableListOf()
    public var inlineCallbackLambdas: ConcurrentHashMap<String, MutableList<VariableCallback>> = ConcurrentHashMap()
    public var inlineLock = ReentrantLock()
    public var variableLock = ReentrantLock()
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
     * Start the dashboard server with a custom port and specified config file, and wait for it to finish initializing
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
            if (config.hasPageWithName(pagename)) {
                response.redirect("/utils/newpage?pageexists=true")
            } else {
                // make the new page and redirect to it
                config.addPage(pagename, pagetitle)
                response.redirect("/page/$pagename")
            }
            null
        })

        // Spark.awaitInitialization()
        SocketHandler.startBroadcastThread() // start broadcasting data
    }

    /**
     * Stop the dashboard and wait for it to shutdown
     */
    fun stop() {
        SocketHandler.stopBroadcastThread()
        SocketHandler.awaitStop()
        Spark.stop()
        Spark.awaitStop()
    }

    /**
     * Set the value of a variable in the dashboard
     *
     * @param key The name of the variable
     * @param value The new value for the variable
     */
    fun setVariable(key: String, value: Any) {
        variableLock.lock()
        try {
            variableUpdates.put(key, value)
        } finally {
            variableLock.unlock()
        }
    }

    /**
     * Get the value of the specified variable
     *
     * @param key The name of the variable to get
     * @return The value of the specified variable
     */
    fun <T> getVariable(key: String): T {
        variableLock.lock()
        try {
            if ((!variables.has(key)) && (!variableUpdates.has(key))) {
                throw DashboardException("The variable with name " + key + " was not found.")
            } else if (variableUpdates.has(key)) {
                return variableUpdates.get(key) as T
            } else {
                return variables.get(key) as T
            }
        } finally {
            variableLock.unlock()
        }
    }

    /**
     * Get the value of the specified variable as an Integer
     *
     * @param key The name of the requested variable
     * @return The value of the desired variable
     */
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

    /**
     * Get the value of the specified variable as a Double
     *
     * @param key The name of the requested variable
     * @return The value of the desired variable
     */
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

    /**
     * Get the value of the specified variable as a String
     *
     * @param key The name of the requested variable
     * @return The value of the desired variable
     */
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

    /**
     * Get the value of the specified variable as a Boolean
     *
     * @param key The name of the requested variable
     * @return The value of the desired variable
     */
    fun getBoolean(key: String): Boolean {
        val rawValue = getVariable<Any>(key)
        return rawValue as Boolean
    }

    /**
     * Add a lambda function to be called when the specified variable's value changes.
     * The lambda is called from a separate thread, so it should be thread-safe.
     * If the robot program updates the variable, the lambda is not called.
     * The lambda is only called if the frontend changes the value
     *
     * @param key The name of the variable to listen to
     * @param callback The lambda to call when the specified variable is updated
     * @return The ID of the listener, which can be used later to remove the listener (See [removeVarListener])
     */
    fun addVarListener(key: String, callback: VariableCallback): Int {
        if (concurrentCallbacks.containsKey(key)) {
            if (!concurrentCallbacks.get(key)!!.contains(callback)) {
                val tmp = concurrentCallbacks.get(key)
                tmp!!.add(callback)
                return concurrentCallbacks.put(key, tmp)!!.size - 1
            } else {
                return concurrentCallbacks.get(key)!!.indexOf(callback)
            }
        } else {
            concurrentCallbacks.put(key, mutableListOf(callback))
            return 0
        }
    }

    /**
     * Only run the lambda if the specified key has been updated since the last time [update] was called.
     * Call this function continuously inside the same loop as [update].
     *
     * @param key The key of the variable to listen for
     * @param callback The lambda to call if that variable has been updated
     *
     * @return Whether the lambda was called or not
     */
    fun runIfUpdate(key: String, callback: VariableCallback): Boolean {
        var shouldUpdate = false
        if (inlineCallbacks.contains(key)) {
            shouldUpdate = true
        }

        if (shouldUpdate) {
            callback(key, getVariable(key))
        }

        return shouldUpdate
    }

    /**
     * Add a lambda to run if the specified variable has been updated. Gets called when the update function is called
     *
     * @param key The variable to listen for
     * @param callback The lambda to call if the variable is updated
     */
    fun addInlineListener(key: String, callback: VariableCallback): Int {
        var tmpList = mutableListOf<VariableCallback>()
        if (inlineCallbackLambdas.containsKey(key)) {
            tmpList = inlineCallbackLambdas.get(key)!!
        }
        tmpList.add(callback)
        inlineCallbackLambdas.put(key, tmpList)
        return tmpList.size - 1
    }

    /**
     * Remove an inline listener with the specified key and ID
     *
     * @param key The variable that the lambda is attached to
     * @param id The callback id returned by [addInlineListener]
     */
    fun removeInlineListener(key: String, id: Int): Boolean {
        if (inlineCallbackLambdas.containsKey(key)) {
            val tmpList = inlineCallbackLambdas.get(key)!!
            tmpList.removeAt(id)
            inlineCallbackLambdas.put(key, tmpList)
            return true
        }
        return false
    }

    /**
     * Should be called if [inline callbacks][runIfUpdate] are used.
     * Put this function at the beginning of `robotPeriodic`.
     */
    fun update() {
        inlineLock.lock()
        try {
            inlineCallbacks = inlineCallbackUpdates.toList()
            inlineCallbackUpdates = mutableListOf<String>()
        } finally {
            inlineLock.unlock()
        }
        inlineCallbacks.forEach({
            if (inlineCallbackLambdas.containsKey(it)) {
                val tmpList = inlineCallbackLambdas.get(it)!!
                val key = it
                tmpList.forEach({
                    it(key, Dashboard.getVariable(key))
                })
            }
        })
    }

    /**
     * Remove a lambda from the list of lambdas that listen for variable changes.
     *
     * @param key The key of the variable that the lambda is listening to
     * @param callbackId The Int returned by the [addVarListener] function
     *
     * @return Whether the lambda was successfully removed
     */
    fun removeVarListener(key: String, callbackId: Int): Boolean {
        if (concurrentCallbacks.contains(key)) {
            val tmp = concurrentCallbacks.get(key)
            if (tmp!!.size > callbackId) {
                tmp.removeAt(callbackId)
                return true
            }
        }
        return false
    }

    fun mergeVariableUpdates() {
        variableLock.lock()
        try {
            for (u in variableUpdates.keys()) {
                variables.put(u, variableUpdates.get(u))
            }
            variableUpdates = JSONObject()
        } finally {
            variableLock.unlock()
        }
    }

    fun mergeVariableUpdates(json: JSONObject) {
        variableLock.lock()
        try {
            for (u in json.keys()) {
                variables.put(u, json.get(u))
            }
        } finally {
            variableLock.unlock()
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
