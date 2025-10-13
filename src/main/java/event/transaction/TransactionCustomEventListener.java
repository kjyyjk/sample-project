package event.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionCustomEventListener {

    @Autowired
    CustomEntityRepository customEntityRepository;

    /**
     * 트랜잭션이 커밋, 혹은 롤백 될때 (혹은 상관없이 완료될때) 수행
     */
    // 트랜잭션이 커밋될 때만 이벤트 처리 (커밋 이전) (여기서 db 작업하면 반영됨) (여기서 예외 발생 시 publisher의 트랜잭션이 롤백된다)
    // @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)

    // 트랜잭션이 커밋될 때만 이벤트 처리 (커밋 이후) (여기서 db 작업해도 반영 안됨) (여기서 예외 발생 시 당연히 publisher의 트랜잭션이 롤백되지 않는다)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)

    // 트랜잭션이 롤백될 때만 이벤트 처리 (롤백 이후) (여기서 db 작업해도 반영 안됨)
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)

    // 트랜잭션이 완료(커밋이나 롤백이나)될 때 이벤트 처리 (커밋 이후) (여기서 db 작업해도 반영 안됨)
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void onApplicationEvent(TransactionCustomEvent event) {
        System.out.println("트랜잭션 이름 : %s, 트랜잭션 활성화 여부 : %s".formatted(
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive())
        );
        System.out.println("[Listener] 이벤트 수신 - Thread: " + Thread.currentThread().getName());
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        customEntityRepository.save(new CustomEntity());
        customEntityRepository.save(new CustomEntity());

        try {
            System.out.println("[Listener] 무거운 작업 시작 (3초 소요)...");
            Thread.sleep(3000); // 3초 대기 (시간이 오래 걸리는 작업 시뮬레이션)
            System.out.println("[Listener] 무거운 작업 완료!\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(""); // 롤백 확인용
    }
}
