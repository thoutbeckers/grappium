import helper.AbstractNoPlatformAppiumTest
import houtbecke.rs.grappium.TestCapabilities
import org.junit.Test;

import houtbecke.rs.grappium.AppiumTest

@TestCapabilities(name="TestCaps")
class TestAppiumTestCapabilities extends AbstractNoPlatformAppiumTest {

    @Test
    @TestCapabilities(name = "my name", includePlatformVersionDateInName = false, appium_version = "1.0.0")
    void testCapabilitiesNameFromMethod() {
        def cap = getCapabilities().asMap()
        assert cap.name == "my name"
        assert cap."appium-version" == "1.0.0"

        getCapabilities().setCapabilityIfNotSet("appium-version", "2.0.0")
        assert cap."appium-version" == "1.0.0", "this should not override the value"

        getCapabilities().setCapabilityIfNotSet("someVersion", "2.0.0")
        assert cap.someVersion == "2.0.0", "But we should be able to set some other value"

    }

    @Test
    void testCapabilitiesFromClass() {
        def cap = getCapabilities().asMap()
        assert cap.name == "TestCaps (UNDETECTED, 1.0.0) ${AppiumTest.jobDate}"
    }






}
