package event.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import event.sync.CustomEvent;

@Component
public class AsyncCustomEventPublisher {

    private String message;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish() {
        System.out.println("[Publisher] 이벤트 발행 시작 - Thread: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();

        applicationEventPublisher.publishEvent(new AsyncCustomEvent(this, message));

        long endTime = System.currentTimeMillis();
        System.out.println("[Publisher] 이벤트 발행 완료 - 소요시간: " + (endTime - startTime) + "ms");
        System.out.println("[Publisher] 다음 작업 계속 진행...\n");
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 테스트 실행해보면 같은 클래스 내 이벤트 리스너도 정상 동작함을 확인할 수 있다.
     */
    @EventListener
    @Async
    public void test(AsyncCustomEvent event) {
        System.out.println("[Listener] 같은 클래스에서 이벤트 수신 - Thread: " + Thread.currentThread().getName());
        System.out.println("이벤트 발생 시각 : %s, 이벤트 발생지 : %s, 이벤트 메시지 : %s"
            .formatted(event.getTimestamp(), event.getSource().getClass().getSimpleName(), event.getMessage())
        );

        try {
            System.out.println("[Listener] 무거운 작업 시작 (3초 소요)...");
            Thread.sleep(3000); // 3초 대기 (시간이 오래 걸리는 작업 시뮬레이션)
            System.out.println("[Listener] 무거운 작업 완료!\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
