package jpa.study.book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jpa.study.book.dto.MemberSearchContidion;
import jpa.study.book.dto.MemberTeamDTO;

public interface MemberRepositoryCustom {
	List<MemberTeamDTO> search(MemberSearchContidion condition);
	Page<MemberTeamDTO> searchPageSimple(MemberSearchContidion condition, Pageable pageable);
	Page<MemberTeamDTO> searchPageComplex(MemberSearchContidion condition, Pageable pageable);
	
}
