package persistencecontext;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Modifying
    List<Customer> findByLastName(String lastName);
}
