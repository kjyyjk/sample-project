package event.transaction;

import org.springframework.context.ApplicationEvent;

public class AfterCompletionEvent extends ApplicationEvent {

    private final String message;

    public AfterCompletionEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
