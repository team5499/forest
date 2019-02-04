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
        Dashboard.setVariable("TEST", false)
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            println(Dashboard.getVariable<Boolean>("TEST"))
            Thread.sleep(1000)
        }
    }
}
