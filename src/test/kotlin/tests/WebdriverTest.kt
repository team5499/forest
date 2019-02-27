package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.AfterAll

import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.By

import org.team5499.dashboard.Dashboard
import org.team5499.dashboard.Utils

import java.net.URL

class WebdriverTest {
    lateinit var driver: WebDriver
    lateinit var actions: Actions
    lateinit var addr: String

    companion object {
        lateinit var driver: WebDriver
        lateinit var actions: Actions
        lateinit var addr: String

        @BeforeAll
        @JvmStatic
        fun init() {
            Dashboard.start(this, "webdriverConfig.json")
            Dashboard.awaitInitialization()
            if (System.getenv("TRAVIS") == "true") {
                addr = "http://host.docker.internal:5800"

                val capabilities = DesiredCapabilities.chrome()
                driver = RemoteWebDriver(URL("$addr/wd/hub"), capabilities)
            } else {
                addr = "localhost:5800"
                if (Utils.isWindows()) {
                    System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\webdrivers\\windows\\chromedriver.exe")
                } else if (Utils.isMac()) {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/webdrivers/mac/chromedriver")
                } else {
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/webdrivers/linux/chromedriver")
                }
                driver = ChromeDriver()
            }
            actions = Actions(driver)
        }

        @AfterAll
        @JvmStatic
        fun uninit() {
            driver.quit()
            Dashboard.stop()
            Dashboard.awaitStop()
        }
    }

    @BeforeEach
    fun setup() {
        driver = WebdriverTest.driver
        actions = WebdriverTest.actions
        addr = WebdriverTest.addr
    }

    @Test
    fun navLinkTest() {
        driver.get("$addr")
        println(driver.title)
        val navlinks = driver.findElements(By.className("nav-link"))
        val navnames = mutableListOf<String>()
        navlinks.forEach({
            navnames.add(it.text)
        })
        navnames.forEach({
            println("clicking on $it")
            val element = driver.findElement(By.linkText("$it"))
            element.click()
            driver.navigate().back()
        })
        Thread.sleep(1000)
    }

    @Test
    fun stringWidgetTest() {
        Dashboard.setVariable("TEST", "testvalue")
        driver.get("$addr/page/widgettest")
        Thread.sleep(1000)
        val widgets = driver.findElements(By.className("card-body"))
        widgets.forEach({
            val input = it.findElement(By.className("form-control"))
            val submit = it.findElement(By.className("btn"))
            val inputText = input.getAttribute("value")
            println("$inputText")
        })
        println("Between")
        widgets.forEach({
            val input = it.findElement(By.className("form-control"))
            val submit = it.findElement(By.className("btn"))
            actions.doubleClick(input).perform()
            input.sendKeys("test")
            submit.click()
            Thread.sleep(30)
            val testvalue = Dashboard.getVariable<String>("TEST")
            println(testvalue)
            assert(testvalue == "test")
        })
    }

    @Test
    fun newPageTest() {
        driver.get("$addr")
        val newPageButton = driver.findElements(By.className("btn")).find({ it.text == "New Page" })
        newPageButton?.click()
        val pageName = driver.findElement(By.name("pagename"))
        val pageTitle = driver.findElement(By.name("pagetitle"))
        val submit = driver.findElement(By.className("btn"))
        pageName.sendKeys("newpage")
        pageTitle.sendKeys("New Page")
        submit.click()
        println(driver.title)
        assert(driver.title == "Dashboard - New Page")
    }

    @AfterEach
    fun cleanup() {
    }
}
