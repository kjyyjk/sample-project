package event.async;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import event.sync.CustomEvent;

@Component
public class AsyncCustomEventListener {

    @EventListener
    @Async
    public void onApplicationEvent(AsyncCustomEvent event) {
        System.out.println("[Listener] 이벤트 수신 - Thread: " + Thread.currentThread().getName());
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
