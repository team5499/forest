package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.json.JSONObject

import org.team5499.dashboard.Config
import org.team5499.dashboard.Utils

class ConfigTest {

    @Test
    fun noPages() {
        var fileConf: Config = Config(Utils.readResourceAsString(this, "noPages.json"))
        println(fileConf.configObject.toString())
        assertTrue(fileConf.configObject.similar(JSONObject("{\"pages\":[]}")))
        var stringConf: Config = Config("{}")
        assertTrue(stringConf.configObject.similar(JSONObject("{\"pages\":[]}")))
    }
}
