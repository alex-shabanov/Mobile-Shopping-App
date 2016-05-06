package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;

import org.admin.pkg.ShoppingPointServer.common.Messages;

public class AdminAccountRegistration {
	
	private String primaryKey;
	private String adminfirstname;
	private String adminlastname;
	private String employeeid;
	private String adminname;
	private String adminpassword;
	private long counter;
	private Timestamp resetTime;
	private static long ZERO = 0;
	
	public AdminAccountRegistration(String firstname, String lastname, String id, String username, String adminpassword, long counter){
		Calendar calendar = Calendar.getInstance();
		setAdminfirstname(firstname);
		setAdminlastname(lastname);
		setEmployeeid(id);
		setAdminname(username);
		setAdminpassword(adminpassword);
		setCounter(counter);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
	}
	
	public AdminAccountRegistration() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, ZERO);
	}
	
	public void setAdminfirstname(String firstname) {
		this.adminfirstname = firstname;
	}
	
	public String getAdminfirstname() {
		return this.adminfirstname;
	}
	
	public void setAdminlastname(String lastname) {
		this.adminlastname = lastname;
	}
	
	public String getAdminlastname() {
		return this.adminlastname;
	}
	
	public void setEmployeeid(String id){
		this.employeeid = id;
	}
	
	public String getEmployeeid(){
		return this.employeeid;
	}
	
	public void setAdminname(String username) {
		this.adminname = username;
	}
	
	public String getAdminname() {
		return this.adminname;
	}
	
	public void setAdminpassword(String password){
		this.adminpassword = password;
	}
	
	public String getAdminpassword(){
		return this.adminpassword;
	}
	
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	
	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	public long getCounter() {
		return this.counter;
	}
	
	public void increment() {
		counter = counter + 1;
	}
	
	public AdminAccountRegistration copy(AdminAccountRegistration user){
		AdminAccountRegistration newAdminAccount = new AdminAccountRegistration(getAdminfirstname(), getAdminlastname(),
				getEmployeeid(), getAdminname(), getAdminpassword(), getCounter());
		newAdminAccount.setPrimaryKey(user.getPrimaryKey());
		newAdminAccount.setResetTime(getResetTime());
		return newAdminAccount;
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
		return getAdminname() + " : " + getEmployeeid() + " : " + getAdminpassword();
	}
}
