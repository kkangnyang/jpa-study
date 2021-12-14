package jpa.study.book.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 1. 왜 테스트 코드는 쿼리가 한개 인가? 트랜잭션과 Persistence Context
 * TODO 2. Member 정보만 쿼리 한번으로 가져오는 방법은? fetch = FetchType.LAZY
 * TODO 3. Member-Team 정보를 쿼리 한번으로 가져오는 방법은? @NamedEntityGraph
 *
 */

@Entity
@Getter @Setter @ToString
@NamedEntityGraph(name = "MemberWithTeam", attributeNodes = @NamedAttributeNode("team"))
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_seq")
	private long memberSeq;
	
	@Column
	private String id;
  
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "tid")
	private Team team;
	
	
	
}
