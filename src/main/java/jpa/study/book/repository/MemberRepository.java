package jpa.study.book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jpa.study.book.dto.MemberDto;
import jpa.study.book.entity.Member;
/**
 * springdatajpa repository
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	//@EntityGraph("MemberWithTeam")
	List<Member> findAll();
	
	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
	
	@Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);
	
	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);
	
	@Query("select m.username from Member m")
	List<String> findUsernameList();
	
	@Query("select new jpa.study.book.dto.MemberDto(m.id, m.username, t.name)" + "from Member m join m.team t")
	List<MemberDto> findMemberDto();
	
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") List<String> names);
	
	Page<Member> findByAge(int age, Pageable pageable);
	
	// count 쿼리를 다음과 같이 분리할 수 있음
	@Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
	Page<Member> findMemberAllCountBy(Pageable pageable);

	@Modifying
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);
	
	//JPQL + 엔티티 그래프
	@EntityGraph(attributePaths = {"team"})
	@Query("select m from Member m")
	List<Member> findMemberJpqlEntityGraph();
	
	//메서드 이름으로 쿼리에서 특히 편리하다.
	@EntityGraph(attributePaths = {"team"})
	List<Member> findEntityGraphByUsername(@Param("username") String username);
	
	//인터페이스 기반 projection
	List<UsernameOnly> findProjectionsByUsername(String username);
	
	//동적 projection
	<T> List<T> findProjectionsByUsername(String username, Class<T> type);
	
	// native query
	@Query(value = "select * from member where username = ?", nativeQuery = true)
	Member findByNativeQuery(String username);

}
