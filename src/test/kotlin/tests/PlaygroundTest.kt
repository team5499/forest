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
        Dashboard.setVariable("INTEG", 3)
        Dashboard.addVarListener("TEST", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("DEEK", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("INTEG", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        println("server started")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            // println(Dashboard.getVariable<String>("DEEK"))
            Thread.sleep(1000)
        }
    }
}
