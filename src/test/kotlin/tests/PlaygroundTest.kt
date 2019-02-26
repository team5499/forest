package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import org.team5499.dashboard.Dashboard

@Tag("playground")
class PlaygroundTest {

    @Test
    @Suppress("LongMethod")
    fun runServer() {
        println("starting server...")
        println("go to http://localhost:5800/")
        Dashboard.start(this, "playgroundConf.json")
        Dashboard.setVariable("TEST", "testvalue")
        Dashboard.setVariable("DEEK", "anothervalue")
        Dashboard.setVariable("SWITCH", "thirdvalue")
        Dashboard.setVariable("INTEG", 3)
        Dashboard.setVariable("DOUBLE", 5.6)
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
        Dashboard.addVarListener("DOUBLE", {
            key: String, value: Any? ->
            println("$key : ${Dashboard.getDouble("DOUBLE")}")
        })

        Dashboard.setVariable("KP", 0.0)
        Dashboard.setVariable("KI", 0.0)
        Dashboard.setVariable("KD", 0.0)
        Dashboard.setVariable("KF", 0.0)
        Dashboard.addVarListener("KI", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("KD", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("KF", {
            key: String, value: Any? ->
            println("$key : $value")
        })

        Dashboard.setVariable("KP2", 0.1)
        Dashboard.setVariable("KI2", 0.1)
        Dashboard.setVariable("KD2", 0.1)
        Dashboard.setVariable("KF2", 0.1)
        Dashboard.addVarListener("KP2", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("KI2", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("KD2", {
            key: String, value: Any? ->
            println("$key : $value")
        })
        Dashboard.addVarListener("KF2", {
            key: String, value: Any? ->
            println("$key : $value")
        })

        println("server started")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            println(Dashboard.addVarListener("KP", {
                key: String, value: Any? ->
                println("$key : $value")
            }))
            println(Dashboard.addVarListener("KP", {
                key: String, value: Any? ->
                println("DEEK")
            }))
            // println(Dashboard.getVariable<String>("DEEK"))
            Thread.sleep(1000)
        }
    }
}
