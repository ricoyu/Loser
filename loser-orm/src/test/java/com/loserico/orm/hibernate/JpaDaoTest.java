package com.loserico.orm.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
	public void testPersist() {
		/*Book book = new Book();
		book.setCreator("俞雪华");
		book.setModifier("俞雪华");
		entityOperations.persist(book);
		book = new Book();
		book.setIsbn("123456");
		book.setName("深入浅出设计模式");
		book.setCreator("俞雪华");
		book.setModifier("俞雪华");
		entityOperations.persist(book);*/
	}
	
	@Test
	public void testNamedSQLQuery() {
		/*CountDownLatch countDownLatch = new CountDownLatch(100);
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		for (int i = 0; i < 99; i++) {
			executorService.execute(() -> {
				List<Book> books = sqlOperations.namedSqlQuery("Book.findAll", Book.class);
				books.forEach(b -> System.out.println(toJson(b)));
				countDownLatch.countDown();
			});
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
	
	@Test
	public void testNamedSQLQueryManyData() {
		/*long begin = System.currentTimeMillis();
//		List<CustomerCode> customerCodes = sqlOperations.namedSqlQuery("CustomerCode.findAll", CustomerCode.class);
		CriteriaQuery<CustomerCode> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(CustomerCode.class);
		criteriaQuery.from(CustomerCode.class);
		List<CustomerCode> customerCodes = entityManager.createQuery(criteriaQuery).getResultList();
		long end = System.currentTimeMillis();
		
		System.out.println(end - begin);*/
	}

	@Test
	public void testEntityOperations() {
		/*long begin = System.currentTimeMillis();
		List<Customer> customers = entityOperations.findAll(Customer.class);
		long end = System.currentTimeMillis();
		System.out.println("查询[" + customers.size() + "]条数据花费毫秒: " + (end - begin));
		System.out.println("查询[" + customers.size() + "]条数据花费秒: " + (end - begin) / 1000);

		begin = System.currentTimeMillis();
		Map<String, Customer> customerMap = new HashMap<String, Customer>();
		for (Customer customer : customers) {
			customerMap.put(customer.getTaidiiUid(), customer);
		}
		end = System.currentTimeMillis();
		System.out.println("组装[" + customers.size() + "]条数据到Map花费毫秒: " + (end - begin));
		System.out.println("组装[" + customers.size() + "]条数据到Map花费秒: " + (end - begin) / 1000);*/
	}

	@Test
	public void testFindUniqueByProperty() {
		/*Customer customer = criteriaQueryOperations.findUniqueByProperty(Customer.class, "id", 76543);*/
	}

	@Test
	public void testNotanEntity() {
//		entityOperations.findAll(com.loserico.orm.hibernate.Customer.class);
	}

	@AfterClass
	public static void tearDown() {
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
