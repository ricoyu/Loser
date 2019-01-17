package com.loserico.orm.hibernate;

public class RefinedOperationsInterfaceTest {

/*	private EntityManager entityManager;
	private EntityOperations entityOperations;
	private SQLOperations nativeSQLOperations;

	@Before
	public void setup() {
		this.entityManager = Persistence.createEntityManagerFactory("loser-orm").createEntityManager();
		JpaDao jpaDao = new JpaDao();
		this.entityOperations = jpaDao;
		this.nativeSQLOperations = jpaDao;
		ReflectionTestUtils.setField(jpaDao, "entityManager", entityManager);
		entityManager.getTransaction().begin();
	}

	@After
	public void tearDown() {
		entityManager.getTransaction().commit();
		this.entityManager.close();
	}

	@Test
	public void testEntityOperations() {
		entityManager.getTransaction().begin();
		User user = new User();
		user.setBirthday(LocalDate.of(1982, 11, 9));
		user.setCellphone("13913582178");
		user.setCreator("Admin");
		user.setEmail("ricoyu520@gmail.com");
		user.setGender(Gender.MALE);
		user.setLocked(false);
		user.setModifier("Admin");
		user.setPassword("123456");
		user.setSalt("123127t87%^%$$%#*(^");
		user.setUsername("ricoyu");
		user.setName("俞雪华");
		entityOperations.persist(user);
		entityManager.getTransaction().commit();
	}

	*//**
	 * 测试注解放在field还是getter上
	 *//*
	@Test
	public void testChooseFieldOrProperty() {
		entityManager.getTransaction().begin();

		entityOperations.persist(prepareUser());
		entityManager.getTransaction().commit();
	}

	@Test(expected = EntityNotFoundException.class)
	public void testLoadWithEntityNotfound() {
		entityManager.getTransaction().begin();
		User another = entityOperations.load(User.class, 3l);
		System.out.println(another);
		entityManager.getTransaction().commit();
	}

	
	 * persist方法第一次执行insert操作，第二次执行update操作 如果在两者中间时间点别的事务把user删掉，则第二次的update操作将失败
	 
	@Test
	public void testPersist() {
		entityManager.getTransaction().begin();
		User user = prepareUser();
		entityOperations.persist(user);
		entityManager.getTransaction().commit();
		System.out.println("======================开始另一个事务=====================");
		entityManager.getTransaction().begin();
		user.setName("三少爷");
		entityOperations.persist(user);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		user = entityOperations.get(User.class, user.getId());
		System.out.println(user.getName());
		entityManager.getTransaction().commit();
	}

	@Test
	public void testMerge() {
		entityManager.getTransaction().begin();
		User user = prepareUser();
		//		entityOperations.persist(user);
		user = entityOperations.merge(user);
		entityManager.getTransaction().commit();
		System.out.println("======================开始另一个事务=====================");
		entityManager.getTransaction().begin();
		user.setName("三少爷");
		entityOperations.merge(user);
		entityManager.getTransaction().commit();

		entityManager.getTransaction().begin();
		user = entityOperations.get(User.class, user.getId());
		System.out.println(user.getName());
		entityManager.getTransaction().commit();
	}

	
	 * 实测插10000条数据时分布式ID方式花费时间更少
	 
	@Test
	public void testDistributedIDGenerator() {
		long begin = System.currentTimeMillis();
		entityManager.getTransaction().begin();
		for (int i = 0; i < 10000; i++) {
			entityOperations.persist(prepareAddress());
		}
		entityManager.getTransaction().commit();
		long end = System.currentTimeMillis();
		System.out.println("IDENTITY ID方式花费时间[" + (end - begin) + "毫秒]");
//		System.out.println("分布式ID方式花费时间[" + (end - begin) + "毫秒]");
	}
	
	@Test
	public void testNativeSQLOperations() {
		entityManager.getTransaction().begin();
		entityOperations.persist(prepareUser());
		entityManager.getTransaction().commit();
		List<User> users = nativeSQLOperations.namedSqlQuery("findUser", User.class);
		users.forEach(System.out::println);
	}
	
	@Test
	public void testUserData() {
		User user = prepareUser();
		
		Address address = prepareAddress();
		Address address2 = prepareAddress();
		Address address3 = prepareAddress();
		address2.setStreet("普惠路淞泽家园");
		address3.setStreet("浒关惠丰花园");
		user.getAddresses().add(address);
		user.getAddresses().add(address2);
		user.getAddresses().add(address3);
		
		entityManager.persist(user);
	}

	private Address prepareAddress() {
		Address address = new Address();
		address.setCity("苏州");
		address.setCountry("中国");
		address.setCreator("俞雪华");
		address.setDistrict("工业园区");
		address.setPostalCode("215028");
		address.setStateOrProvince("江苏");
		address.setStreet("钟慧路");
		address.setModifier("System");
		address.setCreator("System");
		return address;
	}

	private User prepareUser() {
		User user = new User();
		user.setBirthday(LocalDate.of(1982, 11, 9));
		user.setCellphone("13913582178");
		user.setCreator("Admin");
		user.setEmail("ricoyu520@gmail.com");
		user.setGender(Gender.MALE);
		user.setLocked(false);
		user.setModifier("Admin");
		user.setPassword("123456");
		user.setSalt("123127t87%^%$$%#*(^");
		user.setUsername("ricoyu");
		user.setName("俞雪华");
		return user;
	}*/
	
}
