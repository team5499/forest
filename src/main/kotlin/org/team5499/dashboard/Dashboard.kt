package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine

// import org.json.json

import java.io.File

object Dashboard {

    private var config: String = ""
    private var pageSource: String = this::class.java.getResource("/page.html").path

    fun start(confPath: String) {
        @Suppress("MagicNumber")
        start(confPath, 5800)
    }

    fun start(confPath: String, port: Int) {
        config = File(confPath).bufferedReader().readText()
        println(config)

        Spark.port(port)

        Spark.webSocket("/socket", SocketHandler::class.java)

        Spark.staticFiles.location("/static")

        Spark.get("/page/:name", {
            request: Request, response: Response ->
            val attributes: HashMap<String, Any> = getPageAttributes(request.params(":name"))
            JinjavaEngine().render(
                ModelAndView(attributes, pageSource)
            )
        })
        println("here")
    }
}
