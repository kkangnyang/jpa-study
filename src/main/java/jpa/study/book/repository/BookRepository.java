package jpa.study.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jpa.study.book.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
