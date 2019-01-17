package com.loserico.orm.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

import com.loserico.orm.entity.Product;

public class JPATest {
	
	private static EntityManager entityManager;
	
	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		entityManager = emf.createEntityManager();
	}

	@Test
	public void testBootstrapJPA() {
		/*EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.find(User.class, 10l);
		em.getTransaction().commit();*/
	}
	
	@Test
	public void testStatistics() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (int i = 0; i < 10; i++) {
			Product p = new Product();
			p.setName("MyProduct" + i);
			em.persist(p);
		}
		em.getTransaction().commit();
		em.createQuery("Select p From Product p").getResultList();

	}
}
