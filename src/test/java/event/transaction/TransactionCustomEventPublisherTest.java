package event.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

import event.EventApplication;

@SpringBootTest(classes = EventApplication.class)
class TransactionCustomEventPublisherTest {

    @Autowired
    TransactionCustomEventPublisher transactionCustomEventPublisher;

    @Test
    void aa() {
        transactionCustomEventPublisher.setMessage("hihi");
        transactionCustomEventPublisher.publish();
    }
}
