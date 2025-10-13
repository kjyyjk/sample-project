package event.sync;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import event.EventApplication;

@SpringBootTest(classes = EventApplication.class)
class CustomEventPublisherTest {

    @Autowired
    private CustomEventPublisher customEventPublisher;

    @Test
    void aa() {
        customEventPublisher.setMessage("hihi");
        customEventPublisher.publish();
    }

    @Test
    void aaa() {
        customEventPublisher.setMessage("byebye");
        customEventPublisher.publish();
    }
}
