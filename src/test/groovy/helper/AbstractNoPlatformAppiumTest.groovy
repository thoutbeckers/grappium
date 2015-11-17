package helper

import houtbecke.rs.grappium.AppiumTest

class AbstractNoPlatformAppiumTest extends AppiumTest {

    def platform = "unknown"
    def platformVersion = "1.0.0"
    def appPath = "/"

}