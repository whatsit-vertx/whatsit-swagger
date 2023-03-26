package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.Data;

import java.util.ArrayList;
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
    private final List<Map<String, List<String>>> security = new ArrayList<>();

    public void addSecurity(Map<String, List<String>> security) {
        this.security.add(security);
    }
}
