package org.admin.pkg.ShoppingPointServer.models;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import org.admin.pkg.ShoppingPointServer.common.Messages;

@XmlRootElement
public class ConsumerItem {
	
	private String primaryKey;
	private String category;
	private String producttype;
	private String generalname;
	private String specificname;
	private String productnumber;
	private String shortdescription;
	private String longdescription;
	private String quantity;
	private String price;
	private String imagename;
	private String productimage;
	private long counter;
	private Timestamp resetTime;
	private static long ZERO = 0;
	
	public ConsumerItem(String category, String productType, String generalName, String specificName, String productNumber, String shortDescription,
			String longDescription, String qty, String cost, String imageName, String image, long counter){
		Calendar calendar = Calendar.getInstance();
		setCategory(category);
		setProducttype(productType);
		setGeneralname(generalName);
		setSpecificname(specificName);
		setProductnumber(productNumber);
		setShortdescription(shortDescription);
		setLongdescription(longDescription);
		setQuantity(qty);
		setPrice(cost);
		setImagename(imageName);
		setProductimage(image);
		setCounter(counter);
		setResetTime(new Timestamp(calendar.getTimeInMillis()));
	}
	
	public ConsumerItem() {
		this(Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN,
				Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, Messages.UNKNOWN, ZERO);
	}
	
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public String getPrimaryKey() {
		return this.primaryKey;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public void setProducttype(String type){
		producttype = type;
	}
	
	public String getProducttype(){
		return producttype;
	}
	
	public void setGeneralname(String generalName){
		this.generalname = generalName;
	}
	
	public String getGeneralname(){
		return this.generalname;
	}
	
	public void setSpecificname(String specificName){
		this.specificname = specificName;
	}
	
	public String getSpecificname(){
		return this.specificname;
	}
	
	public void setProductnumber(String productNumber){
		this.productnumber = productNumber;
	}
	
	public String getProductnumber(){
		return this.productnumber;
	}
	
	public void setShortdescription(String shortDescr){
		this.shortdescription = shortDescr;
	}
	
	public String getShortdescription(){
		return this.shortdescription;
	}
	
	public void setLongdescription(String longDescr){
		this.longdescription = longDescr;
	}
	
	public String getLongdescription(){
		return this.longdescription;
	}
	
	public void setQuantity(String qnty){
		this.quantity = qnty;
	}
	
	public String getQuantity(){
		return this.quantity;
	}
	
	public void setPrice(String price){
		this.price = price;
	}
	
	public String getPrice(){
		return this.price;
	}
	
	public void setImagename(String name){
		this.imagename = name;
	}
	
	public String getImagename(){
		return this.imagename;
	}
	
	public void setProductimage(String image){
		this.productimage = image;
	}
	
	public String getProductimage(){
		return this.productimage;
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
	
	public ConsumerItem copy(ConsumerItem item){
		ConsumerItem newConsumerItem = new ConsumerItem(getCategory(), getProducttype(), getGeneralname(), getSpecificname(), getProductnumber(),
				getShortdescription(), getLongdescription(), getQuantity(), getPrice(), getImagename(), getProductimage(), getCounter());
		newConsumerItem.setPrimaryKey(item.getPrimaryKey());
		newConsumerItem.setResetTime(getResetTime());
		return newConsumerItem;
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
		return getCategory() + " : " + getGeneralname() + " : " + getSpecificname();
	}
}
