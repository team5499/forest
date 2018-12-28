package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.team5499.dashboard.Dashboard

@Tag("integration")
class ApplicationTest {

    @Test
    fun runServer() {
        println("starting server...")
        println("go to http://localhost:5800/")
        Dashboard.start(this::class.java.getClassLoader().getResource("integrationConf.json").path)
        println("server started")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            Thread.sleep(1000)
        }
    }
}
