package info.android.sqlite.model;

import java.io.Serializable; 
import info.android.sqlite.helper.DatabaseHelper;
import android.util.Log; 

public class Restaurant implements Serializable {
      public static final long serialVersionUID = 20140902L;
			public int id;
      public int server_id;
	    public String name;
	    public String phoneNumber;
	    public String category;
	    public String openingHours;
	    public String closingHours;
	    public boolean has_flyer;
	    public boolean has_coupon;
      public boolean is_new;
      public boolean is_favorite;
	    public String coupon_string;
      public String updated_at;

	    // constructors
	    public Restaurant() {
	    }
	 
	    public Restaurant(String name, String phoneNumber) {
	        this.name = name;
	        this.phoneNumber = phoneNumber;
	    }
	 
	    public Restaurant(int id, String name, String phoneNumber) {
	        this.id = id;
	        this.name = name;
	        this.phoneNumber = phoneNumber;
	    }
	 
	    // setters
	    public void setId(int id) {
	        this.id = id;
	    }

        public void setServer_id(int server_id) {this.server_id = server_id;}
	 
	    public void setName(String name) {
	        this.name = name;
	    }
	 
	    public void setPhoneNumber(String phoneNumber) {
	        this.phoneNumber = phoneNumber;
	    }
	    
	    public void setCategory(String category){
	    	this.category = category;
	    }

	    public void setOpeningHours(String openingHours){
	    	this.openingHours = openingHours;
	    }
	    
	    public void setClosingHours(String closingHours){
	    	this.closingHours = closingHours;
	    }

	    public void setFlyer(boolean has_flyer){this.has_flyer = has_flyer; }

        public void setCoupon(boolean has_coupon){
        this.has_coupon = has_coupon;
    }

        public void setNew(boolean is_new){
        this.is_new = is_new;
    }

        public void setFavorite(boolean is_favorite){this.is_favorite = is_favorite; }

        public void setCouponString(String coupon_string){
	    	this.coupon_string = coupon_string;
	    }

        public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

	    // getters
	    public long getId() {
	        return this.id;
	    }

        public long getServer_id() { return this.server_id; }
	 
	    public String getName() {
	        return this.name;
	    }
	 
	    public String getPhoneNumber() {
	        return this.phoneNumber;
	    }
	    
	    public String getCategory(){
	    	return this.category;
	    }
	    
	    public String getOpeningHours(){
	    	return this.openingHours;
	    }
	    
	    public String getClosingHours(){
	    	return this.closingHours;
	    }
	    
	    public boolean getFlyer(){
	    	return this.has_flyer;
	    }

        public boolean getCoupon(){
        return this.has_coupon;
    }

        public boolean getNew() { return this.is_new;}

        public boolean getFavorite() { return this.is_favorite; }

	    public String getCouponString(){
	    	return this.coupon_string;
	    }

        public String getUpdated_at() {return this.updated_at; }
}
