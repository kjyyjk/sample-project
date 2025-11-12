package event.asynctransaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AsyncCustomEntity {

    @Id
    @GeneratedValue
    private long id;
}
