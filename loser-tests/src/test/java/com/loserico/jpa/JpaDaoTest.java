package com.loserico.jpa;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.loserico.jpa.entity.CustomerCode;
import com.loserico.orm.dao.CriteriaOperations;
import com.loserico.orm.dao.EntityOperations;
import com.loserico.orm.dao.JPQLOperations;
import com.loserico.orm.dao.SQLOperations;
import com.loserico.orm.jpa.dao.JpaDao;

public class JpaDaoTest {

	private static EntityManager entityManager;

	private static EntityOperations entityOperations;
	private static CriteriaOperations criteriaQueryOperations;
	private static SQLOperations sqlOperations;
	private static JPQLOperations jpqlOperations;

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();

		JpaDao jpaDao = new JpaDao();
		ReflectionTestUtils.setField(jpaDao, "entityManager", entityManager);
		entityOperations = jpaDao;
		criteriaQueryOperations = jpaDao;
		sqlOperations = jpaDao;
		jpqlOperations = jpaDao;

	}

	@Test
	public void testNamedSQLQueryManyData() {
		long begin = System.currentTimeMillis();
		List<CustomerCode> customerCodes = sqlOperations.namedSqlQuery("CustomerCode.findAll", CustomerCode.class);
//				CriteriaQuery<CustomerCode> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(CustomerCode.class);
//				criteriaQuery.from(CustomerCode.class);
//				List<CustomerCode> customerCodes = entityManager.createQuery(criteriaQuery).getResultList();
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
		System.out.println(toJson(customerCodes.get(0)));;
	}
	
	@Test
	public void testStudents() {
//		long begin = System.currentTimeMillis();
//		List<StudentVO> studentVOs = sqlOperations.namedSqlQuery("findAllStudents", StudentVO.class);
////				CriteriaQuery<CustomerCode> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(CustomerCode.class);
////				criteriaQuery.from(CustomerCode.class);
////				List<CustomerCode> customerCodes = entityManager.createQuery(criteriaQuery).getResultList();
//		long end = System.currentTimeMillis();
//		System.out.println(end - begin);
//		System.out.println(toJson(studentVOs.get(0)));;
	}

	@AfterClass
	public static void tearDown() {
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}