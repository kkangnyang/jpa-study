package jpa.study.book.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;			// 상품ID
	
	private String productName; 	// 상품명
	
	private int price;				// 가격		
	
	public Product(String productName, int price) {
		this.productName = productName;
		this.price = price;
	}
}
