package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;


import javax.xml.bind.annotation.XmlRootElement;
import org.admin.pkg.ShoppingPointServer.common.Messages;

@XmlRootElement
public class CustomerAccount {
	
	private String primaryKey;
	private String firstname;
	private String lastname;
	private String address;
	private String city;
	private String country;
	private String areacode;
	private String email;
	private String phonenumber;
	private String username;
	private String password;
	private String customerid;
	private long counter;
	private Timestamp resetTime;
	private static long ZERO = 0;
	
	public CustomerAccount(String fname, String lname, String addr, String cty, String ctry, String areacd,
			String mail, String phnum, String uname, String pswd, String id, long counter){
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
		setPassword(pswd);
		setCustomerid(id);
		setCounter(counter);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
	}
	
	public CustomerAccount() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN,
				Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN,
				Messages.UNKNOWN, ZERO);
	}
	
	public void setPrimaryKey(String primaryKey) {this.primaryKey = primaryKey;}
	public String getPrimaryKey() {return this.primaryKey;}
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
	public void setPassword(String pswd){this.password = pswd;}
	public String getPassword(){return this.password;}
	public void setCustomerid(String id){this.customerid = id;}
	public String getCustomerid(){return this.customerid;}
	public void setCounter(long counter) {this.counter = counter;}
	public long getCounter() {return this.counter;}
	public void increment() {counter = counter + 1;}
	
	public CustomerAccount copy(CustomerAccount customer){
		CustomerAccount newCustomerAccount = new CustomerAccount(getFirstname(), getLastname(), getAddress(), getCity(),
				getCountry(), getAreacode(), getEmail(), getPhonenumber(), getUsername(), getPassword(),
				getCustomerid(), getCounter());
		newCustomerAccount.setPrimaryKey(customer.getPrimaryKey());
		newCustomerAccount.setResetTime(getResetTime());
		return newCustomerAccount;
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
