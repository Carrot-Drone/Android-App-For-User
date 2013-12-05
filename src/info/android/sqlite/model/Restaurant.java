package info.android.sqlite.model;

public class Restaurant {
		int id;
	    String name;
	    String phoneNumber;
	    String category;
	    String openingHours;
	    String closingHours;
	    
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
	 
	    // getters
	    public long getId() {
	        return this.id;
	    }
	 
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
}
