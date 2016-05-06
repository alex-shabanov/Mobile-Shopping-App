package org.admin.pkg.ShoppingPointServer.persistence;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.admin.pkg.ShoppingPointServer.common.BookingLogger;
import org.admin.pkg.ShoppingPointServer.common.Messages;
import org.admin.pkg.ShoppingPointServer.models.CustomerAccount;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class HibernateDBCustomerAccountManager extends AbstractHibernateDatabaseManager {
	
	private static String CUSTOMERACCOUNT_TABLE_NAME = "CUSTOMERACCOUNT";
	private static String CUSTOMERACCOUNT_CLASS_NAME = "CustomerAccount";

	private static String SELECT_ALL_CUSTOMERACCOUNTS = "from " + CUSTOMERACCOUNT_CLASS_NAME + " as user";
	private static String SELECT_CUSTOMERACCOUNT_WITH_USERNAME = "from " + CUSTOMERACCOUNT_CLASS_NAME + " as user where user.username = :name";
	private static String DELETE_CUSTOMERACCOUNT_WITH_NAME = "delete from " + CUSTOMERACCOUNT_CLASS_NAME + " where username = :name";
	private static String GET_ADMINACCOUNT_COUNT = "select count(*) from " + CUSTOMERACCOUNT_CLASS_NAME;
	private static String METHOD_GET_OBJECT_WITH_NAME = "getObjectWithName";
	private static String METHOD_GET_ALL = "getAllCounters";
	private static String METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY = "incrementHitCounterByNameBy";
	private static String RESET_ALL = "resetAllCounters";
	
	private static final String DROP_TABLE_SQL = "drop table " + CUSTOMERACCOUNT_TABLE_NAME + ";";
	
	private static String CREATE_TABLE_SQL = "create table CUSTOMERACCOUNT(PRIMARY_KEY char(36) primary key, "
			+ "FIRSTNAME text, LASTNAME text, ADDRESS text, CITY text, COUNTRY text, AREACODE text, EMAIL text, "
			+ "PHONENUMBER text, USERNAME text, PASSWORD text, CUSTOMERID text, COUNTER integer, RESET_TIME timestamp);";
	
	private static HibernateDBCustomerAccountManager manager;

	HibernateDBCustomerAccountManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * @return
	 */
	public synchronized static HibernateDBCustomerAccountManager getDefault() {
		
		if (manager == null) {
			manager = new HibernateDBCustomerAccountManager();
		}
		return manager;
	}

	/**
	 * Returns counter from the database found for a given name.
	 * If not found returns null.
	 * @param name
	 * @return
	 */
	public synchronized CustomerAccount getCustomerAccountByUsername(String name) {
		
		Session session = null;
		CustomerAccount account = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_CUSTOMERACCOUNT_WITH_USERNAME);
			query.setParameter("name", name);
		    account = (CustomerAccount) query.uniqueResult();
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
	
	public synchronized CustomerAccount getCustomerAccountWithHighestId() {
		
		Session session = null;
		CustomerAccount account = null;

		try {
			session = HibernateUtil.getCurrentSession();
			//Query query = session.createQuery(SELECT_CUSTOMERACCOUNT_WITH_MAX_ID);
			Criteria cr = session.createCriteria(CustomerAccount.class);
			cr.addOrder(Order.desc("customerid"));
			cr.setMaxResults(1);
			account = (CustomerAccount) cr.uniqueResult();
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
	
	public synchronized boolean deleteCustomerAccountByName(String name){
		int result = 0;
		Session session = null;
		boolean deleteAccount = false; 

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(DELETE_CUSTOMERACCOUNT_WITH_NAME);
			query.setParameter("name", name);
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
	public synchronized List<CustomerAccount> getAllCustomerAccounts() {
		
		Session session = null;
		List<CustomerAccount> errorResult = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_CUSTOMERACCOUNTS);
			List<CustomerAccount> accounts = query.list();
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

	public synchronized Long getCustomerCount(){
		Session session = null;
		Long count = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(GET_ADMINACCOUNT_COUNT);
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
		return CUSTOMERACCOUNT_TABLE_NAME;
	}

	/**
	 * Adds given counter (object) to the database.
	 * Sets counter's reset time to the current time.
	 *  
	 * @return
	 */
	public synchronized boolean add(Object object){
		Calendar calendar = Calendar.getInstance();
		CustomerAccount account = (CustomerAccount) object;
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
			Query query = session.createQuery(SELECT_CUSTOMERACCOUNT_WITH_USERNAME);
			query.setParameter("name", name);
			CustomerAccount userAccount = (CustomerAccount) query.uniqueResult();
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
			Query query = session.createQuery(SELECT_ALL_CUSTOMERACCOUNTS);
			List<CustomerAccount> userList = query.list();
			for (Iterator<CustomerAccount> iterator = userList.iterator(); iterator.hasNext();) {
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
		return CUSTOMERACCOUNT_CLASS_NAME;
	}
}
