package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.admin.pkg.ShoppingPointServer.common.Messages;


@XmlRootElement
public class CustomerOrders {

	private String primaryKey;
	private String ordernumber;
	private String producttype;
	private String generalname;
	private String specificname;
	private String productnumber;
	private String quantity;
	private String price;
	private long counter;
	private Timestamp resetTime;
	private static long ZERO = 0;
	private Customer customer;
	
	public CustomerOrders(String ordernum, String productType, String generalName, String specificName, String productNumber,
			String qty, String cost, long counter){
		Calendar calendar = Calendar.getInstance();
		setOrdernumber(ordernum);
		setProducttype(productType);
		setGeneralname(generalName);
		setSpecificname(specificName);
		setProductnumber(productNumber);	
		setQuantity(qty);
		setPrice(cost);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
	}
	
	public CustomerOrders() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, ZERO);
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getOrdernumber() {
		return ordernumber;
	}
	
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	
	public String getProducttype() {
		return producttype;
	}
	
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}
	
	public String getGeneralname() {
		return generalname;
	}
	
	public void setGeneralname(String generalname) {
		this.generalname = generalname;
	}
	
	public String getSpecificname() {
		return specificname;
	}
	
	public void setSpecificname(String specificname) {
		this.specificname = specificname;
	}
	
	public String getProductnumber() {
		return productnumber;
	}
	
	public void setProductnumber(String productnumber) {
		this.productnumber = productnumber;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public CustomerOrders copy(CustomerOrders order){
		CustomerOrders newConsumerOrder = new CustomerOrders(getOrdernumber(), getProducttype(), getGeneralname(), getSpecificname(),
				getProductnumber(), getQuantity(), getPrice(), getCounter());
		newConsumerOrder.setPrimaryKey(order.getPrimaryKey());
		newConsumerOrder.setResetTime(getResetTime());
		return newConsumerOrder;
	}
	
	public void setResetTime(Timestamp resetTime) {
		this.resetTime = resetTime;
	}
	
	public void reset() {
		Calendar calendar = Calendar.getInstance();
		setCounter(0);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));	
	}

	public Timestamp getResetTime() {
		return this.resetTime;
	}
	
	public String toString() {
		return getGeneralname() + " : " + getSpecificname();
	}
}
