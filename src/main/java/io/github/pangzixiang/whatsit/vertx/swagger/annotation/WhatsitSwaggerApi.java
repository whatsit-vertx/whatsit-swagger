package io.github.pangzixiang.whatsit.vertx.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WhatsitSwaggerApi {
    String summary() default "";
    String description() default "";
    String[] tags() default {};
    String operationId() default "";
    QueryParameter[] queryParams() default {};
}
