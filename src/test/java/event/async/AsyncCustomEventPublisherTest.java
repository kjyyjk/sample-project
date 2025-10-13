package event.async;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import event.EventApplication;

@SpringBootTest(classes = EventApplication.class)
class AsyncCustomEventPublisherTest {

    @Autowired
    private AsyncCustomEventPublisher asyncCustomEventPublisher;

    @Test
    void aa() {
        asyncCustomEventPublisher.setMessage("hihi");
        asyncCustomEventPublisher.publish();
    }

    @Test
    void aaa() {
        asyncCustomEventPublisher.setMessage("byebye");
        asyncCustomEventPublisher.publish();
    }
}
