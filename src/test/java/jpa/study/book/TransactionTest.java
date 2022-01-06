package jpa.study.book;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jpa.study.book.entity.Product;

@SpringBootTest
public class TransactionTest {
	
	@PersistenceContext
	EntityManager em;
	
	@Test
	void transactionTest() {
		EntityTransaction transaction = em.getTransaction(); //트랜잭션 기능 획득
		
		transaction.begin();

		Product a = new Product("A", 1000);
		Product b = new Product("B", 2000);

		System.out.println("///////////////////");
		em.persist(a);
		System.out.println("a product id : " + a.getProductId());
		em.persist(b);
		System.out.println("b product id : " + b.getProductId());
		System.out.println("///////////////////");

		transaction.commit();
	}

}
