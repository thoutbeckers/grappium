package houtbecke.rs.grappium

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement

import java.lang.reflect.Type

import static groovy.transform.TypeCheckingMode.SKIP;

@CompileStatic
@TypeChecked
class AndroidHelper extends AbstractHelper {

    AndroidHelper(AppiumDriver driver, long timeoutInSeconds) {
        super(driver, timeoutInSeconds)
    }

    enum AndroidTextWidgets {
        TextView, CheckedTextView, Button, EditText
    }

    /**
     * Return a static text locator by xpath index *
     */
    public static By for_text(int xpathIndex) {
        return By.xpath("//android.widget.TextView[" + xpathIndex + "]");
    }

    /**
     * Return a static text locator that contains text *
     */
    @Override
    @TypeChecked(SKIP)
    By for_text(String text) {
        String query = '', sep = ''

        AndroidTextWidgets.values().each {
            query += sep + "//android.widget.${it.name()}[contains(@text, '$text')]";
            sep = '|'
        }

        By.xpath(query)
    }

    @Override
    By for_text_exact(String text) {
        return By.xpath("//android.widget.TextView[@text='" + text + "']");
    }

    @Override
    By for_find(String value) {
        return By.xpath(
        """
            //*[@content-desc="$value" or @resource-id="$value" or @text="$value"] |
            //*[contains(translate(@content-desc,"$value", "$value"), "$value")
                or contains(translate(@text,"$value","$value"), "$value") or @resource-id="$value"]
        """);
    }


    WebElement passwordField() {
        element(for_element_with_attribute_and_value("android.widget.EditText", "password", "true"))
    }

    // //android.widget.EditText[@password='true']
}