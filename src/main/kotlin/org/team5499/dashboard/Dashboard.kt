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
            val attributes = HashMap<String, Any>()
            request.params(":name")
            // attributes.put("teamNumber", )

            // File(this::class.java.getResource("/page.html").path).bufferedReader().readText()
            JinjavaEngine().render(
                ModelAndView(attributes, this::class.java.getResource("/page.html").path)
            )
        })
        println("here")
    }
}
