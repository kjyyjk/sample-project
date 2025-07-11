package fetchtype;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = DemoApplication.class)
public class FetchTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    AuthorRepository authorRepository;

    /**
     * 상황
     * 여러 Book이 연관되어있는 Author를 조회한 뒤 Author의 books에 접근한다.
     * (Author.books의 fecth 타입을 직접 변경해보기!)
     *
     * 가정
     * fetch 타입이 lazy일 경우 book.getTitle() 시점에 Book에 대한 select 쿼리가 나간다.
     * fetch 타입이 eager일 경우 Author를 조회할 때 Book에 대한 select 쿼리가 나간다.
     *
     * 결론
     * lazy일 경우 필요할 때 로딩한다.
     * eager일 경우 즉시 로딩한다.
     */
    @Test
    void aa() {
        Book book1 = new Book(null, "책1");
        Book book2 = new Book(null, "책2");
        Book book3 = new Book(null, "책3");

        testEntityManager.persist(book1);
        testEntityManager.persist(book2);
        testEntityManager.persist(book3);

        Author author = new Author();
        testEntityManager.persist(author);
        book1.setAuthor(author);
        book2.setAuthor(author);
        book3.setAuthor(author);

        testEntityManager.flush();
        testEntityManager.clear();

        System.out.println("===============================");
        Author findAuthor = authorRepository.findById(author.getId()).get();
        System.out.println("===============================");
        for (Book book : findAuthor.getBooks()) {
            System.out.println(book.getTitle());
        }
        System.out.println("===============================");
    }

    /**
     * 상황
     * n명의 Author를 조회하고 각각의 books에 접근한다.
     *
     * 가정
     * fetch 타입이 lazy일 경우 n번의 select 쿼리가 발생한다. -> n+1 문제 발생.
     * fetch 타입이 eager일 경우 n번의 select 쿼리가 발생한다. -> n+1 문제 발생.
     *
     * 결론
     * fetch 타입이 eager여도 조회의 시점만 다를 뿐, 발생하는 쿼리 수는 동일한 것을 확인할 수 있다.
     * 따라서 eager가 n+1 문제에 대한 적절한 해결 방법이 아님을 알 수 있다.
     */
    @Test
    void aaa() {
        Book book1 = new Book(null, "책1");
        testEntityManager.persist(book1);
        Author author1 = new Author();
        book1.setAuthor(author1);
        testEntityManager.persist(author1);

        Book book2 = new Book(null, "책2");
        Book book3 = new Book(null, "책3");
        testEntityManager.persist(book2);
        testEntityManager.persist(book3);
        Author author2 = new Author();
        book2.setAuthor(author2);
        book3.setAuthor(author2);
        testEntityManager.persist(author2);

        testEntityManager.flush();
        testEntityManager.clear();

        System.out.println("===============================");
        List<Author> all = authorRepository.findAll();
        System.out.println("===============================");
        for (Author author : all) {
            for (Book book : author.getBooks()) {
                book.getTitle();
                System.out.println(book.getTitle());
            }
        }
        System.out.println("===============================");
    }
}
