package info.android.sqlite.model;

public class Menu_data {
		int id;
		String section;
	    String menu;
	    int price;
	    long res_id;
	    // constructors
	    public Menu_data() {
	    }
	 
	    public Menu_data(String menu, String section, int price, long res1_id) {
	        this.menu = menu;
	        this.section = section;
	        this.price = price;
	        this.res_id = res1_id;
	    }
	 
	    public Menu_data(int id, String menu, String section, int price, int restaurant_id) {
	        this.id = id;
	        this.menu = menu;
	        this.section = section;
	        this.price = price;
	        this.res_id = restaurant_id;
	    }
	 
	    // setters
	    public void setId(int id) {
	        this.id = id;
	    }
	 
	    public void setMenu(String menu) {
	        this.menu = menu;
	    }
	    
	    public void setSection(String section){
	    	this.section = section;
	    }
	 
	    public void setPrice(int price) {
	    	this.price = price;
	    }
	    
	    public void setRestaurantId(int res_id){
	    	this.res_id = res_id;
	    }
	 
	    // getters
	    public long getId() {
	        return this.id;
	    }
	 
	    public String getMenu() {
	        return this.menu;
	    }
	    
	    public String getSection(){
	    	return this.section;
	    }
	 
	    public int getPrice() {
	        return this.price;
	    }
	    
	    public long getRestaurantId(){
	    	return this.res_id;
	    }
}
