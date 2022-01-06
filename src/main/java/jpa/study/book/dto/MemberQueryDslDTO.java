package jpa.study.book.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberQueryDslDTO {
	private String username;
	private int age;
	
	@QueryProjection
	public MemberQueryDslDTO(String username, int age) {
		this.username = username;
		this.age = age;
	}


}
