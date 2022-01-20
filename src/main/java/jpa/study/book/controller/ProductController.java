package jpa.study.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpa.study.book.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	
	@GetMapping("/v1/products")
	public void identityStrategyTest() {
		productRepository.identityStrategyTest2();
	}
	
	@GetMapping("/v2/products")
	public void identityStrategyTest2() {
		productRepository.identityStrategyTest3();
	}

}
