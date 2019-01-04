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
class Shared {
    @Suppress("MagicNumber")
    fun runThread(s: String) {
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            println("failed")
        }
    }
}

class MyThread : Thread {
    var sr: Shared
    constructor(sr1: Shared) {
        sr = sr1
        start()
    }
    override fun run() {
        synchronized(sr) {
            sr.runThread(sr.currentThread().name)
        }
    }
}

object Dashboard {
    private var config: Config = Config()
    private val pageSource: String = this::class.java.getResource("/page.html").path
    val varsJSON: JSONObject = JSONObject()

    /**
	 * Start the dashboard server with the default port of 5800 and specified config file
	 *
	 * @param confPath the absolute path to the JSON configuration file
	 */
    fun start(confPath: String) {
        @Suppress("MagicNumber")
        start(confPath, "5800")
    }

    // init{
    //     var sr: Shared = Shared()
    //     var thread: MyThread = MyThread(sr)
    // }

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
            val attributes: HashMap<String, Any> = HashMap()
            attributes.put("navbar", config.getNavbarAttributes())
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

        // Utils
        Spark.get("/utils/newpage", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = HashMap()
            attributes.put("navbar", config.getNavbarAttributes())
            JinjavaEngine(JinjavaConfig(), ClasspathResourceLocator()).render(
                ModelAndView(attributes, "newpage.html")
            )
        })

        // Actions
        Spark.post("/actions/newpage", {
            request: Request, response: Response ->
            val pagename: String = request.queryParams("pagename")
            println("Attempting to create page with name: $pagename")
            if (config.hasPageWithName(pagename)) {
                response.header("newpageerror", "Page already exists!")
                response.redirect("/utils/newpage")
            } else {
                // make the new page and redirect to it
            }
            null
        })
    }
    @Suppress("ComplexCondition")
    fun editVarible(varName: String, value: Any) {
        if (varsJSON.has(varName)) {
            val oldValue: Any = varsJSON.get(varName)
            if (value::class == oldValue::class) {
                throw IllegalArgumentException("New varible is not the same type as original varible")
            }
            varsJSON.put(varName, value)
        } else {
            if (!(value is String || value is Int || value is Double || value is Float)) {
                throw IllegalArgumentException("varible is not valid type")
            }
            varsJSON.put(varName, value)
        }
    }
}
