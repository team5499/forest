package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import org.json.JSONObject

import org.team5499.dashboard.Config

class ConfigTest {

    @Test
    fun noPages() {
        var fileConf: Config = Config(File(this::class.java.getClassLoader().getResource("noPages.json").path))
        println(fileConf.configObject.toString())
        assertTrue(fileConf.configObject.similar(JSONObject("{\"pages\":[]}")))
        var stringConf: Config = Config("{}")
        assertTrue(stringConf.configObject.similar(JSONObject("{\"pages\":[]}")))
    }
}
