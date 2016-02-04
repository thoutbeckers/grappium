package houtbecke.rs.grappium

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

interface Helper {
    void setWait(int seconds)
    /**
     * Return an element by locator *
     */
    WebElement element(By locator)
    /**
     * Return a list of elements by locator *
     */
    List<WebElement> elements(By locator)
    /**
     * Press the back button *
     */
    void back()
    /**
     * Return a list of elements by tag name *
     */
    List<WebElement> tags(String tagName)
    /**
     * Return a tag name locator *
     */
    By for_tags(String tagName)
    /**
     * Wait 30 seconds for locator to find an element *
     */
    WebElement wait(By locator)
    /**
     * Wait 60 seconds for locator to find all elements *
     */
    List<WebElement> waitAll(By locator)
    /**
     * Wait 60 seconds for locator to not find a visible element *
     */
    boolean waitInvisible(By locator)

    WebElement string(String value)
    /**
     * Return an element that contains name or text *
     */
    WebElement scroll_to(String value)
    /**
     * Return an element that exactly matches name or text *
     */
    WebElement scroll_to_exact(String value)
    /**
     * Return a static text element by xpath index *
     */
    WebElement s_text(int xpathIndex)
    /**
     * Return a static text element by exact text *
     */
    WebElement text_exact(String text)
    /**
     * Return a static text element that contains text *
     */
    WebElement text(String text)

    WebElement name(String text)

    WebElement passwordField()

    By for_text(int xpathIndex)

    By for_text(String text)

    By for_text_exact(String text)

    By for_find(String value)

    By for_element_with_attribute_and_value(String element, String attribute, String value)

}
