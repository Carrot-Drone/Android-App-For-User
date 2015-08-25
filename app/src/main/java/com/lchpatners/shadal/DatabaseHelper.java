package com.lchpatners.shadal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lchpatners.shadal.restaurant.menu.MenuListActivity;
import com.lchpatners.shadal.unused.CategoryItem;
import com.lchpatners.shadal.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
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
    private static final int VERSION = 21;
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
            "notice TEXT," +
            "retention INTEGER," +
            "number_of_my_calls INTEGER," +
            "total_number_of_calls INTEGER," +
            "total_number_of_goods INTEGER," +
            "total_number_of_bads INTEGER," +
            "my_preference INTEGER," +
            "updated_at INTEGER," +
            "category_id INTEGER," +
            "minimum_price INTEGER)";

    private static final String MENU_COLUMNS = "(id INTEGER PRIMARY KEY, name TEXT, section TEXT, " +
            "price INT,description TEXT, restaurant_id INT)";
    private static final String FLYER_COLUMNS = "(id INTEGER PRIMARY KEY, url TEXT, restaurant_id INT)";
    private static final String CALLLOG_COLUMNS = "(id INTEGER PRIMARY KEY, restaurant_id INT,category_id INT, called_at LONG)";
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
     * reload the {@link MenuListActivity activity}.
     *
     * @param categoryJson {@link org.json.JSONObject JSONObject} from {@link com.lchpatners.shadal.Server Server}.
     * @param activity     {@link MenuListActivity MenuListActivity} to reload.
     */
    public void updateAll(JSONObject categoryJson, int categoryId, final MenuListActivity activity) {
        Cursor cursor = null;
        try {
            Log.d("DH updateAll()", categoryJson.toString());
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values;
            values = new ContentValues();
            values.put("id", categoryId);
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
                values.put("notice", restaurant.getString("notice"));
                values.put("retention", restaurant.getString("retention"));
                values.put("number_of_my_calls", restaurant.getInt("number_of_my_calls"));
                values.put("total_number_of_calls", restaurant.getInt("total_number_of_calls"));
                values.put("total_number_of_goods", restaurant.getInt("total_number_of_goods"));
                values.put("total_number_of_bads", restaurant.getInt("total_number_of_bads"));
                values.put("my_preference", restaurant.getInt("my_preference"));
                values.put("category_id", categoryId);
                values.put("updated_at", 0);
                values.put("minimum_price", restaurant.getInt("minimum_price"));

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
                    JSONArray submenus = null;
                    if (menu.getJSONArray("submenus") != null) {
                        submenus = menu.getJSONArray("submenus");
                    }
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
                        values.put("price", 0);
                        db.insert(MENUS, null, values);

                        db.execSQL(String.format(
                                "DELETE FROM %s WHERE menu_id = %d;",
                                SUBMENUS, menu_id
                        ));

                        for (int k = 0; k < submenus.length(); k++) {
                            values = new ContentValues();
                            JSONObject submenu = submenus.getJSONObject(k);
                            values.put("name", submenu.getString("name"));
                            values.put("price", submenu.getInt("price"));
                            values.put("menu_id", menu_id);
                            db.insert(SUBMENUS, null, values);
                        }

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
            reloadRestaurantListAdapter(RestaurantListFragment.latestAdapter);
        }
    }

    public void updateRestaurant(JSONObject restaurant, int categoryId, MenuListActivity activity) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;
        ContentValues values;
        Server server = new Server(context);
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE id = %d;",
                    RESTAURANTS, restaurant.getInt("id")
            ), null);

            if (cursor != null && cursor.moveToFirst()) {
                // If the existing data is outdated, update from the server.
                // Else, do nothing.
                if (!restaurant.getString("updated_at")
                        .equals(cursor.getString(cursor.getColumnIndex("updated_at")))) {

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
                    values.put("notice", restaurant.getString("notice"));
                    values.put("retention", restaurant.getString("retention"));
                    values.put("number_of_my_calls", restaurant.getInt("number_of_my_calls"));
                    values.put("total_number_of_calls", restaurant.getInt("total_number_of_calls"));
                    values.put("total_number_of_goods", restaurant.getInt("total_number_of_goods"));
                    values.put("total_number_of_bads", restaurant.getInt("total_number_of_bads"));
                    values.put("my_preference", restaurant.getInt("my_preference"));
                    values.put("category_id", categoryId);
                    values.put("updated_at", restaurant.getString("updated_at"));
                    values.put("minimum_price", restaurant.getInt("minimum_price"));

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

                        JSONArray submenus = null;
                        if (menu.getJSONArray("submenus") != null) {
                            submenus = menu.getJSONArray("submenus");
                        }
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
                            values.put("price", 0);
                            db.insert(MENUS, null, values);

                            db.execSQL(String.format(
                                    "DELETE FROM %s WHERE menu_id = %d;",
                                    SUBMENUS, menu_id
                            ));

                            for (int k = 0; k < submenus.length(); k++) {
                                values = new ContentValues();
                                JSONObject submenu = submenus.getJSONObject(k);
                                values.put("name", submenu.getString("name"));
                                values.put("price", submenu.getInt("price"));
                                values.put("menu_id", menu_id);
                                db.insert(SUBMENUS, null, values);
                            }

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
                    reloadMenuListActivity(activity);
                    reloadRestaurantListAdapter(RestaurantListFragment.latestAdapter);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public ArrayList<Restaurant> getRestaurantsByCategory(int category_id) {
        Log.d("getRestaurantByCategory", "called");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Restaurant> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = null;
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category_id = '%d' ORDER BY name ASC;",
                    RESTAURANTS, category_id
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
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

    public ArrayList<Restaurant> getRestaurantsByCategoryFilteredFlyer(int category_id) {
        Log.d("getRestaurantByCategory", "called");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Restaurant> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = null;
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category_id = '%d'and has_flyer = 1 ORDER BY name ASC;",
                    RESTAURANTS, category_id
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
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
     * @param restaurantId The restaurant's server-side id.
     * @return Menu data of a restaurant.
     */
    public ArrayList<Menu> getMenusByRestaurantServerId(int restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Menu> list = new ArrayList<>();
        ArrayList<String> sections = new ArrayList<>();
        ArrayList<SubMenu> subMenus;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT section FROM %s WHERE restaurant_id = %d;",
                    MENUS, restaurantId
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
                        MENUS, restaurantId, section
                ), null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Menu menu = new Menu();
                        menu.setId(cursor.getInt((cursor.getColumnIndex("id"))));
                        menu.setItem((cursor.getString(cursor.getColumnIndex("name"))));
                        menu.setDescription((cursor.getString(cursor.getColumnIndex("description"))));
                        menu.setSection(cursor.getString(cursor.getColumnIndex("section")));
                        menu.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
                        menu.setRestaurantId(cursor.getInt(cursor.getColumnIndex("restaurant_id")));

                        int menuId = cursor.getInt((cursor.getColumnIndex("id")));
                        Log.d("menuId", cursor.getInt((cursor.getColumnIndex("id"))) + "");
                        subMenus = new ArrayList<>();
                        Cursor result = db.rawQuery(String.format(
                                "SELECT * FROM %s WHERE menu_id = %d;",
                                SUBMENUS, menuId
                        ), null);
                        if (result != null && result.moveToFirst()) {
                            do {
                                SubMenu subMenu = new SubMenu();
                                subMenu.setId(result.getInt(result.getColumnIndex("id")));
                                subMenu.setItem(result.getString(result.getColumnIndex("name")));
                                subMenu.setPrice(result.getInt(result.getColumnIndex("price")));
                                subMenu.setMenuId(result.getInt(result.getColumnIndex("menu_id")));
                                subMenus.add(subMenu);
                                Log.d("submenu", result.getInt(result.getColumnIndex("menu_id")) + result.getString(result.getColumnIndex("name")));
                            } while (result.moveToNext());

                        }

                        menu.setSubMenus(subMenus);
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
     * @param restaurantId The restaurant's server-side id.
     * @return Flyer urls of a restaurant.
     */
    public ArrayList<String> getFlyerUrlsByRestaurantServerId(int restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE restaurant_id = %d;",
                    FLYERS, restaurantId
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
     * Reload a {@link MenuListActivity activity}.
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

    public void insertRecentCalls(int restaurantId, int categoryId) {

        try {

            SQLiteDatabase db = getWritableDatabase();

            Date date = new Date();
            Long timestamp = date.getTime();

            ContentValues values = new ContentValues();
            values.put("restaurant_id", restaurantId);
            values.put("called_at", timestamp);
            values.put("category_id", categoryId);
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
                    "SELECT %s FROM %s WHERE restaurant_id = %s",
                    "count(*)",//select
                    CALLLOGS, //from
                    restaurant_id
            ), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null && cursor.moveToFirst()) {
            numberOfCalls = cursor.getInt(0);
            Log.d("databasehelper", "getnumberofcalls :" + numberOfCalls);
        }
        return numberOfCalls;
    }

    public ArrayList<CategoryItem> getCategoryList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<CategoryItem> list = new ArrayList<>();

        Cursor cursor;

        try {
            cursor = db.rawQuery(String.format("SELECT * FROM %s ORDER BY %s;", CATEGORIES, "server_id"), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new CategoryItem(cursor));
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return list;
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
                        "restaurants.id", "restaurants.name", "count(*)",//select
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

    public int getCategoryServerIdFromId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int serverId = 0;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT %s FROM %s WHERE id = %d;",
                    "server_id", CATEGORIES, id), null);
            if (cursor != null && cursor.moveToFirst()) {
                serverId = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return serverId;
    }

    public int getLastDay() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        int lastday = -1;
        try {
            cursor = db.rawQuery(String.format("SELECT %s FROM %s order by id DESC ", "called_at", CALLLOGS), null);
            if (cursor != null && cursor.moveToFirst()) {
                long last = cursor.getLong(0);
                Date date = new Date();
                Long now = date.getTime();
                last = now - last;
                lastday = (int) last / (24 * 60 * 60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lastday;
    }

    public String getTheMostOrderedFood() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        String food = null;
        try {
            cursor = db.rawQuery(String.format("SELECT title FROM %s WHERE id = (SELECT category_id FROM %s GROUP BY category_id ORDER BY count(category_id) DESC) LIMIT 1", CATEGORIES, CALLLOGS), null);
            if (cursor != null && cursor.moveToFirst()) {
                food = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return food;
    }

    public int getTotalNumberOfMyCalls() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        int numberOfMyCalls = 0;
        try {
            cursor = db.rawQuery(String.format("SELECT count(*) FROM %s ", CALLLOGS), null);
            if (cursor != null && cursor.moveToFirst()) {
                numberOfMyCalls = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return numberOfMyCalls;
    }

    public boolean hasRecent(long timestamp, int restaurantId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        long time = 0;
        double hour = 0;
        boolean hasRecent = false;
        try {
            cursor = db.rawQuery(String.format("SELECT called_at FROM %s WHERE restaurant_id = %s ORDER BY id desc LIMIT 1 ", CALLLOGS, restaurantId), null);
            if (cursor != null && cursor.moveToFirst()) {
                time = cursor.getLong(0);
                Log.d("getRecentcallTime", time / (1000 * 60 * 60) + "");
                Log.d("timestamp", time / (1000 * 60 * 60) + "");
                hour = (timestamp - time) / (1000 * 60 * 60);
                if (hour < 3.0) {
                    hasRecent = true;
                }
                Log.d("hour", hour + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return hasRecent;

    }
}
