package jpa.study.book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jpa.study.book.dto.MemberSearchContidion;
import jpa.study.book.dto.MemberTeamDTO;
import jpa.study.book.entity.Member;
import jpa.study.book.repository.MemberRepository;
import jpa.study.book.repository.QMemberJpaRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	
	@Autowired
	MemberRepository memberRepository;
	
	private final QMemberJpaRepository qmemberJpaRepository;
	
	@GetMapping("/members")
	@ResponseBody
	public List<Member> member() {
		return memberRepository.findAll();
	}
	
	@GetMapping("/v1/members")
	public List<MemberTeamDTO> searchMemberV1(MemberSearchContidion condition) {
		return qmemberJpaRepository.search(condition);
	}
	
	@GetMapping("/v2/members")
	public Page<MemberTeamDTO> searchMemberV2(MemberSearchContidion condition, Pageable pageable) {
		return memberRepository.searchPageSimple(condition, pageable);
	}

	@GetMapping("/v3/members")
	public Page<MemberTeamDTO> searchMemberV3(MemberSearchContidion condition, Pageable pageable) {
		return memberRepository.searchPageComplex(condition, pageable);
	}
}
