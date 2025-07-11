package primarykeystrategy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class PrimaryKeyStrategyTest {

    @Autowired
    TestEntityManager entityManager;

    /**
     * 상황
     * 기본키 생성 전략이 SEQUENCE인 Person을 persist한 뒤 IDENTITY 전략인 Publisher를 persist한다.
     *
     * 가정
     * Publisher를 persist하면 Person, Publisher 모두 insert 쿼리가 나간다.
     *
     * 결론
     * IDENTITY 전략은 기본 키를 알기 위해 persist와 동시에 flush한다.
     * 이때 JPA는 특정 쿼리만 flush하지 못하고 쓰기 지연 저장소의 모든 변경 사항을 flush한다.
     */
    @Test
    void aaa() {
        Person person = new Person("레오");
        entityManager.persist(person);
        System.out.println("==============================");
        Publisher publisher = new Publisher("오레"); // 생성 전략 IDENTITY
        entityManager.persist(publisher);
        System.out.println("==============================");
    }

    /**
     * 상황
     * 기본키 생성 전략이 SEQUENCE이며 allocationSize가 3인 Person을 여러번 persist한다.
     *
     * 가정
     * 3번의 persist에 대해 1번의 select 쿼리가 나갈 것이다.
     *
     * 결론
     * SEQUENCE 전략은 allocationSize에 맞게 시퀀스 값을 선점하고 조회해와 메모리에 캐싱한다.
     * 따라서 이후에는 select 쿼리를 날리지 않고 메모리에 존재하는 시퀀스 값을 사용한다.
     * (디폴트 50)
     * 이를 통한 성능 개선도 고려해볼 수 있다.
     */
    @Test
    public void test() {
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
        System.out.println(entityManager.persistAndGetId(new Person("hi")));
        System.out.println("=====================================");
    }
}
