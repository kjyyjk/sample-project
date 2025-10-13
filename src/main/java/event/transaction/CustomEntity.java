package event.transaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class CustomEntity {

    @Id
    @GeneratedValue
    private long id;
}
