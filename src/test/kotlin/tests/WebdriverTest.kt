package tests

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.RepetitionInfo
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.AfterAll

import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.By

import org.team5499.dashboard.Dashboard

@Tag("webdriver")
class WebdriverTest {
    lateinit var driver: WebDriver
    lateinit var actions: Actions

    companion object {
        lateinit var driver: WebDriver
        lateinit var actions: Actions

        @BeforeAll
        @JvmStatic
        fun init() {
            Dashboard.start(this, "webdriverConfig.json")
            driver = WebdriverSetup.getDriver()
            actions = Actions(driver)
        }

        @AfterAll
        @JvmStatic
        fun uninit() {
            driver.quit()
            Dashboard.stop()
        }
    }

    @BeforeEach
    fun setup() {
        driver = WebdriverTest.driver
        actions = WebdriverTest.actions
    }

    @Test
    fun navLinkTest() {
        driver.get("localhost:5800")
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
        driver.get("localhost:5800/page/widgettest")
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
        driver.get("localhost:5800")
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

    @RepeatedTest(10)
    fun inlineCallbackTest(repInfo: RepetitionInfo) {
        if (repInfo.currentRepetition == 1) {
            // First repetition - setup
            driver.get("localhost:5800/page/widgettest")
            Thread.sleep(1000)
        }
        Dashboard.setVariable("TEST", "testbefore${repInfo.currentRepetition}")
        Thread.sleep(50)
        val widgets = driver.findElements(By.className("card-body"))
        val input = widgets.get(0).findElement(By.className("form-control"))
        val submit = widgets.get(0).findElement(By.className("btn"))
        var callCount = 0
        val pollThread = Thread({
            Thread.sleep(50)
            actions.doubleClick(input).perform()
            input.sendKeys("testafter${repInfo.currentRepetition}")
            submit.click()
        })
        pollThread.start()
        val startTime = System.currentTimeMillis()
        while (true) {
            Dashboard.update()
            Dashboard.runIfUpdate("TEST", {
                key: String, value: Any? ->
                assert(value == "testafter${repInfo.currentRepetition}")
                callCount++
            })
            if (System.currentTimeMillis() - startTime > 1000) {
                break
            }
        }
        pollThread.stop()
        println(callCount)
        assert(callCount == 1)
    }

    @RepeatedTest(10)
    fun onetimeInlineCallbackTest(repInfo: RepetitionInfo) {
        if (repInfo.currentRepetition == 1) {
            // First repetition - setup
            driver.get("localhost:5800/page/widgettest")
            Thread.sleep(1000)
        }
        Dashboard.setVariable("TEST", "onetimebefore${repInfo.currentRepetition}")
        Thread.sleep(50)
        val widgets = driver.findElements(By.className("card-body"))
        val input = widgets.get(0).findElement(By.className("form-control"))
        val submit = widgets.get(0).findElement(By.className("btn"))
        var callCount = 0
        val pollThread = Thread({
            actions.doubleClick(input).perform()
            input.sendKeys("onetimeafter${repInfo.currentRepetition}")
            submit.click()
        })
        pollThread.start()
        val callbackId = Dashboard.addInlineListener("TEST") {
            key: String, value: Any? ->
            println(value)
            assert(value == "onetimeafter${repInfo.currentRepetition}")
            callCount++
        }
        val startTime = System.currentTimeMillis()
        while (true) {
            Dashboard.update()
            if (System.currentTimeMillis() - startTime > 1000) {
                break
            }
        }
        pollThread.interrupt()
        println(callCount)
        assert(callCount == 1)
        Dashboard.removeInlineListener("TEST", callbackId)
    }

    @AfterEach
    fun cleanup() {
    }
}
