package jpa.study.book.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO 1. 왜 테스트 코드는 쿼리가 한개 인가? 트랜잭션과 Persistence Context
 * TODO 2. Member 정보만 쿼리 한번으로 가져오는 방법은? fetch = FetchType.LAZY
 * TODO 3. Member-Team 정보를 쿼리 한번으로 가져오는 방법은? @NamedEntityGraph
 *
 */

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(name="Member.findByUsername", query="select m from Member m where m.username = :username")
//@NamedEntityGraph(name = "MemberWithTeam", attributeNodes = @NamedAttributeNode("team"))
public class Member extends JpaBaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mid")
	private long id;
	
	private String username;
	
	private int age;
	
	// 연관관계의 주인. 따라서, Member.team이 DB 외래키 값을 변경, 반대편은 읽기만 가능
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tid")
	private Team team;
	
	public Member(String username) {
		this(username, 0);
	}
	
	public Member(String username, int age) {
		this(username, age, null);
	}
	
	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		if (team != null) {
			changeTeam(team);
		}
	}
	
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}
