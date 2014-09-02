package com.lchpartners.apphelper.server;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.lchpartners.fragments.RestaurantsFragment.RestaurantsAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import info.android.sqlite.helper.DatabaseHelper;

/**
 * Created by swchoi06 on 8/29/14.
 * Note by Gwangrae Kim on Sep 02 : DO NOT update DB if the data received is incomplete
 * Catch ALL ERRORS while updating, so that any error cannot make the entire app being killed.
 * TODO - this part requires refactoring
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

    private final static String TAG = "Server";

    //For handling data
    public static Activity mActivity;

    public Server(Activity activity){
        this.mActivity = activity;
    }

    public void updateAllRestaurant() {
        new HttpAsyncAllData().execute(WEB_BASE_URL + ALL_DATA);
    }

    public class HttpAsyncAllData extends AsyncTask<String, Void, String> {
        private JSONArray mRestaurantsArray;

        @Override
        protected String doInBackground(String... urls) {
            return makeServiceCall(urls[0], GET);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(result == null)
                    return;
                else if (mActivity.isFinishing())
                    return;

                mRestaurantsArray = new JSONArray(result);
                DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.onCreate(db);
                for(int i = 0; i<mRestaurantsArray.length(); i++){
//                    Log.d("tag", String.valueOf(i));
//                    Log.d("tag", resArray.getJSONObject(i).getString("name"));
                    dbHelper.updateRestaurant(mRestaurantsArray.getJSONObject(i));
                }
                dbHelper.closeDB();
            }
            catch (Exception e) {
                Log.e(TAG, "", e);
                //TODO - DO NOT use .printStackTrace for errors
                // as it's log level is just fixed to 'Warning'
            }
        }
    }

    // 특정 음식점 데이터 Update
    //TODO : executing AsyncTask in the AsyncTask is forbidden.
    public void updateRestaurant(int restaurant_id, String updated_at){
 //       Log.d("tag", "Update Res" + String.valueOf(restaurant_id));
        HttpAsyncRestaurant async = new HttpAsyncRestaurant();
        async.restaurant_id = restaurant_id;
        async.updated_at = updated_at;
        async.execute(WEB_BASE_URL + CHECK_FOR_UPDATE);
    }

    public void updateRestaurant(int restaurant_id, String updated_at, RestaurantsAdapter mAdapter){
 //       Log.d("tag", "Update Res with Adapter" + String.valueOf(restaurant_id));
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
            try {
                if(result == null)
                    return;
                else if (mActivity.isFinishing())
                    return;

                DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
                //           Log.d("tag", result);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.updateRestaurant(new JSONObject(result));
                dbHelper.closeDB();

                if(mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
            catch (Exception e) {
                Log.e(TAG,"",e);
            }
        }
    }

    public void updateRestaurantInCategory(String category){
        HttpAsyncRestaurantInCategory async = new HttpAsyncRestaurantInCategory();
        async.category = category;
        async.execute(WEB_BASE_URL+CHECK_FOR_RES_IN_CATEGORY);
    }

    //TODO - change method name : can be confused with the DatabaseHelper method.
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
            if(result == null)
                return;
            else if (mActivity.isFinishing())
                return;

            //TODO - ensure db closing use finally blocks.
            DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
            try {
                dbHelper.updateRestaurantInCategory(new JSONArray(result), category, mAdapter);
            }
            catch (Exception e) {
                Log.e(TAG, "", e);
            }
            finally {
                dbHelper.closeDB();
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
            }
            else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);

            }

            httpEntity = httpResponse.getEntity();
            if(httpEntity != null) {
                response = EntityUtils.toString(httpEntity, HTTP.UTF_8);
            }
            else {
                response = null;
            }
        }
        catch (UnsupportedEncodingException e) {
            response = null;
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            response = null;
            e.printStackTrace();
        }
        catch (Exception e) {
            response = null;
            e.printStackTrace();
        }

        return response;
    }
}