package mdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AsyncExample {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AsyncExample.class, args);
        NotificationController controller = context.getBean(NotificationController.class);
        controller.uploadFile();
    }
}
