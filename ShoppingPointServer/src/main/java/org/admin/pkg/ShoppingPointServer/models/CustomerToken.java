package org.admin.pkg.ShoppingPointServer.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomerToken {

	boolean creditcardstatus;
	String statusmessage;
	
	public CustomerToken(boolean status, String message){
		setCreditcardstatus(status);
		setStatusmessage(message);
	}
	
	public CustomerToken(){
		
	}

	public void setCreditcardstatus(boolean creditcardstatus) {
		this.creditcardstatus = creditcardstatus;
	}
	
	public boolean getCreditcardstatus() {
		return creditcardstatus;
	}

	public void setStatusmessage(String statusmessage) {
		this.statusmessage = statusmessage;
	}
	
	public String getStatusmessage() {
		return statusmessage;
	}
}
