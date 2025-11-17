package nplus1;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    // @BatchSize(size = 100)
    private List<Member> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public List<Member> getMembers() {
        return members;
    }
}
