package jpa.study.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jpa.study.book.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	@EntityGraph("MemberWithTeam")
	List<Member> findAll();
}
