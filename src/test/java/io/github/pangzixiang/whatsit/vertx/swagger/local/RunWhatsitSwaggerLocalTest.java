package io.github.pangzixiang.whatsit.vertx.swagger.local;

import io.github.pangzixiang.whatsit.vertx.swagger.local.Controller.EchoController;
import io.github.pangzixiang.whatsit.vertx.swagger.controller.SwaggerController;
import io.github.pangzixiang.whatsit.core.ApplicationRunner;
import io.github.pangzixiang.whatsit.core.context.ApplicationContext;

public class RunWhatsitSwaggerLocalTest {
    public static void main(String[] args) {
        System.setProperty("whatsit.env", "local");
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.registerController(SwaggerController.class, EchoController.class);
        ApplicationRunner applicationRunner = new ApplicationRunner(applicationContext);
        applicationRunner.run();
    }
}
