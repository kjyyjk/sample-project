package event.asynctransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class AsyncTransactionCustomEventListener {

    Logger log = LoggerFactory.getLogger(AsyncTransactionCustomEventListener.class);

    @Autowired
    AsyncCustomEntityRepository customEntityRepository;

    /**
     * @param event
     * @Async -> 트랜잭션 비활성화
     * @Async, @EventListener -> 트랜잭션 비활성화됨
     * @Async, @EventListener, @Transactional -> 새로운 트랜잭션 활성화
     * @Async, @TransactionalEventListener(aftercommit) -> 트랜잭션 비활성화, insert 됨.
     * @Async, @TransactionalEventListener(aftercommit), @Transactional ->
     * Caused by: java.lang.IllegalStateException: @TransactionalEventListener method must not be annotated with @Transactional unless when declared as REQUIRES_NEW or NOT_SUPPORTED: public void event.asynctransaction.AsyncTransactionCustomEventListener.onApplicationEventAfterCommit(event.asynctransaction.AfterCommitEvent)
     *
     * @Async, @TransactionalEventListener(aftercommit), @Transactional(requires_new)
     * -> 새로운 트랜잭션 활성화
     * @TransactionalEventListener(aftercommit) -> 기존 트랜잭션 참여, insert 못함
     * @TransactionalEventListener(aftercommit), @Transactional(requires_new) -> 새로운 트랜잭션 활성화
     * =======================
     * 결론
     *
     * 비동기면 트랜잭션 컨텍스트 전파 x.
     *
     * transactional after commit이면 부모 트랜잭션 참여는 되지만 이미 커밋 이후라서 db 작업이 반영되지 않는다.
     * (이미 커밋된 트랜잭션이니.)
     *
     * transactional after commit에서 @Transactional 붙이면 에러 발생함.
     * 부모 트랜잭션이 이미 커밋되었기 때문에 트랜잭션 참여가 불가.

     * 만약 after commit에서 트랜잭션 작업을 하려면 requires_new를 사용해야한다.
     *
     * ===================
     * @Async@TransactionalEventListener(aftercommit)에서 insert가 되는 이유는
     * 트랜잭션 비활성화 -> jparepsoitory.save 자체가 새로운 트랜잭션.
     *
     * @TransactionalEventListener(aftercommit)에서 insert가 안되는 이유는
     * 부모 트랜잭션 참여함 -> but 이미 커밋됨.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // @Transactional //requires_new나 not_supported 아니면 IllegalStateException 에러 발생
    public void onApplicationEventAfterCommit(AfterCommitEvent event) {
        checkTransaction();
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );
        saveCustomEntity();
    }

    private void saveCustomEntity() {
        checkTransaction();
        customEntityRepository.save(new AsyncCustomEntity());
    }

    private void checkTransaction() {
        log.info("트랜잭션명: {}, 트랜잭션 활성 여부: {}",
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive()
        );
    }
}

* 결론
*
* 비동기면 트랜잭션 컨텍스트 전파 x.
*
* transactional after commit이면 부모 트랜잭션 참여는 되지만 이미 커밋 이후라서 db 작업이 반영되지 않는다.
* (이미 커밋된 트랜잭션이니.)
*
* transactional after commit에서 @Transactional 붙이면 에러 발생함.
* 부모 트랜잭션이 이미 커밋되었기 때문에 트랜잭션 참여가 불가.

* 만약 after commit에서 트랜잭션 작업을 하려면 requires_new를 사용해야한다.
*
* ===================
* @Async@TransactionalEventListener(aftercommit)에서 insert가 되는 이유는
* 트랜잭션 비활성화 -> jparepsoitory.save 자체가 새로운 트랜잭션.
*
* @TransactionalEventListener(aftercommit)에서 insert가 안되는 이유는
* 부모 트랜잭션 참여함 -> but 이미 커밋됨.
