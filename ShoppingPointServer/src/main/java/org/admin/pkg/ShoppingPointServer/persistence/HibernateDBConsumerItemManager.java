package org.admin.pkg.ShoppingPointServer.persistence;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.admin.pkg.ShoppingPointServer.common.BookingLogger;
import org.admin.pkg.ShoppingPointServer.common.Messages;
import org.admin.pkg.ShoppingPointServer.models.ConsumerItem;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateDBConsumerItemManager extends AbstractHibernateDatabaseManager {
	
	private static String CONSUMERITEM_TABLE_NAME = "CONSUMERITEM";
	private static String CONSUMERITEM_CLASS_NAME = "ConsumerItem";
	
	private static String SELECT_ALL_ITEMS = "from " + CONSUMERITEM_CLASS_NAME + " as item";
	private static String SELECT_ALL_ITEMS_BY_CATEGORY_AND_BY_NAME = "from " + CONSUMERITEM_CLASS_NAME + 
			" as item where item.category = :cat and item.generalname = :name";
	private static String SELECT_ITEMS_BY_CATEGORY_TYPE = "from " + CONSUMERITEM_CLASS_NAME + 
			" as item where item.category = :cat and item.producttype = :type";
	private static String SELECT_ITEM_BY_CATEGORY_NAME_NUMBER = "from " + CONSUMERITEM_CLASS_NAME + 
			" as item where item.category = :cat and item.specificname = :name and item.productnumber = :num";
	private static String SELECT_ITEM_BY_CATEGORY_TYPE_NAME_NUMBER = "from " + CONSUMERITEM_CLASS_NAME + 
			" as item where item.category = :cat and item.producttype = :type and item.specificname = :name and item.productnumber = :num";
	private static String SELECT_ITEM_WITH_NAME_AND_NUMBER = "from " + CONSUMERITEM_CLASS_NAME + " as item where item.specificname = :name "
			+ "and item.productnumber = :id";
	private static String SELECT_ITEM_WITH_NAME = "from " + CONSUMERITEM_CLASS_NAME + " as item where item.specificname = :name";
	private static String DELETE_ITEM_WITH_NAME = "delete from " + CONSUMERITEM_CLASS_NAME + " where category = :cat and specificname = :name "
			+ "and productnumber = :id";
	private static String GET_ITEM_COUNT = "select count(*) from " + CONSUMERITEM_CLASS_NAME;
	private static String METHOD_GET_OBJECT_WITH_NAME = "getObjectWithName";
	private static String METHOD_GET_ALL = "getAllCounters";
	private static String METHOD_INCREMENT_HIT_COUNTER_BY_NAME_BY = "incrementHitCounterByNameBy";
	private static String RESET_ALL = "resetAllCounters";

	private static final String DROP_TABLE_SQL = "drop table " + CONSUMERITEM_TABLE_NAME + ";";
	private static String CREATE_TABLE_SQL = "create table CONSUMERITEM(PRIMARY_KEY char(36) primary key, "
			+ "CATEGORY text, PRODUCTTYPE text, GENERALNAME text, SPECIFICNAME text, PRODUCTNUMBER text, SHORTDESCRIPTION text, " 
			+ "LONGDESCRIPTION text, QUANTITY text, PRICE text, IMAGENAME text, PRODUCTIMAGE longtext, COUNTER integer, RESET_TIME timestamp);";

	private static HibernateDBConsumerItemManager manager;

	HibernateDBConsumerItemManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * @return
	 */
	public synchronized static HibernateDBConsumerItemManager getDefault() {
		if (manager == null) {
			manager = new HibernateDBConsumerItemManager();
		}
		return manager;
	}

	/**
	 * Returns counter from the database found for a given name.
	 * If not found returns null.
	 * @param name
	 * @return
	 */
	public synchronized ConsumerItem getConsumerItemWithSpecificName(String name) {
		Session session = null;
		ConsumerItem item = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_WITH_NAME);
			query.setParameter("name", name);
			item = (ConsumerItem) query.uniqueResult();
			return item;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return item;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return item;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return item;
		} finally {
			closeSession();
		}
	}
	
	public synchronized ConsumerItem getConsumerItemWithSpecificCategoryNameNumber(String category, String name, String number) {
		Session session = null;
		ConsumerItem item = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_BY_CATEGORY_NAME_NUMBER);
			query.setParameter("cat", category);
			query.setParameter("name", name);
			query.setParameter("num", number);
			item = (ConsumerItem) query.uniqueResult();
			return item;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return item;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return item;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return item;
		} finally {
			closeSession();
		}
	}
	
	public synchronized ConsumerItem getItemByCategoryTypeNameNumber(String category, String type, String name, String number) {
		Session session = null;
		ConsumerItem item = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_BY_CATEGORY_TYPE_NAME_NUMBER);
			query.setParameter("cat", category);
			query.setParameter("type", type);
			query.setParameter("name", name);
			query.setParameter("num", number);
			item = (ConsumerItem) query.uniqueResult();
			return item;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return item;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return item;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return item;
		} finally {
			closeSession();
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<ConsumerItem> getItemsByCategoryAndType(String category, String type) {
		Session session = null;
		List<ConsumerItem> errorResult = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEMS_BY_CATEGORY_TYPE);
			query.setParameter("cat", category);
			query.setParameter("type", type);
			List<ConsumerItem> itemsList = query.list();
			return itemsList;
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
	
	public synchronized ConsumerItem getItemWithSpecificnameAndNumber(String name, String num) {
		Session session = null;
		ConsumerItem item = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ITEM_WITH_NAME_AND_NUMBER);
			query.setParameter("name", name);
			query.setParameter("id", num);
			item = (ConsumerItem) query.uniqueResult();
			return item;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.OBJECT_NOT_FOUND_FAILED,exception);
			return item;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.HIBERNATE_FAILED,exception);
			return item;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,METHOD_GET_OBJECT_WITH_NAME,Messages.GENERIC_FAILED,exception);
			return item;
		} finally {
			closeSession();
		}
	}
	
	public synchronized boolean deleteItemByCategoryNameNumber(String itemCategory, String name, String num){
		int result = 0;
		Session session = null;
		boolean deleteItem = false; 
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(DELETE_ITEM_WITH_NAME);
			query.setParameter("cat", itemCategory);
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

	/**
	 * Returns all counters from the database.
	 * Upon error returns null.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<ConsumerItem> getAllItemsWithGeneralNameAndCategory(String category, String name) {
		Session session = null;
		List<ConsumerItem> errorResult = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_ITEMS_BY_CATEGORY_AND_BY_NAME);
			query.setParameter("cat", category);
			query.setParameter("name", name);
			List<ConsumerItem> itemsList = query.list();
			return itemsList;
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
		return CONSUMERITEM_TABLE_NAME;
	}

	/**
	 * Adds given counter (object) to the database.
	 * Sets counter's reset time to the current time.
	 *  
	 * @return
	 */
	public synchronized boolean add(Object object){
		Calendar calendar = Calendar.getInstance();
		ConsumerItem item = (ConsumerItem) object;
		item.setResetTime(new Timestamp(calendar.getTimeInMillis()));
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
			ConsumerItem consumerItem = (ConsumerItem) query.uniqueResult();
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
			List<ConsumerItem> itemList = query.list();
			for (Iterator<ConsumerItem> iterator = itemList.iterator(); iterator.hasNext();) {
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
		return CONSUMERITEM_CLASS_NAME;
	}
}
