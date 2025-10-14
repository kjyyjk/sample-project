package event.transaction;

import org.springframework.context.ApplicationEvent;

public class AfterRollbackEvent extends ApplicationEvent {

    private final String message;

    public AfterRollbackEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
