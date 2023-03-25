package io.github.pangzixiang.whatsit.vertx.swagger.service;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.config.ApplicationConfiguration;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.core.utils.AutoClassLoader;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.QueryParameter;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.WhatsitSwaggerApi;
import io.github.pangzixiang.whatsit.vertx.swagger.controller.SwaggerController;
import io.github.pangzixiang.whatsit.vertx.swagger.model.Info;
import io.github.pangzixiang.whatsit.vertx.swagger.model.Parameter;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerApiDetail;
import io.github.pangzixiang.whatsit.vertx.swagger.model.SwaggerJson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

import static io.github.pangzixiang.whatsit.vertx.core.utils.CoreUtils.refactorControllerPath;


@Slf4j
public class SwaggerJsonBuilder {
    private final ApplicationConfiguration applicationConfiguration = ApplicationContext.getApplicationContext().getApplicationConfiguration();
    public SwaggerJson build() {
        SwaggerJson json = new SwaggerJson();
        json.setHost("localhost:" + this.applicationConfiguration.getPort());
        json.setBasePath(this.applicationConfiguration.getString("swagger.apiBasePath"));
        Info info = new Info();
        info.setDescription(this.applicationConfiguration.getString("swagger.description"));
        info.setVersion(this.applicationConfiguration.getString("swagger.version"));
        info.setTitle(this.applicationConfiguration.getString("swagger.title"));
        json.setInfo(info);

        List<Class<?>> controllers =
                AutoClassLoader.getClassesByCustomFilter(clz -> clz.isAnnotationPresent(RestController.class) && BaseController.class.isAssignableFrom(clz));
        controllers.stream()
                .filter(clz -> !clz.equals(SwaggerController.class))
                .forEach(clz -> {
                    RestController restController = clz.getAnnotation(RestController.class);
                    Method[] endpoints = clz.getMethods();
                    Arrays.stream(endpoints)
                            .filter(m1 -> m1.getAnnotation(RestEndpoint.class) != null)
                            .forEach(endpoint -> {
                                RestEndpoint restEndpoint = endpoint.getAnnotation(RestEndpoint.class);

                                Map<String, SwaggerApiDetail> details = new HashMap<>();

                                String path = refactorControllerPath(restController.basePath() + restEndpoint.path(),
                                        this.applicationConfiguration);

                                //Sub Details
                                SwaggerApiDetail sub = new SwaggerApiDetail();

                                List<Parameter> parameters = new ArrayList<>();

                                WhatsitSwaggerApi whatsitSwaggerApi = endpoint.getAnnotation(WhatsitSwaggerApi.class);
                                if (whatsitSwaggerApi != null) {
                                    sub.setTags(whatsitSwaggerApi.tags().length > 0 ?
                                            Arrays.asList(whatsitSwaggerApi.tags()) : List.of(clz.getSimpleName()));
                                    sub.setSummary(StringUtils.isNotBlank(whatsitSwaggerApi.summary()) ?
                                            whatsitSwaggerApi.summary() : endpoint.getName());
                                    sub.setDescription(StringUtils.isNotBlank(whatsitSwaggerApi.description()) ?
                                            whatsitSwaggerApi.description() : endpoint.getName());
                                    sub.setOperationId(StringUtils.isNotBlank(whatsitSwaggerApi.operationId()) ?
                                            whatsitSwaggerApi.operationId() : endpoint.getName());

                                    QueryParameter[] queryParameters = whatsitSwaggerApi.queryParams();
                                    for (QueryParameter queryParameter: queryParameters) {
                                        Parameter parameter = new Parameter();
                                        parameter.setRequired(queryParameter.required());
                                        parameter.setType(queryParameter.type());
                                        parameter.setFormat(queryParameter.format());
                                        parameter.setName(queryParameter.name());
                                        parameter.setDescription(queryParameter.description());
                                        parameter.setIn(queryParameter.in());
                                        parameters.add(parameter);
                                    }
                                } else {
                                    sub.setTags(List.of(clz.getSimpleName()));
                                    sub.setSummary(endpoint.getName());
                                    sub.setDescription(endpoint.getName());
                                    sub.setOperationId(endpoint.getName());
                                }

                                if (path.contains(":")) {
                                    String[] temp = path.split("/");
                                    for (String tempParam: temp) {
                                        if (tempParam.contains(":")) {
                                            Parameter parameter = new Parameter();
                                            parameter.setName(tempParam.substring(1));
                                            parameter.setIn("path");
                                            parameter.setDescription(tempParam.substring(1));
                                            parameter.setRequired(true);
                                            path = path.replace(tempParam, "{" + tempParam.substring(1) + "}");
                                            parameters.add(parameter);
                                        }
                                    }
                                }

                                if (restEndpoint.method().name().toUpperCase(Locale.ROOT).equals(HttpRequestMethod.POST.name())) {
                                    parameters.add(bodyParameter());
                                }

                                sub.setParameters(parameters);
                                sub.setResponses(responses());

                                // Details
                                details.put(restEndpoint.method().name().toLowerCase(Locale.ROOT), sub);

                                // path
                                json.addPath(path, details);
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
