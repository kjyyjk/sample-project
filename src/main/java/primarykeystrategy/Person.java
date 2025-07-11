package primarykeystrategy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Person {

    @Id
    @SequenceGenerator(
            name = "pseq",
            sequenceName = "person_seq",
            allocationSize = 3
    )
    @GeneratedValue(generator = "pseq")
    private Long id;

    public String name;

    public Person() {

    }

    public Person(String name) {
        this.id = null;
        this.name = name;
    }


    public Long getId() {
        return id;
    }
}
