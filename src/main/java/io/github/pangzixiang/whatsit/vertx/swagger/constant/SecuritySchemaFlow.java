package io.github.pangzixiang.whatsit.vertx.swagger.constant;

import lombok.Getter;

public enum SecuritySchemaFlow {
    IMPLICIT("implicit"),
    PASSWORD("password"),
    APPLICATION("application"),
    ACCESS_CODE("accessCode"),
    NULL(null)
    ;

    @Getter
    private final String value;

    SecuritySchemaFlow(String value) {
        this.value = value;
    }
}
