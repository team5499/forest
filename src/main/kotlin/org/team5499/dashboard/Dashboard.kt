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
import java.util.UUID
import java.io.File

typealias VariableCallback<reified T> = (String, T?) -> Unit

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
@SuppressWarnings("ReturnCount", "TooManyFunctions", "LargeClass")
object Dashboard {
    private var config: Config = Config()
    public val concurrentCallbacks: ConcurrentHashMap<String, ConcurrentHashMap<Long, () -> Unit>> = ConcurrentHashMap()
    public var inlineCallbacks: List<String> = listOf()
    public var inlineCallbackUpdates: MutableList<String> = mutableListOf()
    @Suppress("MaxLineLength")
    public var inlineCallbackLambdas: ConcurrentHashMap<String, ConcurrentHashMap<Long, () -> Unit>> = ConcurrentHashMap()
    public var inlineLock = ReentrantLock()
    public var variableLock = ReentrantLock()
    public var concurrentCallbackLock = ReentrantLock()
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
            _: Request, _: Response ->
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
            _: Request, response: Response ->
            response.type("text/json")
            @Suppress("MagicNumber")
            config.configObject.toString(4)
        })

        Spark.post("/config", {
            request: Request, _: Response ->
            config.setConfigJSON(request.body())
        })

        // Utils
        Spark.get("/utils/newpage", {
            request: Request, _: Response ->
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
                @Suppress("UNCHECKED_CAST")
                return variableUpdates.get(key) as T
            } else {
                @Suppress("UNCHECKED_CAST")
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
            return rawValue.toInt()
        } else if (rawValue is String) {
            return rawValue.toInt()
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
            return rawValue.toDouble()
        } else if (rawValue is String) {
            return rawValue.toDouble()
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
            return rawValue.toString()
        } else if (rawValue is String) {
            return rawValue.toString()
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
     * NOTE: Renamed to [addConcurrentListener]
     * Add a lambda function to be called when the specified variable's value changes.
     * The lambda is called from a separate thread, so it should be thread-safe.
     * If the robot program updates the variable, the lambda is not called.
     * The lambda is only called if the frontend changes the value
     *
     * @param key The name of the variable to listen to
     * @param callback The lambda to call when the specified variable is updated
     * @return The ID of the listener, which can be used later to remove the listener (See [removeVarListener])
     */
    @Suppress("ComplexMethod")
    @Deprecated("Renamed")
    inline fun <reified T> addVarListener(key: String, crossinline callback: VariableCallback<T>): Long {
        return addConcurrentListener(key, callback)
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
    @Suppress("ComplexMethod")
    inline fun <reified T> addConcurrentListener(key: String, crossinline callback: VariableCallback<T>): Long {
        var type = ""

        if (T::class == Int::class) {
            type = "Int"
        } else if (T::class == Double::class) {
            type = "Double"
        } else if (T::class == Boolean::class) {
            type = "Boolean"
        } else if (T::class == String::class) {
            type = "String"
        }

        val wrappedCallback = {
            if (type == "Int") {
                callback(key, getInt(key) as T)
            } else if (type == "Double") {
                callback(key, getDouble(key) as T)
            } else if (type == "Boolean") {
                callback(key, getBoolean(key) as T)
            } else if (type == "String") {
                callback(key, getString(key) as T)
            } else {
                callback(key, getVariable<T>(key))
            }
        }

        concurrentCallbackLock.lock()
        val uuid = UUID.randomUUID().leastSignificantBits
        try {
            if (concurrentCallbacks.containsKey(key)) {
                val tmp = concurrentCallbacks.get(key)
                tmp!!.put(uuid, wrappedCallback)
                concurrentCallbacks.put(key, tmp)!!
            } else {
                val tmp = ConcurrentHashMap<Long, () -> Unit>()
                tmp.put(uuid, wrappedCallback)
                concurrentCallbacks.put(key, tmp)
            }
        } finally {
            concurrentCallbackLock.unlock()
            return uuid
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
    @Suppress("ComplexMethod")
    inline fun <reified T> runIfUpdate(key: String, crossinline callback: VariableCallback<T>): Boolean {
        if (inlineCallbacks.contains(key)) {
            if (T::class == Int::class) {
                callback(key, getInt(key) as T)
            } else if (T::class == Double::class) {
                callback(key, getDouble(key) as T)
            } else if (T::class == Boolean::class) {
                callback(key, getBoolean(key) as T)
            } else if (T::class == String::class) {
                callback(key, getString(key) as T)
            } else {
                callback(key, getVariable<T>(key))
            }
            return true
        } else {
            return false
        }
    }

    /**
     * Add a lambda to run if the specified variable has been updated. Gets called when the update function is called
     *
     * @param key The variable to listen for
     * @param callback The lambda to call if the variable is updated
     */
    @Suppress("ComplexMethod")
    inline fun <reified T> addInlineListener(key: String, crossinline callback: VariableCallback<T>): Long {
        var tmpList = ConcurrentHashMap<Long, () -> Unit>()
        if (inlineCallbackLambdas.containsKey(key)) {
            tmpList = inlineCallbackLambdas.get(key)!!
        }

        var type = ""
        if (T::class == Int::class) {
            type = "Int"
        } else if (T::class == Double::class) {
            type = "Double"
        } else if (T::class == Boolean::class) {
            type = "Boolean"
        } else if (T::class == String::class) {
            type = "String"
        }

        val wrappedCallback = {
            if (type == "Int") {
                callback(key, getInt(key) as T)
            } else if (type == "Double") {
                callback(key, getDouble(key) as T)
            } else if (type == "Boolean") {
                callback(key, getBoolean(key) as T)
            } else if (type == "String") {
                callback(key, getString(key) as T)
            } else {
                callback(key, getVariable<T>(key))
            }
        }

        val uuid = UUID.randomUUID().leastSignificantBits
        tmpList.put(uuid, wrappedCallback)
        inlineCallbackLambdas.put(key, tmpList)
        return uuid
    }

    /**
     * Remove an inline listener with the specified key and ID
     *
     * @param key The variable that the lambda is attached to
     * @param id The callback id returned by [addInlineListener]
     */
    fun removeInlineListener(key: String, id: Long): Boolean {
        if (inlineCallbackLambdas.containsKey(key)) {
            val tmpList = inlineCallbackLambdas.get(key)!!
            tmpList.remove(id)
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
                tmpList.values.forEach({
                    it()
                })
            }
        })
    }

    /**
     * NOTE: Renamed to [removeConcurrentListener]
     * Remove a lambda from the list of lambdas that listen for variable changes.
     *
     * @param key The key of the variable that the lambda is listening to
     * @param callbackId The Int returned by the [addVarListener] function
     *
     * @return Whether the lambda was successfully removed
     */
    @Deprecated("Renamed")
    fun removeVarListener(key: String, callbackId: Long): Boolean {
        return removeConcurrentListener(key, callbackId)
    }

    /**
     * Remove a lambda from the list of lambdas that listen for variable changes.
     *
     * @param key The key of the variable that the lambda is listening to
     * @param callbackId The Int returned by the [addVarListener] function
     *
     * @return Whether the lambda was successfully removed
     */
    @Suppress("TooGenericExceptionCaught")
    fun removeConcurrentListener(key: String, callbackId: Long): Boolean {
        concurrentCallbackLock.lock()
        try {
            if (concurrentCallbacks.containsKey(key)) {
                val tmp = concurrentCallbacks.get(key)
                if (tmp!!.containsKey(callbackId)) {
                    tmp.remove(callbackId)
                    concurrentCallbacks.put(key, tmp)
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        } finally {
            concurrentCallbackLock.unlock()
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
