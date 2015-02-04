package com.lchpatners.shadal;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guanadah on 2015-01-26.
 */
public class Server {

    public static final String GWANAK = "Gwanak";

    public static final int GET = 0;
    public static final int POST = 1;

    private static final String BASE_URL = "http://www.shadal.kr";
    private static final String ALL_RESTAURANTS = "/allRestaurants?campus=";
    private static final String CHECK_FOR_UPDATE = "/checkForUpdate";
    private static final String CHECK_FOR_RES_IN_CATEGORY = "/checkForResInCategory";

    private String campus;
    private Context context;
    private static TotalUpdateTask totalUpdateTask;
    private static CategoryUpdateTask categoryUpdateTask;
    private static RestaurantUpdateTask restaurantUpdateTask;

    public Server(Context context, String campus) {
        this.context = context;
        this.campus = campus;
    }

    public String allDataAddress() {
        return ALL_RESTAURANTS + campus;
    }

    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
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

    public void updateAll() {
        if (totalUpdateTask != null) {
            totalUpdateTask.cancel(true);
        }
        totalUpdateTask = new TotalUpdateTask();
        totalUpdateTask.execute(BASE_URL + allDataAddress());
    }

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

    public void updateRestaurant(int id, String updatedTime) {
        updateRestaurant(id, updatedTime, null);
    }

    public void updateRestaurant(int id, String updatedTime, MenuListActivity activity) {
        if (restaurantUpdateTask != null) {
            restaurantUpdateTask.cancel(true);
        }
        restaurantUpdateTask = new RestaurantUpdateTask(id, updatedTime, activity);
        restaurantUpdateTask.execute(BASE_URL + CHECK_FOR_UPDATE);
    }

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

    public void updateCategory(String category, RestaurantListAdapter adapter) {
        if (categoryUpdateTask != null) {
            categoryUpdateTask.cancel(true);
        }
        categoryUpdateTask = new CategoryUpdateTask(category, adapter);
        categoryUpdateTask.execute(BASE_URL + CHECK_FOR_RES_IN_CATEGORY);
    }

    private class CategoryUpdateTask extends AsyncTask<String, Void, Void> {
        private String category;
        private RestaurantListAdapter adapter;

        public CategoryUpdateTask(String category, RestaurantListAdapter adapter) {
            this.category = category;
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("campus", campus));
                params.add(new BasicNameValuePair("category", category));
                String serviceCall = makeServiceCall(urls[0], GET, params);
                if (serviceCall == null) {
                    return null;
                }
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                helper.updateCategory(new JSONArray(serviceCall), category, adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
