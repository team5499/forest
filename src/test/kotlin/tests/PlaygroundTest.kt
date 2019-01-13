package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.team5499.dashboard.Dashboard

@Tag("playground")
class PlaygroundTest {

    @Test
    fun runServer() {
        println("starting server...")
        println("go to http://localhost:5800/")
        Dashboard.start(this, "playgroundConf.json")
        println("server started")
        Dashboard.setVariable("TEST", "test value here")
        Dashboard.setVariable("DEEK", "deek value here")
        Thread.sleep(30000)
        println("setting new variables")
        Dashboard.setVariable("TEST", "new test value here")
        Dashboard.setVariable("DEEK", "new deek value here")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            Thread.sleep(1000)
        }
    }
}
