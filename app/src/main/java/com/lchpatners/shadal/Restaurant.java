package com.lchpatners.shadal;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class representing a restaurant data.
 */
public class Restaurant implements Parcelable {
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
    Context context;
    /**
     * An instance-unique integer value.
     */
    private int restaurantId;
    // Booleans are represented as bytes to implement the Parcelable interface.
    // See how getters and setters work in using byte variables for representing boolean ones.
    // NOTE: it is possible that without implementing Parcelable interface,
    // you can just pass id or serverId as a Intent parameter like you do in the web communication.
    /**
     * Server-side id. This works as a universal fingerprint.
     * If two instances have the same value, then it is guaranteed that
     * they represent the same restaurants of the real world.
     */
    private int serverId;
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
    private Float retention;
    private int categoryId;
    private int numberOfMyCalls;
    private int totalNumberOfCalls;
    private int totalNumberOfGoods;
    private int totalNumberOfBads;


    // Must be read by the order which it was written by.
    private int myPreference;

    /**
     * Construct by retrieving from the {@link android.os.Parcel Parcel}.
     * @param source {@link android.os.Parcel}
     */
    public Restaurant(Parcel source) {
        restaurantId = source.readInt();
        hasFlyer = source.readByte();
        hasCoupon = source.readByte();
        isNew = source.readByte();
        name = source.readString();
        phoneNumber = source.readString();
        openingHour = source.readString();
        closingHour = source.readString();
        couponString = source.readString();
        retention = source.readFloat();
        numberOfMyCalls = source.readInt();
        totalNumberOfCalls = source.readInt();
        totalNumberOfGoods = source.readInt();
        totalNumberOfBads = source.readInt();
        myPreference = source.readInt();
        categoryId = source.readInt();

    }

    /**
     * Construct by retrieving from the {@link android.database.Cursor Cursor}.
     * @param cursor {@link android.database.Cursor}
     */
    public Restaurant(Cursor cursor) {
        restaurantId = cursor.getInt(cursor.getColumnIndex("id"));
        name = cursor.getString(cursor.getColumnIndex("name"));
        phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
        openingHour = cursor.getString(cursor.getColumnIndex("opening_hours"));
        closingHour = cursor.getString(cursor.getColumnIndex("closing_hours"));
        hasFlyer = (byte)cursor.getInt(cursor.getColumnIndex("has_flyer"));
        hasCoupon = (byte)cursor.getInt(cursor.getColumnIndex("has_coupon"));
        isNew = (byte)cursor.getInt(cursor.getColumnIndex("is_new"));
        couponString = cursor.getString(cursor.getColumnIndex("coupon_string"));
        retention = cursor.getFloat(cursor.getColumnIndex("retention"));
        numberOfMyCalls = cursor.getInt(cursor.getColumnIndex("number_of_my_calls"));
        totalNumberOfCalls = cursor.getInt(cursor.getColumnIndex("total_number_of_calls"));
        totalNumberOfGoods = cursor.getInt(cursor.getColumnIndex("total_number_of_goods"));
        totalNumberOfBads = cursor.getInt(cursor.getColumnIndex("total_number_of_bads"));
        myPreference = cursor.getInt(cursor.getColumnIndex("my_preference"));
        categoryId = cursor.getInt(cursor.getColumnIndex("category_id"));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(restaurantId);
        dest.writeByte(hasFlyer);
        dest.writeByte(hasCoupon);
        dest.writeByte(isNew);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(openingHour);
        dest.writeString(closingHour);
        dest.writeString(couponString);
        dest.writeFloat(retention);
        dest.writeInt(numberOfMyCalls);
        dest.writeInt(totalNumberOfCalls);
        dest.writeInt(totalNumberOfCalls);
        dest.writeInt(totalNumberOfBads);
        dest.writeInt(myPreference);
        dest.writeInt(categoryId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
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

    public Float getRetention() {
        return retention;
    }

    public void setRetention(Float retention) {
        this.retention = retention;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Restaurant && restaurantId == ((Restaurant) object).getRestaurantId();
    }

    @Override
    public int hashCode() {
        return restaurantId;
    }

    public int getMyNumberOfCalls(int restaurantId) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        int number_of_calls = helper.getNumberOfCalls(restaurantId);
        return number_of_calls;
    }
}
