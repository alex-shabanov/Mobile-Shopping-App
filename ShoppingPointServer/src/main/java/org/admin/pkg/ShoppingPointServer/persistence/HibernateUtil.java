package org.admin.pkg.ShoppingPointServer.persistence;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;
	private static String DATABASE_NAME = "social";

	//private static  Session session;

	/**
	* Builds Hibernate session factory based on the Hibernate configuration file.
	*/
	private static void buildSessionFactory(){

	    if (sessionFactory == null) {
	            try {
	                Configuration configuration = new Configuration();
	                configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");  //Should read from file
	                configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?autoReconnect=true"); //Should read from file
	                configuration.setProperty("show_sql", "false");
	                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	                configuration.setProperty("hibernate.connection.password", "logarithm"); //Should read from file or prompt
	                configuration.setProperty("hibernate.connection.username", "Alex"); //Should read from file or prompt
	                configuration.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
	                configuration.setProperty("hibernate.current_session_context_class", "thread");
	                configuration.setProperty("hibernate.connection.autocommit", "true");
	                configuration.setProperty("javax.persistence.validation.mode", "none");
	                configuration.configure();
	                serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
	                        configuration.getProperties()).configure().build();
	                sessionFactory = new MetadataSources( serviceRegistry ).buildMetadata().buildSessionFactory();
	            } catch (HibernateException ex) {
	                throw new RuntimeException("Exception building SessionFactory: " + ex.getMessage(), ex);
	            }
	        }
	}

	public static void clearSessionFactory(){

	    if (sessionFactory != null) {
	            try {
	                sessionFactory.close();
	            } catch (HibernateException ex) {
	                sessionFactory = null;
	                throw new RuntimeException("Exception closing SessionFactory: " + ex.getMessage(), ex);
	            }
	    }
	    sessionFactory = null;
	}

	// Keeps track of all Hibernate threads
	public static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();

	/**
	  * Returns current Hibernate session based on the thread local.
	  * First looks into thread local for a session. If found returns the session.
	  * If not found creates new session and adds it to the thread local.
	  *
	  * @return
	  * @throws HibernateException
	  */
	public synchronized static Session getCurrentSession() throws HibernateException {

	    Session session = (Session) sessionThreadLocal.get();
	    
	    // Open a new Session, if this Thread has none yet
	    if (session == null) {
	        buildSessionFactory();
	        session = sessionFactory.openSession();
	        session.setCacheMode(CacheMode.IGNORE);
	        session.setFlushMode(FlushMode.COMMIT);
	        sessionThreadLocal.set(session);
	    }
	    return session;
	}

	/**
	  * Closes current Hibernate session.
	  * Looks for a current session in thread local, and if found closes the session.
	  * Also sets thread local to null.
	  *
	  * @throws HibernateException
	  */
	public static void closeTheThreadLocalSession() throws HibernateException {

	    Session session = (Session) sessionThreadLocal.get();
	    sessionThreadLocal.set(null);
	    if (session != null) {
	        session.close();
	    }
	    session = null;

	}


	/**
	* Used for initial table setup in the database.
	*
	* @param sql
	* @return
	*/
	public static synchronized boolean executeSQLQuery(String sql) {

	    Session session = null;
	    Transaction transaction = null;

	    try {
	        session = getCurrentSession();
	        transaction = session.beginTransaction();
	        SQLQuery query = session.createSQLQuery(sql);
	        query.executeUpdate();
	        transaction.commit();
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    }
	    finally {
	        closeTheThreadLocalSession();
	    }
	    return true;
	}

	public static void setupTables() {
		HibernateDBAdminAccountsManager.getDefault().setupTable();
	}
}
