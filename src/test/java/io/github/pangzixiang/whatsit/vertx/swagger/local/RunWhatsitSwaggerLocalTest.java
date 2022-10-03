package io.github.pangzixiang.whatsit.vertx.swagger.local;

import io.github.pangzixiang.whatsit.vertx.core.ApplicationRunner;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.swagger.local.Controller.EchoController;
import io.github.pangzixiang.whatsit.vertx.swagger.controller.SwaggerController;

public class RunWhatsitSwaggerLocalTest {
    public static void main(String[] args) {
        System.setProperty("whatsit.env", "local");
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.registerController(SwaggerController.class, EchoController.class);
        ApplicationRunner applicationRunner = new ApplicationRunner(applicationContext);
        applicationRunner.run();
    }
}
