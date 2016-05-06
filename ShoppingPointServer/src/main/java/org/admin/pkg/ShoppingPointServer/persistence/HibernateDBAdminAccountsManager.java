package org.admin.pkg.ShoppingPointServer.persistence;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.admin.pkg.ShoppingPointServer.common.BookingLogger;
import org.admin.pkg.ShoppingPointServer.common.Messages;
import org.admin.pkg.ShoppingPointServer.models.AdminAccounts;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateDBAdminAccountsManager extends AbstractHibernateDatabaseManager {
	
	private static String ADMINACCOUNT_TABLE_NAME = "ADMINACCOUNTS";
	private static String ADMINACCOUNT_CLASS_NAME = "AdminAccounts";

	private static String SELECT_ALL_ADMINACCOUNTS = "from " + ADMINACCOUNT_CLASS_NAME + " as user";
	private static String SELECT_ADMINACCOUNT_WITH_NAME = "from " + ADMINACCOUNT_CLASS_NAME + " as user where user.adminname = :id";
	private static String DELETE_ADMINACCOUNT_WITH_NAME = "delete from " + ADMINACCOUNT_CLASS_NAME + " where adminname = :adminname";
	private static String GET_ADMINACCOUNT_COUNT = "select count(*) from " + ADMINACCOUNT_CLASS_NAME;
	private static String METHOD_GET_OBJECT_WITH_NAME = "getObjectWithName";
	private static String METHOD_GET_ALL = "getAllCounters";
	private static String METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY = "incrementHitCounterByNameBy";
	private static String RESET_ALL = "resetAllCounters";

	private static final String DROP_TABLE_SQL = "drop table " + ADMINACCOUNT_TABLE_NAME + ";";
	private static String CREATE_TABLE_SQL = "create table ADMINACCOUNTS(PRIMARY_KEY char(36) primary key, "
			+ "ADMINNAME varchar(25), ADMINPASSWORD text, LOGINSTATUS text, COUNTER integer, RESET_TIME timestamp);";

	private static HibernateDBAdminAccountsManager manager;

	HibernateDBAdminAccountsManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * @return
	 */
	public synchronized static HibernateDBAdminAccountsManager getDefault() {
		
		if (manager == null) {
			manager = new HibernateDBAdminAccountsManager();
		}
		return manager;
	}

	/**
	 * Returns counter from the database found for a given name.
	 * If not found returns null.
	 * @param name
	 * @return
	 */
	public synchronized AdminAccounts getAdmninAccountWithName(String name) {
		
		Session session = null;
		AdminAccounts account = null;

		try {
			session = HibernateUtil.getCurrentSession();		
			Query query = session.createQuery(SELECT_ADMINACCOUNT_WITH_NAME);
			query.setParameter("id", name);
		    account = (AdminAccounts) query.uniqueResult();
			return account;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return account;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return account;
		} catch (RuntimeException exception) {
			//exception.printStackTrace();
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return account;
		} finally {
			closeSession();
		}
	}
	
	public synchronized boolean deleteUserAccountByName(String name){
		int result = 0;
		Session session = null;
		boolean deleteAccount = false; 

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(DELETE_ADMINACCOUNT_WITH_NAME);
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
	public synchronized List<AdminAccounts> getAllUserAccounts() {
		
		Session session = null;
		List<AdminAccounts> errorResult = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_ADMINACCOUNTS);
			List<AdminAccounts> accounts = query.list();
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

	public synchronized Long getUserCount(){
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
		return ADMINACCOUNT_TABLE_NAME;
	}

	/**
	 * Adds given counter (object) to the database.
	 * Sets counter's reset time to the current time.
	 *  
	 * @return
	 */
	public synchronized boolean add(Object object){
		Calendar calendar = Calendar.getInstance();
		AdminAccounts account = (AdminAccounts) object;
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
			Query query = session.createQuery(SELECT_ADMINACCOUNT_WITH_NAME);
			query.setParameter("id", name);
			AdminAccounts userAccount = (AdminAccounts) query.uniqueResult();
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
			Query query = session.createQuery(SELECT_ALL_ADMINACCOUNTS);
			List<AdminAccounts> userList = query.list();
			for (Iterator<AdminAccounts> iterator = userList.iterator(); iterator.hasNext();) {
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
		return ADMINACCOUNT_CLASS_NAME;
	}
}
