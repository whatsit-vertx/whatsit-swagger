package io.github.pangzixiang.whatsit.vertx.swagger.constant;

import lombok.Getter;

public enum SecuritySchemaIn {
    QUERY("query"),
    HEADER("header");

    @Getter
    private final String value;

    SecuritySchemaIn(String value) {
        this.value = value;
    }
}
