package io.github.pangzixiang.whatsit.vertx.swagger.constant;

import lombok.Getter;

public enum ParameterType {

    INTEGER("integer"),
    STRING("string"),
    FILE("file")
    ;

    @Getter
    private final String value;

    ParameterType(String value) {
        this.value = value;
    }
}
