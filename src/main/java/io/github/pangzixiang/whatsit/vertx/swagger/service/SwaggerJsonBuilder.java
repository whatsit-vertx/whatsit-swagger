package io.github.pangzixiang.whatsit.vertx.swagger.service;

import com.github.victools.jsonschema.generator.*;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.config.ApplicationConfiguration;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.core.utils.AutoClassLoader;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.ParameterAnnotation;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.SecuritySchema;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.WhatsitSwaggerApi;
import io.github.pangzixiang.whatsit.vertx.swagger.constant.SecuritySchemaFlow;
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

    private final SchemaGenerator generator;

    public SwaggerJsonBuilder() {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        this.generator = new SchemaGenerator(config);
    }

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

        Set<Class<?>> definitionsClasses = new HashSet<>();

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

                                    if (whatsitSwaggerApi.requestBodyClass() != Class.class) {
                                        definitionsClasses.add(whatsitSwaggerApi.requestBodyClass());
                                        String clzName = whatsitSwaggerApi.requestBodyClass().getSimpleName();
                                        parameters.add(bodyParameter(clzName));
                                    }

                                    if (whatsitSwaggerApi.responseClass() != Class.class) {
                                        definitionsClasses.add(whatsitSwaggerApi.responseClass());
                                    }

                                    sub.setTags(whatsitSwaggerApi.tags().length > 0 ?
                                            Arrays.asList(whatsitSwaggerApi.tags()) : List.of(clz.getSimpleName()));
                                    sub.setSummary(StringUtils.isNotBlank(whatsitSwaggerApi.summary()) ?
                                            whatsitSwaggerApi.summary() : endpoint.getName());
                                    sub.setDescription(StringUtils.isNotBlank(whatsitSwaggerApi.description()) ?
                                            whatsitSwaggerApi.description() : endpoint.getName());
                                    sub.setOperationId(StringUtils.isNotBlank(whatsitSwaggerApi.operationId()) ?
                                            whatsitSwaggerApi.operationId() : endpoint.getName());

                                    ParameterAnnotation[] queryParameterAnnotations = whatsitSwaggerApi.params();
                                    for (ParameterAnnotation queryParameterAnnotation : queryParameterAnnotations) {
                                        Parameter parameter = new Parameter();
                                        parameter.setRequired(queryParameterAnnotation.required());
                                        parameter.setType(queryParameterAnnotation.type().getValue());
                                        parameter.setFormat(queryParameterAnnotation.format());
                                        parameter.setName(queryParameterAnnotation.name());
                                        parameter.setDescription(queryParameterAnnotation.description());
                                        parameter.setIn(queryParameterAnnotation.in().getValue());
                                        parameters.add(parameter);
                                    }

                                    SecuritySchema securitySchema = whatsitSwaggerApi.securitySchema();
                                    if (securitySchema != null && StringUtils.isNotBlank(securitySchema.name())) {
                                        JsonObject securitySchemaJsonObject = new JsonObject();
                                        securitySchemaJsonObject.put("type", securitySchema.type().getValue());
                                        securitySchemaJsonObject.put("description", securitySchema.description());
                                        securitySchemaJsonObject.put("name", securitySchema.name());
                                        securitySchemaJsonObject.put("in", securitySchema.in().getValue());
                                        securitySchemaJsonObject.put("flow", securitySchema.flow().getValue());
                                        if ((securitySchema.flow().equals(SecuritySchemaFlow.ACCESS_CODE) ||
                                                securitySchema.flow().equals(SecuritySchemaFlow.IMPLICIT)) &&
                                                StringUtils.isNotBlank(securitySchema.authorizationUrl())) {
                                            securitySchemaJsonObject.put("authorizationUrl", securitySchema.authorizationUrl());
                                        }
                                        if ((securitySchema.flow().equals(SecuritySchemaFlow.ACCESS_CODE) ||
                                                securitySchema.flow().equals(SecuritySchemaFlow.APPLICATION) ||
                                                securitySchema.flow().equals(SecuritySchemaFlow.PASSWORD)) &&
                                                StringUtils.isNotBlank(securitySchema.tokenUrl())
                                        ) {
                                            securitySchemaJsonObject.put("tokenUrl", securitySchema.tokenUrl());
                                        }
                                        json.addSecurityDefinitions(securitySchema.name(), securitySchemaJsonObject);
                                        sub.addSecurity(Map.of(securitySchema.name(), List.of()));
                                    }
                                } else {
                                    sub.setTags(List.of(clz.getSimpleName()));
                                    sub.setSummary(endpoint.getName());
                                    sub.setDescription(endpoint.getName());
                                    sub.setOperationId(endpoint.getName());
                                }

                                if (path.contains(":")) {
                                    String[] temp = path.split("/");
                                    for (String tempParam : temp) {
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

                                sub.setParameters(parameters);
                                sub.setResponses(responses(whatsitSwaggerApi));

                                // Details
                                details.put(restEndpoint.method().name().toLowerCase(Locale.ROOT), sub);

                                // path
                                json.addPath(path, details);
                            });
                });

        definitionsClasses.forEach(clz -> {
            json.addDefinitions(clz.getSimpleName(), generator.generateSchema(clz));
        });

        return json;
    }

    private Parameter bodyParameter(String bodyClzName) {
        Parameter parameter = new Parameter();
        parameter.setIn("body");
        parameter.setName("body");
        parameter.setDescription("%s object that needs to be sent".formatted(bodyClzName));
        parameter.setRequired(true);
        parameter.setSchema(new JsonObject().put("$ref", "#/definitions/" + bodyClzName));
        return parameter;
    }

    private Map<String, Object> responses(WhatsitSwaggerApi whatsitSwaggerApi) {
        Map<String, Object> responses = new HashMap<>();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.put("description", HttpResponseStatus.OK.reasonPhrase());
        if (whatsitSwaggerApi != null && whatsitSwaggerApi.responseClass() != Class.class) {
            responseJsonObject.put("schema", new JsonObject().put("$ref", "#/definitions/" + whatsitSwaggerApi.responseClass().getSimpleName()));
        }
        responses.put(String.valueOf(HttpResponseStatus.OK.code()), responseJsonObject);
//        responses.put(String.valueOf(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()),
//                new JsonObject().put("description", HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()).getMap());
//        responses.put(String.valueOf(HttpResponseStatus.BAD_REQUEST.code()),
//                new JsonObject().put("description", HttpResponseStatus.BAD_REQUEST.reasonPhrase()).getMap());
//        responses.put(String.valueOf(HttpResponseStatus.UNAUTHORIZED.code()),
//                new JsonObject().put("description", HttpResponseStatus.UNAUTHORIZED.reasonPhrase()).getMap());
//        responses.put(String.valueOf(HttpResponseStatus.NOT_FOUND.code()),
//                new JsonObject().put("description", HttpResponseStatus.NOT_FOUND.reasonPhrase()).getMap());
        return responses;
    }
}
