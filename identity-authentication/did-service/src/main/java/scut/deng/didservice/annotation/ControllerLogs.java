package scut.deng.didservice.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLogs {
    String description() default "";
    boolean printResponse() default true;
}
