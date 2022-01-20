package jpa.study.book.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import jpa.study.book.entity.Product;

@Repository
@Transactional
public class ProductRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	public void identityStrategyTest() {
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
	
	public void identityStrategyTest2() {

		Product a = new Product("A", 1000);
		Product b = new Product("B", 2000);

		System.out.println("///////////////////");
		em.persist(a);
		System.out.println("a product id : " + a.getProductId());
		em.persist(b);
		System.out.println("b product id : " + b.getProductId());
		a.setPrice(300);
		System.out.println("///////////////////");
	}
	
	public void identityStrategyTest3() {

		Product a = new Product("A", 1000);
		Product b = new Product("B", 2000);

		System.out.println("///////////////////");
		em.persist(a);
		System.out.println("a product id : " + a.getProductId());
		em.persist(b);
		System.out.println("b product id : " + b.getProductId());
		a.setPrice(300);
		if (true) throw new RuntimeException();
		System.out.println("///////////////////");
	}
	
}
