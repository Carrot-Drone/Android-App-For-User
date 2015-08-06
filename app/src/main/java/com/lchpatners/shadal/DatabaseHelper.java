package com.lchpatners.shadal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manages the SQLite Database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LEGACY_DATABASE_NAME = "Shadal";
    public static final String NAME = "name";
    public static final String CALL = "call";
    /**
     * Database version.
     */
    private static final int VERSION = 19;
    /**
     * The restaurants table's name.
     */
    private static final String RESTAURANTS = "restaurants";
    /**
     * The menu table's name.
     */
    private static final String MENUS = "menus";
    /**
     * The flyer table's name.
     */
    private static final String FLYERS = "flyers";
    private static final String CALLLOGS = "calllogs";
    private static final String CATEGORIES = "categories";
    private static final String SUBMENUS = "submenus";
    private static final String CATEGORY_COLUMNS = "(id INTEGER PRIMARY KEY, server_id INTEGER, campus_id INTEGER, title TEXT)";
    private static final String RESTAURANT_COLUMNS = "(id INTEGER PRIMARY KEY, " +
            "name TEXT, " +
            "opening_hours TEXT, " +
            "closing_hours TEXT, " +
            "phone_number TEXT," +
            "has_flyer INTEGER, " +
            "has_coupon INTEGER," +
            "is_new INTEGER," +
            "coupon_string TEXT," +
            "retention INTEGER," +
            "number_of_my_calls INTEGER," +
            "total_number_of_calls INTEGER," +
            "total_number_of_goods INTEGER," +
            "total_number_of_bads INTEGER," +
            "my_preference INTEGER," +
            "category_id INTEGER)";

    private static final String MENU_COLUMNS = "(id INTEGER PRIMARY KEY, name TEXT, section TEXT, " +
            "price INT,description TEXT, restaurant_id INT)";
    private static final String FLYER_COLUMNS = "(id INTEGER PRIMARY KEY, url TEXT, restaurant_id INT)";
    private static final String CALLLOG_COLUMNS = "(id INTEGER PRIMARY KEY, restaurant_id INT, called_at LONG)";
    private static final String SUBMENU_COLUMNS = "(id INTEGER PRIMARY KEY, name TEXT, price INTEGER, menu_id INTEGER)";

    /**
     * The singleton object.
     */
    private static DatabaseHelper instance;
    /**
     * The campus whose database is currently loaded to be handled..f
     */
    private static String loadedCampus;
    private Context context;

    /**
     * Constructs a {@link com.lchpatners.shadal.DatabaseHelper DatabaseHelper}.
     *
     * @param context        {@link android.content.Context}
     * @param selectedCampus The campus database to be handled.
     */
    private DatabaseHelper(Context context, String selectedCampus) {
        super(context.getApplicationContext(), selectedCampus, null, VERSION);
        this.context = context;
    }

    /**
     * If {@link #instance} is null, or {@link #loadedCampus} is different from the
     * user's last pick, instantiate a new object of {@link com.lchpatners.shadal.DatabaseHelper
     * DatabaseHelper}.
     *
     * @param context {@link android.content.Context}
     * @return A {@link com.lchpatners.shadal.DatabaseHelper DatabaseHelper} instance.
     */
    public static DatabaseHelper getInstance(Context context) {
        String selectedCampus = Preferences.getCampusEnglishName(context);
        if (instance == null || (loadedCampus != null && !loadedCampus.equals(selectedCampus))) {
            // Thread-safely
            synchronized (DatabaseHelper.class) {
                loadedCampus = selectedCampus;
                instance = new DatabaseHelper(context, selectedCampus);
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", RESTAURANTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", MENUS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", FLYERS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", CALLLOGS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", CATEGORIES));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", SUBMENUS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", RESTAURANTS, RESTAURANT_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", MENUS, MENU_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", FLYERS, FLYER_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", CALLLOGS, CALLLOG_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", CATEGORIES, CATEGORY_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", SUBMENUS, SUBMENU_COLUMNS));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    /**
     * @param dbName The name of database to be checked.
     * @return If the database exists.
     */
    public boolean checkDatabase(String dbName) {
        if (dbName == null) return false;
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    public void updateAll(JSONObject categoryJson, int categoryId) {
        updateAll(categoryJson, categoryId, null);
        reloadRestaurantListAdapter(RestaurantListFragment.latestAdapter);
    }

    /**
     * Insert if new to the table, or otherwise update the existing data.
     * Data are identified by the server-side id value. And then
     * reload the {@link com.lchpatners.shadal.MenuListActivity activity}.
     *
     * @param categoryJson {@link org.json.JSONObject JSONObject} from {@link com.lchpatners.shadal.Server Server}.
     * @param activity     {@link com.lchpatners.shadal.MenuListActivity MenuListActivity} to reload.
     */
    public void updateAll(JSONObject categoryJson, int categoryId, final MenuListActivity activity) {
        Cursor cursor = null;
        try {
            Log.d("DH updateAll()", categoryJson.toString());
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values;
            values = new ContentValues();
            values.put("server_id", categoryJson.getInt("id"));
            values.put("campus_id", categoryJson.getInt("campus_id"));
            values.put("title", categoryJson.getString("title"));

            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE id = %d", CATEGORIES, categoryId), null);

            if (cursor != null && cursor.moveToFirst()) {
                db.update(CATEGORIES, values, "id = ?", new String[]{String.valueOf(categoryId)});
            } else {
                db.insert(CATEGORIES, null, values);
            }
            JSONArray restaurants = categoryJson.getJSONArray("restaurants");
            Log.d("DH RESTAURANTs", restaurants.toString());
            Log.d("restaurantLength", restaurants.length() + "");
            cursor = null;
            for (int i = 0; i < restaurants.length(); i++) {
                JSONObject restaurant = restaurants.getJSONObject(i);
                Log.d("loof restaurant", i + "");
                Log.d("DH restaurant", restaurant.toString());
                int restaurantId = restaurant.getInt("id");

                values = new ContentValues();
                values.put("id", restaurant.getInt("id"));
                values.put("name", restaurant.getString("name"));
                values.put("phone_number", restaurant.getString("phone_number"));
                values.put("opening_hours", restaurant.getString("opening_hours"));
                values.put("closing_hours", restaurant.getString("closing_hours"));
                values.put("has_flyer", (restaurant.getBoolean("has_flyer")) ? 1 : 0);
                values.put("has_coupon", (restaurant.getBoolean("has_coupon")) ? 1 : 0);
                values.put("is_new", (restaurant.getBoolean("is_new")) ? 1 : 0);
                values.put("coupon_string", restaurant.getString("coupon_string"));
                values.put("retention", restaurant.getString("retention"));
                values.put("number_of_my_calls", restaurant.getInt("number_of_my_calls"));
                values.put("total_number_of_calls", restaurant.getInt("total_number_of_calls"));
                values.put("total_number_of_goods", restaurant.getInt("total_number_of_goods"));
                values.put("total_number_of_bads", restaurant.getInt("total_number_of_bads"));
                values.put("my_preference", restaurant.getInt("my_preference"));
                values.put("category_id", categoryId);

                cursor = db.rawQuery(String.format(
                        "SELECT * FROM %s WHERE id = %d;",
                        RESTAURANTS, restaurantId
                ), null);
                if (cursor != null && cursor.moveToFirst()) {
                    db.update(RESTAURANTS, values, "id = ?", new String[]{String.valueOf(restaurantId)});
                } else {
                    db.insert(RESTAURANTS, null, values);

                }

                // Update menus and leaflet urls corresponding to the restaurant
                db.execSQL(String.format(
                        "DELETE FROM %s WHERE restaurant_id = %d;",
                        MENUS, restaurantId
                ));

                JSONArray menus = restaurant.getJSONArray("menus");
                for (int j = 0; j < menus.length(); j++) {
                    JSONObject menu = menus.getJSONObject(j);
                    JSONArray submenus = menu.getJSONArray("submenus");
                    if (submenus.length() == 0 || submenus == null) {
                        values = new ContentValues();
                        values.put("id", menu.getInt("id"));
                        values.put("name", menu.getString("name"));
                        values.put("section", menu.getString("section"));
                        values.put("price", menu.getInt("price"));
                        values.put("description", menu.getString("description"));
                        values.put("restaurant_id", restaurantId);

                        db.insert(MENUS, null, values);
                    } else {
                        values = new ContentValues();
                        int menu_id = menu.getInt("id");
                        values.put("id", menu.getInt("id"));
                        values.put("name", menu.getString("name"));
                        values.put("section", menu.getString("section"));
                        values.put("description", menu.getString("description"));
                        values.put("restaurant_id", restaurantId);
                        db.insert(MENUS, null, values);

                        db.execSQL(String.format(
                                "DELETE FROM %s WHERE menu_id = %d;",
                                SUBMENUS, menu_id
                        ));

                        values = new ContentValues();
                        JSONObject submenu = submenus.getJSONObject(0);
                        values.put("name", submenu.getString("name"));
                        values.put("price", submenu.getInt("price"));
                        values.put("menu_id", menu_id);
                        db.insert(SUBMENUS, null, values);


                    }
                }

                db.execSQL(String.format(
                        "DELETE FROM %s WHERE restaurant_id = %d;",
                        FLYERS, restaurantId
                ));
                JSONArray urls = restaurant.getJSONArray("flyers_url");
                for (int j = 0; j < urls.length(); j++) {
                    String url = urls.getString(j);
                    values = new ContentValues();
                    values.put("url", url);
                    values.put("restaurant_id", restaurantId);
                    db.insert(FLYERS, null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            reloadMenuListActivity(activity);
        }
    }
/*

    public void updateCategory(JSONArray restaurants, int category_id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        Server server = new Server(context);
        try {
            for (int i = 0; i < restaurants.length(); i++) {
                JSONObject restaurant = restaurants.getJSONObject(i);
                cursor = db.rawQuery(String.format(
                        "SELECT * FROM %s WHERE server_id = %d;",
                        RESTAURANTS, restaurant.getInt("id")
                ), null);
                // If there is an existing data, check if the data is outdated.
                // Else, or if the restaurant is a new one, insert it into the database.
                if (cursor != null && cursor.moveToFirst()) {
                    // If the existing data is outdated, update from the server.
                    // Else, do nothing.
                    if (!restaurant.getString("updated_at")
                            .equals(cursor.getString(cursor.getColumnIndex("updated_at")))) {
                        server.updateRestaurant(restaurant.getInt("id"),
                                cursor.getString(cursor.getColumnIndex("updated_at")));
                    }
                } else {
                    restaurant.put("category_id", category_id);
                    restaurant.put("openingHours", "0.0");
                    restaurant.put("closingHours", "0.0");
                    restaurant.put("coupon_string", "loading...");
                    restaurant.put("is_favorite", 0);
                    restaurant.put("menus", new JSONArray());
                    restaurant.put("flyers_url", new JSONArray());
                    restaurant.put("updated_at", "00:00");
                    updateRestaurant(restaurant);
                }
            }

            // Delete restaurants no more available from the server.
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category_id = '%s';",
                    RESTAURANTS, category_id
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // I'm making a note here, "Huge Success!"
                    boolean stillAlive = false;
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject restaurant = restaurants.getJSONObject(i);
                        if (restaurant.getInt("id") == cursor.getInt(cursor.getColumnIndex("server_id"))) {
                            stillAlive = true;
                            break;
                        }
                    }
                    if (!stillAlive) {
                        db.delete(RESTAURANTS, "server_id = ?", new String[]{
                                String.valueOf(cursor.getInt(cursor.getColumnIndex("server_id")))
                        });
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            reloadRestaurantListAdapter(RestaurantListFragment.latestAdapter);
        }
    }
*/

    /**
     * @return Bookmarked restaurants.
     */
    public ArrayList<Restaurant> getFavoriteRestaurants() {
        ArrayList<Restaurant> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            for (String category : CategoryListAdapter.categories) {
                cursor = db.rawQuery(String.format(
                        "SELECT * FROM %s WHERE is_favorite = 1 AND category ='%s' ORDER BY has_flyer DESC, name ASC;",
                        RESTAURANTS, category
                ), null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        list.add(new Restaurant(cursor));
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    public ArrayList<Restaurant> getRestaurantsByCategory(int category_id) {
        Log.d("getRestaurantByCategory", "called");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Restaurant> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category_id = '%d' ORDER BY has_flyer DESC, name ASC;",
                    RESTAURANTS, category_id
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Log.d("RestaurantByCategory", cursor.toString());
                    list.add(new Restaurant(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * @param restaurantServerId The restaurant's server-side id.
     * @return Menu data of a restaurant.
     */
    public ArrayList<Menu> getMenusByRestaurantServerId(long restaurantServerId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Menu> list = new ArrayList<>();
        ArrayList<String> sections = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT section FROM %s WHERE restaurant_id = %d;",
                    MENUS, restaurantServerId
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String section = cursor.getString(cursor.getColumnIndex("section"));
                    if (!sections.contains(section)) {
                        sections.add(section);
                    }
                } while (cursor.moveToNext());
            }
            for (String section : sections) {
                cursor = db.rawQuery(String.format(
                        "SELECT * FROM %s WHERE restaurant_id = %d AND section = '%s';",
                        MENUS, restaurantServerId, section
                ), null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Menu menu = new Menu();
                        menu.setId(cursor.getInt((cursor.getColumnIndex("id"))));
                        menu.setItem((cursor.getString(cursor.getColumnIndex("menu"))));
                        menu.setSection(cursor.getString(cursor.getColumnIndex("section")));
                        menu.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
                        menu.setRestaurantId(cursor.getInt(cursor.getColumnIndex("restaurant_id")));
                        list.add(menu);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * @param restaurantServerId The restaurant's server-side id.
     * @return Flyer urls of a restaurant.
     */
    public ArrayList<String> getFlyerUrlsByRestaurantServerId(long restaurantServerId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE restaurant_id = %d;",
                    FLYERS, restaurantServerId
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(cursor.getColumnIndex("url")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * A WILD RESTAURANT APPEARS!
     *
     * @return A randomly selected restaurant.
     */
    public Restaurant getRandomRestaurant() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Restaurant restaurant = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s ORDER BY RANDOM() LIMIT 1;",
                    RESTAURANTS
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                restaurant = new Restaurant(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return restaurant;
    }

    /**
     * @param id The restaurant's server-side id.
     * @return A restaurant with the <code>id</code>.
     */
    public Restaurant getRestaurantFromId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        Restaurant restaurant = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE id = %d;",
                    RESTAURANTS, id), null);
            if (cursor != null && cursor.moveToFirst()) {
                restaurant = new Restaurant(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return restaurant;
    }

    /**
     * Reload a {@link com.lchpatners.shadal.RestaurantListAdapter adapter}.
     *
     * @param adapter An adapter to be reloaded.
     */
    public void reloadRestaurantListAdapter(final RestaurantListAdapter adapter) {
        if (adapter != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.reloadData();
                }
            });
        }
    }

    /**
     * Reload a {@link com.lchpatners.shadal.MenuListActivity activity}.
     *
     * @param activity An activity to be reloaded.
     */
    public void reloadMenuListActivity(final MenuListActivity activity) {
        if (activity != null) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setView();
                }
            });
        }
    }

    public void insertRecentCalls(int restaurant_id) {

        try {

            SQLiteDatabase db = getWritableDatabase();

            Date date = new Date();
            Long timestamp = date.getTime();

            ContentValues values = new ContentValues();
            values.put("restaurant_id", restaurant_id);
            values.put("called_at", timestamp);

            db.insert(CALLLOGS, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfCalls(int restaurant_id) {
        SQLiteDatabase db = getReadableDatabase();
        int numberOfCalls = 0;
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(String.format(
                    "SELECT %s FROM %s GROUP BY %s;",
                    "count(*)",//select
                    CALLLOGS, //from
                    restaurant_id //group by
            ), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {
            numberOfCalls = cursor.getColumnIndex(cursor.getColumnName(0));
        }
        return numberOfCalls;
    }

    public ArrayList<Call> getRecentCallsList(String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Call> list = new ArrayList<>();
        Cursor cursor = null;
        try {

            if (orderBy.equals(CALL)) {
                cursor = db.rawQuery(String.format(
                        "SELECT %s,%s,%s FROM %s,%s WHERE %s = %s GROUP BY %s ORDER BY %s DESC; ",
                        "restaurants.id", "restaurants.name", "count(*)",//select
                        RESTAURANTS, CALLLOGS, //from
                        "restaurants.id", "calllogs.restaurant_id",//where
                        "restaurants.id", //group by
                        "calllogs.called_at" //order by
                ), null);
            } else if (orderBy.equals(NAME)) {
                cursor = db.rawQuery(String.format(
                        "SELECT %s,%s,%s FROM %s,%s WHERE %s = %s GROUP BY %s ORDER BY %s ASC; ",
                        "restaurants.restaurant_id", "restaurants.name", "count(*)",//select
                        RESTAURANTS, CALLLOGS, //from
                        "restaurants.id", "calllogs.restaurant_id",//where
                        "restaurants.id", //group by
                        "restaurants.name" //order by
                ), null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new Call(cursor));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public int getCategoryIdFromTitle(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int id = 0;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT %d FROM %s WHERE title = %s;",
                    "id", CATEGORIES, title), null);
            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return id;
    }

}
