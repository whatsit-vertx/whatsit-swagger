package io.github.pangzixiang.whatsit.vertx.swagger.controller;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerJson;
import io.github.pangzixiang.whatsit.vertx.swagger.service.SwaggerJsonBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.webjars.WebJarAssetLocator;

import java.nio.file.Paths;

import static io.github.pangzixiang.whatsit.vertx.core.constant.HttpConstants.CONTENT_TYPE_JSON;
import static io.github.pangzixiang.whatsit.vertx.swagger.constant.SwaggerConstants.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@RestController
public class SwaggerController extends BaseController {
    private final WebJarAssetLocator locator = new WebJarAssetLocator();

    private final SwaggerJson swaggerJson;

    private final String baseUrl = ApplicationContext.getApplicationContext().getApplicationConfiguration().getString(SWAGGER_BASEURL);

    public SwaggerController(Router router) {
        super(router);
        SwaggerJsonBuilder builder = new SwaggerJsonBuilder();
        this.swaggerJson = builder.build();
    }

    @Override
    public void start() throws Exception {
        super.start();
        getRouter()
                .get(baseUrl + "/*")
                .handler(StaticHandler.create(Paths.get(locator.getFullPath(SWAGGER_UI, INDEX)).getParent().toString()));
    }

    @RestEndpoint(path = SWAGGER_JSON_URL, method = HttpRequestMethod.GET)
    public void swaggerJson(RoutingContext routingContext) {
        routingContext.response()
                .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .end(Json.encode(this.swaggerJson));
    }

    @RestEndpoint(path = SWAGGER_CONFIG_URL, method = HttpRequestMethod.GET)
    public void config(RoutingContext routingContext) {
        JsonObject configJson = new JsonObject();
        configJson.put(URL, baseUrl + "/swagger.json");
        routingContext.response()
                .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .end(configJson.encode());
    }

    @RestEndpoint(path = SWAGGER_INITIALIZER_JS_URL, method = HttpRequestMethod.GET)
    public void initJS(RoutingContext routingContext) {
        getVertx().fileSystem()
                .readFile("swagger-initializer.js")
                .onSuccess(buffer ->
                        routingContext.response()
                                .putHeader(CONTENT_TYPE, CONTENT_TYPE_JAVASCRIPT)
                                .end(String.format(buffer.toString(), baseUrl + "/swagger-config")))
                .onFailure(throwable ->
                        sendJsonResponse(routingContext, HttpResponseStatus.NOT_FOUND, "swagger-initializer.js not found!")
                );
    }
}
