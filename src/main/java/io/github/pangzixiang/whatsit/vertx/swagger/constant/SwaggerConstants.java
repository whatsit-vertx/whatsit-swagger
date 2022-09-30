package io.github.pangzixiang.whatsit.vertx.swagger.constant;

public class SwaggerConstants {
    private SwaggerConstants() {
        super();
    }

    public static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript";

    public static final String SWAGGER_CONFIG_URL = "{swagger.baseUrl}/swagger-config";

    public static final String SWAGGER_INITIALIZER_JS_URL = "{swagger.baseUrl}/swagger-initializer.js";

    public static final String SWAGGER_STATIC_URL = "{swagger.baseUrl}/*";

    public static final String SWAGGER_UI = "swagger-ui";

    public static final String INDEX = "index.html";

    public static final String SWAGGER_JSON_URL = "{swagger.baseUrl}/swagger.json";

    public static final String URL = "url";

    public static final String SWAGGER_BASEURL = "swagger.baseUrl";

    public static class DEFAULT {

        private DEFAULT() {
            super();
        }

        public static final String SWAGGER_BASEURL = "/swagger/v0";
    }

    public static final String SWAGGER_INITIALIZER_JS = """
            window.onload = function () {
                window.ui = SwaggerUIBundle({
                    url: "https://petstore.swagger.io/v2/swagger.json",
                    dom_id: '#swagger-ui',
                    deepLinking: true,
                    presets: [
                        SwaggerUIBundle.presets.apis,
                        SwaggerUIStandalonePreset
                    ],
                    plugins: [
                        SwaggerUIBundle.plugins.DownloadUrl
                    ],
                    layout: "StandaloneLayout",
                        
                    //Overwrite the config
                    configUrl: "%s"
                });
                        
                //</editor-fold>
            };
            """;
}
