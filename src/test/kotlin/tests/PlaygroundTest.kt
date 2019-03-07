package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag

import javax.imageio.ImageIO

import org.team5499.dashboard.Dashboard
import org.team5499.dashboard.StringChooser

@Tag("playground")
class PlaygroundTest {

    @Test
    @Suppress("LongMethod")
    fun runServer() {
        println("starting server...")
        val img = ImageIO.read(this::class.java.getResource("/Calibration.001.jpeg"))
        val server = ImageServer(img)
        val imgThread = Thread(server)
        imgThread.start()
        println("go to http://localhost:5800/")
        Dashboard.start(this, "playgroundConf.json")
        Dashboard.setVariable("TEST", "testvalue")
        Dashboard.setVariable("DEEK", "anothervalue")
        Dashboard.setVariable("SWITCH", "thirdvalue")
        val autoSelector = StringChooser("AUTO_MODE", "First Option", "First Option",
                                                                        "Second Option",
                                                                        "Third Option")
        autoSelector.addConcurrentListener({
            key: String, value: String? ->
            println("$key : $value")
        })
        autoSelector.addInlineListener({
            _: String, value: String? ->
            println("onetime inline : $value")
        })
        Dashboard.setVariable("INTEG", 3)
        Dashboard.setVariable("DOUBLE", 5.6)
        Dashboard.addConcurrentListener("TEST", {
            key: String, value: String? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("DEEK", {
            key: String, value: String? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("INTEG", {
            key: String, value: Int? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("DOUBLE", {
            key: String, value: Double? ->
            println("$key : ${Dashboard.getDouble("DOUBLE")} : $value")
        })

        Dashboard.setVariable("KP", 0.0)
        Dashboard.setVariable("KI", 0.0)
        Dashboard.setVariable("KD", 0.0)
        Dashboard.setVariable("KF", 0.0)
        Dashboard.addConcurrentListener("KP", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KI", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KD", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KF", {
            key: String, value: Double? ->
            println("$key : $value")
        })

        Dashboard.setVariable("KP2", 0.1)
        Dashboard.setVariable("KI2", 0.1)
        Dashboard.setVariable("KD2", 0.1)
        Dashboard.setVariable("KF2", 0.1)
        Dashboard.addConcurrentListener("KP2", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KI2", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KD2", {
            key: String, value: Double? ->
            println("$key : $value")
        })
        Dashboard.addConcurrentListener("KF2", {
            key: String, value: Double? ->
            println("$key : $value")
        })

        println("server started")
        while (true) { // find a better way to wait?
            @Suppress("MagicNumber")
            Dashboard.update()
            autoSelector.runIfUpdate() {
                _: String, value: String? ->
                println("inline repeated: $value")
            }
            // println(Dashboard.getVariable<String>("DEEK"))
            // println(autoSelector.selected)
            Thread.sleep(1000)
        }
    }
}
