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
	
	@OneToMany(mappedBy = "bookStore")
	private Set<Book> books = new HashSet<>();
	
	public void add(Book book) {
		this.books.add(book);
	}
	
}
