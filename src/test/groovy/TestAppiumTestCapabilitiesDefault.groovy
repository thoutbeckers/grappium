import helper.AbstractNoPlatformAppiumTest
import houtbecke.rs.grappium.AppiumTest
import org.junit.Test

class TestAppiumTestCapabilitiesDefault extends AbstractNoPlatformAppiumTest {

    @Test
    void testNotSettingsAnyCapabilities() {
        assert true, "Make sure there are no exceptions thrown"
        assert getCapabilities().asMap().name == "TestAppiumTestCapabilitiesDefault::testNotSettingsAnyCapabilities (UNDETECTED, 1.0.0) ${AppiumTest.jobDate}"
    }
}
