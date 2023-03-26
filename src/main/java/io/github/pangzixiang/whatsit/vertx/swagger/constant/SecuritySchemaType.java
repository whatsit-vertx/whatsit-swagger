package io.github.pangzixiang.whatsit.vertx.swagger.constant;

import lombok.Getter;

public enum SecuritySchemaType {
    BASIC("basic"),
    API_KEY("apiKey"),
    OAUTH2("oauth2");

    @Getter
    private final String value;

    SecuritySchemaType(String value) {
        this.value = value;
    }
}
