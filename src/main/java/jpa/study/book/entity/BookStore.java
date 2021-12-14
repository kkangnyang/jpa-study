package jpa.study.book.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BookStore {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long bsid;
	
	private String name;
	
	/*
	 * 양방향 연결 방법 : mappedBy 
	 * mappedBy의 의미
	 * - 관계의 주인을 알려줌. 관계의 주인이 Book 이라고 알려줌
	 * - Book에서 자기자신을 bookStore라고 참조하고 있다.
	 * 
	 */
	@OneToMany(mappedBy = "bookStore")
	private Set<Book> books = new HashSet<>();
	
	public void add(Book book) {
		/*
		 * 관계의 주인은 Book인데 Book에다가는 관계를 설정하지 않고, 자기자신한테만 관계설정.
		 * 그럼 Database에 sync 할게 없음. 이 객체의 상태가 변경됫음에도 불구하고 database의 아무런 일이 이러나지 않음
		 * 이 관계의 주인인 book 쪽에 아무런 변경이 일어나지 않앗으므로.
		 */
		book.setBookStore(this);
		this.books.add(book);
	}
	
}
