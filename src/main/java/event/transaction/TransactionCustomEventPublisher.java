package event.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.transaction.Transactional;

@Component
public class TransactionCustomEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CustomEntityRepository customEntityRepository;

    private String message;

    @Transactional
    public void publishBeforeCommitEvent() {
        System.out.println("트랜잭션 이름 : %s, 트랜잭션 활성화 여부 : %s".formatted(
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive())
        );
        System.out.println("[Publisher] 이벤트 발행 시작 - Thread: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        customEntityRepository.save(new CustomEntity());
        applicationEventPublisher.publishEvent(new BeforeCommitEvent(this, message));

        long endTime = System.currentTimeMillis();
        System.out.println("[Publisher] 이벤트 발행 완료 - 소요시간: " + (endTime - startTime) + "ms");
        System.out.println("[Publisher] 다음 작업 계속 진행...\n");
    }

    @Transactional
    public void publishAfterCommitEvent() {
        System.out.println("트랜잭션 이름 : %s, 트랜잭션 활성화 여부 : %s".formatted(
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive())
        );
        System.out.println("[Publisher] 이벤트 발행 시작 - Thread: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        customEntityRepository.save(new CustomEntity());
        applicationEventPublisher.publishEvent(new AfterCommitEvent(this, message));

        long endTime = System.currentTimeMillis();
        System.out.println("[Publisher] 이벤트 발행 완료 - 소요시간: " + (endTime - startTime) + "ms");
        System.out.println("[Publisher] 다음 작업 계속 진행...\n");
    }

    @Transactional
    public void publishAfterRollbackEvent() {
        System.out.println("트랜잭션 이름 : %s, 트랜잭션 활성화 여부 : %s".formatted(
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive())
        );
        System.out.println("[Publisher] 이벤트 발행 시작 - Thread: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        customEntityRepository.save(new CustomEntity());
        applicationEventPublisher.publishEvent(new AfterRollbackEvent(this, message));

        long endTime = System.currentTimeMillis();
        System.out.println("[Publisher] 이벤트 발행 완료 - 소요시간: " + (endTime - startTime) + "ms");
        System.out.println("[Publisher] 다음 작업 계속 진행...\n");

        // throw new IllegalArgumentException("");
    }

    @Transactional
    public void publishAfterCompletionEvent() {
        System.out.println("트랜잭션 이름 : %s, 트랜잭션 활성화 여부 : %s".formatted(
            TransactionSynchronizationManager.getCurrentTransactionName(),
            TransactionSynchronizationManager.isActualTransactionActive())
        );
        System.out.println("[Publisher] 이벤트 발행 시작 - Thread: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        customEntityRepository.save(new CustomEntity());
        applicationEventPublisher.publishEvent(new AfterCompletionEvent(this, message));

        long endTime = System.currentTimeMillis();
        System.out.println("[Publisher] 이벤트 발행 완료 - 소요시간: " + (endTime - startTime) + "ms");
        System.out.println("[Publisher] 다음 작업 계속 진행...\n");
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
