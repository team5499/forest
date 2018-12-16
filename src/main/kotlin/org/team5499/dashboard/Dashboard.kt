package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.jinjava.JinjavaEngine

import java.io.File

object Dashboard {

    private var config: String = ""

    fun start(confPath: String) {
        @Suppress("MagicNumber")
        println("deek")
        //start(confPath, 5800)
    }

    fun start(confPath: String, port: Int) {
        config = File(confPath).bufferedReader().readText()

        Spark.port(port)

        Spark.staticFiles.location("/static")

        Spark.get("/page/:name", {
            request: Request, response: Response ->
            val attributes = HashMap<String, Any>()
            request.params(":name")

            // File(this::class.java.getResource("/page.html").path).bufferedReader().readText()
            JinjavaEngine().render(
                ModelAndView(attributes, this::class.java.getResource("/page.html").path)
            )
        })

        Spark.webSocket("/socket", SocketHandler::class)
    }
}
