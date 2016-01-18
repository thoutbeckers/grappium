package houtbecke.rs.grappium.thirdparty

import com.saucelabs.common.SauceOnDemandAuthentication
import com.saucelabs.common.SauceOnDemandSessionIdProvider
import com.saucelabs.junit.SauceOnDemandTestWatcher
import com.saucelabs.saucerest.SauceREST
import groovy.transform.SelfType
import groovyx.net.http.HTTPBuilder
import houtbecke.rs.grappium.AppiumTest
import houtbecke.rs.grappium.Platforms
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

import java.security.MessageDigest

@SelfType(AppiumTest)
trait SauceTrait implements Platforms, SauceOnDemandSessionIdProvider {

    /** Report pass/fail to Sauce Labs **/
    // false to silence Sauce connect messages.
    public @Rule
    SauceOnDemandTestWatcher reportToSauce //= new SauceOnDemandTestWatcher2(this);

    static class SauceOnDemandTestWatcher2 extends SauceOnDemandTestWatcher {
        SauceOnDemandTestWatcher2(SauceTrait sauceTest) {
            super(sauceTest, sauceTest.user, sauceTest.key, sauceTest.connectVerbose)
        }
    }

    /** Authenticate to Sauce with environment variables SAUCE_USER_NAME and SAUCE_API_KEY **/
    private SauceOnDemandAuthentication auth = new SauceOnDemandAuthentication();

    boolean connectVerbose = false

    String serverAddress, user, key



    @Before
    void setUpAppium() throws Exception {
        ifdefSauce {

            connectVerbose = definedValueSafe("sauceConnectVerbose");

            user = definedValueSafe("sauceUser", auth.getUsername())

            key = definedValueSafe("sauceKey", auth.getAccessKey())

            serverAddress = "http://$user:$key@ondemand.saucelabs.com:80/wd/hub"

            String fileName = uploadFileOrReturnFileName(user, key, appPathUsed, appNameUsed);
            capabilities.setCapability "app", "sauce-storage:$fileName"
        }

        super.setUpAppium()
    }

    static String uploadFileOrReturnFileName(String user, String key, File appPath, String appName) {

        String md5 = MessageDigest.getInstance('MD5').digest(appPath.bytes).encodeHex().toString()
        String name = "grappium_${md5}.apk"

        def remote = new HTTPBuilder("https://saucelabs.com/rest/v1/storage/")
        remote.auth.basic(user, key)

        boolean found=false

        remote.get(path:user) { resp, json ->
             json?.files?.each {
                 if (it.name == name && it.md5 == md5) {
                     found = true
                     println "using cached version in SauceLabs"
                 }
            }
        }

        if (!found) {
            SauceREST rest = new SauceREST(user, key);
            def returnedMd5 = rest.uploadFile(appPath, name, false);
            if (returnedMd5 != md5)
                throw new Exception("Uploaded file's md5 ($md5) is not what the server says it is ($returnedMd5)")
        }
        name
    }

    @Override
    String getSessionId() {
        driver?.getSessionId()?.toString()?: null
    }

    @Rule
    public TestRule printSession = new SessionPrintingTestWatcher(sauceTest: this)

    static class SessionPrintingTestWatcher extends TestWatcher {
        SauceTrait sauceTest

        protected void starting(Description description) {
        }

        protected void finished(Description description) {

            if (sauceTest.sessionId) println "Test: $description.methodName: https://saucelabs.com/tests/$sauceTest.sessionId"
        }
    }
}