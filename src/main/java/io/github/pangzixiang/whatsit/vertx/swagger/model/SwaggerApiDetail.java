package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SwaggerApiDetail {
    private List<String> tags;
    private String summary;
    private String description;
    private String operationId;
    private List<String> consumes;
    private List<String> produces;
    private List<Parameter> parameters;
    private Map<String, Object> responses;
    private List<Map<String, Object>> security;

    @Data
    public static class Parameter {
        private String name;
        private String in;
        private String description;
        private boolean required;
        private String type;
        private String format;
    }
}
