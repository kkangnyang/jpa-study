package jpa.study.book;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jpa.study.book.entity.Member;
import jpa.study.book.entity.Team;
import jpa.study.book.repository.MemberRepository;
import jpa.study.book.repository.TeamRepository;

@SpringBootTest
class MemberControllerTest {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Test
	public void testSelect() {
		Team team = new Team("New team 1");
		Team savedTeam = teamRepository.save(team);
		
		Team team2 = new Team("New team 2");
		Team savedTeam2 = teamRepository.save(team2);
		
		Member member1 = new Member("gayoung");
		member1.setTeam(savedTeam);
		memberRepository.save(member1);
		
		Member member2 = new Member("gary");
		member2.setTeam(savedTeam2);
		memberRepository.save(member2);
		
		memberRepository.findAll().forEach(System.out::println);
	}

}
