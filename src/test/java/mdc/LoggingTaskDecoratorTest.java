package mdc;


import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import io.micrometer.observation.Observation;

class LoggingTaskDecoratorTest {

    static Logger log =  LoggerFactory.getLogger(LoggingTaskDecoratorTest.class);

    @Test
    public void test() {
        LoggingTaskDecorator loggingTaskDecorator = new LoggingTaskDecorator();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // threadPoolTaskExecutor.setTaskDecorator(loggingTaskDecorator);

        // 스레드 풀 사이즈를 1로 설정
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(1);

        threadPoolTaskExecutor.initialize();

        threadPoolTaskExecutor.execute(() -> {
            MDC.put("key", "value");
        });

        threadPoolTaskExecutor.execute(() -> {
            String value = MDC.get("key");
            System.out.println(value);
        });
    }

    /**
     * Decorator에서 작업 스레드로 MDC 정보를 복사하면
     * 전파된다.
     */
    @Test
    public void test2() {
        LoggingTaskDecorator loggingTaskDecorator = new LoggingTaskDecorator();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setTaskDecorator(loggingTaskDecorator);
        threadPoolTaskExecutor.initialize();

        MDC.put("key", "value");

        threadPoolTaskExecutor.execute(() -> {
            Runnable runnable = () -> {
                System.out.println(MDC.get("key"));
            };

            threadPoolTaskExecutor.execute(runnable);
        });
    }

    /**
     * 단 Executor로 제출된 작업에만 Decorator가 적용됨.
     * 만약 별도 Thread를 생성해서 실행하면 Decorator가 적용안되고
     * 전파가 되지 않겠지.
     */
    @Test
    public void test3() {
        LoggingTaskDecorator loggingTaskDecorator = new LoggingTaskDecorator();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setTaskDecorator(loggingTaskDecorator);
        threadPoolTaskExecutor.initialize();

        MDC.put("key", "value");

        threadPoolTaskExecutor.execute(() -> {
            Runnable runnable = () -> {
                System.out.println(MDC.get("key"));
            };

            Thread thread = new Thread(runnable);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void test4() {
        LogbackMDCAdapter logbackMDCAdapter = new LogbackMDCAdapter();

        Runnable thread1 = () -> {
            logbackMDCAdapter.put("forgather", "good");
            logbackMDCAdapter.put("leo", "good");
            for (String key : logbackMDCAdapter.getPropertyMap().keySet()) {

            }
        };

        Runnable thread2 = () -> {
            logbackMDCAdapter.put("leo", "great");
        };
    }

    /**
     * 스레드 풀 환경에서
     * MDC 정보가 그대로 남아 있음을 보여줌.
     */
    @Test
    public void threadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        // 스레드 풀 사이즈를 1로 설정
        threadPoolTaskExecutor.setMaxPoolSize(1);
        threadPoolTaskExecutor.initialize();

        threadPoolTaskExecutor.execute(() -> {
            MDC.put("forgather", "great");
            log.info("forgather is {}", MDC.get("forgather"));
        });

        threadPoolTaskExecutor.execute(() -> {
            log.info("forgather is {}", MDC.get("forgather"));
        });
    }

    /**
     * ThreadPoolExecutor 구현체 이용해서 MDC 정보 지우기
     */
    @Test
    void aaa() {
        // 스레드 풀 사이즈를 1로 설정
        MyThreadPoolExecutor threadPoolTaskExecutor = new MyThreadPoolExecutor(
            1,
            1,
            0,
            MINUTES,
            new LinkedBlockingQueue<>()
        );

        threadPoolTaskExecutor.execute(() -> {
            MDC.put("forgather", "great");
            log.info("forgather is {}", MDC.get("forgather"));
        });

        threadPoolTaskExecutor.execute(() -> {
            log.info("forgather is {}", MDC.get("forgather"));
        });
    }
}
