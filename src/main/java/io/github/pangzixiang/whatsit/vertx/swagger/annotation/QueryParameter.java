package io.github.pangzixiang.whatsit.vertx.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParameter {
    boolean required() default false;
    String name() default "";
    String description() default "";
    String type() default "";
    String format() default "";
    String in() default "query";
}
