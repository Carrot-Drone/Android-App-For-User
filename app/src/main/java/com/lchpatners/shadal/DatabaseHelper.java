package com.lchpatners.shadal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Guanadah on 2015-01-26.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 17;

    private static final String RESTAURANTS = "restaurants";
    private static final String MENUS = "menus";
    private static final String FLYERS = "flyers";

    private static final String RESTAURANT_COLUMNS = "(id INTEGER PRIMARY KEY, server_id INT, name TEXT, " +
            "category TEXT, openingHours TEXT, closingHours TEXT, phoneNumber TEXT, has_flyer INTEGER, " +
            "has_coupon INTEGER, is_new INTEGER, is_favorite INTEGER, coupon_string TEXT, updated_at TEXT)";
    private static final String MENU_COLUMNS = "(id INTEGER PRIMARY KEY, menu TEXT, section TEXT, " +
            "price INT, restaurant_id INT)";
    private static final String FLYER_COLUMNS = "(id INTEGER PRIMARY KEY, url TEXT, restaurant_id INT)";

    public static final String LEGACY_DATABASE_NAME = "Shadal";
    public static ArrayList<Integer> legacyBookmarks = new ArrayList<>();

    private static DatabaseHelper instance;
    private static String loadedCampus;

    private Context context;

    public static DatabaseHelper getInstance(Context context) {
        String selectedCampus = Preferences.getCampusEnglishName(context);
        //Log.d("SHADAL", "selectedCampus is " + selectedCampus);
        //Log.d("SHADAL", "loadedCampus is " + loadedCampus);
        if (instance == null || !loadedCampus.equals(selectedCampus)) {
            //Log.d("SHADAL", "Instantiating a new instance");
            synchronized (DatabaseHelper.class) {
                loadedCampus = selectedCampus;
                instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), Preferences.getCampusEnglishName(context), null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", RESTAURANTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", MENUS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", FLYERS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", RESTAURANTS, RESTAURANT_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", MENUS, MENU_COLUMNS));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s %s;", FLYERS, FLYER_COLUMNS));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean checkDatabase() {
        File dbFile = context.getDatabasePath(Preferences.getCampusEnglishName(context));
        return dbFile.exists();
    }

    public void updateRestaurant(JSONObject restaurantJson) {
        updateRestaurant(restaurantJson, null);
        reloadRestaurantListAdapter(RestaurantListFragment.latestAdapter);
    }

    public void updateRestaurant(JSONObject restaurantJson, final MenuListActivity activity) {
        Cursor cursor = null;
        try {
            int restaurantServerId = restaurantJson.getInt("id");
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values;

            boolean isLegacyBookmark = legacyBookmarks.contains(restaurantJson.getInt("id"));

            values = new ContentValues();
            values.put("server_id", restaurantJson.getInt("id"));
            values.put("updated_at", restaurantJson.getString("updated_at"));
            values.put("name", restaurantJson.getString("name"));
            values.put("phoneNumber", restaurantJson.getString("phone_number"));
            values.put("category", restaurantJson.getString("category"));
            values.put("openingHours", restaurantJson.getString("openingHours"));
            values.put("closingHours", restaurantJson.getString("closingHours"));
            values.put("has_flyer", (restaurantJson.getBoolean("has_flyer")) ? 1 : 0);
            values.put("has_coupon", (restaurantJson.getBoolean("has_coupon")) ? 1 : 0);
            values.put("is_new", (restaurantJson.getBoolean("is_new")) ? 1 : 0);
            values.put("is_favorite", isLegacyBookmark ? 1 : 0);
            values.put("coupon_string", restaurantJson.getString("coupon_string"));

            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE server_id = %d;",
                    RESTAURANTS, restaurantServerId
            ), null);
            // you could simply always delete and insert, but then data would be messed up
            if (cursor != null && cursor.moveToFirst()) {
                db.update(RESTAURANTS, values, "server_id = ?", new String[]{String.valueOf(restaurantServerId)});
            } else {
                db.insert(RESTAURANTS, null, values);
                if (isLegacyBookmark) {
                    reloadRestaurantListAdapter(BookmarkFragment.latestAdapter);
                }
            }

            // update menus and leaflet urls corresponding to the restaurant
            db.execSQL(String.format(
                    "DELETE FROM %s WHERE restaurant_id = %d;",
                    MENUS, restaurantServerId
            ));
            JSONArray menus = restaurantJson.getJSONArray("menus");
            for (int i = 0; i < menus.length(); i++) {
                JSONObject menu = menus.getJSONObject(i);
                db.execSQL(String.format(
                        "DELETE FROM %s WHERE restaurant_id = %d AND section = '%s' AND menu = '%s';",
                        MENUS, restaurantServerId, menu.getString("section"), menu.getString("name")
                ));
                values = new ContentValues();
                values.put("menu", menu.getString("name"));
                values.put("section", menu.getString("section"));
                values.put("price", menu.getInt("price"));
                values.put("restaurant_id", restaurantServerId);

                db.insert(MENUS, null, values);
            }

            db.execSQL(String.format(
                    "DELETE FROM %s WHERE restaurant_id = %d;",
                    FLYERS, restaurantServerId
            ));
            JSONArray urls = restaurantJson.getJSONArray("flyers_url");
            for (int i = 0; i < urls.length(); i++) {
                String url = urls.getString(i);
                values = new ContentValues();
                values.put("url", url);
                values.put("restaurant_id", restaurantServerId);
                db.insert(FLYERS, null, values);
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

    public void updateCategory(JSONArray restaurants, String category, final RestaurantListAdapter adapter) {
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
                // if there is an existing data, check if the data is outdated
                // else, if the restaurant is a new one, put it into the database and notify latestAdapter
                if (cursor != null && cursor.moveToFirst()) {
                    // if the existing data is outdated, update from the server
                    // else, do nothing
                    if (!restaurant.getString("updated_at")
                            .equals(cursor.getString(cursor.getColumnIndex("updated_at")))) {
                        server.updateRestaurant(restaurant.getInt("id"),
                                cursor.getString(cursor.getColumnIndex("updated_at")));
                    }
                } else {
                    restaurant.put("category", category);
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

            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category = '%s';",
                    RESTAURANTS, category
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
                        Restaurant restaurant = getRestaurantFromCursor(cursor);
                        list.add(restaurant);
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

    public void toggleFavoriteById(long restaurantId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        Restaurant restaurant = getRestaurantFromId(restaurantId);
        values.put("is_favorite", (!restaurant.isFavorite()) ? 1 : 0);
        db.update(RESTAURANTS, values, "id = " + restaurantId, null);
        reloadRestaurantListAdapter(BookmarkFragment.latestAdapter);
    }

    public ArrayList<Restaurant> getRestaurantsByCategory(String category) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Restaurant> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE category = '%s' ORDER BY has_flyer DESC, name ASC;",
                    RESTAURANTS, category
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(getRestaurantFromCursor(cursor));
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

    public ArrayList<Menu> getMenusByRestaurantServerId(long restaurantServerId) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Menu> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE restaurant_id = %d;",
                    MENUS, restaurantServerId
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

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
                restaurant = getRestaurantFromCursor(cursor);
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

    public Restaurant getRestaurantFromId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        Restaurant restaurant = null;
        try {
            cursor = db.rawQuery(String.format(
                    "SELECT * FROM %s WHERE id = %d;",
                    RESTAURANTS, id
            ), null);
            if (cursor != null && cursor.moveToFirst()) {
                restaurant = getRestaurantFromCursor(cursor);
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

    public Restaurant getRestaurantFromCursor(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(cursor.getInt(cursor.getColumnIndex("id")));
        restaurant.setServerId(cursor.getInt(cursor.getColumnIndex("server_id")));
        restaurant.setName((cursor.getString(cursor.getColumnIndex("name"))));
        restaurant.setCategory(cursor.getString(cursor.getColumnIndex("category")));
        restaurant.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        restaurant.setOpeningHour(cursor.getString(cursor.getColumnIndex("openingHours")));
        restaurant.setClosingHour(cursor.getString(cursor.getColumnIndex("closingHours")));
        restaurant.setHasFlyer(cursor.getInt(cursor.getColumnIndex("has_flyer")) == 1);
        restaurant.setHasCoupon(cursor.getInt(cursor.getColumnIndex("has_coupon")) == 1);
        restaurant.setNew(cursor.getInt(cursor.getColumnIndex("is_new")) == 1);
        restaurant.setFavorite(cursor.getInt(cursor.getColumnIndex("is_favorite")) == 1);
        restaurant.setCouponString(cursor.getString(cursor.getColumnIndex("coupon_string")));
        restaurant.setUpdatedTime(cursor.getString(cursor.getColumnIndex("updated_at")));
        return restaurant;
    }

    public void reloadRestaurantListAdapter(final RestaurantListAdapter adapter) {
        if (adapter != null) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.reloadData();
                }
            });
        }
    }

    public void reloadMenuListActivity(final MenuListActivity activity) {
        if (activity != null) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setView();
                }
            });
        }
    }

}
