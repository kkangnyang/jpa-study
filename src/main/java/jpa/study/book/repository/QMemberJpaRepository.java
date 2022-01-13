package jpa.study.book.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jpa.study.book.dto.MemberSearchContidion;
import jpa.study.book.dto.MemberTeamDTO;
import jpa.study.book.dto.QMemberTeamDTO;
import jpa.study.book.entity.Member;
import jpa.study.book.entity.QMember;
import jpa.study.book.entity.QTeam;

@Repository
public class QMemberJpaRepository {
	
	private final EntityManager em;
	private final JPQLQueryFactory queryFactory;
	
	public QMemberJpaRepository(EntityManager em, JPQLQueryFactory queryFactory) {
		this.em = em;
		this.queryFactory = queryFactory;
	}
	
	public void save(Member member) {
		em.persist(member);
	}
	
	public Optional<Member> findById(Long id) {
		Member findMember = em.find(Member.class, id);
		return Optional.ofNullable(findMember);
	}
	
	public List<Member> findAll() {
		return em.createQuery("select m from Member m", Member.class).getResultList();
	}
	
	public List<Member> findAll_Querydsl() {
		return queryFactory
					.selectFrom(QMember.member)
					.fetch();
	}

	public List<Member> findByUsername(String username) {
		return em.createQuery("select m from Member m where m.username = :username", Member.class)
				.setParameter("username", username).getResultList();
	}
	
	public List<Member> findByUsername_Querydsl(String username) {
		return queryFactory
					.selectFrom(QMember.member)
					.where(QMember.member.username.eq(username))
					.fetch();
	}
	
	public List<MemberTeamDTO> searchByBuilder(MemberSearchContidion condition) {
		
		BooleanBuilder builder = new BooleanBuilder();
		if (StringUtils.hasText(condition.getUsername())) {
			builder.and(QMember.member.username.eq(condition.getUsername()));
		}
		if (StringUtils.hasText(condition.getTeamName())) {
			builder.and(QTeam.team.name.eq(condition.getTeamName()));
		}
		if (condition.getAgeGoe() != null) {
			builder.and(QMember.member.age.goe(condition.getAgeGoe()));
		}
		if (condition.getAgeLoe() != null) {
			builder.and(QMember.member.age.loe(condition.getAgeLoe()));
		}
		
		return queryFactory
					.select(new QMemberTeamDTO(
							QMember.member.id.as("mid"), 
							QMember.member.username, 
							QMember.member.age, 
							QTeam.team.id.as("tid"), 
							QTeam.team.name))
					.from(QMember.member)
					.leftJoin(QMember.member.team, QTeam.team)
					.where(builder)
					.fetch();
	}
	
	public List<MemberTeamDTO> search(MemberSearchContidion condition) {
		return queryFactory
				.select(new QMemberTeamDTO(
						QMember.member.id.as("mid"), 
						QMember.member.username, 
						QMember.member.age, 
						QTeam.team.id.as("tid"), 
						QTeam.team.name))
				.from(QMember.member)
				.leftJoin(QMember.member.team, QTeam.team)
				.where(
						usernameEq(condition.getUsername()),
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()),
						ageLoe(condition.getAgeLoe())
				)
				.fetch();
	}
	
	private BooleanExpression usernameEq(String username) {
		return StringUtils.hasText(username) ? null : QMember.member.username.eq(username);
	}

	private BooleanExpression teamNameEq(String teamName) {
		return StringUtils.hasText(teamName) ? null : QTeam.team.name.eq(teamName);
	}

	private BooleanExpression ageGoe(Integer ageGoe) {
		return ageGoe == null ? null : QMember.member.age.goe(ageGoe);
	}

	private BooleanExpression ageLoe(Integer ageLoe) {
		return ageLoe == null ? null : QMember.member.age.loe(ageLoe);
	}

}
