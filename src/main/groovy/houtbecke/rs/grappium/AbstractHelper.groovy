package houtbecke.rs.grappium

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

import java.util.concurrent.TimeUnit

@CompileStatic
@TypeChecked
abstract class AbstractHelper implements Helper {

    final AppiumDriver driver
    final WebDriverWait driverWait

    AbstractHelper(AppiumDriver driver, long timeoutInSeconds) {
        this.driver = driver
        driverWait = new WebDriverWait(driver, timeoutInSeconds)
    }

    @Override
    void setWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS)
    }

    /**
     * Return an element by locator *
     */
    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    WebElement element(By locator) {
        WebElement we = driver.findElement(locator)
        if (locator instanceof By.ByXPath)
            we.metaClass.xpath = ((By.ByXPath)locator).xpathExpression
        we
    }

    /**
     * Return a list of elements by locator *
     */
    @Override
    List<WebElement> elements(By locator) {
        driver.findElements(locator)
    }

    /**
     * Press the back button *
     */
    @Override
    void back() {
        driver.navigate().back();
    }

    /**
     * Return a list of elements by tag name *
     */
    @Override
    List<WebElement> tags(String tagName) {
        elements(for_tags(tagName));
    }

    /**
     * Return a tag name locator *
     */
    @Override
    By for_tags(String tagName) {
        By.className(tagName);
    }

    /**
     * Wait 30 seconds for locator to find an element *
     */
    @Override
    WebElement wait(By locator) {
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(locator))
    }

    /**
     * Wait 60 seconds for locator to find all elements *
     */
    @Override
    List<WebElement> waitAll(By locator) {
        driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator))
    }

    /**
     * Wait 60 seconds for locator to not find a visible element *
     */
    @Override
    boolean waitInvisible(By locator) {
        driverWait.until(ExpectedConditions.invisibilityOfElementLocated(locator))
    }


    WebElement contains(String value) {
        element(for_find(value));
    }

    /**
     * Return an element that contains name or text *
     */
    @Override
    WebElement scroll_to(String value) {
        driver.scrollTo(value);
    }

    /**
     * Return an element that exactly matches name or text *
     */
    @Override
    WebElement scroll_to_exact(String value) {
        driver.scrollToExact(value);
    }

    /**
     * Return a static text element by xpath index *
     */
    @Override
    WebElement s_text(int xpathIndex) {
        element(for_text(xpathIndex));
    }

    /**
     * Return a static text element by exact text *
     */
    @Override
    WebElement text_exact(String text) {
        element(for_text_exact(text));
    }

    /**
     * Return a static text element that contains text *
     */
    @Override
    WebElement text(String text) {
        element(for_text(text))
    }

    /**
     * Return a static text element that contains text *
     */
    @Override
    WebElement name(String text) {
        element(By.name(text))
    }

    @Override
    By for_element_with_attribute_and_value(String element, String attribute, String value) {
        return By.xpath(
                "//$element[@$attribute='$value']"
        )
    }

    @Override
    void tap(Map args) {

        // help IDEs with named parameters
        args.tapCount
        args.touchCount
        args.duration
        args.x
        args.y

        ((JavascriptExecutor)driver).executeScript("mobile: tap", args)
    }

}