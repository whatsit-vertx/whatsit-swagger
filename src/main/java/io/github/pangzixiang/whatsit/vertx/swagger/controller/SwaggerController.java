package io.github.pangzixiang.whatsit.vertx.swagger.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.pangzixiang.whatsit.core.annotation.RestController;
import io.github.pangzixiang.whatsit.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerJson;
import io.github.pangzixiang.whatsit.vertx.swagger.service.SwaggerJsonBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;
import org.webjars.WebJarAssetLocator;

import java.nio.file.Paths;

import static io.github.pangzixiang.whatsit.vertx.swagger.constant.SwaggerConstants.*;
import static io.github.pangzixiang.whatsit.core.constant.HttpConstants.CONTENT_TYPE;
import static io.github.pangzixiang.whatsit.core.constant.HttpConstants.CONTENT_TYPE_JSON;
import static io.github.pangzixiang.whatsit.core.utils.CoreUtils.objectToString;
import static java.util.Objects.requireNonNullElse;

@Slf4j
public class SwaggerController extends BaseController {
    private final WebJarAssetLocator locator = new WebJarAssetLocator();

    private final SwaggerJson swaggerJson;

    private final String baseUrl = requireNonNullElse(getApplicationContext().getApplicationConfiguration().getString(SWAGGER_BASEURL), DEFAULT.SWAGGER_BASEURL);

    public SwaggerController(ApplicationContext applicationContext) {
        super(applicationContext);
        SwaggerJsonBuilder builder = new SwaggerJsonBuilder(applicationContext);
        this.swaggerJson = builder.build();

    }

    @RestController(path = SWAGGER_JSON_URL, method = HttpRequestMethod.GET)
    public void swaggerJson(RoutingContext routingContext) {
        try {
            routingContext.response()
                    .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .end(objectToString(this.swaggerJson));
        } catch (JsonProcessingException e) {
            routingContext.response()
                    .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end();
        }
    }

    @RestController(path = SWAGGER_CONFIG_URL, method = HttpRequestMethod.GET)
    public void config(RoutingContext routingContext) {
        JsonObject configJson = new JsonObject();
        configJson.put(URL, baseUrl + "/swagger.json");
        routingContext.response()
                .putHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
                .end(configJson.encode());
    }

    @RestController(path = SWAGGER_INITIALIZER_JS_URL, method = HttpRequestMethod.GET)
    public void aInitJS(RoutingContext routingContext) {
        routingContext.response()
                .putHeader(CONTENT_TYPE, CONTENT_TYPE_JAVASCRIPT)
                .end(String.format(SWAGGER_INITIALIZER_JS, baseUrl + "/swagger-config"));
    }

    @RestController(path = SWAGGER_STATIC_URL, method = HttpRequestMethod.GET)
    public StaticHandler zIndex() {
        return StaticHandler.create(
                Paths.get(locator.getFullPath(SWAGGER_UI, INDEX)).getParent().toString()
        );
    }
}
