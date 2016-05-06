package org.admin.pkg.ShoppingPointServer.persistence;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.admin.pkg.ShoppingPointServer.common.BookingLogger;
import org.admin.pkg.ShoppingPointServer.common.Messages;
import org.admin.pkg.ShoppingPointServer.models.AdminAccountRegistration;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateDBAdminRegistrationManager extends AbstractHibernateDatabaseManager {
	
	private static String ADMINREGISTRATION_TABLE_NAME = "ADMINACCOUNTREGISTRATION";
	private static String ADMINREGISTRATION_CLASS_NAME = "AdminAccountRegistration";

	private static String SELECT_ALL_ADMINREGISTRATION = "from " + ADMINREGISTRATION_CLASS_NAME + " as user";
	private static String SELECT_ADMINREGISTRATION_WITH_NAME = "from " + ADMINREGISTRATION_CLASS_NAME + " as user where user.adminname = :id";
	private static String SELECT_ADMINREGISTRATION_WITH_EMPLOYEEID = "from " + ADMINREGISTRATION_CLASS_NAME + " as user where user.employeeid = :id";
	private static String DELETE_ADMINREGISTRATION_WITH_NAME = "delete from " + ADMINREGISTRATION_CLASS_NAME + " where adminname = :adminname";
	private static String GET_ADMINREGISTRATION_COUNT = "select count(*) from " + ADMINREGISTRATION_CLASS_NAME;
	private static String METHOD_GET_OBJECT_WITH_NAME = "getObjectWithName";
	private static String METHOD_GET_ALL = "getAllCounters";
	private static String METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY = "incrementHitCounterByNameBy";
	private static String RESET_ALL = "resetAllCounters";

	private static final String DROP_TABLE_SQL = "drop table " + ADMINREGISTRATION_TABLE_NAME + ";";
	private static String CREATE_TABLE_SQL = "create table ADMINACCOUNTREGISTRATION(PRIMARY_KEY char(36) primary key, "
			+ "ADMINFIRSTNAME varchar(25), ADMINLASTNAME varchar(25), EMPLOYEEID text, " 
			+ "ADMINNAME varchar(25), ADMINPASSWORD text, COUNTER integer, RESET_TIME timestamp);";

	private static HibernateDBAdminRegistrationManager manager;

	HibernateDBAdminRegistrationManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * @return
	 */
	public synchronized static HibernateDBAdminRegistrationManager getDefault() {
		
		if (manager == null) {
			manager = new HibernateDBAdminRegistrationManager();
		}
		return manager;
	}

	/**
	 * Returns counter from the database found for a given name.
	 * If not found returns null.
	 * @param name
	 * @return
	 */
	public synchronized AdminAccountRegistration getAdmninRegistrationWithName(String name) {
		
		Session session = null;
		AdminAccountRegistration account = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ADMINREGISTRATION_WITH_NAME);
			query.setParameter("id", name);
		    account = (AdminAccountRegistration) query.uniqueResult();
			return account;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return account;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return account;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return account;
		} finally {
			closeSession();
		}
	}
	
public synchronized AdminAccountRegistration getAdmninRegistrationWithEmployeeId(String empId) {
		
		Session session = null;
		AdminAccountRegistration account = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ADMINREGISTRATION_WITH_EMPLOYEEID);
			query.setParameter("id", empId);
		    account = (AdminAccountRegistration) query.uniqueResult();
			return account;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return account;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return account;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return account;
		} finally {
			closeSession();
		}
	}
	
	public synchronized boolean deleteAdminRegistrationByName(String name){
		int result = 0;
		Session session = null;
		boolean deleteAccount = false; 

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(DELETE_ADMINREGISTRATION_WITH_NAME);
			query.setParameter("adminname", name);
			result = query.executeUpdate();
			if(result == 1){ deleteAccount = true; } // the user account is actually deleted from database
			return deleteAccount;
		} catch (ObjectNotFoundException exception) {
			exception.printStackTrace();
			return deleteAccount;
		} catch (HibernateException exception) {
			exception.printStackTrace();	
			return deleteAccount;
		} catch (RuntimeException exception) {
			exception.printStackTrace();
			return deleteAccount;
		} finally {
			closeSession();
		}
	}

	/**
	 * Returns all counters from the database.
	 * Upon error returns null.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<AdminAccountRegistration> getAllAdminRegistrations() {
		
		Session session = null;
		List<AdminAccountRegistration> errorResult = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_ADMINREGISTRATION);
			List<AdminAccountRegistration> accounts = query.list();
			return accounts;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_ALL,
			Messages.OBJECT_NOT_FOUND_FAILED, exception);
			return errorResult;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_ALL,
			Messages.HIBERNATE_FAILED, exception);
			return errorResult;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_ALL,
			Messages.GENERIC_FAILED, exception);
			return errorResult;
		} finally {
			closeSession();
		}
	}

	public synchronized Long getAdminRegistrationCount(){
		Session session = null;
		Long count = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(GET_ADMINREGISTRATION_COUNT);
			count = (Long) query.uniqueResult();
			return count;
		} catch (ObjectNotFoundException exception){
			exception.printStackTrace();
			return count;
		} catch (HibernateException exception){
			exception.printStackTrace();
			return count;
		} catch (RuntimeException exception){
			exception.printStackTrace();
			return count;
		} finally {
			closeSession();
		}
	}
	
	public String getTableName() {
		return ADMINREGISTRATION_TABLE_NAME;
	}

	/**
	 * Adds given counter (object) to the database.
	 * Sets counter's reset time to the current time.
	 *  
	 * @return
	 */
	public synchronized boolean add(Object object){
		Calendar calendar = Calendar.getInstance();
		AdminAccountRegistration account = (AdminAccountRegistration) object;
		account.setResetTime(new Timestamp(calendar.getTimeInMillis()));
		return super.add(object);
	}

	public synchronized void incrementHitCounterByName(String name) {
		incrementHitCounterByNameBy(name, 1);
	}
	
	/**
	 * Increments counter found for given name by given count.
	 * @param name
	 * @param count
	 */
	public synchronized void incrementHitCounterByNameBy(String name, int count) {
		
		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_ADMINREGISTRATION_WITH_NAME);
			query.setParameter("id", name);
			AdminAccountRegistration userAccount = (AdminAccountRegistration) query.uniqueResult();
			if (userAccount != null) {
				userAccount.setCounter(userAccount.getCounter() + count);
				session.update(userAccount);
				transaction.commit();
			} else {
				BookingLogger.getDefault().severe(this,
						METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY,
						Messages.OBJECT_NOT_FOUND_FAILED + ":" + name, null);
			}
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,
					METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY,
					Messages.OBJECT_NOT_FOUND_FAILED, exception);
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY,
					Messages.HIBERNATE_FAILED, exception);
			exception.printStackTrace();
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,
					METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY,
					Messages.GENERIC_FAILED, exception);
		} finally {
			closeSession();
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized boolean resetAllCounters() {
		
		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_ALL_ADMINREGISTRATION);
			List<AdminAccountRegistration> userList = query.list();
			for (Iterator<AdminAccountRegistration> iterator = userList.iterator(); iterator.hasNext();) {
				iterator.next().reset();
			}
			transaction.commit();
			return true;
		} catch (ObjectNotFoundException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, RESET_ALL, Messages.OBJECT_NOT_FOUND_FAILED, exception);
			return false;
		} catch (HibernateException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, RESET_ALL, Messages.HIBERNATE_FAILED, exception);
			return false;
		} catch (RuntimeException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, RESET_ALL, Messages.GENERIC_FAILED, exception);
			return false;
		} finally {
			closeSession();
		}
	}
	
	public boolean setupTable() {
		HibernateUtil.executeSQLQuery(DROP_TABLE_SQL);
		return HibernateUtil.executeSQLQuery(CREATE_TABLE_SQL);
	}

	public String getClassName() {
		return ADMINREGISTRATION_CLASS_NAME;
	}
}
