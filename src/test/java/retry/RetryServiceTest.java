package retry;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.ExhaustedRetryException;

@SpringBootTest(classes = MainApplication.class)
class RetryServiceTest {

    @Autowired
    private RetryService retryService;

    /**
     * 별도의 설정이 없으면 총 3회 시도(초기 호출, 재시도 포함)하는 것을 확인.
     * RetrySynchronizationManager를 활용해 시도 횟수를 알 수 있음을 확인.
     * 끝까지 성공하지 못하면 ExhaustedRetryException을 던짐을 확인.
     */
    @Test
    void retry1() {
        assertThatCode(() -> retryService.retryDefault())
            .isInstanceOf(ExhaustedRetryException.class);
    }

    /**
     * 지정한 예외에 대해서만 재시도함을 확인.
     */
    @Test
    void retry2() {
        assertThatCode(() -> retryService.retrySpecificException())
            .isInstanceOf(ExhaustedRetryException.class);
    }

    /**
     * `@Retryable`의 속성을 통해 시도 횟수(초기 호출, 재시도 포함), 재시도 딜레이 등을 설정할 수 있음을 확인.
     */
    @Test
    void retry3() {
        assertThatCode(() -> retryService.retryCustom())
            .isInstanceOf(ExhaustedRetryException.class);
    }

    /**
     * `@Recover`를 활용해 재시도 실패 시 복구 작업 수행함을 확인.
     */
    @Test
    void retry4() {
        retryService.retryWithRecover1();
    }

    /**
     * `@Retryable` 메서드의 파라미터를 받을 수 있음을 확인.
     * `@Recover`가 여러개 있어도 재시도 메서드와 동일한 메서드 파라미터를 받는 복구 메서드가 선택됨을 확인.
     */
    @Test
    void retry5() {
        retryService.retryWithRecover2("레오");
    }

    /**
     * `@Recover`가 재시도 메서드 대신 값을 반환할 수 있음을 확인.
     */
    @Test
    void retry6() {
        String crew = "레오";
        String result = retryService.retryWithRecoverWithReturnValue(crew);
        assertThat(result).isEqualTo(crew);
    }
}
