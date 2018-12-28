package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine

// import org.json.json

/**
 * The main Dashboard object
 *
 * Handles starting the server
 */
object Dashboard {

    private var config: Config? = null
    private var pageSource: String = this::class.java.getResource("/page.html").path

    /**
     * Start the dashboard server with the default port of 5800 and specified config file
     *
     * @param confPath the absolute path to the JSON configuration file
     */
    fun start(confPath: String) {
        @Suppress("MagicNumber")
        start(confPath, 5800)
    }

    /**
     * Start the dashboard server with a custom port and specified config file
     *
     * Open ports on the FMS are 5800 - 5810
     *
     * @param confPath the absolute path to the JSON configuration file
     * @param port the port to host the dashboard on
     */
    fun start(confPath: String, port: Int) {
        config = Config(confPath)
        println(config)

        Spark.port(port)

        Spark.webSocket("/socket", SocketHandler::class.java)

        Spark.staticFiles.location("/static")

        Spark.get("/page/:name", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = config!!.getPageAttributes(request.params(":name"))
            JinjavaEngine().render(
                ModelAndView(attributes, pageSource)
            )
        })
    }
}
