package event.asynctransaction;

import org.springframework.context.ApplicationEvent;

public class AfterCommitEvent extends ApplicationEvent {

    private final String message;

    public AfterCommitEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
