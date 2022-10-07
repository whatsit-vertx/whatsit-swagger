# Whatsit Swagger Module

1. setting the base url to conf file
```text
swagger: {
  baseUrl: /swagger/v1
}
```
2. register the Swagger Controller and your Self Controller

```java
import Controller.io.github.pangzixiang.whatsit.vertx.swagger.local.EchoController;
import io.github.pangzixiang.whatsit.controller.SwaggerController;
import io.github.pangzixiang.whatsit.vertx.core.ApplicationRunner;
import io.github.pangzixiang.whatsit.vertx.core.ApplicationContext;

public class RunWhatsitSwaggerLocalTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.registerController(SwaggerController.class, EchoController.class);
        ApplicationRunner applicationRunner = new ApplicationRunner(applicationContext);
        applicationRunner.run();
    }
}
```
