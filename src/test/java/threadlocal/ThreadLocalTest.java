package threadlocal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import ch.qos.logback.classic.util.LogbackMDCAdapter;

public class ThreadLocalTest {

    @Test
    void aa() {
        ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 1);

        // 첫 번째 스레드
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1 initial value: " + threadLocalValue.get());
            threadLocalValue.set(100);
            System.out.println("Thread 1 updated value: " + threadLocalValue.get());
        });

        // 두 번째 스레드
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2 initial value: " + threadLocalValue.get());
            threadLocalValue.set(200);
            System.out.println("Thread 2 updated value: " + threadLocalValue.get());
        });

        // 스레드를 시작합니다.
        thread1.start();
        thread2.start();

        // 메인 스레드에서 값을 확인합니다.
        System.out.println("Main thread value: " + threadLocalValue.get());
    }

    @Test
    void aaa() throws InterruptedException {
        // 하나의 Adapter 인스턴스
        LogbackMDCAdapter adapter = new LogbackMDCAdapter();

        Thread thread1 = new Thread(() -> {
            adapter.put("userId", "user123"); // adapter#put은 ThreadLocal에 저장함.
            adapter.put("requestId", "req123");
            System.out.println("Thread1: " + adapter.get("userId")); // user123
        });

        Thread thread2 = new Thread(() -> {
            adapter.put("userId", "user456");
            adapter.put("requestId", "req456");
            System.out.println("Thread2: " + adapter.get("userId")); // user456
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // 메인 스레드에서는?
        System.out.println("Main: " + adapter.get("userId")); // null (별도 스레드)
    }

    /**
     * MyClass 내부의 threadLocalMap은 ThreadLocal 타입 변수다.
     * Thread를 기반으로 동작한다.
     *
     * MDC도 같은 원리.
     * MDC.put을 하면 LogbackMDCAdapter.put() 호출.
     * LogbackMDCAdapter는 readWriteThreadLocalMap를 가지고 있음.
     * readWriteThreadLocalMap는 ThreadLocal 변수임.
     * ThreadLocal의 값은 컨텍스트 정보를 저장하는 Map.
     * 이를 통해 MDC는 각 스레드 별로 독립적으로 동작한다.
     */
    @Test
    void myClass() throws InterruptedException {
        MyClass myClass = new MyClass();

        myClass.put("key1", "val1");
        String result1 = myClass.get("key1");
        Assertions.assertThat(result1).isEqualTo("val1");

        Thread thread1 = new Thread(() -> {
            String result2 = myClass.get("key1");
            Assertions.assertThat(result2).isNull();
        });

        Thread thread2 = new Thread(() -> {
            myClass.put("key2", "val2");
            String val2 = myClass.get("key2");
            Assertions.assertThat(val2).isEqualTo("val2");
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        String val3 = myClass.get("key2");
        Assertions.assertThat(val3).isNull();
    }

    /**
     * ThreadPool에 스레드 한 개만 보관하고 재사용.
     *
     * 첫번째 execute에서 컨텍스트 정보를
     */
    @Test
    void thredLocalInThreadPool() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        MyClass myClass = new MyClass();

        Future<?> future1 = executorService.submit(() -> {
            myClass.put("hi", "hello");
            // myClass.clear(); // 주석 여부에 따라 테스트가 성공/실패함
        });
        future1.get();

        Future<?> future2 = executorService.submit(() -> {
            Assertions.assertThat(myClass.get("hi")).isNull();
        });
        future2.get();
    }
}
