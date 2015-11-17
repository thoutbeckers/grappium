package houtbecke.rs.grappium

import houtbecke.rs.grappium.thirdparty.SauceTrait
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import org.openqa.selenium.remote.DesiredCapabilities

import java.util.concurrent.TimeUnit;

public class AppiumTest implements Platforms, SauceTrait {

    AbstractHelper helper;
    AppiumDriver driver;

    @Rule public TestName testName = new TestName()

    static {
        // Disable annoying cookie warnings.
        // WARNING: Invalid cookie header
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    /** Keep the same date prefix to identify job sets. **/
    private static Date jobDate = new Date();


    File getAppPathUsed() {
        String userDir = System.getProperty("user.dir");
        String path = getClass().getAnnotation(AppPath)?.value()

        ifdefAppPath {
            path = it
        }

        if (!path) throw new MissingPropertyException("appPath is not set", "appPath", null)

        path.startsWith("/") ? new File(path) : new File(System.getProperty("user.dir"), path)
    }

    String getAppNameUsed() {
        String path = appPathUsed.toString()
        return path.substring(path.lastIndexOf('/')+1)
    }

    DesiredCapabilities capabilities = new DesiredCapabilities()


    TestCapabilities getTestCapabilitiesForClass() {
        this.getClass().getAnnotation(TestCapabilities.class)
    }

    TestCapabilities getTestCapabilitiesForMethod() {
        this.getClass().getMethod(testName.methodName)?.getAnnotation(TestCapabilities.class)
    }

    def static getValueFromTestCapabilities(TestCapabilities capabilities, String key) {
        capabilities && capabilities.respondsTo(key) ? capabilities.class.getMethod(key).invoke(capabilities) : ""
    }

    def getTestCapabilitiesValueFromAnnotations(String key, boolean useDefaultIfNotFound=false) {
        // see if an annotation on the method defines this
        def val = getValueFromTestCapabilities(testCapabilitiesForMethod, key)
        // if it's a value or a Boolean return it, so we return a defined false
        if (val || val instanceof Boolean) return val

        // do the same, but on the class level
        val = getValueFromTestCapabilities(testCapabilitiesForClass, key)
        if (val || val instanceof Boolean) return val

        // if we have a value, or if we don't fall back to default in the annotation, return what we have
        if (!useDefaultIfNotFound) return val;

        TestCapabilities.getMethod(key)?.defaultValue
    }


    /** Run before each test **/
    @Before
    void setUpAppium() throws Exception {

        capabilities.metaClass.setCapabilityIfNotSet << { String key, fallbackValue='' ->

            key = key.replaceAll('-', '_')

            // There are very few keys (eg appium-version that are not lowerCamel for some reason)
            String keyHyphen = key.replaceAll('_', '-')

            // Only set capabilities that were not set already
            if (capabilities.asMap().containsKey(keyHyphen) && capabilities.asMap()."$keyHyphen")
                return;


            // look if this value is defined from an environment variable or property
            def val = definedValueSafe(key)

            // else look if we have an annotation on the method or class
            val = val?: getTestCapabilitiesValueFromAnnotations(key)

            // else use our fallback value
            val = val?:fallbackValue

            // only makes sense to set a capability if we have a value
            if (!val) return;

            // special cases
            switch(keyHyphen) {
                case "name":
                    if (getTestCapabilitiesValueFromAnnotations("includePlatformVersionDateInName", true))
                        val += " (${platformUsed.name()}, $platformVersionUsed) ${AppiumTest.jobDate}"
                    break;
            }

            capabilities.setCapability keyHyphen, val
        }

        TestCapabilities.class.methods.each {
            if (it.getReturnType() == String.class && it.name != "toString") // only try to set capabilities that return a String
                capabilities.setCapabilityIfNotSet it.getName(), it.getDefaultValue()
        }

        // Set job name if it was not yet set
        capabilities.setCapabilityIfNotSet "name", generatedSimpleName()

        URL serverAddress = new URL(definedValueSafe("serverAddress", "http://127.0.0.1:4723/wd/hub"))

        capabilities.setCapabilityIfNotSet("app", getAppPathUsed());

        android {
            capabilities.setCapabilityIfNotSet "platformName", "Android"
            capabilities.setCapabilityIfNotSet "deviceName", "Android"
            capabilities.setCapabilityIfNotSet "platformVersion", "5.1"

            driver = new AndroidDriver(serverAddress, capabilities)
            helper = new AndroidHelper(driver, 30)
        }
        iOS {
            driver = new IOSDriver(serverAddress, capabilities)
            //helper = new IOSHelper()
        }

        driver?.manage()?.timeouts()?.implicitlyWait 30, TimeUnit.SECONDS
    }

    String generatedSimpleName() {
        "${getClass().simpleName}::$testName.methodName"
    }

    String getSimpleName() {
        definedValueSafe("name", generatedSimpleName())
    }

    /** Run after each test **/
    @After
    public void tearDownDriver() throws Exception {
        driver?.quit()
    }

}