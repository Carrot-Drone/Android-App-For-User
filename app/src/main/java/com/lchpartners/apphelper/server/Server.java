package com.lchpartners.apphelper.server;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.lchpartners.android.adaptor.RestaurantsAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import org.json.JSONObject;
import org.json.JSONException;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

/**
 * Created by swchoi06 on 8/29/14.
 */
public class Server{
    public static final String CAMPUS = "Gwanak";

    public static final String WEB_BASE_URL = "http://www.shadal.kr";
    public static final String CHECK_FOR_UPDATE = "/checkForUpdate";
    public static final String CHECK_FOR_RES_IN_CATEGORY = "/checkForResInCategory";
    public static final String ALL_DATA = "/campus/" + CAMPUS + ".json";

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    //For handling data
    public static Context context;
    DatabaseHelper mDbHelper;

    public Server(Context context){
        this.context = context;
    }
    // 전체 음식점 데이터를 업데이트
    public void updateAllRestaurant(){
        new HttpAsyncAllData().execute(WEB_BASE_URL + ALL_DATA);
    }
    public class HttpAsyncAllData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return makeServiceCall(urls[0], GET);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            mDbHelper.onCreate(db);
            try {
                JSONArray resArray = new JSONArray(result);
                for(int i = 0; i<resArray.length(); i++){
                    Log.d("tag", String.valueOf(i));
                    Log.d("tag", resArray.getJSONObject(i).getString("name"));
                    mDbHelper.updateRestaurant(resArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("tag", String.valueOf(18));

            } catch (Exception e) {
                Log.d("tag", String.valueOf(28));

                e.printStackTrace();
            }
            mDbHelper.closeDB();
        }
    }
    // 특정 음식점 데이터를 업데이트
    public void updateRestaurant(int restaurant_id, String updated_at){
        Log.d("tag", "Update Res" + String.valueOf(restaurant_id));
        HttpAsyncRestaurant async = new HttpAsyncRestaurant();
        async.restaurant_id = restaurant_id;
        async.updated_at = updated_at;
        async.execute(WEB_BASE_URL + CHECK_FOR_UPDATE);
    }
    public void updateRestaurant(int restaurant_id, String updated_at, RestaurantsAdapter mAdapter){
        Log.d("tag", "Update Res with Adapter" + String.valueOf(restaurant_id));
        HttpAsyncRestaurant async = new HttpAsyncRestaurant();
        async.restaurant_id = restaurant_id;
        async.updated_at = updated_at;
        async.mAdapter = mAdapter;
        async.execute(WEB_BASE_URL + CHECK_FOR_UPDATE);
    }
    public class HttpAsyncRestaurant extends AsyncTask<String, Void, String> {
        public int restaurant_id;
        public String updated_at;
        public RestaurantsAdapter mAdapter;
        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("restaurant_id",Integer.toString(restaurant_id)));
            params.add(new BasicNameValuePair("updated_at",updated_at));

            return makeServiceCall(urls[0], POST, params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            mDbHelper = new DatabaseHelper(context);
            Log.d("tag", result);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                mDbHelper.updateRestaurant(new JSONObject(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDbHelper.closeDB();
            if(mAdapter != null){
                Log.d("tag", "Notify Data Changed");

                mAdapter.notifyDataSetChanged();
            }
        }
    }
    public void updateRestaurantInCategory(String category){
        HttpAsyncRestaurantInCategory async = new HttpAsyncRestaurantInCategory();
        async.category = category;
        async.execute(WEB_BASE_URL+CHECK_FOR_RES_IN_CATEGORY);
    }
    public void updateRestaurantInCategory(String category, RestaurantsAdapter mAdapter){
        HttpAsyncRestaurantInCategory async = new HttpAsyncRestaurantInCategory();
        async.category = category;
        async.mAdapter = mAdapter;
        async.execute(WEB_BASE_URL+CHECK_FOR_RES_IN_CATEGORY);
    }
    public class HttpAsyncRestaurantInCategory extends AsyncTask<String, Void, String> {
        public String category;
        public String campus = CAMPUS;
        public RestaurantsAdapter mAdapter;
        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("category",category));
            params.add(new BasicNameValuePair("campus", campus));

            return makeServiceCall(urls[0], POST, params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("tag", "Update Restaurant In Category");
            if(result != null){
                Log.d("tag", "++" + result);
            }
            mDbHelper = new DatabaseHelper(context);
            try {
                mDbHelper.updateRestaurantInCategory(new JSONArray(result), category, mAdapter);
                mDbHelper.closeDB();

            } catch (Exception e) {
                e.printStackTrace();
                mDbHelper.closeDB();
            }
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     *      * Making service call
     *      * @url - url to make request
     *      * @method - http request method
     *      *
     */
    static public String makeServiceCall(String url, int method) {
        return makeServiceCall(url, method, null);
    }
    /**
     *      * Making service call
     *      * @url - url to make request
     *      * @method - http request method
     *      * @params - http request params
     *      *
     */
    static public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                }

                httpResponse = httpClient.execute(httpPost);
            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}