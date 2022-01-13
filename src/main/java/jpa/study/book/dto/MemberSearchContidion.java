package jpa.study.book.dto;

import lombok.Data;

@Data
public class MemberSearchContidion {
	
	private String username;
	private String teamName;
	private Integer ageGoe;
	private Integer ageLoe;
}
