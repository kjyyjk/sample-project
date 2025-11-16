package nplus1.fetchjoin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select t from Team t join fetch t.members")
    List<Team> findAllWithFetchJoin();

    @Query("select t from Team t join fetch t.members")
    List<Team> findAllWithFetchJoinPaging(Pageable pageable);

    @EntityGraph(attributePaths = "members")
    @Query("select t from Team t")
    List<Team> findAllWithEntityGraph();
}
