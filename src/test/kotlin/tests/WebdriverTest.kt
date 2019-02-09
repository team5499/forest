package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

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
        driver.get("https://google.com")
        val element = driver.findElement(By.name("q"))
        element.sendKeys("Cheese!")
        element.submit()
        println("Page title is: " + driver.getTitle())
        (WebDriverWait(driver, 10)).until(TitleExpectedCondition())
        driver.quit()
    }

    class TitleExpectedCondition : ExpectedCondition<Boolean> {
        override fun apply(d: WebDriver?): Boolean? {
            return d?.getTitle()?.toLowerCase()?.startsWith("cheese!")
        }
    }
}
