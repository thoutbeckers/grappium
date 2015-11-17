package houtbecke.rs.grappium

import com.google.common.base.CaseFormat

import static houtbecke.rs.grappium.Platforms.OS.ANDROID
import static houtbecke.rs.grappium.Platforms.OS.IOS
import static houtbecke.rs.grappium.Platforms.OS.UNDETECTED

public trait Platforms {

    // allow for easy mocking
    Map<String,String> getEnv() {
        return System.getenv()
    }

    boolean isAllUpper(String key) {
        key.every{Character.isUpperCase(it as Character) || "_".equals(it)}
    }

    String convertedEnv(String key) {

        if (!isAllUpper(key))
            key = "${CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, key)}"

        return env."$key"
    }

    String lowerCamelPropertyName(String key) {
        isAllUpper(key) ?
                CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key)
                :
                CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, key)
    }

    String convertedProperty(Object o, String key) {
        key = lowerCamelPropertyName(key)
        o."$key"
    }

    String definedValue(String key) {
        convertedEnv(key)?:convertedProperty(this, key)
    }

    String definedValueSafe(String key, String defaultValue='') {
        convertedEnv(key)?:( this.hasProperty(lowerCamelPropertyName(key)) ? convertedProperty(this, key)?: defaultValue : defaultValue)
    }

    void ifNotDefined(String key, Closure ifnotdefBlock) {
        def defined = definedValueSafe(key)
        if (!defined || defined?.toString()?.equalsIgnoreCase("false")) ifnotdefBlock ""
    }

    void ifDefined(String key, Closure ifdefBlock) {
        def defined = definedValueSafe(key)
        if (defined && !defined?.toString()?.equalsIgnoreCase("false")) ifdefBlock defined
    }

    def methodMissing(String name, args) {
        if (name.startsWith("ifdef") && args.length == 1 && args[0] instanceof Closure) {
            return ifDefined(name.substring("ifdef".length()), args[0] as Closure)
        }

        if (name.startsWith("ifnotdef") && args.length == 1 && args[0] instanceof Closure) {
            return ifNotDefined(name.substring("ifnotdef".length()), args[0] as Closure)
        }

    }

    void iOS(Closure c) {
        if (IOS.name().equalsIgnoreCase(definedValue("PLATFORM"))) c.run()
    }

    void android(Closure c) {
        if (ANDROID.name().equalsIgnoreCase(definedValue("PLATFORM"))) c.run()

    }

    static enum OS {
        IOS(defaultVersion:"8.1"),
        ANDROID(defaultVersion:"4.3"),
        UNDETECTED

        String defaultVersion

    }

    OS getPlatformUsed() {
        OS platform = UNDETECTED
        iOS { platform = IOS}
        android { platform = ANDROID}
        platform
    }

    String getPlatformVersionUsed() {
        def ret = ''

        ifdefPlatformVersion {
            ret = it;
        }

        if (!ret) {
            android {
                ret = ANDROID.defaultVersion
            }
            iOS {
                ret = IOS.defaultVersion
            }
        }
        ret
    }

}
