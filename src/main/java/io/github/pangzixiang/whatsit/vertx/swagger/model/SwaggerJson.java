package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SwaggerJson {
    private String swagger = "2.0";
    private Info info;
    private String host;
    private String basePath;
    private Tags tags;
    private List<String> schemas = List.of("http", "https");
    private Map<String, Object> paths;
    private Map<String, Object> securityDefinitions;
    @Data
    public static class Info {
        private String description;
        private String version;
        private String title;
        private String termsOfService;
        private Contact contact;
        private License license;
        @Data
        public static class Contact {
            private String email;
        }
        @Data
        public static class License {
            private String name;
            private String url;
        }
    }
    @Data
    public static class Tags {
        private String name;
        private String description;
        private ExternalDocs externalDocs;
        @Data
        private static class ExternalDocs {
            private String description;
            private String url;
        }
    }
}
