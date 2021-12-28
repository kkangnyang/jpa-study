package jpa.study.book.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
	
	// 조회할 엔티티의 필드를 getter 형식으로 지저하면 해당 필드만 선택해서 조회(Projection)
	
	//@Value("#{target.username + ' ' + target.age + ' ' + target.team.name}")
	String getUsername();
	
}
