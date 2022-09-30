package io.github.pangzixiang.whatsit.vertx.swagger.service;

import io.github.pangzixiang.whatsit.core.annotation.RestController;
import io.github.pangzixiang.whatsit.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.swagger.controller.SwaggerController;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerApiDetail;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerJson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

import static io.github.pangzixiang.whatsit.core.utils.CoreUtils.refactorControllerPath;

@Slf4j
public class SwaggerJsonBuilder {

    private final ApplicationContext applicationContext;

    public SwaggerJsonBuilder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SwaggerJson build() {
        SwaggerJson json = new SwaggerJson();
        json.setHost("localhost:" + this.applicationContext.getApplicationConfiguration().getPort());
        json.setBasePath("");
        SwaggerJson.Info info = new SwaggerJson.Info();
        info.setDescription("Swagger API Document");
        info.setVersion("1.0.0");
        info.setTitle("Swagger API Document");
        json.setInfo(info);

        Map<String, Object> paths = new HashMap<>();
        List<Class<? extends BaseController>> controllers = this.applicationContext.getControllers();
        controllers.stream()
                .filter(clz -> !clz.equals(SwaggerController.class))
                .forEach(clz -> {
                    Method[] endpoints = clz.getMethods();
                    Arrays.stream(endpoints)
                            .filter(m1 -> m1.getAnnotation(RestController.class) != null)
                            .forEach(endpoint -> {
                                RestController restController = endpoint.getAnnotation(RestController.class);

                                Map<String, Object> details = new HashMap<>();

                                //Sub Details
                                SwaggerApiDetail sub = new SwaggerApiDetail();

                                sub.setTags(List.of(clz.getSimpleName()));

                                sub.setSummary(endpoint.getName());

                                sub.setDescription(endpoint.getName());

                                sub.setOperationId(endpoint.getName());

                                if (restController.method().name().toUpperCase(Locale.ROOT).equals(HttpRequestMethod.POST.name())) {
                                    sub.setParameters(List.of(bodyParameter()));
                                } else {
                                    sub.setParameters(List.of());
                                }

                                sub.setResponses(responses());

                                // Details
                                details.put(restController.method().name().toLowerCase(Locale.ROOT), sub);

                                // path
                                paths.put(refactorControllerPath(restController.path(), this.applicationContext.getApplicationConfiguration()), details);
                            });
                });

        json.setPaths(paths);
        return json;
    }

    private SwaggerApiDetail.Parameter bodyParameter() {
        SwaggerApiDetail.Parameter body = new SwaggerApiDetail.Parameter();
        body.setIn("body");
        body.setName("body");
        body.setDescription("body");
        body.setRequired(false);
        return body;
    }

    private Map<String, Object> responses() {
        Map<String, Object> responses = new HashMap<>();
        responses.put(String.valueOf(HttpResponseStatus.OK.code()),
                new JsonObject().put("description", HttpResponseStatus.OK.reasonPhrase()).getMap());
        responses.put(String.valueOf(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()),
                new JsonObject().put("description", HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()).getMap());
        responses.put(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
                new JsonObject().put("description", HttpResponseStatus.BAD_REQUEST.reasonPhrase()).getMap());
        responses.put(String.valueOf(HttpResponseStatus.UNAUTHORIZED.code()),
                new JsonObject().put("description", HttpResponseStatus.UNAUTHORIZED.reasonPhrase()).getMap());
        responses.put(String.valueOf(HttpResponseStatus.NOT_FOUND.code()),
                new JsonObject().put("description", HttpResponseStatus.NOT_FOUND.reasonPhrase()).getMap());
        return responses;
    }
}
