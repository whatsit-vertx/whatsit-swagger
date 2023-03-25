package io.github.pangzixiang.whatsit.vertx.swagger.local.Controller;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
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

    @RestEndpoint(path = "/echoTest", method = HttpRequestMethod.GET)
    public void echoTest(RoutingContext routingContext) {
        log.info("Echo Controller handle request!");
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @RestEndpoint(path = "/{echo.test}", method = HttpRequestMethod.GET)
    public void pathTest(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @RestEndpoint(path = "/{echo.test}/{echo.test2.path}", method = HttpRequestMethod.GET)
    public void pathTest2(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @RestEndpoint(path = "/post/test", method = HttpRequestMethod.POST)
    public void postTest(RoutingContext routingContext) {
        log.info(routingContext.body().asString());
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "post done");
    }
}
