package tests

import org.junit.jupiter.api.Test

import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

class WebdriverTest {

    var caps: DesiredCapabilities
    var driver: WebDriver

    init {
        caps = DesiredCapabilities()
        caps.setJavascriptEnabled(true)
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "src/test/resources/phantomjs")
        driver = PhantomJSDriver(caps)
    }

    @Test
    fun webTest() {
        driver.get("https://example.com")
    }
}
