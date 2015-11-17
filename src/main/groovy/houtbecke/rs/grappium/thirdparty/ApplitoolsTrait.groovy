package houtbecke.rs.grappium.thirdparty

import com.applitools.eyes.Eyes
import groovy.transform.SelfType
import houtbecke.rs.grappium.AppiumTest
import houtbecke.rs.grappium.Platforms
import org.junit.After
import org.junit.Before
import org.openqa.selenium.WebDriver

@SelfType(AppiumTest)
trait ApplitoolsTrait implements Platforms {


    Eyes eyes;
    WebDriver eyesDriver;

    @Before
    void setUpApplitools() {

        ifdefApplitools {

            eyes = new Eyes()
            eyes.setApiKey(definedValue("applitoolsKey"))

            eyes.open(driver, appNameUsed, simpleName)
        }
    }

    @After
    void tearDownApplitools() {
        ifdefApplitools {
            eyes?.abortIfNotClosed();
            eyesDriver?.quit()
        }
    }


}