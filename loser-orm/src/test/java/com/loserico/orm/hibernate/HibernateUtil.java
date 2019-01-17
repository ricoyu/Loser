package com.loserico.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	/*static {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).addAnnotatedClass(User.class).addAnnotatedClass(Address.class)
					.buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
			// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
			// so destroy it manually.
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}*/
	
	public static Session getSession() {
		if (sessionFactory == null) {
			return null;
		}
		return sessionFactory.openSession();
	}
	
}
