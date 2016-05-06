package com.shoppingpoint;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductItem implements Parcelable {

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

    public static final Parcelable.Creator<ProductItem> CREATOR
            = new Parcelable.Creator<ProductItem>() {
        public ProductItem createFromParcel(Parcel in) {
            return new ProductItem(in);
        }

        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };

    public ProductItem(Parcel in){
        setCategory(in.readString());
        setProducttype(in.readString());
        setGeneralname(in.readString());
        setSpecificname(in.readString());
        setProductnumber(in.readString());
        setShortdescription(in.readString());
        setLongdescription(in.readString());
        setQuantity(in.readString());
        setPrice(in.readString());
        setImagename(in.readString());
        setProductimage(in.readString());
        setCounter(in.readLong());
    }

    public ProductItem(String category, String productType, String generalName, String specificName, String productNumber, String shortDescription,
                       String longDescription, String qty, String cost, String imageName, String image, long counter) {
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
    }


    public void setCategory(String category) {this.category = category;}
    public String getCategory() {return this.category;}
    public void setProducttype(String type) {producttype = type;}
    public String getProducttype() {return producttype;}
    public void setGeneralname(String generalName) {this.generalname = generalName;}
    public String getGeneralname() {return this.generalname;}
    public void setSpecificname(String specificName) {this.specificname = specificName;}
    public String getSpecificname() {return this.specificname;}
    public void setProductnumber(String productNumber) {this.productnumber = productNumber;}
    public String getProductnumber() {return this.productnumber;}
    public void setShortdescription(String shortDescr) {this.shortdescription = shortDescr;}
    public String getShortdescription() {return this.shortdescription;}
    public void setLongdescription(String longDescr) {this.longdescription = longDescr;}
    public String getLongdescription() {return this.longdescription;}
    public void setQuantity(String qnty) {this.quantity = qnty;}
    public String getQuantity() {return this.quantity;}
    public void setPrice(String price) {this.price = price;}
    public String getPrice() {return this.price;}
    public void setImagename(String name) {this.imagename = name;}
    public String getImagename() {return this.imagename;}
    public void setProductimage(String image) {this.productimage = image;}
    public String getProductimage() {return this.productimage;}

    public void setCounter(long counter) {this.counter = counter;}
    public long getCounter() {return this.counter;}

    public String toString() {
        return getCategory() + " : " + getGeneralname() + " : " + getSpecificname();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCategory());
        dest.writeString(getProducttype());
        dest.writeString(getGeneralname());
        dest.writeString(getSpecificname());
        dest.writeString(getProductnumber());
        dest.writeString(getShortdescription());
        dest.writeString(getLongdescription());
        dest.writeString(getQuantity());
        dest.writeString(getPrice());
        dest.writeString(getImagename());
        dest.writeString(getProductimage());
        dest.writeLong(getCounter());
    }
}