package org.team5499.dashboard

import spark.Spark
import spark.Request
import spark.Response
import spark.ModelAndView
import spark.template.pebble.PebbleTemplateEngine

object Dashboard {
    fun start() {
        @Suppress("MagicNumber")
        start(5800)
    }

    fun start(port: Int) {
        Spark.port(port)

        Spark.get("/hello", {
            request: Request, response: Response ->
            val attributes = HashMap<String, Any>()
            attributes.put("message", "Hello World!")

            // The hello.pebble file is located in directory:
            // src/test/resources/spark/template/pebble
            println("here")
            ModelAndView(attributes, this::class.java.getResource("/spark/template/pebble/hello.pebble").path)
        }, PebbleTemplateEngine())

        @Suppress("MagicNumber")
        Thread.sleep(10000000) // figure out a better way to do this
    }
}
