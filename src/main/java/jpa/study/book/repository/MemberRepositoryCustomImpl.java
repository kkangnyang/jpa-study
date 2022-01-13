package jpa.study.book.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jpa.study.book.dto.MemberSearchContidion;
import jpa.study.book.dto.MemberTeamDTO;
import jpa.study.book.dto.QMemberTeamDTO;
import jpa.study.book.entity.Member;
import jpa.study.book.entity.QMember;
import jpa.study.book.entity.QTeam;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
	
	/*
	 * JPA 직접사용(EntityManager)
	 * 스프링 JDBCTemplate 사용
	 * Mybatis 사용
	 * Querydsl 사용 등등
	 */
	
	private final JPAQueryFactory queryFactory;
	
	public MemberRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	@Override
	public List<MemberTeamDTO> search(MemberSearchContidion condition) {
		return queryFactory
				.select(new QMemberTeamDTO(QMember.member.id.as("mid"), QMember.member.username, QMember.member.age,
						QTeam.team.id.as("tid"), QTeam.team.name))
				.from(QMember.member).leftJoin(QMember.member.team, QTeam.team)
				.where(usernameEq(condition.getUsername()), teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), ageLoe(condition.getAgeLoe()))
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
	
	/**
	 * 단순한 페이징, fetchResults() 사용
	 */
	@Override
	public Page<MemberTeamDTO> searchPageSimple(MemberSearchContidion condition, Pageable pageable) {
		QueryResults<MemberTeamDTO> results = queryFactory
				.select(new QMemberTeamDTO(
						QMember.member.id, 
						QMember.member.username, 
						QMember.member.age, 
						QTeam.team.id.as("teamId"), 
						QTeam.team.name.as("teamName"))
					)
				.from(QMember.member)
				.leftJoin(QMember.member.team, QTeam.team)
				.where(usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();
		
		
		List<MemberTeamDTO> content = results.getResults();
		long total = results.getTotal();
		
		return new PageImpl<>(content, pageable, total);
	}
	
	/*
	 * 복잡한 페이징
	 * 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
	 */
	@Override
	public Page<MemberTeamDTO> searchPageComplex(MemberSearchContidion condition, Pageable pageable) {
		List<MemberTeamDTO> content = queryFactory
				.select(new QMemberTeamDTO(
						QMember.member.id, 
						QMember.member.username, 
						QMember.member.age, 
						QTeam.team.id, 
						QTeam.team.name))
				.from(QMember.member)
				.leftJoin(QMember.member.team, QTeam.team)
				.where(usernameEq(condition.getUsername()), 
						teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), 
						ageLoe(condition.getAgeLoe()))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		
		JPAQuery<Member> countQuery = queryFactory
						.select(QMember.member)
						.from(QMember.member)
						.leftJoin(QMember.member.team, QTeam.team)
						.where(usernameEq(condition.getUsername()), 
								teamNameEq(condition.getTeamName()),
								ageGoe(condition.getAgeGoe()), 
								ageLoe(condition.getAgeLoe()));
		
		return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
		//return new PageImpl<>(content, pageable, total);
	}


}
