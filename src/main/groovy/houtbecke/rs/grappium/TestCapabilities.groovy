package houtbecke.rs.grappium

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
public @interface TestCapabilities {
    String name() default ""
    boolean includePlatformVersionDateInName() default true
    String appium_version() default "1.4.13" // for some reason not camelCase in capabilities JSON. _ gets replaced with -
    String platform() default ""
    String platformVersion() default ""

}
