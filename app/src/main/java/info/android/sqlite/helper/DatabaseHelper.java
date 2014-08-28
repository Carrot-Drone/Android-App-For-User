package info.android.sqlite.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import info.android.sqlite.model.Restaurant;
import info.android.sqlite.model.Menu_data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private Context mContext = null;
	
    // Database Path
    private static String DATABASE_PATH;

    // Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 17;
 
    // Database Name
    private static final String DATABASE_NAME = "Shadal";
     
    // Table Names
    private static final String TABLE_RES = "restaurants";
    private static final String TABLE_MENU = "menus";

    
    public boolean doesDatabaseExist(){
    	File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
    	return dbFile.exists();
    }
    
    public void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open("databases/"+DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
 
    public DatabaseHelper(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	this.mContext = context;
        DATABASE_PATH = "/data/data/" + mContext.getPackageName() + "/databases/";
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // creating required tables
 //       db.execSQL(CREATE_TABLE_RESTAURANT);
 //       db.execSQL(CREATE_TABLE_MENU);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (newVersion > oldVersion) {
    		mContext.deleteDatabase(DATABASE_NAME);
    	}
    	
        try {
			copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
 // ------------------------ "retaurants" table methods ----------------//
    
    /**
    * Creating a restaurant
    */
   public long createRestaurant(Restaurant res) {
       SQLiteDatabase db = this.getWritableDatabase();

       ContentValues values = new ContentValues();
       values.put("name", res.getName());
       values.put("phoneNumber", res.getPhoneNumber());
       values.put("category", res.getCategory());
       values.put("openingHours", res.getOpeningHours());
       values.put("closingHours", res.getClosingHours());
       
       // insert row
       long res_id = db.insert(TABLE_RES, "nullColumnHack", values);
       
       return res_id;
   }

   /**
    * get single restaurant
    */
   public Restaurant getRestaurant(long res_id) {
       SQLiteDatabase db = this.getReadableDatabase();

       String selectQuery = "SELECT  * FROM " + TABLE_RES + " WHERE "
               + "id = " + res_id;

       Log.e(LOG, selectQuery);

       Cursor c = db.rawQuery(selectQuery, null);

       if (c != null)
           c.moveToFirst();

       Restaurant res = new Restaurant();
       res.setId(c.getInt(c.getColumnIndex("id")));
       res.setName((c.getString(c.getColumnIndex("name"))));       
       res.setCategory(c.getString(c.getColumnIndex("category")));
       res.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
       res.setOpeningHours(c.getString(c.getColumnIndex("openingHours")));
       res.setClosingHours(c.getString(c.getColumnIndex("closingHours")));
       if(c.getInt(c.getColumnIndex("flyer")) == 1){
    	   res.setFlyer(true);
       }else{
    	   res.setFlyer(false);
       }
       if(c.getInt(c.getColumnIndex("coupon")) == 1){
    	   res.setCoupon(true);
       }else{
    	   res.setCoupon(false);
       }
       res.setCouponString(c.getString(c.getColumnIndex("couponString")));


       return res;
   }

   /**
    * getting all restaurants
    * */
   public ArrayList<Restaurant> getAllRestaurants() {
       ArrayList<Restaurant> ress = new ArrayList<Restaurant>();
       String selectQuery = "SELECT  * FROM " + TABLE_RES + " ORDER BY name ASC";

       Log.e(LOG, selectQuery);

       SQLiteDatabase db = this.getReadableDatabase();
       Cursor c = db.rawQuery(selectQuery, null);

       // looping through all rows and adding to list
       if (c.moveToFirst()) {
           do {
               Restaurant res = new Restaurant();
               res.setId(c.getInt((c.getColumnIndex("id"))));
               res.setName((c.getString(c.getColumnIndex("name"))));
               System.out.println(res.getName());
               res.setCategory(c.getString(c.getColumnIndex("category")));
               res.setOpeningHours(c.getString(c.getColumnIndex("openingHours")));
               res.setClosingHours(c.getString(c.getColumnIndex("closingHours")));
               if(c.getInt(c.getColumnIndex("flyer")) == 1){
            	   res.setFlyer(true);
               }else{
            	   res.setFlyer(false);
               }
               if(c.getInt(c.getColumnIndex("coupon")) == 1){
            	   res.setCoupon(true);
               }else{
            	   res.setCoupon(false);
               }
               res.setCouponString(c.getString(c.getColumnIndex("couponString")));

               // adding to res list
               ress.add(res);
           } while (c.moveToNext());
       }

       return ress;
   }
   
   /**
    * getting all restaurants with category
    * */
   public ArrayList<Restaurant> getAllRestaurantsWithCategory(String category) {
       ArrayList<Restaurant> ress = new ArrayList<Restaurant>();
       String selectQuery = "SELECT  * FROM " + TABLE_RES + " WHERE category = '" + category + "' ORDER BY name ASC";

       Log.e(LOG, selectQuery);

       SQLiteDatabase db = this.getReadableDatabase();
       Cursor c = db.rawQuery(selectQuery, null);

       // looping through all rows and adding to list
       if (c.moveToFirst()) {
           do {
               Restaurant res = new Restaurant();
               res.setId(c.getInt((c.getColumnIndex("id"))));
               res.setName((c.getString(c.getColumnIndex("name"))));
               System.out.println(res.getName());
               res.setCategory(c.getString(c.getColumnIndex("category")));
               res.setOpeningHours(c.getString(c.getColumnIndex("openingHours")));
               res.setClosingHours(c.getString(c.getColumnIndex("closingHours")));
               if(c.getInt(c.getColumnIndex("flyer")) == 1){
            	   res.setFlyer(true);
               }else{
            	   res.setFlyer(false);
               }
               if(c.getInt(c.getColumnIndex("coupon")) == 1){
            	   res.setCoupon(true);
               }else{
            	   res.setCoupon(false);
               }
               res.setCouponString(c.getString(c.getColumnIndex("couponString")));


               // adding to res list
               ress.add(res);
           } while (c.moveToNext());
       }

       return ress;
   }

   /**
    * getting all menus under single restaurant
    * */
   public ArrayList<Menu_data> getAllMenusByRestaurant(long res_id) {
       ArrayList<Menu_data> menus = new ArrayList<Menu_data>();

       String selectQuery = "SELECT  * FROM " + TABLE_MENU + " WHERE " 
    		   + "restaurant_id = " + res_id;

       Log.e(LOG, selectQuery);

       SQLiteDatabase db = this.getReadableDatabase();
       Cursor c = db.rawQuery(selectQuery, null);

       // looping through all rows and adding to list
       if (c.moveToFirst()) {
           do {
               Menu_data menu = new Menu_data();
               menu.setId(c.getInt((c.getColumnIndex("id"))));
               menu.setMenu((c.getString(c.getColumnIndex("menu"))));
               menu.setSection(c.getString(c.getColumnIndex("section")));
               menu.setPrice(c.getInt(c.getColumnIndex("price")));
               menu.setRestaurantId(c.getInt(c.getColumnIndex("restaurant_id")));
               
               // adding to menu list
               menus.add(menu);
           } while (c.moveToNext());
       }

       return menus;
   }

   /**
    * Deleting a restaurant
    */
   public void deleteRestaurant(long res_id) {
       SQLiteDatabase db = this.getWritableDatabase();
       db.delete(TABLE_RES, "id" + " = ?",
               new String[] { String.valueOf(res_id) });
   }

   // ------------------------ "menus" table methods ----------------//

   /**
    * Creating menu
    */
   public long createMenu(Menu_data menu) {
       SQLiteDatabase db = this.getWritableDatabase();

       ContentValues values = new ContentValues();
       values.put("menu", menu.getMenu());
       values.put("section", menu.getSection());
       values.put("price", menu.getPrice());
       values.put("restaurant_id", menu.getRestaurantId());

       // insert row
       long menu_id = db.insert(TABLE_MENU, null, values);

       return menu_id;
   }

   // closing database
   public void closeDB() {
       SQLiteDatabase db = this.getReadableDatabase();
       if (db != null && db.isOpen())
           db.close();
   }
}