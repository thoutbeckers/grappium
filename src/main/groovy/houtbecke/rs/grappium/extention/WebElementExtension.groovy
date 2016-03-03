package houtbecke.rs.grappium.extention

import io.appium.java_client.android.AndroidElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement

class WebElementExtension {

    static WebElement followingSibling(final WebElement webElement) {
        following(webElement)
    }

    private static WebElement following(WebElement webElement) {

        if (webElement.xpath && webElement instanceof RemoteWebElement) {
            WebDriver driver = ((RemoteWebElement)webElement).getWrappedDriver()
            return driver.findElement(By.xpath("($webElement.xpath)/following-sibling::*"))
        }
        throw RuntimeException("Trying to use xpath from an element not found with XPath")
    }

}
