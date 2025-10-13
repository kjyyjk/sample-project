package event.async;

import org.springframework.context.ApplicationEvent;

public class AsyncCustomEvent extends ApplicationEvent {

    private final String message;

    public AsyncCustomEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
