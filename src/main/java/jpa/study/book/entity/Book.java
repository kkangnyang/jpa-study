package jpa.study.book.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

// 일대다 양방향 관계

@Entity
@Getter @Setter
public class Book {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long bid;
	
	private String title;
	
	@ManyToOne
	@JoinColumn(name = "bsid")
	private BookStore bookStore;
	
}
