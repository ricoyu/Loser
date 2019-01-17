package com.loserico.orm.hibernate;

import java.time.LocalDate;
import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.orm.operations.User;
import com.peacefish.orm.commons.enums.Gender;

public class HelloHibernate {

	Logger log = LoggerFactory.getLogger(HelloHibernate.class);

	@Test
	public void testBootstrap() {
		BootstrapServiceRegistryBuilder bootstrapRegistryBuilder = new BootstrapServiceRegistryBuilder();
		BootstrapServiceRegistry bootstrapRegistry = bootstrapRegistryBuilder.build();
	}
	/*
		@Test
		public void testEntityManager() {
			//		Session session = HibernateUtil.getSession();
			//		Transaction tx = session.beginTransaction();
			EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
			EntityTransaction ex = entityManager.getTransaction();
			ex.begin();
	
			User rico = new User();
			Calendar calendar = Calendar.getInstance();
			calendar.set(1982, 11, 9);
			rico.setBirthday(calendar.getTime());
			rico.setCellphone("13913582186");
			rico.setCreator("System");
			rico.setLastModifier("system");
			rico.setGender(Gender.MALE);
			rico.setLocked(true);
			rico.setEmail("ricoyu_521@163.com");
			rico.setPassword("loserico");
			rico.setUsername("ricoyu");
			rico.setName("Rico Yu");
			rico.setSalt("12");
	
			Address address = new Address();
			address.setCountry("中国");
			address.setStateOrProvince("江苏");
			address.setCity("苏州");
			address.setDistrict("工业园区");
			address.setStreet("钟慧路湖畔天城");
			address.setPostalCode("215028");
			address.setCreator("loserico");
			address.setLastModifier("loserico");
			//		address.setUser(rico);
			//		
			List<Address> addresses = new ArrayList<Address>();
			addresses.add(address);
			rico.setAddresses(addresses);
			//		rico.setAddress(address);
			entityManager.persist(rico);
			//
			//		session.persist(rico);
			//		tx.commit();
			//		session.close();
			ex.commit();
		}*/

	@Test
	public void testSessionFactory() {
		//		Session session = HibernateUtil.getSession();
		//		Transaction tx = session.beginTransaction();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		tx.begin();

		User rico = new User();
		Calendar calendar = Calendar.getInstance();
		rico.setBirthday(LocalDate.of(1982, 11, 9));
		rico.setCellphone("13913582186");
		rico.setCreator("System");
		rico.setModifier("system");
		rico.setGender(Gender.MALE);
		rico.setEmail("ricoyu_521@163.com");
		rico.setPassword("loserico");
		rico.setLocked(false);
		rico.setUsername("ricoyu");
		rico.setName("Rico Yu");

		/*Address address = new Address();
		address.setCountry("中国");
		address.setStateOrProvince("江苏");
		address.setCity("苏州");
		address.setDistrict("工业园区");
		address.setStreet("钟慧路湖畔天城");
		address.setPostalCode("215028");
		address.setCreator("loserico");
		address.setModifier("loserico");
		//		address.setUser(rico);
		//		
		Set<Address> addresses = new HashSet<Address>();
		addresses.add(address);
		rico.setAddresses(addresses);*/
		//		rico.setAddress(address);
		//
		session.persist(rico);
		tx.commit();
		session.close();
	}
	/*
		@Test
		public void testEntityManagerNamedQuery() {
			HibernateDao baseHibernateDao = new HibernateDao();
			baseHibernateDao.setHibernateQueryMode("dev");
			EntityManagerFactory entityManagerFactory = JPAUtil.getEntityManagerFactory();
			baseHibernateDao.setEntityManagerFactory(entityManagerFactory);
			List<User> users = baseHibernateDao.namedSqlQuery("User.byUsername", "username", "ricoyu", User.class);
			for (User user : users) {
				System.out.println(user);
			}
		}*/

	/*	@Test
		public void testBaseHibernateDao() {
			HibernateDao baseHibernateDao = new HibernateDao();
			baseHibernateDao.setHibernateQueryMode("dev");
			SessionFactory sessionFactory = PersistenceUtils.getSessionFactory();
			sessionFactory.openSession();
			baseHibernateDao.setSessionFactory(sessionFactory);
			
			//		List<User2> users = baseHibernateDao.namedSqlQuery("User.findByName", User2.class);
			//		for (User2 user2 : users) {
			//			System.out.println(user2);
			//		}
			//		
			//		List<User> users2 = baseHibernateDao.namedQuery("User.findByUsername", "username", "ricoyu");
			//		for (User user : users2) {
			//			System.out.println(user);
			//		}
			//		
			List<User> users3 = baseHibernateDao.namedQuery("User.findWithAddressesByUsername", "username", "ricoyu");
			for (User user : users3) {
				System.out.println(user);
			}
		}*/

	/*	@Test
		public void testSecondLevelCache() {
			Session session = PersistenceUtils.getSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			User user = session.get(User.class, 1);
			System.out.println(user);
			tx.commit();
			PersistenceUtils.closeSession(session);
			session = PersistenceUtils.getSession();
			tx = session.beginTransaction();
			tx.begin();
			session.beginTransaction();
			User user2 = session.get(User.class, 1);
			System.out.println(user2);
			tx.commit();
			PersistenceUtils.closeSession(session);
	
		}*/
	/*
		@Test
		public void testSecondLevelCacheWithNamedQuery() {
			Session session = PersistenceUtils.getSession();
			Transaction tx = session.beginTransaction();
			tx.begin();
			User user = (User) session.getNamedQuery("User.ricoyu").setParameter("username", "ricoyu").list().get(0);
			System.out.println(user);
			tx.commit();
			PersistenceUtils.closeSession(session);
			session = PersistenceUtils.getSession();
			tx = session.beginTransaction();
			tx.begin();
			session.beginTransaction();
			session.getNamedQuery("User.ricoyu").setParameter("username", "rico").list();
			session.getNamedQuery("User.ricoyu").setParameter("username", "rico").list();
			tx.commit();
			PersistenceUtils.closeSession(session);
	
		}*/

	private void printUser(User user) {
		/*System.out.println(user);
		Set<Address> addresses = user.getAddresses();
		for (Address address : addresses) {
			System.out.println(address);
		}*/
	}

}
