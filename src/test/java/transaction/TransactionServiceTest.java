package transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * 그래서 self invocation 문제가 발생하는 이유?
 *
 * `@Transactional`은 프록시 기반으로 동작한다.
 * 해당 어노테이션을 가진 클래스에 대한 의존성을 주입할 시 Spring은 트랜잭션 로직을 감싼 프록시 객체를 주입한다.
 * 디버깅 모드로 `@Transactional` 어노테이션 이 포함된 인스턴스를 확인해보면 CGLIB이라는 것을 확인할 수 있다.
 * 외부에서 들어오는 요청에만 프록시가 적용된다.
 * 그렇지 않은 요청은 프록시 객체가 아닌 실제 인스턴스의 메서드를 호출한다.
 * 따라서 외부가 아닌 내부 호출 시 트랜잭션이 적용되지 않는 것!
 *
 * self-invocation에 대해서는 간단하게
 * 1. 자기 자신 주입
 * 2. 클래스 분리
 * 로 해결할 수 있다.
 *
 * 추상적으로 사고해보기
 *
 * 트랜잭션?
 *      반드시 성공/실패 해야하는 DB 작업의 최소 단위.
 * 왜 self invocation을 해결해야하는데?
 *      서로 다른 트랜잭션 경계를 설정하려고 했지만, 그 경계가 무효화 되기 때문이다.
 * 정말 각 작업들이 다른 트랜잭션 경계를 가져야할까?
 *
 * 이런 문제 상황을 마주한 이유는?
 *      각기 다른 트랜잭션 경계를 가진 작업을 하나의 클래스에 정의되어 있기 때문이다.
 * 정말 해당 객체가 모든 책임을 지니는 것이 맞을까? 애초에 책임 분리가 덜 된 것 아닐까?
 *
 * 결국 트랜잭션을 분리한다는 것은 경계를 짓는다는 것이고, 경계가 구분된 로직들은 각각 다른 객체들의 책임으로 볼 수 있지 않을까?
 * 그렇기 때문에 트랜잭션 self invocation 문제를 마주했을 때는 단순히 기술적으로 해결하려고 하기 보다는.
 *  내가 의도한 트랜잭션 경계가 비즈니스 로직적으로 적절한지?
 *  그리고 그 경계/로직들에 대해 적절한 책임 분리되었는지를 생각해 볼 필요가 있다.
 */
@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    /**
     * 상황
     * 트랜잭션 outer에서 자기 자신의 트랜잭션 inner를 호출
     *
     * 가정
     * outer와 inner 모두 같은 트랜잭션에 묶인다.
     *
     * 결론
     * inner가 outer 트랜잭션에 포함된다.
     */
    @Test
    void selfInvocationTransactionalTest() {
        transactionService.aa1();
    }

    /**
     * 상황
     * outer에서 자기 자신의 트랜잭션 inner를 호출
     *
     * 가정
     * outer와 inner 모두 트랜잭션 범위에 포함되지 않는다.
     *
     * 결론
     * 트랜잭션에 포함되지 않은 outer가 호출하는 자기 자신의 작업 전부 트랜잭션에 포함되지 않는다.
     */
    @Test
    void selfInvocationTransactionalTest2() {
        transactionService.aa2();
    }

    /**
     * 상황
     * 트랜잭션 outer에서 자기 자신의 Requires New 트랜잭션 inner를 호출
     *
     * 가정
     * outer와 inner 각각 다른 트랜잭션에 포함된다.
     *
     * 결과
     * outer와 inner는 모두 같은 outer의 트랜잭션에 포함된다.
     *
     * 결론
     * 전파 범위를 Requires New로 설정해도 자기 자신 호출 시 동일한 트랜잭션에 묶인다.
     */
    @Test
    void selfInvocationTransactionalTest3() {
        transactionService.aa3();
    }

    /**
     * 상황
     * 트랜잭션 outer에서 다른 service의 Requires New 트랜잭션 inner를 호출
     *
     * 가정
     * outer와 inner는 각기 다른 트랜잭션에 포함된다.
     *
     * 결론
     * 자기 자신이 아닌 경우 Requires New는 새로운 트랜잭션에 포함된다.
     */
    @Test
    void selfInvocationTransactionalTest4() {
        transactionService.aa4();
    }

    /**
     * 상황
     * 트랜잭션 outer에서 자기 자신의 No 트랜잭션 inner를 호출
     *
     * 가정
     * inner도 outer와 같은 트랜잭션에 묶인다.
     *
     * 결론
     * 트랜잭션 범위에서 실행되는 작업은 모두(별도의 전파범위 설정이 없으면) 트랜잭션에 포함된다.
     */
    @Test
    void selfInvocationTransactionalTest5() {
        transactionService.aa5();
    }

    /**
     * 상황
     * 트랜잭션 outer에서 다른 service의 트랜잭션 inner를 호출
     *
     * 가정
     * inner도 outer와 같은 트랜잭션에 묶인다.
     *
     * 결론
     * 트랜잭션 범위에서 실행되는 작업은 모두(별도의 전파범위 설정이 없으면) 자기 자신 상관 없이 outer의 트랜잭션에 포함된다.
     */
    @Test
    void selfInvocationTransactionalTest6() {
        transactionService.aa6();
    }

    /**
     * 상황
     * 트랜잭션 outer에서 다른 service의 No 트랜잭션 inner를 호출
     *
     * 가정
     * inner도 outer와 같은 트랜잭션에 묶인다.
     *
     * 결론
     * 트랜잭션 범위에서 실행되는 작업은 모두(별도의 전파범위 설정이 없으면) 자기 자신 상관 없이 outer의 트랜잭션에 포함된다.
     */
    @Test
    void selfInvocationTransactionalTest7() {
        transactionService.aa7();
    }
}
