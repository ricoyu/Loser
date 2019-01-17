package com.loserico.security;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.loserico.orm.dao.CriteriaOperations;
import com.loserico.orm.dao.EntityOperations;
import com.loserico.orm.dao.JPQLOperations;
import com.loserico.orm.dao.SQLOperations;
import com.loserico.orm.jpa.dao.JpaDao;
import com.loserico.security.service.PasswordHelper;

public class ShiroAuthcTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroAuthcTest.class);

	private static EntityManager entityManager;

	private static EntityOperations entityOperations;
	private static CriteriaOperations criteriaQueryOperations;
	private static SQLOperations nativeSQLOperations;
	private static JPQLOperations jpqlOperations;

	private static PasswordHelper passwordHelper = new PasswordHelper();

	@BeforeClass
	public static void setup() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("loser-orm");
		entityManager = emf.createEntityManager();
		entityManager.getTransaction().begin();

		JpaDao jpaDao = new JpaDao();
		ReflectionTestUtils.setField(jpaDao, "entityManager", entityManager);
		entityOperations = jpaDao;
		criteriaQueryOperations = jpaDao;
		nativeSQLOperations = jpaDao;
		jpqlOperations = jpaDao;

	}

/*	@Test
	public void testInitUser() {
		User user = new User();
		user.setSalt(passwordHelper.privateSalt());
		user.setBirthday(LocalDate.of(1982, 11, 9));
		user.setEmail("ricoyu520@gmail.com");
//		user.setGender(Gender.MALE);
		user.setLocked(false);
		user.setCellphone("13913582189");
		user.setName("俞雪华");
		user.setPassword(passwordHelper.encryptPassword("123456", "123456", user.getSalt()));
		user.setUsername("ricoyu");
		user.setCreator("System");
		user.setModifier("System");
		entityManager.persist(user);
	}*/

/*	@Test
	public void testEntityOperations() {
		entityOperations.findAll(User.class);
	}*/

	@AfterClass
	public static void tearDown() {
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
