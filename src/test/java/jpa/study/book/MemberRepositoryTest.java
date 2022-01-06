package jpa.study.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import jpa.study.book.entity.Member;
import jpa.study.book.entity.Team;
import jpa.study.book.repository.MemberRepository;
import jpa.study.book.repository.NestedClosedProjection;
import jpa.study.book.repository.TeamRepository;
import jpa.study.book.repository.UsernameOnly;

/**
 * Spring Data Jpa 기반 테스트
 */
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	// 실 동작 테스트
	@Test
	void testMember() {
		Member member = new Member("memberB");
		Member savedMember = memberRepository.save(member);
		
		Member findMember = memberRepository.findById(savedMember.getId()).get();
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
				 
		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성보장
	}
	
	/*
	 * JpaRepository 인터페이스는 공통 CRUD 기능을 제공
	 * JpaRepository는 대부분의 공통 메서드를 제공
	 */
	@Test
	public void basicCRUD() {
		Member member1 = new Member("member_sdj_basic1");
		Member member2 = new Member("member_sdj_basic2");
		memberRepository.save(member1);
		memberRepository.save(member2);
		
		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		
		// 리스트 조회 검증
		memberRepository.findAll().forEach(System.out::println);
		
		// 카운트 검증
		long count = memberRepository.count();
		System.out.println("count ==>" + count);
		
		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		long deletedCount = memberRepository.count();
		System.out.println("deletedCount ==>" + deletedCount);
	}
	
	/**
	 * 쿼리 메소드 기능 
	 * 
	 * - 메소드 이름으로 쿼리 생성
	 * - 메소드 이름으로 JPA NamedQuery 호출
	 * - @Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 생성
	 */
	
	/*
	 * [메소드 이름으로 쿼리 생성]
	 * 메소드 이름을 분석해서 JPQL 쿼리 실행
	 * 스프링 데이터 JPA는 메소드 이름을 분석해서 JPQL을 생성하고 실행
	 * 
	 * 참조. 쿼리 메소드 필터 조건
	 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference
	 * 
	 * 참고. 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 됩니다.
	 *      그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생합니다.
	 *      애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점입니다.
	 */
	@Test
	void findByUsernameAndAgeGreaterThan() {
		
		Member m1 = new Member("BBB", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1); 
		memberRepository.save(m2);
		
		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);
		assertThat(result.get(0).getUsername()).isEqualTo("BBB");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}
	
	/*
	 * [JPA NamedQuery]
	 * @NamedQuery 어노테이션으로 Named 쿼리 Entity에 정의
	 * 스프링 데이터 JPA는 선언한 "도메인 클래스 + . + 메서드 이름으로 Named 쿼리를 찾아서 실행
	 * 만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리 생성 전략을 사용한다.
	 * 
	 * 참고. 스프링 데이터 JPA를 사용하면 실무에서 Named Query를 직접 등록해서 사용하는 일은 드물다.
	 * 대신 @Query를 사용해서 레파지토리 메소드에 쿼리를 직접 정의한다.
	 */
	@Test
	void findByUsername() {
		memberRepository.findByUsername("memberA").forEach(System.out::println);
	}
	
	/*
	 * [@Query, repository 메소드에 쿼리 정의]
	 * 
	 * 메서드에 JPQL 쿼리 작성
	 * 실행할 메서드에 정적 쿼리를 직접 작성하므로 이름 없는 Named 쿼리라 할 수 있음
	 * JPA Named 쿼리처럼 애프릴케이션 실행 시점에 문법 오류를 발견할 수 있음
	 * 
	 * 참고. 실무에서도 메소드 이름으로 쿼리 생성 기능을 파라미터가 증가하면서 메서드 이름이 매우 지저분해진다.
	 * 따라서 @Query 기능을 자주 사용하게 된다.
	 */
	@Test
	void findUser() {
		memberRepository.findUser("BBB", 10);
	}
	
	/*
	 * 단순히 값 하나를 조회
	 */
	@Test
	void findUsernameList() {
		memberRepository.findUsernameList().forEach(System.out::println);
	}
	
	/*
	 * DTO로 직접 조회
	 * DTO로 직접 조회 하려면 JPA의 new 명령어를 사용해야 한다.
	 * 생성자가 맞는 DTO가 필요
	 */
	@Test
	void findMemberDto() {
		memberRepository.findMemberDto().forEach(System.out::println);
	}
	
	/**
	 * 파라미터 바인딩
	 * - 위치기반 : select m from Member m where m.username = ?0
	 * - 이름기반 : select m from Member m where m.username = :name
	 */
	
	/*
	 * 컬렉션 파라미터 바인딩
	 * Collection 타입으로 in절 지원
	 */
	@Test
	void findByNames() {
		List<String> names = new ArrayList<>();
		names.add("AAA");
		names.add("BBB");
		
		memberRepository.findByNames(names).forEach(System.out::println);
	}
	
	/**
	 * 반환 타입
	 * 
	 * spring data jpa는 유연한 반환 타입 지원
	 * 
	 * List<Member> findByUsername(String name); 		//컬렉션
	 * Member findByUsername(String name); 				//단건
	 * Optional<Member> findByUsername(String name);    //단건 Optional
	 */
	
	/**
	 * spring data jpa 페이징 및 정렬
	 *
	 *- 파라미터
	 * org.springframework.data.domain.Sort : 정렬 기능
	 * org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
	 * 
	 * - 반환타입
	 * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
	 * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능.(내부적으로 limit + 1조회)
	 * List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
	 */
	
	@Test
	void page() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));
		
		// when
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
		Page<Member> page = memberRepository.findByAge(10, pageRequest);
		
		// then
		List<Member> content = page.getContent(); // 조회된 데이터
		assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
		//assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
		assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
		//assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
		assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
		assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
	}
	
	/*
	 * 벌크성 수정 쿼리
	 * 
	 * 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션 사용
	 * * 사용하지 않으면 org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations Exception 발생
	 * 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트 초기화: @Modifying(clearAutomatically = true) (이 옵션의 기본값은 false )
	 * 이 옵션 없이 회원을 findById 로 다시 조회하면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수있다. 만약 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화 하자
	 */
	@Test
	void bulkUpdate() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));
		
		// when
		int resultCount = memberRepository.bulkAgePlus(20);
		
		// then
		assertThat(resultCount).isEqualTo(3);
	}
	
	// @EntityGraph
	/*
	 * member -> team 지연로딩 관계. team 데이터를 조회할 떄마다 N+1 문제 발생
	 */
	@Test
	void findMemberLazy() throws Exception {
		// given
		// member1 -> teamA
		// member2 -> teamB
		Team teamA = new Team("teamC");
		Team teamB = new Team("teamD");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		memberRepository.save(new Member("member_lazy1", 10, teamA));
		memberRepository.save(new Member("member_lazy2", 20, teamB));
		em.flush();
		em.clear();
		// when
		List<Member> members = memberRepository.findAll();
		// then
		for (Member member : members) {
			//member.getTeam().getName();
		}
	}
	
	@Test
	void findMemberJpqlEntityGraph() {
		memberRepository.findMemberJpqlEntityGraph().forEach(System.out::println);
	}
	
	@Test
	void findEntityGraphByUsername() {
		memberRepository.findEntityGraphByUsername("member1").forEach(System.out::println);
	}
	
	// Auditing
	
	/*
	 * Projections
	 * 
	 * 엔티티 대신에 DTO를 편리하게 조회할 때 사용
	 * 인터페이스 기반 말고도 클래스도 가능
	 */
	@Test
	void findProjectionsByUsername () {
		List<UsernameOnly> usernames = memberRepository.findProjectionsByUsername("member1");
		usernames.forEach(name -> System.out.println(name.getUsername()));
	}
	
	@Test
	void findGenericProjectionsByUsername () {
		List<UsernameOnly> usernames = memberRepository.findProjectionsByUsername("member1", UsernameOnly.class);
		usernames.forEach(name -> System.out.println(name.getUsername()));
	}
	
	@Test
	void findGenericProjections2ByUsername () {
		List<NestedClosedProjection> usernames = memberRepository.findProjectionsByUsername("member1", NestedClosedProjection.class);
		usernames.forEach(name -> System.out.println(name.getUsername()));
	}
	
	// native query
	@Test
	void findByNativeQuery () {
		memberRepository.findByNativeQuery("BBB").forEach(System.out::println);
	}
	
	@Test
	void generatedTypeTest() {
		Team teamA = new Team("teamA");
		Member memberA = new Member("memberA", 30, teamA);
		Member member = memberRepository.save(memberA);
		System.out.println("id = " + member.getId());
	}
	
	@BeforeEach
	void beforeTest() {
		Team teamB = new Team("teamB");
		teamRepository.save(teamB);
	}
	
	@Test
	void generatedTypeBadTest() {
		/*
		 * Team teamB = new Team("teamB"); teamRepository.save(teamB);
		 */
		
		Optional<Team> teamB = teamRepository.findById(1L);
		
		Member memberB = new Member("memberB", 30, teamB.get());
		memberRepository.save(memberB);
		
		Member member = memberRepository.findByTeam(teamB.get());
		System.out.println("id = " + member.getId());
		
		/*
		 * String mid = memberRepository.findMid(teamB.getId());
		 * 
		 * System.out.println("id = " + mid);
		 */
	}
}
