package com.lchpatners.shadal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class Restaurant implements Parcelable {

    public static final long serialVersionUID = 20140902L;

    private int id;
    private int serverId;
    /*
    booleans are represented as bytes to implement the Parcelable interface
    (Parcelable interface is needed to be a Bundle argument or an Intent extra)
    see how getters and setters work in using byte variables for representing boolean ones
    */
    private byte hasFlyer; // in the server : has_flyer
    private byte hasCoupon; // in the server : has_coupon
    private byte isNew; // in the server : is_new
    private byte isFavorite; // in the server : is_favorite
    private String name;
    private String phoneNumber; // in the server : phone_number
    private String category;
    private String openingHour;
    private String closingHour;
    private String couponString; // in the server : coupon_string
    private String updatedTime; // in the server : updated_at

    public Restaurant() {}

    // must be read by the order which it was written by
    public Restaurant(Parcel source) {
        id = source.readInt();
        serverId = source.readInt();
        hasFlyer = source.readByte();
        hasCoupon = source.readByte();
        isNew = source.readByte();
        name = source.readString();
        phoneNumber = source.readString();
        category = source.readString();
        openingHour = source.readString();
        closingHour = source.readString();
        couponString = source.readString();
        updatedTime = source.readString();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(serverId);
        dest.writeByte(hasFlyer);
        dest.writeByte(hasCoupon);
        dest.writeByte(isNew);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(category);
        dest.writeString(openingHour);
        dest.writeString(closingHour);
        dest.writeString(couponString);
        dest.writeString(updatedTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public boolean hasFlyer() {
        return hasFlyer == (byte)1;
    }

    public void setHasFlyer(boolean hasFlyer) {
        this.hasFlyer = hasFlyer ? (byte)1 : (byte)0;
    }

    public boolean hasCoupon() {
        return hasCoupon == 1;
    }

    public void setHasCoupon(boolean hasCoupon) {
        this.hasCoupon = hasCoupon ? (byte)1 : (byte)0;
    }

    public boolean isNew() {
        return isNew == 1;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew ? (byte)1 : (byte)0;
    }

    public boolean isFavorite() {
        return isFavorite == (byte)1;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite ? (byte)1 : (byte)0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public String getClosingHour() {
        return closingHour;
    }

    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }

    public String getCouponString() {
        return couponString;
    }

    public void setCouponString(String couponString) {
        this.couponString = couponString;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Restaurant && id == ((Restaurant)object).getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

}
