package io.github.pangzixiang.whatsit.vertx.swagger.service;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.core.utils.AutoClassLoader;
import io.github.pangzixiang.whatsit.vertx.swagger.controller.SwaggerController;
import io.github.pangzixiang.whatsit.vertx.swagger.model.Info;
import io.github.pangzixiang.whatsit.vertx.swagger.model.Parameter;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerApiDetail;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerJson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

import static io.github.pangzixiang.whatsit.vertx.core.utils.CoreUtils.refactorControllerPath;


@Slf4j
public class SwaggerJsonBuilder {

    private final ApplicationContext applicationContext = ApplicationContext.getApplicationContext();

    public SwaggerJson build() {
        SwaggerJson json = new SwaggerJson();
        json.setHost("localhost:" + this.applicationContext.getApplicationConfiguration().getPort());
        json.setBasePath("");
        Info info = new Info();
        info.setDescription("Swagger API Document");
        info.setVersion("1.0.0");
        info.setTitle("Swagger API Document");
        json.setInfo(info);

        Map<String, Object> paths = new HashMap<>();
        List<Class<?>> controllers =
                AutoClassLoader.getClassesByCustomFilter(clz -> clz.isAnnotationPresent(RestController.class) && BaseController.class.isAssignableFrom(clz));
        controllers.stream()
                .filter(clz -> !clz.equals(SwaggerController.class))
                .forEach(clz -> {
                    Method[] endpoints = clz.getMethods();
                    Arrays.stream(endpoints)
                            .filter(m1 -> m1.getAnnotation(RestEndpoint.class) != null)
                            .forEach(endpoint -> {
                                RestEndpoint restEndpoint = endpoint.getAnnotation(RestEndpoint.class);

                                Map<String, SwaggerApiDetail> details = new HashMap<>();

                                //Sub Details
                                SwaggerApiDetail sub = new SwaggerApiDetail();

                                sub.setTags(List.of(clz.getSimpleName()));

                                sub.setSummary(endpoint.getName());

                                sub.setDescription(endpoint.getName());

                                sub.setOperationId(endpoint.getName());

                                if (restEndpoint.method().name().toUpperCase(Locale.ROOT).equals(HttpRequestMethod.POST.name())) {
                                    sub.setParameters(List.of(bodyParameter()));
                                } else {
                                    sub.setParameters(List.of());
                                }

                                sub.setResponses(responses());

                                // Details
                                details.put(restEndpoint.method().name().toLowerCase(Locale.ROOT), sub);

                                // path
                                json.addPath(refactorControllerPath(restEndpoint.path(), this.applicationContext.getApplicationConfiguration()), details);
                            });
                });

        return json;
    }

    private Parameter bodyParameter() {
        Parameter body = new Parameter();
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
