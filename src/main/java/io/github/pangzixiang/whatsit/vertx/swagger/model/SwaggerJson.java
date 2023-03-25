package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerJson {
    private final String swagger = "2.0";
    private Info info;
    private String host;
    private String basePath;
    private Tags tags;
    private final List<String> schemas = List.of("http", "https");
    private final Map<String, Map<String, SwaggerApiDetail>> paths = new ConcurrentHashMap<>();
    private final Map<String, Object> securityDefinitions = new ConcurrentHashMap<>();

    public void addPath(String path, Map<String, SwaggerApiDetail> swaggerApiDetailMap) {
        this.paths.put(path, swaggerApiDetailMap);
    }
}
