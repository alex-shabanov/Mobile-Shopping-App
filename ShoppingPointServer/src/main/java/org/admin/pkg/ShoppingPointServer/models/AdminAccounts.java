package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;
import org.admin.pkg.ShoppingPointServer.common.Messages;

public class AdminAccounts {
	
	private String primaryKey;
	private String adminname;
	private String adminpassword;
	private String loginstatus;
	private long counter;
	private Timestamp resetTime;
	private static long ZERO = 0;
	
	public AdminAccounts(String adminname, String adminpassword, String status, long counter){
		Calendar calendar = Calendar.getInstance();
		setAdminname(adminname);
		setAdminpassword(adminpassword);
		setLoginstatus(status);
		setCounter(counter);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
	}
	
	public AdminAccounts() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, ZERO);
	}
	
	public void setAdminname(String adminname) {
		this.adminname = adminname;
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
	
	public String getLoginstatus() {
		return loginstatus;
	}

	public void setLoginstatus(String loginstatus) {
		this.loginstatus = loginstatus;
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
	
	public AdminAccounts copy(AdminAccounts user){
		AdminAccounts newAdminAccount = new AdminAccounts(getAdminname(),getAdminpassword(), getLoginstatus(), getCounter());
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
		return getAdminname() + " : " + getAdminpassword();
	}
}
