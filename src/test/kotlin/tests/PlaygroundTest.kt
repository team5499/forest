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
        Dashboard.setVariable("TEST", "testvalue")
        Dashboard.setVariable("DEEK", "anothervalue")
        println("server started")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            Thread.sleep(1000)
        }
    }
}
