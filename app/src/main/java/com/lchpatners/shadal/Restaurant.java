package com.lchpatners.shadal;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing a restaurant data.
 */
public class Restaurant implements Parcelable {

    /**
     * An instance-unique integer value.
     */
    private int id;
    /**
     * Server-side id. This works as a universal fingerprint.
     * If two instances have the same value, then it is guaranteed that
     * they represent the same restaurants of the real world.
     */
    private int serverId;
    // Booleans are represented as bytes to implement the Parcelable interface.
    // See how getters and setters work in using byte variables for representing boolean ones.
    // NOTE: it is possible that without implementing Parcelable interface,
    // you can just pass id or serverId as a Intent parameter like you do in the web communication.
    /**
     * Does this have a flyer(s)?
     */
    private byte hasFlyer;                                  // Server-side name: has_flyer
    /**
     * Does this offer a coupon(s) for orders?
     */
    private byte hasCoupon;                                 // Server-side name: has_coupon
    /**
     * Is this newly added?
     */
    private byte isNew;                                     // Server-side name: is_new
    /**
     * Is this bookmarked?
     */
    private byte isFavorite;                                // Server-side name: is_favorite
    /**
     * The restaurant's name.
     */
    private String name;
    /**
     * The restaurant's phone number.
     */
    private String phoneNumber;                             // Server-side name: phone_number
    /**
     * The restaurant's category.
     * @see com.lchpatners.shadal.CategoryListAdapter CategoryListAdapter
     */
    private String category;
    /**
     * The restaurant's opening hour.
     */
    private String openingHour;
    /**
     * The restaurant's closing hour.
     */
    private String closingHour;
    /**
     * Describes how to get coupons if {@link #hasCoupon},
     * displays notices otherwise.
     */
    private String couponString;                            // Server-side name: coupon_string
    /**
     * Lastly updated time.
     */
    private String updatedTime;                             // Server-side name: updated_at

    // Must be read by the order which it was written by.
    /**
     * Construct by retrieving from the {@link android.os.Parcel Parcel}.
     * @param source {@link android.os.Parcel}
     */
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

    /**
     * Construct by retrieving from the {@link android.database.Cursor Cursor}.
     * @param cursor {@link android.database.Cursor}
     */
    public Restaurant(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex("id"));
        serverId = cursor.getInt(cursor.getColumnIndex("server_id"));
        name = cursor.getString(cursor.getColumnIndex("name"));
        category = cursor.getString(cursor.getColumnIndex("category"));
        phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
        openingHour = cursor.getString(cursor.getColumnIndex("openingHours"));
        closingHour = cursor.getString(cursor.getColumnIndex("closingHours"));
        hasFlyer = (byte)cursor.getInt(cursor.getColumnIndex("has_flyer"));
        hasCoupon = (byte)cursor.getInt(cursor.getColumnIndex("has_coupon"));
        isNew = (byte)cursor.getInt(cursor.getColumnIndex("is_new"));
        isFavorite = (byte)cursor.getInt(cursor.getColumnIndex("is_favorite"));
        couponString = cursor.getString(cursor.getColumnIndex("coupon_string"));
        updatedTime = cursor.getString(cursor.getColumnIndex("updated_at"));
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
