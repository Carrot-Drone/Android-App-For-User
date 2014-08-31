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

import com.lchpartners.server.Server;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private static final String TABLE_FLYER = "flyers";

    private static final String CREATE_TABLE_RESTAURANT = "create table restaurants (id INTEGER PRIMARY KEY, server_id INT, name TEXT, category TEXT, "
            + "openingHours TEXT, closingHours TEXT, phoneNumber TEXT, has_flyer BOOL, has_coupon BOOL, is_new BOOL, is_favorite BOOL, coupon_string TEXT, updated_at TEXT);";
    private static final String CREATE_TABLE_FLYERS = "create table flyers (id INTEGER PRIMARY KEY, url TEXT, restaurant_id INT);";
    private static final String CREATE_TABLE_MENU = "create table menus (id INTEGER PRIMARY KEY, menu TEXT, section TEXT, "
            + "price INT, restaurant_id INT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DATABASE_PATH = "/data/data/" + mContext.getPackageName() + "/databases/";
    }
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

 
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("tag1", "onCreate Database");

        // creating required tables
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RES);
        db.execSQL(CREATE_TABLE_RESTAURANT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_MENU);
        db.execSQL(CREATE_TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLYER);
        db.execSQL(CREATE_TABLE_FLYERS);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
    		mContext.deleteDatabase(DATABASE_NAME);
    	}
    	/*
        try {
			copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    }
    
    
 // ------------------------ "retaurants" table methods ----------------//
    
    /**
    * Creating a restaurant
    */
    /*
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
    */
    // Create and Update Restaurant. if exist, update if not, create.
    public long updateRestaurant(JSONObject res) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("server_id", res.getInt("id"));
        values.put("updated_at", res.getString("updated_at"));

        values.put("name", res.getString("name"));
        values.put("phoneNumber", res.getString("phone_number"));
        values.put("category", res.getString("category"));
        values.put("openingHours", res.getString("openingHours"));
        values.put("closingHours", res.getString("closingHours"));

        values.put("has_flyer", res.getBoolean("has_flyer"));
        values.put("has_coupon", res.getBoolean("has_coupon"));
        values.put("is_new", false);
        values.put("is_favorite", false);

        values.put("coupon_string", res.getString("coupon_string"));

        JSONArray menus = res.getJSONArray("menus");
        for(int i=0; i<menus.length(); i++){
            createMenu(menus.getJSONObject(i));
        }

        JSONArray urls = res.getJSONArray("flyers_url");
        for(int i=0; i<urls.length(); i++){
            createFlyer(urls.getString(i), res.getInt("id"));
        }

        // Check where the res exist already
        String selectQuery = "SELECT  * FROM " + TABLE_RES + " WHERE "
                + "server_id = " + res.getInt("id");
        Cursor c = db.rawQuery(selectQuery, null);

        long res_id;

        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            Log.d("tag", "updateRestaurant1");
            res_id = db.update(TABLE_RES, values, "server_id = ?",new String[] {  String.valueOf(res.getInt("id"))});
            Log.d("tag", "updateRestaurant3");
        }else{
            // insert res
            res_id = db.insert(TABLE_RES, "nullColumnHack", values);
        }
        return res_id;
    }

    public void updateRestaurantInCategory(JSONArray ress, String category) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("tag", "update Res In category"+category);
        // 없던 음식점 추가하기
        for(int i=0; i<ress.length(); i++){
            JSONObject res = ress.getJSONObject(i);
            String sql = "SELECT * FROM" + TABLE_RES + "WHERE " + "server_id =" + res.getInt("id");
            Cursor c = db.rawQuery(sql, null);
            if(c!=null && c.getCount()>0) {
                // 혹시 같은 음식점 2개 이상 들어간 경우
                if (c.getCount() > 1) {
                    db.delete(TABLE_RES, "server_id=?", new String[]{String.valueOf(res.getInt("id"))});
                    Server server = new Server();
                    server.updateRestaurant(res.getInt("id"), "12:00");
                }

                sql = "SELECT * FROM" + TABLE_RES + "WHERE " + "server_id =" + res.getInt("id");
                c = db.rawQuery(sql, null);
                c.moveToFirst();
                // 업데이트가 필요한 경우.
                if(!res.getString("updated_at").equals(c.getString(c.getColumnIndex("updated_at")))){
                    Server server = new Server();
                    server.updateRestaurant(res.getInt("id"), c.getString(c.getColumnIndex("updated_at")));
                }
            }else{
                Server server = new Server();
                server.updateRestaurant(res.getInt("id"), "12:00");
            }
        }
        // 삭제된 음식점 삭제하기
        String sql = "SELECT * FROM" + TABLE_RES + "WHERE " + "category =" + category;
        Cursor c = db.rawQuery(sql, null);
        if(c!=null && c.getCount()>0) {
            boolean exist = false;
            for(int i=0; i<ress.length(); i++){
                JSONObject res = ress.getJSONObject(i);
                if(res.getInt("id") == c.getInt(c.getColumnIndex("server_id"))) {
                    exist = true;
                    break;
                }
            }
            if(exist==false){
                db.delete(TABLE_RES, "server_id=?", new String[]{String.valueOf(c.getInt(c.getColumnIndex("server_id")))});
            }
        }
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

        Restaurant res = this.getRestaurantFromCursor(c);

        return res;
    }
    /**
     * get single restaurant
     */
    public Restaurant getRandomRestaurant() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_RES + " ORDER BY RANDOM() LIMIT 1";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){
            c.moveToFirst();
        }
        else{
            Log.e("tag", "No res in database");
            return null;
        }

        Restaurant res = this.getRestaurantFromCursor(c);

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
               Restaurant res = this.getRestaurantFromCursor(c);
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
               Restaurant res = this.getRestaurantFromCursor(c);
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
     * getting all flyer urls under single restaurant
     * */
    public ArrayList<String> getALLURLsForRestaurant(long res_id) {
        ArrayList<String> urls = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + TABLE_FLYER + " WHERE "
                + "restaurant_id = " + res_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                urls.add(c.getString(c.getColumnIndex("url")));
            } while (c.moveToNext());
        }

        return urls;
    }

   /**
    * Deleting a restaurant
    */
   public void deleteRestaurant(long res_id) {
       SQLiteDatabase db = this.getWritableDatabase();
       db.delete(TABLE_RES, "id" + " = ?",
               new String[] { String.valueOf(res_id) });
   }

    private Restaurant getRestaurantFromCursor(Cursor c){
        Restaurant res = new Restaurant();
        res.setId(c.getInt(c.getColumnIndex("id")));
        res.setServer_id(c.getInt(c.getColumnIndex("server_id")));
        res.setName((c.getString(c.getColumnIndex("name"))));
        res.setCategory(c.getString(c.getColumnIndex("category")));
        res.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
        res.setOpeningHours(c.getString(c.getColumnIndex("openingHours")));
        res.setClosingHours(c.getString(c.getColumnIndex("closingHours")));
        if(c.getInt(c.getColumnIndex("has_flyer")) == 1){
            res.setFlyer(true);
        }else{
            res.setFlyer(false);
        }
        if(c.getInt(c.getColumnIndex("has_coupon")) == 1){
            res.setCoupon(true);
        }else{
            res.setCoupon(false);
        }
        if(c.getInt(c.getColumnIndex("is_new")) == 1){
            res.setNew(true);
        }else{
            res.setNew(false);
        }
        if(c.getInt(c.getColumnIndex("is_favorite")) == 1){
            res.setFavorite(true);
        }else{
            res.setFavorite(false);
        }
        res.setCouponString(c.getString(c.getColumnIndex("coupon_string")));
        res.setUpdated_at(c.getString(c.getColumnIndex("updated_at")));

        return res;
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

    public long createMenu(JSONObject menu) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("menu", menu.getString("name"));
        values.put("section", menu.getString("section"));
        values.put("price", menu.getInt("price"));
        values.put("restaurant_id", menu.getInt("restaurant_id"));

        // insert row
        long menu_id = db.insert(TABLE_MENU, null, values);

        return menu_id;
    }

    public long createFlyer(String flyer, int restaurant_id) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url", flyer);
        values.put("restaurant_id", restaurant_id);

        // insert row
        long flyer_id = db.insert(TABLE_FLYER, null, values);

        return flyer_id;
    }

        // closing database
   public void closeDB() {
       SQLiteDatabase db = this.getReadableDatabase();
       if (db != null && db.isOpen())
           db.close();
   }
}