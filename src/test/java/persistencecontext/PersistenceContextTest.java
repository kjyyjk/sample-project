package persistencecontext;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = DemoApplication.class)
public class PersistenceContextTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void persist() {
        Customer customer = new Customer("first name", "last name");
        assertThat(entityManager.contains(customer)).isFalse(); // 영속성 컨텍스트가 관리하는 객체인지.(영속 상태인지)

        System.out.println("===========================");
        entityManager.persist(customer); // insert 쿼리가 나가지 않을 것임. select이 나가는 것은 sequence 때문.
        System.out.println("===========================");
        assertThat(entityManager.contains(customer)).isTrue(); // 영속 상태인지.

        String sql = "select * from customer";
        List<Customer> customers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));
        assertThat(customers).hasSize(0); // 쿼리가 나가지 않았기 때문에 아무것도 없다.
    }

    @Test
    void flush() {
        Customer customer = new Customer("first name", "last name");

        System.out.println("===========================");
        entityManager.persist(customer); // 비영속 -> 영속 (영속성 컨텍스트가 객체를 관리할뿐 DB에 반영하지 않는다.)
        System.out.println("===========================");
        assertThat(entityManager.contains(customer)).isTrue();

        String sql = "select * from customer";
        List<Customer> customers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));
        assertThat(customers).hasSize(0);

        System.out.println("===========================");
        entityManager.flush(); // 영속성 컨텍스트의 변경 내용을 DB에 반영한다. INSERT 쿼리가 나가는 모습.
        System.out.println("===========================");
        assertThat(entityManager.contains(customer)).isTrue();

        List<Customer> afterCustomers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));
        assertThat(afterCustomers).hasSize(1);
    }

    @Test
    void detach() {
        Customer customer = new Customer("first name", "last name");
        entityManager.persist(customer); // 비영속 -> 영속

        System.out.println("================================");
        entityManager.find(Customer.class, customer.getId()); // 영속성 컨텍스트에서 관리하기 때문에 select 쿼리가 나가지 않고 영속성 컨텍스트에서 조회한다.
        System.out.println("================================");

        assertThat(entityManager.contains(customer)).isTrue();

        /**
         * 준영속과 비영속의 차이는?
         * 비영속은 식별자도 가지지 않으며 영속성 컨텍스트에서 관리하지 않는 객체 상태
         * 준영속은 식별자를 가지지만 영속성 컨텍스트에서 관리하지 않는 객체 상태
         */
        entityManager.detach(customer); // 영속 -> 준영속
        assertThat(entityManager.contains(customer)).isFalse(); // 준영속이기 때문에 영속성

        System.out.println("================================");
        entityManager.find(Customer.class, customer.getId()); // 영속성 컨텍스트에서 관리하지 않기 때문에 select 쿼리를 보내 DB에서 조회한다.
        System.out.println("================================");
    }

    @Nested
    class NestedTest {
        @Test
        void detachAgainstPersist() {
            Customer customer = new Customer("first name", "last name");
            entityManager.persist(customer);
            entityManager.flush();

            entityManager.detach(customer); // <-- 준영속 상태로 변경
            customer.updateName("new first name", "new last name");
            System.out.println("================================");
            entityManager.flush(); // 영속성 컨텍스트에서 관리하지 않기 때문에 변경된 상태가 반영되지 않는다.
            System.out.println("================================");

            String sql = "select * from customer";
            List<Customer> customers = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    ));

            assertThat(customers.get(0).getFirstName()).isEqualTo("first name");
        }

        /**
         * 김영한 JPA 104p
         * 변경 감지는 다음 과정으로 이뤄짐.
         * - 트랜잭션 커밋 시 flush 호출
         * - flush 호출 시 1차 캐시 내부에서 엔티티의 상태를 스냅샷과 비교
         * - 변경된 엔티티가 있으면 UPDATE 쿼리 생성해서 쓰기 지연 저장소에 보관
         * - 쓰기 지연 저장소의 SQL을 전부 DB 반영
         * - 트랜잭션 커밋
         */
        @Test
        void persistAgainstDetach() {
            Customer customer = new Customer("first name", "last name");
            entityManager.persist(customer);
            entityManager.flush();

            customer.updateName("new first name", "new last name");
            System.out.println("================================");
            entityManager.flush(); // 영속성 상태에서 관리하는 객체이기 때문에 변경된 상태가 DB로 반영된다.
            System.out.println("================================");

            String sql = "select * from customer";
            List<Customer> customers = jdbcTemplate.query(sql,
                    (rs, rowNum) -> new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    ));

            assertThat(customers.get(0).getFirstName()).isEqualTo("new first name");
        }

        /**
         * 김영한 JPA 105p ~ 106p
         * JPA 기본 전략처럼 모든 필드를 업데이트 하는 경우의 장점
         *  - 수정쿼리가 항상 같아서 쿼리 재사용 가능
         *  - 데이터베이스는 같은 쿼리에 대해서 파싱된 쿼리를 재사용 가능
         *  -
         *
         *  DynamicUpdate로 변경된 필드만 업데이트 하는 경우의 장점
         *  - 상황에 따라 다르지만 컬럼이 대략 30개 이상 정도되면 변경된 컬럼만 변경하는 것이 더 빠르다고 한다.
         *  하지만 컬럼이 30개 이상인 경우 애초에 설계가 잘못된게 아닌지...
         *
         *  정배는 우선 기본 전략 사용하다가 성능 최적화가 필요하다면 그때 성능 개선을 체크하고 DynamicUpdate를 적용하면 된다.
         */
        @Test
        void updateOnlyChangedState() {
            Customer customer = new Customer("first name", "last name");
            entityManager.persist(customer);
            entityManager.flush();

            customer.updateFirstName("new first name");
            System.out.println("================================");
            entityManager.flush(); // JPA는 기본적으로 변경된 상태가 아닌, 모든 상태를 update한다.(변경된 상태만 update하고 싶은 경우 @DynamicUpdate)
            System.out.println("================================");
        }
    }

    @Test
    void merge() {
        Customer customer = new Customer("first name", "last name");
        entityManager.persist(customer);
        entityManager.flush();

        entityManager.detach(customer); // <-- 준영속 상태로 변경
        customer.updateName("new first name", "new last name");
        System.out.println("================================");
        /**
         * merge할때 select 쿼리가 나간다.
         * 왜?
         * 준영속 상태는 식별자를 가지지만 영속성 컨텍스트에서 관리하지 않는 객체.
         * 준영속 상태를 merge하면 식별자로 DB에서 조회해와 영속성 컨텍스트에서 관리?
         * 만약 준영속 상태가 DB와 상태가 다르다면 어떻게 될까? -> 현재 테스트가 그런 환경.
         *
         * 그럼 왜 select 쿼리를 날리는거지? 그냥 준영속 상태를 그대로 영속성 컨텍스트에서 관리하면 되는거아닌가?
         *
         * 정답은 준영속 상태를 영속 상태로 만드는 동작 과정에 있다.
         * 내가 말한대로 준영속 상태를 그대로 영속 상태로 만드는 것이 아닌.
         * 준영속 상태의 식별자를 가지고 DB에서 조회해와 영속성 객체를 새로 만든다.
         * 이후 준영속 객체의 상태를 새로 만든 영속성 객체의 상태로 복사한다.
         * 즉 새로 영속성 객체를 만들고 준영속 상태를 복사하는것이고, 새로 영속성 객체를 만들기 위해 select 쿼리가 발생하는 것.
         */
        entityManager.merge(customer); // <-- 다시 영속 상태로 변경
        System.out.println("================================");
        entityManager.flush(); // 영속성 컨텍스트가 관리하는 객체 DB 반영
        System.out.println("================================");

        String sql = "select * from customer";
        List<Customer> customers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        assertThat(customers.get(0).getFirstName()).isEqualTo("new first name");
    }

    @Test
    void merge1() {
        Customer customer = new Customer("first name", "last name");
        entityManager.persist(customer);

        entityManager.detach(customer); // <-- 준영속 상태로 변경
        customer.updateName("new first name", "new last name");
        System.out.println("================================");
        entityManager.merge(customer); // <-- 다시 영속 상태로 변경
        System.out.println("================================");
    }

    @Test
    void remove() {
        Customer customer = new Customer("first name", "last name");
        entityManager.persist(customer); // 비영속 -> 영속
        entityManager.flush(); // DB 반영

        String sql = "select * from customer";
        List<Customer> customers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        assertThat(customers.size()).isEqualTo(1);

        System.out.println("================================");
        entityManager.remove(customer); // 영속 -> 비영속 (영속성 컨텍스트 내에서만 삭제되므로 delete 쿼리가 나가지 않는다.)
        System.out.println("================================");

        List<Customer> afterRemoveCustomers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        assertThat(afterRemoveCustomers.size()).isEqualTo(1);

        System.out.println("================================");
        entityManager.flush(); // DB 반영
        System.out.println("================================");

        List<Customer> afterRemoveAndFlushCustomers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        assertThat(afterRemoveAndFlushCustomers.size()).isEqualTo(0);
    }

    @Test
    void removeAndFind() {
        Customer customer = new Customer("first name", "last name");
        entityManager.persist(customer); // 비영속 -> 영속
        entityManager.flush(); // DB 반영

        System.out.println("================================");
        entityManager.remove(customer); // 영속 -> 비영속 (영속성 컨텍스트 내에서만 삭제되므로 delete 쿼리가 나가지 않는다.)
        System.out.println("================================");

        /**
         * 여기서 다시 조회해온 객체가 영속성 컨텍스트에서 관리되는가?
         * 조회 쿼리가 나가지 않는다. 왜?
         *
         * 1차 캐시에 삭제 예정 상태로 등록되어 있기 때문에 SELECT 쿼리가 나가지 않는다.
         * `삭제`도 하나의 엔티티 상태다! (비영속, 영속, 준영속, 삭제)
         */
        System.out.println("================================");
        entityManager.find(Customer.class, customer.getId());
        System.out.println("================================");

        System.out.println("================================");
        entityManager.flush(); // 삭제 예정 상태가 DB에 반영된다.
        System.out.println("================================");

        String sql = "select * from customer";
        List<Customer> afterRemoveAndFlushCustomers = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                ));

        assertThat(afterRemoveAndFlushCustomers.size()).isEqualTo(0);
    }
}
