package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

class WebdriverTest {

    lateinit var driver: WebDriver
        private set

    @BeforeEach
    fun setup() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver")
        driver = ChromeDriver()
    }

    @Test
    fun webTest() {
        driver.get("https://example.com")
        driver.quit()
    }
}
