package event.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionCustomEventListener {

    Logger log = LoggerFactory.getLogger(TransactionCustomEventListener.class);

    @Autowired
    CustomEntityRepository customEntityRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onApplicationEventBeforeCommit(BeforeCommitEvent event) {
        checkTransaction();
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        customEntityRepository.save(new CustomEntity());
        customEntityRepository.save(new CustomEntity());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationEventAfterCommit(AfterCommitEvent event) {
        checkTransaction();
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        customEntityRepository.save(new CustomEntity());
        customEntityRepository.save(new CustomEntity());

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onApplicationEventAfterRollback(AfterRollbackEvent event) {
        checkTransaction();
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        customEntityRepository.save(new CustomEntity());
        customEntityRepository.save(new CustomEntity());

        throw new IllegalArgumentException("ㅁㄴㅇㄹ"); // 롤백 확인용
    }

    // 트랜잭션이 완료(커밋이나 롤백이나)될 때 이벤트 처리 (커밋 이후) (여기서 db 작업해도 반영 안됨)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void onApplicationEventAfterCompletion(AfterCompletionEvent event) {
        checkTransaction();
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        customEntityRepository.save(new CustomEntity());
        customEntityRepository.save(new CustomEntity());
    }

    private void checkTransaction() {
        log.info("트랜잭션명: {}, 트랜잭션 활성 여부: {}",
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive()
        );
    }

    private void checkThread() {
        System.out.println("[Listener] 이벤트 수신 - Thread: " + Thread.currentThread().getName());
    }
}
