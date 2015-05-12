package za.co.smartcall.smartload.model;

import lombok.extern.log4j.Log4j;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Fasctory to create Hibernate session used for all d
 * @author rudig
 *
 */
@Log4j
public class HibernateUtil {
	
	
	private static SessionFactory sessionFactory;
	
	static  {
			try {
				Configuration configuration = new Configuration().configure();
				StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
				applySettings(configuration.getProperties());
				sessionFactory = configuration.buildSessionFactory(builder.build());
			} catch (Throwable ex) {
				log.error(ex);
				throw new ExceptionInInitializerError(ex);
			}
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public static void shutdown(){
		getSessionFactory().close();
	}
}
