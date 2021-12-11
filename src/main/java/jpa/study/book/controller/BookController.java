package jpa.study.book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jpa.study.book.entity.Book;
import jpa.study.book.entity.BookStore;
import jpa.study.book.repository.BookRepository;
import jpa.study.book.repository.BookStoreRepository;

@RestController
@RequestMapping("/hello")
public class BookController {
	
	@Autowired
	BookStoreRepository bookStoreRepository;
	
	@Autowired
	BookRepository bookRepository;
	
	@GetMapping("/hello")
	public String hello() {
		
		BookStore bookStore = new BookStore();
		bookStore.setName("신림 책방");
		bookStoreRepository.save(bookStore);
		
		Book book = new Book();
		book.setTitle("JPA 공부하기");
		
		bookStore.add(book);
		
		bookRepository.save(book);
		
		List<BookStore> bookStoreList = bookStoreRepository.findAll();
		System.out.println("size: " + bookStoreList.size());
		
		return "hello";
	} 
	
}
