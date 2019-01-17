package com.loserico.orm.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.BeforeClass;
import org.junit.Test;

import com.loserico.orm.entity.AttendeeRegister;

/**
 * @of
 * As we progress through this chapter, we will explore each of these methods in detail, 
 * but for now we will look at the bigger picture. 
 * First, there's the {@code CriteriaBuilder} interface, obtained here from the {@code EntityManager} interface 
 * through the getCriteriaBuilder() method. The CriteriaBuilder interface is the main gateway
 * into the Criteria API, acting as a factory for the various objects that link together to form a query definition.
 * <p/>
 * The first use of the {@code CriteriaBuilder} interface in this example is to create an instance of {@code CriteriaQuery}.
 * The CriteriaQuery object forms the shell of the query definition and generally contains the methods that match up
 * with the JP QL query clauses. 
 * <p/>
 * The second use of the CriteriaBuilder interface in this example is to construct the
 * conditional expressions in the WHERE clause. All of the conditional expression keywords, operators, and functions
 * from JP QL are represented in some manner on the CriteriaBuilder interface.
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-04-01 11:07
 * @version 1.0
 *
 */
public class CriteriaAPITest {

	private static EntityManager entityManager;

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		entityManager = emf.createEntityManager();
	}

	/**
	 * @of
	 * 对应JPQL语句
	 * SELECT e FROM AttendeeRegister e WHERE e.childName = '彭魁'
	 * 
	 * The JP QL keywords SELECT,   FROM,   WHERE    and LIKE have matching methods in the form of 
	 * 					  select(), from(), where(), and like().
	 * @on
	 */
	@Test
	public void testCriteriaAPI1() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AttendeeRegister> criteriaQuery = criteriaBuilder.createQuery(AttendeeRegister.class);

		/*
		 * @of
		 * The first step is to establish the root of the query by invoking from() to get back a Root object. 
		 * This is equivalent to declaring the identification variable e in the JP QL example 
		 * and the Root object will form the basis for path expressions in the rest of the query. 
		 * 
		 * The next step establishes the SELECT clause of the query by passing the root into the select() method.
		 * 
		 * The last step is to construct the WHERE clause, by passing an expression composed from 
		 * CriteriaBuilder methods that represent JP QL condition expressions into the where() method. 
		 * When path expressions are needed, such as accessing the name attribute in this example, 
		 * the get() method on the Root object is used to create the path.
		 * 
		 * 这个Root类型的e就相当于上述JPQL中的e
		 * @on
		 */
		Root<AttendeeRegister> e = criteriaQuery.from(AttendeeRegister.class);
		criteriaQuery.select(e)
				.where(criteriaBuilder.equal(e.get("childName"), "彭魁"));
	}

	@Test
	public void testDynamicQueries() {

	}
}
