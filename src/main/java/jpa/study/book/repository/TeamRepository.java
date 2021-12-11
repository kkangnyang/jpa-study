package jpa.study.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jpa.study.book.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
