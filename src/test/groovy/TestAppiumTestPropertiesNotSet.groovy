import houtbecke.rs.grappium.AppiumTest
import houtbecke.rs.grappium.Platforms
import org.junit.Test

class TestAppiumTestPropertiesNotSet extends AppiumTest {

    Exception exception;
    @Override
    void setUpAppium() throws Exception {

        //noinspection GroovyFallthrough
        switch(testName.methodName) {
            case "testSetBoth":
                this.metaClass.appPath = "appName"
            case "testPlatformSet":
                this.metaClass.platform = "unknown"
        }

        try {
            super.setUpAppium()
        } catch (Exception e) {
            exception = e;
        }
    }

    @Test
    void testNothingSet() {
        assert exception instanceof MissingPropertyException
        assert exception.property == "platform"
    }

    @Test
    void testPlatformSet() {
        assert exception instanceof MissingPropertyException
        assert exception.property == "appPath"
    }

    @Test
    void testSetBoth() {
        assert exception == null
        assert platformVersionUsed == ""
        assert platformUsed == Platforms.OS.UNDETECTED

        assert appNameUsed == "appName"

    }
}
