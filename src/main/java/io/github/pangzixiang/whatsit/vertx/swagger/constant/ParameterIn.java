package io.github.pangzixiang.whatsit.vertx.swagger.constant;

import lombok.Getter;

public enum ParameterIn {

    PATH("path"),
    FORM_DATA("formData"),
    QUERY("query")
    ;

    @Getter
    private final String value;

    ParameterIn(String value) {
        this.value = value;
    }
}
