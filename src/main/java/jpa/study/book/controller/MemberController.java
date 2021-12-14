package jpa.study.book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jpa.study.book.entity.Member;
import jpa.study.book.repository.MemberRepository;

@RequestMapping("/member")
@RestController
public class MemberController {
	
	@Autowired
	MemberRepository memberRepository;
	
	@GetMapping("/members")
	@ResponseBody
	public List<Member> member() {
		return memberRepository.findAll();
	}
}
