package io.github.pangzixiang.whatsit.vertx.swagger.model;

import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class Parameter {
    private String name;
    private String in;
    private String description;
    private boolean required;
    private String type;
    private String format;
    private JsonObject schema;
}
