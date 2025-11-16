package nplus1.fetchjoin;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

/**
 * 결론
 *
 * - 연관 엔티티 N+1 발생 시 FetchJoin과 EntityGraph 사용해서 해결 가능
 * - FetchJoin -> inner join -> 교집합
 * - EntityGraph -> outer join -> 왼쪽 테이블 기준 매칭
 * - 컬렉션 fetch 절대 금지: 메모리 위험
 */
@SpringBootTest
@Transactional
class TeamRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Team team1 = new Team();
        Team team2 = new Team();
        Team team3 = new Team();
        Team team4 = new Team();
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);
        Member member1 = new Member("name1", team1);
        Member member2 = new Member("name2", team2);
        Member member22 = new Member("name22", team2);
        Member member222 = new Member("name222", team2);
        // Member member3 = new Member("name3", team3);
        Member member4 = new Member("name4", team4);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member22);
        memberRepository.save(member222);
        // memberRepository.save(member3);
        memberRepository.save(member4);
        em.flush();
        em.clear();
    }

    /**
     * 조회한 Team의 개수인 N만큼 다시 Member 조회 쿼리가 N번 나가고 있음
     */
    @Test
    void nplus1() {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            System.out.println(team.getId());
            for (Member member : team.getMembers()) {
                member.getName();
            }
        }
    }

    /**
     * 한 번의 쿼리로 Team과 연관된 Member 엔티티까지 조회 -> N+1 해결
     *
     * inner join.
     * team3은 member가 없어서 누락된 것을 확인 -> 만약 누락되면 안되는 경우 사용하면 논리적 오류 발생 가능!
     *
     * 컬렉션 페이징 불가.
     * 이유 -> team.members는 컬렉션. 1:n 관계이므로 카테시안 곱하면 행수 늘어남 -> limit 이상 -> 애플리케이션 단 limit -> 메모리 끝
     */
    @Test
    void fetchJoin() {
        List<Team> teams = teamRepository.findAllWithFetchJoin();
        for (Team team : teams) {
            System.out.println(team.getId());
            for (Member member : team.getMembers()) {
                member.getName();
            }
        }
    }

    /**
     * 한 번의 쿼리로 Team과 연관된 Member 엔티티까지 조회 -> N+1 해결
     * left join
     *
     * 왼쪽 테이블을 중심으로 오른쪽 테이블 매칭시킴.
     * 누락 없음. (오른쪽 null)
     * 컬렉션 아닌 페이징 가능!
     *
     * 컬렉션 페이징 금지.
     * 이유 -> team.members는 컬렉션. 1:n 관계이므로 카테시안 곱하면 행수 늘어남 -> limit 이상 -> 애플리케이션 단 limit -> 메모리 끝
     */
    @Test
    void entityGraph() {
        List<Team> teams = teamRepository.findAllWithEntityGraph();
        for (Team team : teams) {
            System.out.println(team.getId());
            for (Member member : team.getMembers()) {
                member.getName();
            }
        }
    }

    /**
     * 예상!
     * 카테시안 곱으로 아래처럼 행이 늘어날 것이고.
     * team1 | member1
     * team2 | member2
     * team2 | member22
     * team2 | member222
     * team4 | member4
     * 에서 3개만 골라서 team1, team2만 조회될 것이다.
     *
     * 결과!
     * team1, team2, team4 전부 조회됨.
     *
     * 이유!
     * org.hibernate.orm.query -- ip: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
     * 하이버네이트가 컬렉션 fetch join 문제를 고려해서 db 단의 limit를 사용하지 않고, 메모리로 다 fetch 해서 애플리케이션 단에서 limit 함.
     * 실제로 쿼리 보면 limit가 없음
     * 뭐 의도한대로 결과는 잘나오나,,
     * 메모리로 전부 로드하는게 문제. OOM 발생 가능.
     *
     * 만약 컬렉션이 아니라 그냥 1:1 연관 엔티티를 fetch join하는거라면 경고 없이 db 단에서 limit를 한 행을 가져옴.
     */
    @Test
    void cartesianProduct() {
        List<Team> teams = teamRepository.findAllWithFetchJoinPaging(PageRequest.of(0, 3));
        for (Team team : teams) {
            System.out.println(team.getId());
        }
    }

    // TODO
    // 안전하게 컬렉션 N+1 해결하는법
}
