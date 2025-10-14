package event.transaction;

import org.springframework.context.ApplicationEvent;

public class BeforeCommitEvent extends ApplicationEvent {

    private final String message;

    public BeforeCommitEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
