package tests

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import org.team5499.dashboard.Utils

import java.net.URL

object WebdriverSetup {
    fun getDriver(): WebDriver {
        if (System.getenv("TRAVIS") == "true") {
            val capabilities = DesiredCapabilities.chrome()
            return RemoteWebDriver(URL("http://127.0.0.1:4444/wd/hub"), capabilities)
        } else {
            if (Utils.isWindows()) {
                System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\webdrivers\\windows\\chromedriver.exe")
            } else if (Utils.isMac()) {
                System.setProperty("webdriver.chrome.driver", "src/test/resources/webdrivers/mac/chromedriver")
            } else {
                System.setProperty("webdriver.chrome.driver", "src/test/resources/webdrivers/linux/chromedriver")
            }
            return ChromeDriver()
        }
    }
}
