package org.team5499.dashboard

import spark.Spark

object Dashboard {
    fun start() {
        @Suppress("MagicNumber")
        start(5800)
    }

    fun start(port: Int) {
        Spark.port(port)
    }
}
