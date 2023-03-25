package io.github.pangzixiang.whatsit.vertx.swagger.local.Controller;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.QueryParameter;
import io.github.pangzixiang.whatsit.vertx.swagger.annotation.WhatsitSwaggerApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EchoController extends BaseController {

    /**
     * Instantiates a new Base controller.
     *
     * @param router the router
     */
    public EchoController(Router router) {
        super(router);
    }

    @WhatsitSwaggerApi(tags = {"A", "B"}, description = "test echo Test desc", summary = "test echo Test", operationId = "test")
    @RestEndpoint(path = "/echoTest", method = HttpRequestMethod.GET)
    public void echoTest(RoutingContext routingContext) {
        log.info("Echo Controller handle request!");
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @WhatsitSwaggerApi(tags = "C",
            description = "test echo Test path desc",
            summary = "test echo path Test",
            operationId = "test")
    @RestEndpoint(path = "/path/:path", method = HttpRequestMethod.GET)
    public void echoTestPath(RoutingContext routingContext) {
        log.info("Echo Controller handle request! {}", routingContext.pathParam("path"));
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @WhatsitSwaggerApi(tags = "C",
            description = "test echo Test path desc",
            summary = "test echo path Test",
            operationId = "test")
    @RestEndpoint(path = "/path/:path/:test", method = HttpRequestMethod.GET)
    public void echoTestPath2(RoutingContext routingContext) {
        log.info("Echo Controller handle request! {}", routingContext.pathParam("path") + routingContext.pathParam("test"));
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @WhatsitSwaggerApi(tags = "C",
            description = "test echo Test desc",
            summary = "test echo Test",
            operationId = "test",
            queryParams = @QueryParameter(name = "test", description = "test", required = true))
    @RestEndpoint(path = "/query", method = HttpRequestMethod.GET)
    public void queryParam(RoutingContext routingContext) {
        log.info("Echo Controller handle request! {}", routingContext.queryParams().get("test"));
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @WhatsitSwaggerApi(tags = "A")
    @RestEndpoint(path = "/{echo.test}", method = HttpRequestMethod.GET)
    public void pathTest(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @WhatsitSwaggerApi(tags = "B")
    @RestEndpoint(path = "/{echo.test}/{echo.test2.path}", method = HttpRequestMethod.GET)
    public void pathTest2(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @WhatsitSwaggerApi(tags = "B")
    @RestEndpoint(path = "/post/test", method = HttpRequestMethod.POST)
    public void postTest(RoutingContext routingContext) {
        log.info(routingContext.body().asString());
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "post done");
    }
}
