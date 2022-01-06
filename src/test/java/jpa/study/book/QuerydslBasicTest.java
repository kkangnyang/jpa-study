package jpa.study.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jpa.study.book.dto.MemberDto;
import jpa.study.book.dto.MemberQueryDslDTO;
import jpa.study.book.dto.QMemberQueryDslDTO;
import jpa.study.book.dto.UserDto;
import jpa.study.book.entity.Member;
import jpa.study.book.entity.QMember;
import jpa.study.book.entity.QTeam;
import static com.querydsl.jpa.JPAExpressions.select;

@SpringBootTest
@Transactional
@Commit
class QuerydslBasicTest {
	
	@Autowired
	EntityManager em;
	
	JPAQueryFactory queryFactory;
	
	// 테스트 메서드 실행 이전에 수행된다.
	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(em);
	}
	
	/**
	 * QueryDSL
	 * SQL, JPQL을 코드로 작성할 수 있도록 도와주는 빌더 API
	 * 
	 * 
	 */

	@Test
	void startJPQL() {
		//member1을 찾아라.
		String qlString = 
				"select m from Member m " + 
				"where m.username =:username";
		Member findMember = em.createQuery(qlString, Member.class)
				.setParameter("username", "member1")
				.getSingleResult();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
			
	}
	
	@Test
	void startQuerydsl() {
		//QMember m = new QMember("m");	// 별칭
		//QMember m = QMember.member;
		
		// jdbc에 있는 preparestatement에 parameter binding 방식을 사용하여
		// .where(m.username.eq("member1"))
		// 위 처럼 where 조건을 사용할 수 있다.
		// 파라미터에 문자 더하기나 이런게 들어가면 sql injection 공격을 받을 수 있는데
		// 이런걸 방지할 수 있으서 좋다.
		// 파라미터 바인딩 처리
		Member findMember = queryFactory
			.select(QMember.member)
			.from(QMember.member)
			.where(QMember.member.username.eq("member1")) 
			.fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
		
	}
	
	@Test
	void search() {
		Member findMember = queryFactory
			.selectFrom(QMember.member)
			.where(QMember.member.username.eq("member1")
					.and(QMember.member.age.eq(10)))
			.fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	@Test
	void searchAndParam() {
		Member findMember = queryFactory
			.selectFrom(QMember.member)
			.where(
					QMember.member.username.eq("member1"),
					QMember.member.age.eq(10)
			)
			.fetchOne();
		
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	/** JPQL이 제공하는 모든 검색 조건 제공
	    member.username.eq("member1") // username = 'member1'
		member.username.ne("member1") //username != 'member1'
		member.username.eq("member1").not() // username != 'member1'
		member.username.isNotNull() //이름이 is not null
		member.age.in(10, 20) // age in (10,20)
		member.age.notIn(10, 20) // age not in (10, 20)
		member.age.between(10,30) //between 10, 30
		member.age.goe(30) // age >= 30
		member.age.gt(30) // age > 30
		member.age.loe(30) // age <= 30
		member.age.lt(30) // age < 30
		member.username.like("member%") //like 검색
		member.username.contains("member") // like ‘%member%’ 검색
		member.username.startsWith("member") //like ‘member%’ 검색
	 */
	
	@Test
	void resultFetch() {
		/*
		 * List<Member> fetch = queryFactory .selectFrom(QMember.member) .fetch();
		 * 
		 * Member fetchOne = queryFactory .selectFrom(QMember.member) .fetchOne();
		 * 
		 * Member fetchFirst = queryFactory .selectFrom(QMember.member) .fetchFirst();
		 */
		
		/*
		 * QueryResults<Member> results = queryFactory .selectFrom(QMember.member)
		 * .fetchResults();
		 * 
		 * results.getTotal(); List<Member> content = results.getResults();
		 */
		
		long total = queryFactory
				.selectFrom(QMember.member)
				.fetchCount();
		
	}
	
	/**
	 * 회원 정렬 순서
	 * 1. 회원 나이 내림차순(desc)
	 * 2. 회원 이름 오름차순(asc)
	 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
	 */
	@Test
	void sort() {
		em.persist(new Member(null, 100));
		em.persist(new Member("member5", 100));
		em.persist(new Member("member6", 100));
		
		List<Member> result = queryFactory
				.selectFrom(QMember.member)
				.where(QMember.member.age.eq(100))
				.orderBy(QMember.member.age.desc(), QMember.member.username.asc().nullsLast())
				.fetch();
		
		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(2);
		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}
	
	@Test
	void paging1() {
		List<Member> result = queryFactory
			.selectFrom(QMember.member)
			.orderBy(QMember.member.username.desc())
			.offset(1) //0부터 시작(zero index)
			.limit(2) //최대 2건 조회
			.fetch();
		
		assertThat(result.size()).isEqualTo(2);
		
	}
	
	@Test
	void paging2() {
		QueryResults<Member> queryResults = queryFactory
			.selectFrom(QMember.member)
			.orderBy(QMember.member.username.desc())
			.offset(1) //0부터 시작(zero index)
			.limit(2) //최대 2건 조회
			.fetchResults();
		
		assertThat(queryResults.getTotal()).isEqualTo(4);
		assertThat(queryResults.getLimit()).isEqualTo(2);
		assertThat(queryResults.getOffset()).isEqualTo(1);
		assertThat(queryResults.getResults().size()).isEqualTo(2);
		
	}
	
	@Test
	void aggregation() {
		QMember member = QMember.member;
		List<Tuple> result = queryFactory
			.select(
					member.count(),
					member.age.sum(),
					member.age.avg(),
					member.age.max(),
					member.age.min()
			)
			.from(member)
			.fetch();
		
		Tuple tuple = result.get(0);
		assertThat(tuple.get(member.count())).isEqualTo(4);
		assertThat(tuple.get(member.age.sum())).isEqualTo(100);
		assertThat(tuple.get(member.age.avg())).isEqualTo(25);
		assertThat(tuple.get(member.age.max())).isEqualTo(40);
		assertThat(tuple.get(member.age.min())).isEqualTo(10);
	}
	
	@Test
	void group() throws Exception {
		List<Tuple> result = queryFactory
			.select(QTeam.team.name, QMember.member.age.avg())
			.from(QMember.member)
			.join(QMember.member.team, QTeam.team)
			.groupBy(QTeam.team)
			.fetch();
		
		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);
		
		assertThat(teamA.get(QTeam.team.name)).isEqualTo("teamA");
		assertThat(teamA.get(QMember.member.age.avg())).isEqualTo(15);
		assertThat(teamB.get(QTeam.team.name)).isEqualTo("teamB");
		assertThat(teamB.get(QMember.member.age.avg())).isEqualTo(35);
	}
	
	/**
	 * 팀 A에 소속된 모든 회원
	 */
	@Test
	void join() {
		List<Member> result = queryFactory
			.selectFrom(QMember.member)
			.join(QMember.member.team, QTeam.team)
			.where(QTeam.team.name.eq("teamA"))
			.fetch();
		
		assertThat(result)
			.extracting("username")
			.containsExactly("member1", "member2");
	}
	
	/**
	 * 세타 조인
	 * 회원의 이름이 팀 이름과 같은 회원 조회
	 */
	@Test
	void theta_join() {
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));
		em.persist(new Member("teamC"));
		
		List<Member> result = queryFactory
			.select(QMember.member)
			.from(QMember.member, QTeam.team)
			.where(QMember.member.username.eq(QTeam.team.name))
			.fetch();
		
		assertThat(result)
			.extracting("username")
			.containsExactly("teamA", "teamB");
	}
	
	/**
	 * ex) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
	 * JPQL : select m, t from Member m left join m.team on t.name = 'teamA'
	 */
	@Test
	void join_on_filtering() {
		List<Tuple> result = queryFactory
			.select(QMember.member, QTeam.team)
			.from(QMember.member)
			.leftJoin(QMember.member.team, QTeam.team)
			.on(QTeam.team.name.eq("teamA"))
			.fetch();
		
		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}
	
	/**
	 * 연관관계 없는 엔티티 외부 조인
	 * 회원의 이름이 팀 이름과 같은 대상 회원 조회
	 * 
	 * 일반조인 : leftJoin(member.team, team) => pk까지 같이 조회
	 * on조인 : from(member).leftJoin(team).on(xxx)
	 */
	@Test
	void join_on_no_relation() {
		
		List<Tuple> result = queryFactory
			.select(QMember.member, QTeam.team)
			.from(QMember.member)
			.leftJoin(QTeam.team).on(QMember.member.username.eq(QTeam.team.name))
			.fetch();
		
		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}
	
	/**
	 * jpa fetch join 설명 아래 링크 참조
	 * https://cobbybb.tistory.com/18
	 */
	
	@PersistenceUnit
	EntityManagerFactory emf;
	
	@Test
	void fetchJoinNo() {
		em.flush();
		em.clear();
		
		Member findMember = queryFactory
			.selectFrom(QMember.member)
			.where(QMember.member.username.eq("member1"))
			.fetchOne();
		
		// 로딩 된 entity인지, 초기화 안된 entity인지 알려줌
		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).as("페치 조인 미적용").isFalse();
		
	}
	
	@Test
	void fetchJoinUse() {
		em.flush();
		em.clear();
		
		Member findMember = queryFactory
			.selectFrom(QMember.member)
			.join(QMember.member.team, QTeam.team).fetchJoin()
			.where(QMember.member.username.eq("member1"))
			.fetchOne();
		
		// 로딩 된 entity인지, 초기화 안된 entity인지 알려줌
		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).as("페치 조인 적용").isFalse();
		
	}
	
	/**
	 * 나이가 가장 많은 회원 조회
	 */
	@Test
	void subQuery() {
		QMember member = QMember.member;
		QMember memberSub = new QMember("memberSub");
		
		List<Member> result = queryFactory
				.selectFrom(QMember.member)
				.where(member.age.eq(
						JPAExpressions
							.select(memberSub.age.max())
							.from(memberSub)
				))
				.fetch();
		
		assertThat(result).extracting("age").containsExactly(40);
	}
	
	/**
	 * 나이가 평균 이상인 회원
	 */
	@Test
	void subQueryGoe() {
		QMember member = QMember.member;
		QMember memberSub = new QMember("memberSub");
		
		List<Member> result = queryFactory
				.selectFrom(QMember.member)
				.where(member.age.goe(
						JPAExpressions
							.select(memberSub.age.avg())
							.from(memberSub)
				))
				.fetch();
		
		assertThat(result).extracting("age").containsExactly(30, 40);
	}
	
	/**
	 * 나이가 특정값 이상인 회원
	 */
	@Test
	void subQueryIn() {
		QMember member = QMember.member;
		QMember memberSub = new QMember("memberSub");
		
		List<Member> result = queryFactory
				.selectFrom(QMember.member)
				.where(member.age.in(
						JPAExpressions
							.select(memberSub.age)
							.from(memberSub)
							.where(memberSub.age.gt(10))
				))
				.fetch();
		
		assertThat(result).extracting("age").containsExactly(20, 30, 40);
	}
	
	@Test
	void selectSubquery() {
		QMember member = QMember.member;
		QMember memberSub = new QMember("memberSub");
		List<Tuple> result = queryFactory
			.select(member.username,
					JPAExpressions
					.select(memberSub.age.avg())
					.from(memberSub))
			.from(member)
			.fetch();
		
		for (Tuple tuple : result) {
			System.out.println("tuple="+tuple);
		}
	}
	
	/**
	 * from 절의 서브쿼리 한계
	 * 
	 * JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 
	 * 당연히 Querydsl도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다.
	 * Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.
	 * 
	 * from 절의 서브쿼리 해결방안
	 * 
	 * 1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
	 * 2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
	 * 3. nativeSQL을 사용한다
	 */
	
	// -----case 문
	
	@Test
	void basicCase() {
		List<String> result = queryFactory
			.select(QMember.member.age
					.when(10).then("열살")
					.when(20).then("스무살")
					.otherwise("기타"))
			.from(QMember.member)
			.fetch();
		
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}
	
	@Test
	void complexCase() {
		List<String> result = queryFactory
			.select(new CaseBuilder()
					.when(QMember.member.age.between(0, 20)).then("열살")
					.when(QMember.member.age.between(20, 30)).then("스무살")
					.otherwise("기타"))
			.from(QMember.member)
			.fetch();
		
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}
	
	/** orderBy에서 Case문 함께 사용하지
	 * 
	 * 예를 들어서 다음과 같은 임의의 순서로 회원을 출력하고 싶다면?
	 * 1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
	 * 2. 0 ~ 20살 회원 출력
	 * 3. 21 ~ 30살 회원 출력
	 */
	@Test
	void orderByCase() {
		NumberExpression<Integer> rankPath = new CaseBuilder()
			.when(QMember.member.age.between(0, 20)).then(2)
			.when(QMember.member.age.between(21, 30)).then(1)
			.otherwise(3);
		
		List<Tuple> result = queryFactory
			.select(QMember.member.username, QMember.member.age, rankPath)
			.from(QMember.member)
			.orderBy(rankPath.desc())
			.fetch();
		
		for (Tuple tuple : result) {
			String username = tuple.get(QMember.member.username);
			Integer age = tuple.get(QMember.member.age);
			Integer rank = tuple.get(rankPath);
			System.out.println("username = " + username + " age = "+ age + " rank = " + rank);
		}
		
	}
	
	@Test
	void constantCase() {
		Tuple result = queryFactory
			.select(QMember.member.username, Expressions.constant("A"))
			.from(QMember.member)
			.fetchFirst();
		
		System.out.println("tuple = " + result);
			
	}
	
	@Test
	void concatCase() {
		String result = queryFactory.select(
						QMember.member.username
						.concat("_").concat(QMember.member.age.stringValue()))
					.from(QMember.member)
					.where(QMember.member.username.eq("member1"))
					.fetchOne();
		
		System.out.println("tuple = " + result);
	}
	
	@Test
	void simpleProjection() {
		List<String> result = queryFactory
				.select(QMember.member.username)
				.from(QMember.member)
				.fetch();
		
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}
	
	@Test
	void tupleProjection() {
		List<Tuple> result = queryFactory
					.select(QMember.member.username, QMember.member.age)
					.from(QMember.member)
					.fetch();
		
		for (Tuple tuple : result) {
			String username = tuple.get(QMember.member.username);
			Integer age = tuple.get(QMember.member.age);
			
			System.out.println("username = " + username);
			System.out.println("age = " + age);
		}
	}
	
	@Test
	void findDtoByJPQL() {
		List<MemberQueryDslDTO> result = em.createQuery("select new jpa.study.book.dto.MemberQueryDslDTO(m.username, m.age) from Member m", MemberQueryDslDTO.class)
				.getResultList();
		
		for (MemberQueryDslDTO memberQueryDslDTO : result) {
			System.out.println("memberQueryDslDTO = " + memberQueryDslDTO);
		}
	}
	
	// setter 필요
	@Test
	void findDtoByQuerySetter() {
		List<MemberQueryDslDTO> result = queryFactory
					.select(Projections.bean(MemberQueryDslDTO.class, 
												QMember.member.username, QMember.member.age))
					.from(QMember.member)
					.fetch();
		
		for (MemberQueryDslDTO memberQueryDslDTO : result) {
			System.out.println("memberQueryDslDTO = " + memberQueryDslDTO);
		}
	}
	
	// getter, setter 필요없음. 값이 fields에 바로 꽂힘
	@Test
	void findDtoByQueryField() {
		List<MemberQueryDslDTO> result = queryFactory
					.select(Projections.fields(MemberQueryDslDTO.class, 
												QMember.member.username, QMember.member.age))
					.from(QMember.member)
					.fetch();
		
		for (MemberQueryDslDTO memberQueryDslDTO : result) {
			System.out.println("memberQueryDslDTO = " + memberQueryDslDTO);
		}
	}
	
	@Test
	void findDtoByConstructor() {
		List<MemberQueryDslDTO> result = queryFactory
					.select(Projections.constructor(MemberQueryDslDTO.class, 
												QMember.member.username, QMember.member.age))
					.from(QMember.member)
					.fetch();
		
		for (MemberQueryDslDTO memberQueryDslDTO : result) {
			System.out.println("memberQueryDslDTO = " + memberQueryDslDTO);
		}
	}
	
	@Test
	void findUserDto() {
		QMember memberSub = new QMember("memberSub");
		List<UserDto> result = queryFactory
					.select(Projections.fields(UserDto.class, 
							QMember.member.username.as("name"), 
							ExpressionUtils.as(JPAExpressions
									.select(memberSub.age.max())
									.from(memberSub)
									, "age")
					))
					.from(QMember.member)
					.fetch();
		
		for (UserDto userDto : result) {
			System.out.println("UserDto = " + userDto);
		}
	}
	
	@Test
	void findUserDtoByConstructor() {
		List<UserDto> result = queryFactory
					.select(Projections.constructor(UserDto.class, 
												QMember.member.username, QMember.member.age))
					.from(QMember.member)
					.fetch();
		
		for (UserDto userDto : result) {
			System.out.println("UserDto = " + userDto);
		}
	}
	
	@Test
	void findDtoByQueryProjection() {
		List<MemberQueryDslDTO> result =  queryFactory
					.select(new QMemberQueryDslDTO(QMember.member.username, QMember.member.age))
					.from(QMember.member)
					.fetch();
		
		for (MemberQueryDslDTO memberQueryDslDTO : result) {
			System.out.println("memberQueryDslDTO = " + memberQueryDslDTO);
		}
	}
	
	@Test
	void findDistinct() {
		List<String> result = queryFactory
			.select(QMember.member.username).distinct()
			.from(QMember.member)
			.fetch();
		
	}
	
	@Test
	void dynamicQuery_BooleanBuilder() {
		String usernameParam = "member1";
		Integer ageParam = null;
		
		List<Member> result = searchMember1(usernameParam, ageParam);
		assertThat(result.size()).isEqualTo(1);
	}

	private List<Member> searchMember1(String usernameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder();
		
		if (usernameCond != null) {
			builder.and(QMember.member.username.eq(usernameCond));
		}
		
		if (ageCond != null) {
			builder.and(QMember.member.age.eq(ageCond));
		}
		
		return queryFactory
			.selectFrom(QMember.member)
			.where(builder)
			.fetch();
	}
	
	@Test
	void dynaminQuery_WhereParam() {
		String usernameParam = "member1";
		Integer ageParam = 10;
		
		List<Member> result = searchMember2(usernameParam, ageParam);
		assertThat(result.size()).isEqualTo(1);
	}

	private List<Member> searchMember2(String usernameCond, Integer ageCond) {
		return queryFactory
					.selectFrom(QMember.member)
					.where(usernameEq(usernameCond), ageEq(ageCond))
					.fetch();
	}

	private BooleanExpression usernameEq(String usernameCond) {
		return usernameCond != null ? QMember.member.username.eq(usernameCond) : null;
	}

	private BooleanExpression ageEq(Integer ageCond) {
		return ageCond != null ? QMember.member.age.eq(ageCond) : null;
	}
	
	private BooleanExpression allEq(String usernameCond, Integer ageCond) {
		return usernameEq(usernameCond).and(ageEq(ageCond));
	}
}
