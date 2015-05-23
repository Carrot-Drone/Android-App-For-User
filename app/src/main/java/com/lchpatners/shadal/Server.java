package com.lchpatners.shadal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Communicates with the server.
 */
public class Server {

    /**
     * Indicates HTTP method GET.
     */
    public static final int GET = 0;
    /**
     * Indicates HTTP method POST.
     */
    public static final int POST = 1;

    /**
     * The server's domain name.
     */
    public static final String BASE_URL = "http://www.shadal.kr";
    /**
     * Campus list directory.
     */
    public static final String CAMPUSES = "/campuses_all";
    /**
     * Device update directory.
     */
    public static final String UPDATE_DEVICE = "/updateDevice";
    /**
     * Restaurant list directory.
     */
    public static final String ALL_RESTAURANTS = "/allRestaurants?campus=";
    /**
     * A restaurant's update checking directory.
     */
    public static final String CHECK_FOR_UPDATE = "/checkForUpdate";
    /**
     * A category's restaurant list directory.
     */
    public static final String CHECK_FOR_RES_IN_CATEGORY = "/checkForResInCategory";
    /**
     * App version checking directory.
     */
    public static final String APP_MINIMUM_VERSION = "/appMinimumVersion";

    /**
     * {@link android.content.Context Context} this belongs to.
     */
    private Context context;

    public Server(Context context) {
        this.context = context;
    }

    /**
     * Make a service call with the arguments and return the result.
     * @param url URL to fetch.
     * @param method HTTP method.
     * @param params HTTP request parameters.
     * @return The content of the request.
     */
    public static String makeServiceCall(String url, int method, List<NameValuePair> params) {
        String result;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpEntity entity;
            HttpResponse response = null;
            switch (method) {
                case GET:
                    if (params != null) {
                        url += "?" + URLEncodedUtils.format(params, "utf-8");
                    }
                    HttpGet get = new HttpGet(url);
                    response = client.execute(get);
                    break;
                case POST:
                    HttpPost post = new HttpPost(url);
                    if (params != null) {
                        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                    }
                    response = client.execute(post);
                    break;
            }
            assert response != null;
            entity = response.getEntity();
            result = entity == null ? null : EntityUtils.toString(entity, HTTP.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * Send the device UUID. Server registers if the ID's new to it.
     */
    public void sendUuid() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... urls) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("uuid", Preferences.getDeviceUuid(context)));
                params.add(new BasicNameValuePair("campus", Preferences.getCampusEnglishName(context)));
                params.add(new BasicNameValuePair("device", "android"));
                makeServiceCall(urls[0], GET, params);
                return null;
            }
        }.execute(BASE_URL + UPDATE_DEVICE);
    }

    /**
     * Check whether the app is newer or not than the minimally required version.
     * @see com.lchpatners.shadal.Server.AppMinimumVersionTask AppMinimumVersionTask
     */
    public void checkAppMinimumVersion() {
        new AppMinimumVersionTask().execute();
    }

    /**
     * An {@link android.os.AsyncTask} to load minimum version data from the server.
     * If the app is too old, have the user get the latest version from the Play store.
     */
    private class AppMinimumVersionTask extends AsyncTask<Void, Void, Void> {
        int minimumVersion, appVersion;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                minimumVersion = -1;
                String serviceCall = makeServiceCall(BASE_URL + APP_MINIMUM_VERSION, GET, null);
                if (serviceCall == null) {
                    return null;
                }
                minimumVersion = new JSONObject(serviceCall).getInt("minimum_android_version");
                if (minimumVersion == -1) {
                    throw new RuntimeException("failed to get minimum version code from the server");
                }
                appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (JSONException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (appVersion < minimumVersion) {
                Toast.makeText(context, context.getString(R.string.too_old_version), Toast.LENGTH_LONG).show();
                try {
                    Activity activity = (Activity)context;
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + activity.getPackageName())));
                } catch (ActivityNotFoundException e) {
                    Activity activity = (Activity)context;
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                } finally {
                    ((Activity)context).finish();
                }
            }
        }
    }

    /**
     * Update all {@link com.lchpatners.shadal.Restaurant Restaurants}
     * of the currently selected campus.
     * @see com.lchpatners.shadal.Server.TotalUpdateTask TotalUpdateTask
     * @see CampusSelectionActivity#tryLoadingFromServer() CampusSelectionActivity.tryLoadingFromServer()
     * @see com.lchpatners.shadal.MainActivity#onCreate(android.os.Bundle) MainActivity.onCreate(Bundle)
     */
    public void updateAll() {
        new TotalUpdateTask().execute(BASE_URL + ALL_RESTAURANTS +
                Preferences.getCampusEnglishName(context));
    }

    /**
     * An {@link android.os.AsyncTask} to update all in the campus.
     */
    private class TotalUpdateTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            try {
                String serviceCall = makeServiceCall(urls[0], GET, null);
                if (serviceCall == null) {
                    return null;
                }
                JSONArray restaurants = new JSONArray(serviceCall);
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                for (int i = 0; i < restaurants.length(); i++) {
                    helper.updateRestaurant(restaurants.getJSONObject(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Update a single {@link com.lchpatners.shadal.Restaurant Restaurant}.
     * @param id Server-side id of the {@link com.lchpatners.shadal.Restaurant Restaurant}
     * @param updatedTime The time when the {@link com.lchpatners.shadal.Restaurant Restaurant}
     *                    was updated for the last time on the device.
     * @see com.lchpatners.shadal.Server.RestaurantUpdateTask RestaurantUpdateTask
     */
    public void updateRestaurant(int id, String updatedTime) {
        updateRestaurant(id, updatedTime, null);
    }

    /**
     * Update a single {@link com.lchpatners.shadal.Restaurant Restaurant}.
     * @param id Server-side id of the {@link com.lchpatners.shadal.Restaurant Restaurant}
     * @param updatedTime The time when the {@link com.lchpatners.shadal.Restaurant Restaurant}
     *                    was updated for the last time on the device.
     * @param activity {@link com.lchpatners.shadal.MenuListActivity MenuListActivity}
     * to be refreshed as soon as the update is done.
     * @see com.lchpatners.shadal.Server.RestaurantUpdateTask RestaurantUpdateTask
     * @see MenuListActivity#setView() MenuListActivity.setView()
     */
    public void updateRestaurant(int id, String updatedTime, MenuListActivity activity) {
        new RestaurantUpdateTask(id, updatedTime, activity).execute(BASE_URL + CHECK_FOR_UPDATE);
    }

    /**
     * An {@link android.os.AsyncTask} to update a single {@link com.lchpatners.shadal.Restaurant Restaurant}.
     */
    private class RestaurantUpdateTask extends AsyncTask<String, Void, Void> {
        private int id;
        private String updatedTime;
        private MenuListActivity activity;

        public RestaurantUpdateTask(int id, String updatedTime, MenuListActivity activity) {
            this.id = id;
            this.updatedTime = updatedTime;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("restaurant_id", Integer.toString(id)));
                params.add(new BasicNameValuePair("updated_at", updatedTime));
                String serviceCall = makeServiceCall(urls[0], GET, params);
                if (serviceCall == null) {
                    return null;
                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                helper.updateRestaurant(new JSONObject(serviceCall), activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Update {@link com.lchpatners.shadal.Restaurant Restaurants}
     * in a {@link com.lchpatners.shadal.Restaurant#category category}.
     * @param category The category to be updated.
     * @see com.lchpatners.shadal.Server.CategoryUpdateTask CategoryUpdateTask
     */
    public void updateCategory(String category) {
        new CategoryUpdateTask(category).execute(BASE_URL + CHECK_FOR_RES_IN_CATEGORY);
    }

    /**
     * An {@link android.os.AsyncTask} to update {@link com.lchpatners.shadal.Restaurant Restaurants}
     * in a {@link com.lchpatners.shadal.Restaurant#category category}.
     * @see com.lchpatners.shadal.RestaurantListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    private class CategoryUpdateTask extends AsyncTask<String, Void, Void> {
        private String category;

        public CategoryUpdateTask(String category) {
            this.category = category;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("campus", Preferences.getCampusEnglishName(context)));
                params.add(new BasicNameValuePair("category", category));
                String serviceCall = makeServiceCall(urls[0], GET, params);
                if (serviceCall == null) {
                    return null;
                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                helper.updateCategory(new JSONArray(serviceCall), category);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Send a call log to the server.
     * @param restaurant The target {@link com.lchpatners.shadal.Restaurant Restaurant}
     */
    public void sendCallLog(final Restaurant restaurant) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://shadal.kr/new_call");
                    ArrayList<BasicNameValuePair> value = new ArrayList<>();
                    value.add(new BasicNameValuePair("phoneNumber", restaurant.getPhoneNumber()));
                    value.add(new BasicNameValuePair("name", restaurant.getName()));
                    value.add(new BasicNameValuePair("device", "android"));
                    value.add(new BasicNameValuePair("campus", Preferences.getCampusEnglishName(context)));
                    value.add(new BasicNameValuePair("server_id", Integer.toString(restaurant.getServerId())));
                    value.add(new BasicNameValuePair("uuid", Preferences.getDeviceUuid(context)));
                    post.setEntity(new UrlEncodedFormEntity(value, "UTF-8"));
                    client.execute(post);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }



    /**
     * An {@link android.os.AsyncTask} to load campuses from the server.
     */
    public static class CampusesLoadingTask extends AsyncTask<Void, Void, Void> {
        String serviceCall;
        JSONArray results;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                serviceCall = Server.makeServiceCall(
                        Server.BASE_URL + Server.CAMPUSES, Server.GET, null);
                if (serviceCall == null) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                results = new JSONArray(serviceCall);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
