package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.admin.pkg.ShoppingPointServer.common.Messages;

public class Customer {
	
	private String customerIdPrimaryKey;
	private String firstname;
	private String lastname;
	private String address;
	private String city;
	private String country;
	private String areacode;
	private String email;
	private String phonenumber;
	private String username;
	private String customerid;
	private long counter;
	private Timestamp resetTime;
	private Set<CustomerOrders> customerorders;
	private static final long ZERO = 0;
	
	public Customer(String fname, String lname, String addr, String cty, String ctry, String areacd,
			String mail, String phnum, String uname, String id, long counter){
		Calendar calendar = Calendar.getInstance();
		setFirstname(fname);
		setLastname(lname);
		setAddress(addr);
		setCity(cty);
		setCountry(ctry);
		setAreacode(areacd);
		setEmail(mail);
		setPhonenumber(phnum);
		setUsername(uname);
		setCustomerid(id);
		setCounter(counter);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
		setCustomerorders(new HashSet<CustomerOrders>());
	}

	public Customer() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN,
				Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN,
				Messages.UNKNOWN, ZERO);
	}
	
	public void setCustomerIdPrimaryKey(String primaryKey) {this.customerIdPrimaryKey = primaryKey;}
	public String getCustomerIdPrimaryKey() {return this.customerIdPrimaryKey;}
	public void setFirstname(String name) {this.firstname = name;}
	public String getFirstname() {return this.firstname;}
	public void setLastname(String name) {this.lastname = name;}
	public String getLastname() {return this.lastname;}
	public void setAddress(String addr) {this.address = addr;}
	public String getAddress() {return this.address;}
	public void setCity(String cty){this.city = cty;}
	public String getCity(){return this.city;}
	public void setCountry(String ctry){this.country = ctry;}
	public String getCountry(){return this.country;}
	public void setAreacode(String code){this.areacode = code;}
	public String getAreacode(){return this.areacode;}
	public void setEmail(String mail){this.email = mail;}
	public String getEmail(){return this.email;}
	public void setPhonenumber(String phnum){this.phonenumber = phnum;}
	public String getPhonenumber(){return this.phonenumber;}
	public void setUsername(String name){this.username = name;}
	public String getUsername(){return this.username;}
	public void setCustomerid(String id){this.customerid = id;}
	public String getCustomerid(){return this.customerid;}
	public void setCounter(long counter) {this.counter = counter;}
	public long getCounter() {return this.counter;}
	public void increment() {counter = counter + 1;}
	public Set<CustomerOrders> getCustomerorders() {return customerorders;}
	public void setCustomerorders(Set<CustomerOrders> customerorders) {this.customerorders = customerorders;}
	public boolean equals(Customer acc) {return getCustomerIdPrimaryKey().equals(acc.getCustomerIdPrimaryKey());}
	
	public void addCustomerOrders(CustomerOrders order) {
		getCustomerorders().add(order);
	}
	
	public Customer copy(Customer customer){
		Customer newCustomer = new Customer(getFirstname(), getLastname(), getAddress(), getCity(),
				getCountry(), getAreacode(), getEmail(), getPhonenumber(), getUsername(), 
				getCustomerid(), getCounter());
		newCustomer.setCustomerIdPrimaryKey(customer.getCustomerIdPrimaryKey());
		newCustomer.setResetTime(getResetTime());
		return newCustomer;
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
		return getFirstname() + " : " + getLastname() + " : " + getEmail();
	}
}
