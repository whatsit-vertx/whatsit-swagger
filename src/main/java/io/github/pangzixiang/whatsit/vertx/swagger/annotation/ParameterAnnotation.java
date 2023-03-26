package io.github.pangzixiang.whatsit.vertx.swagger.annotation;

import io.github.pangzixiang.whatsit.vertx.swagger.constant.ParameterIn;
import io.github.pangzixiang.whatsit.vertx.swagger.constant.ParameterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterAnnotation {
    boolean required() default false;
    String name() default "";
    String description() default "";
    ParameterType type() default ParameterType.STRING;
    String format() default "";
    ParameterIn in() default ParameterIn.QUERY;
}
