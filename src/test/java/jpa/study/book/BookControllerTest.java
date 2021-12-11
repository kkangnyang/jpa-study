package jpa.study.book;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jpa.study.book.entity.Book;
import jpa.study.book.entity.BookStore;
import jpa.study.book.repository.BookRepository;
import jpa.study.book.repository.BookStoreRepository;

@SpringBootTest
class BookControllerTest {
	
	@Autowired(required=true)
	BookStoreRepository bookStoreRepository;
	
	@Autowired(required=true)
	BookRepository bookRepository;
	
	@Test
	void test() {
		BookStore bookStore = new BookStore();
		bookStore.setName("신림동 책방");
		bookStoreRepository.save(bookStore);
		
		Book book = new Book();
		book.setTitle("JPA 열심히 공부하기");
		
		bookStore.add(book);
		
		bookRepository.save(book);
		
		List<BookStore> bookStoreList = bookStoreRepository.findAll();
		System.out.println("size: " + bookStoreList.size());
		
	}

}
