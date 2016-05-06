package org.admin.pkg.ShoppingPointServer.persistence;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.admin.pkg.ShoppingPointServer.common.BookingLogger;
import org.admin.pkg.ShoppingPointServer.common.Messages;
import org.admin.pkg.ShoppingPointServer.models.Customer;
import org.admin.pkg.ShoppingPointServer.models.CustomerOrders;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class HibernateDBCustomerOrdersManager extends AbstractHibernateDatabaseManager {
	
	private static String CUSTOMERORDERS_TABLE_NAME = "CUSTOMERORDERS";
	private static String CUSTOMERORDERS_CLASS_NAME = "CustomerOrders";
	
	private static String SELECT_ALL_ITEMS = "from " + CUSTOMERORDERS_CLASS_NAME + " as item";
	
	private static String SELECT_ITEMS_BY_TYPE = "from " + CUSTOMERORDERS_CLASS_NAME + 
			" as itme where item.producttype = :type";
	
	private static String SELECT_ITEM_BY_NAME_NUMBER = "from " + CUSTOMERORDERS_CLASS_NAME + 
			" as item where item.specificname = :name and item.productnumber = :num";
	private static String SELECT_ITEM_WITH_NAME_AND_NUMBER = "from " + CUSTOMERORDERS_CLASS_NAME + " as item where item.specificname = :name "
			+ "and item.productnumber = :id";
	private static String SELECT_ITEM_WITH_NAME = "from " + CUSTOMERORDERS_CLASS_NAME + " as item where item.specificname = :name";
	private static String DELETE_ITEM_WITH_NAME = "delete from " + CUSTOMERORDERS_CLASS_NAME + " where specificname = :name "
			+ "and productnumber = :id";
	private static String GET_ITEM_COUNT = "select count(*) from " + CUSTOMERORDERS_CLASS_NAME;
	private static String METHOD_GET_OBJECT_WITH_NAME = "getObjectWithName";
	
	private static String METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY = "incrementHitCounterByNameBy";
	private static String RESET_ALL = "resetAllCounters";
	private static String METHOD_GET_ALL = "getAllCounters";
	private static final String DROP_TABLE_SQL = "drop table " + CUSTOMERORDERS_TABLE_NAME + ";";
	
	private static String CREATE_TABLE_SQL = "create table CUSTOMERORDERS(PRIMARY_KEY char(36) primary key, ORDERNUMBER text, "
			+ "PRODUCTTYPE text, GENERALNAME text, SPECIFICNAME text, PRODUCTNUMBER text, " 
			+ "QUANTITY text, PRICE text, COUNTER integer, RESET_TIME timestamp, CUSTOMER_ID_FK char(36));";

	private static HibernateDBCustomerOrdersManager manager;

	HibernateDBCustomerOrdersManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * @return
	 */
	public synchronized static HibernateDBCustomerOrdersManager getDefault() {
		if (manager == null) {
			manager = new HibernateDBCustomerOrdersManager();
		}
		return manager;
	}

	/**
	 * Returns counter from the database found for a given name.
	 * If not found returns null.
	 * @param name
	 * @return
	 */
	public synchronized CustomerOrders getOrderWithSpecificName(String name) {
		Session session = null;
		CustomerOrders order = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_WITH_NAME);
			query.setParameter("name", name);
			order = (CustomerOrders) query.uniqueResult();
			return order;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return order;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return order;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return order;
		} finally {
			closeSession();
		}
	}
	
public synchronized CustomerOrders getCustomerOrderWithHighestId() {
		
		Session session = null;
		CustomerOrders order = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Criteria cr = session.createCriteria(CustomerOrders.class);
			cr.addOrder(Order.desc("ordernumber"));
			cr.setMaxResults(1);
			order = (CustomerOrders) cr.uniqueResult();
			return order;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return order;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return order;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return order;
		} finally {
			closeSession();
		}
	}

	public synchronized CustomerOrders getCustomerOrderWithSpecificNameNumber(String name, String number) {
		Session session = null;
		CustomerOrders order = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_BY_NAME_NUMBER);
			query.setParameter("name", name);
			query.setParameter("num", number);
			order = (CustomerOrders) query.uniqueResult();
			return order;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return order;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return order;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return order;
		} finally {
			closeSession();
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<CustomerOrders> getOrdersByType(String type) {
		Session session = null;
		List<CustomerOrders> errorResult = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEMS_BY_TYPE);
			query.setParameter("type", type);
			List<CustomerOrders> ordersList = query.list();
			return ordersList;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return errorResult;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return errorResult;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return errorResult;
		} finally {
			closeSession();
		}
	}
	
public synchronized List<CustomerOrders> getAllCustomerOrders() {
		
		Session session = null;
		List<CustomerOrders> errorResult = null;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_ITEMS);
			List<CustomerOrders> orders = query.list();
			return orders;
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
	
	public synchronized CustomerOrders getOrdersWithSpecificnameAndNumber(String name, String num) {
		Session session = null;
		CustomerOrders order = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_WITH_NAME_AND_NUMBER);
			query.setParameter("name", name);
			query.setParameter("id", num);
			order = (CustomerOrders) query.uniqueResult();
			return order;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return order;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return order;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return order;
		} finally {
			closeSession();
		}
	}
	
	public synchronized boolean deleteOrderByNameNumber(String name, String num){
		int result = 0;
		Session session = null;
		boolean deleteItem = false; 
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(DELETE_ITEM_WITH_NAME);
			query.setParameter("name", name);
			query.setParameter("id", num);
			result = query.executeUpdate();
			if(result == 1){ deleteItem = true; } // the consumer item is deleted from database
			return deleteItem;
		} catch (ObjectNotFoundException exception) {
			exception.printStackTrace();
			return deleteItem;
		} catch (HibernateException exception) {
			exception.printStackTrace();	
			return deleteItem;
		} catch (RuntimeException exception) {
			exception.printStackTrace();
			return deleteItem;
		} finally {
			closeSession();
		}
	}

	public synchronized Long getConsumerItemCount(){
		Session session = null;
		Long count = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(GET_ITEM_COUNT);
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
		return CUSTOMERORDERS_TABLE_NAME;
	}

	/**
	 * Adds given counter (object) to the database.
	 * Sets counter's reset time to the current time.
	 *  
	 * @return
	 */
	public synchronized boolean add(Object object){
		Calendar calendar = Calendar.getInstance();
		CustomerOrders order = (CustomerOrders) object;
		order.setResetTime(new Timestamp(calendar.getTimeInMillis()));
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
			Query query = session.createQuery(SELECT_ITEM_WITH_NAME);
			query.setParameter("name", name);
			CustomerOrders consumerItem = (CustomerOrders) query.uniqueResult();
			if (consumerItem != null) {
				consumerItem.setCounter(consumerItem.getCounter() + count);
				session.update(consumerItem);
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
			Query query = session.createQuery(SELECT_ALL_ITEMS);
			List<CustomerOrders> itemList = query.list();
			for (Iterator<CustomerOrders> iterator = itemList.iterator(); iterator.hasNext();) {
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
		return CUSTOMERORDERS_CLASS_NAME;
	}
}
