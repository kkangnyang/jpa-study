package jpa.study.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jpa.study.book.entity.BookStore;

@Repository
public interface BookStoreRepository extends JpaRepository<BookStore, Long> {

}
