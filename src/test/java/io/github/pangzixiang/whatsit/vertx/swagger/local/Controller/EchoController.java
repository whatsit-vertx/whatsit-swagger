package io.github.pangzixiang.whatsit.vertx.swagger.local.Controller;

import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoController extends BaseController {
    public EchoController(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @RestController(path = "/echoTest", method = HttpRequestMethod.GET)
    public void echoTest(RoutingContext routingContext) {
        log.info("Echo Controller handle request!");
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "echo");
    }

    @RestController(path = "/{echo.test}", method = HttpRequestMethod.GET)
    public void pathTest(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @RestController(path = "/{echo.test}/{echo.test2.path}", method = HttpRequestMethod.GET)
    public void pathTest2(RoutingContext routingContext) {
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "ok");
    }

    @RestController(path = "/post/test", method = HttpRequestMethod.POST)
    public void postTest(RoutingContext routingContext) {
        log.info(routingContext.body().asString());
        sendJsonResponse(routingContext, HttpResponseStatus.OK, "post done");
    }
}
