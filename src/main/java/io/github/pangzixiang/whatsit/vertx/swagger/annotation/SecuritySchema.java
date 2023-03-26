package io.github.pangzixiang.whatsit.vertx.swagger.annotation;

import io.github.pangzixiang.whatsit.vertx.swagger.constant.SecuritySchemaFlow;
import io.github.pangzixiang.whatsit.vertx.swagger.constant.SecuritySchemaIn;
import io.github.pangzixiang.whatsit.vertx.swagger.constant.SecuritySchemaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuritySchema {
    SecuritySchemaType type() default SecuritySchemaType.BASIC;
    String description() default "";
    String name();
    SecuritySchemaIn in() default SecuritySchemaIn.HEADER;
    SecuritySchemaFlow flow() default SecuritySchemaFlow.NULL;
    String authorizationUrl() default "";
    String tokenUrl() default "";
}
