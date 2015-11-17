import houtbecke.rs.grappium.Platforms
import helper.ClassUsingIfdef

import static TestPlatforms.Platform.*

class TestPlatforms extends GroovyTestCase {

    enum Platform implements Platforms {
        None(shouldBeAndroid:false, shouldBeiOS:false),
        Android(shouldBeAndroid:true, shouldBeiOS:false),
        Apple(shouldBeAndroid:false, shouldBeiOS:true) // use Apple instead of iOS to avoid naming conflict

        boolean shouldBeAndroid, shouldBeiOS

        boolean is () {
            boolean isAndroid = false, isiOS = false

            android {
                isAndroid = true
            }

            iOS {
                isiOS = true
            }

            shouldBeAndroid == isAndroid && shouldBeiOS == isiOS

        }
    }


    static def getEnvResponse

    static void mockPlatformEnvResponse(final String v, String version="") {
        getEnvResponse = new HashMap();
        getEnvResponse.put("PLATFORM", v)
        if (version)
            getEnvResponse.put("PLATFORM_VERSION", version)


        // override the getEnv call in Platforms to return our own mocked result
        Platforms.metaClass.getEnv = {
            return getEnvResponse
        }
    }

    static void mockDetectEnvResponse() {
        getEnvResponse = new HashMap();
        getEnvResponse.put("DETECT", "detected")

        // override the getEnv call in Platforms to return our own mocked result
        Platforms.metaClass.getEnv = {
            return getEnvResponse
        }
    }


    void setUp() {
        doNotMockEnvResponse()
    }

    static void doNotMockEnvResponse() {
        // return the response to the default

        getEnvResponse = System.env
    }

    void testSettingPlatforms() {

        // check picking up environment variables

        mockPlatformEnvResponse('anDroId')
        assert Android.is(), "Setting (case insensitive)Android as the env value of PLATFORM should work"

        mockPlatformEnvResponse('IoS')
        assert Apple.is(), "Setting (case insensitive) iOS as the env value of PLATFORM should work"

        doNotMockEnvResponse()
        shouldFail(MissingPropertyException) { // Not setting a property in your test and not having an env variable should fail"
            None.is()
        }
        None.metaClass.platform = "something random"
        assert None.is(), "setting the platform to something not recognized should work, but it should not throw an exception either"

        shouldFail(MissingPropertyException) {
            Android.is() // Not setting a property in your test and not having an env variable should fail"
        }
        Android.metaClass.platform = "AnDRoiD"
        assert Android.is(), "setting the platform property in your class to (case insensitive)Android should work"

        shouldFail(MissingPropertyException) {
            Apple.is() // Not setting a property in your test and not having an env variable should fail"
        }
        Apple.metaClass.platform = "ioS"
        assert Apple.is(), "setting the platform property in your class to (case insensitive)iOS should work"

    }

    void testPlatformVersion() {

        mockPlatformEnvResponse('android')
        assert Android.getPlatformVersionUsed() == "4.3"

        mockPlatformEnvResponse('ios')
        assert Apple.getPlatformVersionUsed() == "8.1"

        mockPlatformEnvResponse('android', "6.0")
        assert Android.getPlatformVersionUsed() == "6.0"

        mockPlatformEnvResponse('ios', "9.0")
        assert Apple.getPlatformVersionUsed() == "9.0"

        mockPlatformEnvResponse('android')
        Android.metaClass.platformVersion = "4.4"
        assert Android.getPlatformVersionUsed() == "4.4"

        mockPlatformEnvResponse('iOS')
        Apple.metaClass.platformVersion = "8.2"
        assert Apple.getPlatformVersionUsed() == "8.2"
    }


    void testIfdef() {

        mockDetectEnvResponse()

        def testObject = new ClassUsingIfdef()

        assert testObject.detectsIfdef()

        doNotMockEnvResponse()

        assert !testObject.detectsIfdef()

        testObject.metaClass.getDetect << {"detected"}

        assert testObject.detectsIfdef()

        testObject.metaClass.getDetect << {"false"}

        assert !testObject.detectsIfdef()



    }

    void testIfnotdef() {

        mockDetectEnvResponse()

        def testObject = new ClassUsingIfdef()

        assert !testObject.detectsIfnotdef()

        doNotMockEnvResponse()

        assert testObject.detectsIfnotdef()

        testObject.metaClass.getDetect << { "detect" }

        assert !testObject.detectsIfnotdef()

        testObject.metaClass.getDetect << { "false" }

        assert testObject.detectsIfnotdef()

    }

}
