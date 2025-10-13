package mdc;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

public class LoggingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable task) {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(copyOfContextMap);
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
