package jpa.study.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jpa.study.book.dto.MemberSearchContidion;
import jpa.study.book.dto.MemberTeamDTO;
import jpa.study.book.entity.Member;
import jpa.study.book.entity.Team;
import jpa.study.book.repository.QMemberJpaRepository;

@SpringBootTest
@Transactional
class QMemberJpaRepositoryTest {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	QMemberJpaRepository qmemberJpaRepository;

	@Test
	public void basicTest() {
		Member member = new Member("member1", 10);
		qmemberJpaRepository.save(member);
		
		Member findMember = qmemberJpaRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);
		
		List<Member> result1 = qmemberJpaRepository.findAll();
		assertThat(result1).containsExactly(member);
		
		List<Member> result2 = qmemberJpaRepository.findByUsername("member1");
		assertThat(result2).containsExactly(member);
	}
	
	@Test
	public void basicQuerydslTest() {
		Member member = new Member("member1", 10);
		qmemberJpaRepository.save(member);
		
		Member findMember = qmemberJpaRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);
		
		List<Member> result1 = qmemberJpaRepository.findAll_Querydsl();
		assertThat(result1).containsExactly(member);
		
		List<Member> result2 = qmemberJpaRepository.findByUsername_Querydsl("member1");
		assertThat(result2).containsExactly(member);
	}
	
	@Test
	public void searchTest() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		
		MemberSearchContidion condition = new MemberSearchContidion();
		condition.setAgeGoe(35);
		condition.setAgeLoe(40);
		condition.setTeamName("teamB");
		
		List<MemberTeamDTO> result = qmemberJpaRepository.searchByBuilder(condition);
		assertThat(result).extracting("username").containsExactly("member4");
	}
	
	@Test
	public void searchTest2() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		
		MemberSearchContidion condition = new MemberSearchContidion();
		condition.setAgeGoe(35);
		condition.setAgeLoe(40);
		condition.setTeamName("teamB");
		
		List<MemberTeamDTO> result = qmemberJpaRepository.search(condition);
		assertThat(result).extracting("username").containsExactly("member4");
	}



}
