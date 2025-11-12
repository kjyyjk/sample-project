package event.asynctransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import event.EventApplication;

@SpringBootTest(classes = EventApplication.class)
class AsyncTransactionCustomEventListenerTest {

    @Autowired
    AsyncTransactionCustomEventPublisher transactionCustomEventPublisher;

    /**
     * Hibernate: insert into custom_entity (id) values (?)
     * 이벤트 발생 시각 : 1760407025556, 이벤트 발생지 : TransactionCustomEventPublisher, 이벤트 메시지 : hihi
     * 트랜잭션명: event.transaction.TransactionCustomEventPublisher.publishAfterCommitEvent, 트랜잭션 활성 여부: true
     */
    /**
     * 트랜잭션이 커밋될 때만 이벤트 처리 (커밋 이후) (여기서 db 작업해도 반영 안됨) (여기서 예외 발생 시 당연히 publisher의 트랜잭션이 롤백되지 않는다)
     */
    @Test
    void afterCommit() {
        transactionCustomEventPublisher.setMessage("hihi");
        transactionCustomEventPublisher.publishAfterCommitEvent();
    }
}
