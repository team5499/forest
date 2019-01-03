package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine

import com.hubspot.jinjava.loader.ClasspathResourceLocator
import com.hubspot.jinjava.JinjavaConfig

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
object Dashboard {

    private var config: Config = Config()
    private val pageSource: String = Utils.readResourceAsString(this, "page.html")

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
    }
}
