package event.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import event.EventApplication;

@SpringBootTest(classes = EventApplication.class)
class TransactionCustomEventPublisherTest {

    @Autowired
    TransactionCustomEventPublisher transactionCustomEventPublisher;

    /**
     * 이벤트 발생 시각 : 1760407118199, 이벤트 발생지 : TransactionCustomEventPublisher, 이벤트 메시지 : hihi
     * 트랜잭션명: event.transaction.TransactionCustomEventPublisher.publishBeforeCommitEvent, 트랜잭션 활성 여부: true
     * Hibernate: insert into custom_entity (id) values (?)
     * Hibernate: insert into custom_entity (id) values (?)
     * Hibernate: insert into custom_entity (id) values (?)
     */
    /**
     * 트랜잭션이 커밋될 때만 이벤트 처리 (커밋 이전) (여기서 db 작업하면 반영됨) (여기서 예외 발생 시 publisher의 트랜잭션이 롤백된다)
     */
    @Test
    void beforeCommit() {
        transactionCustomEventPublisher.setMessage("hihi");
        transactionCustomEventPublisher.publishBeforeCommitEvent();
    }

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

    /**
     * // 커밋 시
     * Hibernate: insert into custom_entity (id) values (?)
     *
     * // 롤백 시
     * 트랜잭션명: event.transaction.TransactionCustomEventPublisher.publishAfterRollbackEvent, 트랜잭션 활성 여부: true
     * 이벤트 발생 시각 : 1760407861172, 이벤트 발생지 : TransactionCustomEventPublisher, 이벤트 메시지 : hihi
     */
    /**
     * 커밋될 때는 이벤트 처리를 하지 않음.
     * publishAfterRollbackEvent의 마지막 주석을 해제하면 롤백이 발생해 이벤트 처리가 되는 것을 확인할 수 있다.
     *
     * 트랜잭션이 롤백될 때만 이벤트 처리 (롤백 이후) (여기서 db 작업해도 반영 안됨)
     */
    @Test
    void afterRollback() {
        transactionCustomEventPublisher.setMessage("hihi");
        transactionCustomEventPublisher.publishAfterRollbackEvent();
    }

    /**
     * Hibernate: insert into custom_entity (id) values (?)
     * 이벤트 발생 시각 : 1760407337754, 이벤트 발생지 : TransactionCustomEventPublisher, 이벤트 메시지 : hihi
     * 트랜잭션명: event.transaction.TransactionCustomEventPublisher.publishAfterCompletionEvent, 트랜잭션 활성 여부: true
     */
    @Test
    void afterCompletion() {
        transactionCustomEventPublisher.setMessage("hihi");
        transactionCustomEventPublisher.publishAfterCompletionEvent();
    }
}
