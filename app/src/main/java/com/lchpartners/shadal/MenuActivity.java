package com.lchpartners.shadal;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Menu_data;
import info.android.sqlite.model.Restaurant;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lchpartners.android.adaptor.ExpandableMenuAdapter;
import com.lchpartners.apphelper.server.Server;

public class MenuActivity extends Activity implements OnClickListener {
	private DatabaseHelper db;
	
	private Restaurant restaurant;
	
	private ArrayList<String> sectionList;
	private ArrayList<ArrayList<String>> menuList;
	private ArrayList<ArrayList<String>> priceList;
	private ExpandableListView elView;

	private void setRestaurantFromDatabase(int res_id){
	    Context context = getApplicationContext();
	    db = new DatabaseHelper(context);
	    
	    restaurant = db.getRestaurant((long)res_id);
	    
	    ArrayList<Menu_data> menus = db.getAllMenusByRestaurant((long)restaurant.server_id);
	    int menu_size = menus.size();
	

		sectionList = new ArrayList<String>();
		menuList = new ArrayList<ArrayList<String>>();
		priceList = new ArrayList<ArrayList<String>>();
	    
	    String current_sec = menus.get(0).getSection();
	    ArrayList<String> menuList_i = new ArrayList<String>(); 
	    ArrayList<String> priceList_i = new ArrayList<String>();
	    
	    for(int i=0; i<menu_size; i++){
	    	Menu_data menu = menus.get(i);
	    	if(!menu.getSection().equals(current_sec)){
	    		sectionList.add(current_sec);
	    		menuList.add(menuList_i);
	    		priceList.add(priceList_i);

	    		menuList_i = new ArrayList<String>();
	    		priceList_i = new ArrayList<String>();
	    	}
	    	menuList_i.add(menu.getMenu());
	    	priceList_i.add(String.valueOf(menu.getPrice()));
	    	
	    	current_sec = menu.getSection();
	    }
	    // Add last section.
		sectionList.add(current_sec);
		menuList.add(menuList_i);
		priceList.add(priceList_i);
		
		// release memory
		menuList_i = null;
		priceList_i = null;
	}

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu); 
		
		// Show the Up button in the action bar.
//		setupActionBar();
		
		Intent caller = getIntent();
		int res_id = caller.getIntExtra("res_id", -1);
		
		setRestaurantFromDatabase(res_id);

        // check for update
        if(this.isConnected()){
            Server server = new Server(this.getApplicationContext());
            server.updateRestaurant(restaurant.server_id, restaurant.updated_at);
        }
		
		TextView nameView = (TextView)findViewById(R.id.textview_restaurantName);
		nameView.setText(restaurant.name);
		TextView numberView = (TextView)findViewById(R.id.editText_phonenumber);
		numberView.setText(restaurant.phoneNumber);
		numberView.setOnClickListener(this);
		TextView otimeView = (TextView)findViewById(R.id.textview_opentime);
		TextView ctimeView = (TextView)findViewById(R.id.textview_closeTime);
		
		if(restaurant.openingHours.equals("0") || restaurant.closingHours.equals("0")){
			TextView fromto = (TextView)findViewById(R.id.textview_fromto);
			TextView time = (TextView)findViewById(R.id.textView_time);
			otimeView.setVisibility(View.INVISIBLE);
			ctimeView.setVisibility(View.INVISIBLE);
			fromto.setVisibility(View.INVISIBLE);
			time.setVisibility(View.INVISIBLE);
		}else{
			int otime = (int)Double.parseDouble(restaurant.openingHours)*100;
			int ctime = (int)Double.parseDouble(restaurant.closingHours)*100;
			if(otime%100>=10)
				otimeView.setText("" + otime/100 + ":" + otime%100);
			else
				otimeView.setText("" + otime/100 + ":0" + otime%100);
			if(ctime%100>=10)
				ctimeView.setText("" + ctime/100 + ":" + ctime%100);
			else
				ctimeView.setText("" + ctime/100 + ":0" + ctime%100);	
		}
		
		if(restaurant.has_coupon == true){
			TextView couponString = (TextView)findViewById(R.id.textview_couponString);
			couponString.setText(restaurant.coupon_string);
			couponString.setTextSize(20);
		}else{
			TextView couponString = (TextView)findViewById(R.id.textview_couponString);
			couponString.setVisibility(ImageView.INVISIBLE);
			elView = (ExpandableListView) findViewById(R.id.expandableListView_menus);
			
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			        ViewGroup.LayoutParams.WRAP_CONTENT);
			p.addRule(RelativeLayout.BELOW, R.id.textView_time);
			elView.setLayoutParams(p);
		}
		elView = (ExpandableListView) findViewById(R.id.expandableListView_menus);
		elView.setIndicatorBounds(0, 30);

		elView.setAdapter(new ExpandableMenuAdapter(this,sectionList,menuList,priceList));
	}



	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(restaurant.name);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(restaurant.getFlyer()){
			getMenuInflater().inflate(R.menu.menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_flyer:
			Intent moveToFlyer = new Intent(MenuActivity.this, FlyerActivity.class);
			moveToFlyer.putExtra("phoneNumber", restaurant.phoneNumber);
			startActivity(moveToFlyer);
			onPause();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}



	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.editText_phonenumber : sendLog(restaurant.id);break;
		default : break;
		}
	}

	private void sendLog(int res_id){
		Log.d("sendLog", "sendLog");

		LogSender sender = new LogSender(getApplicationContext());
		sender.execute(String.valueOf(res_id));
		try {
			if(sender.get() != null)
				Log.d("sendLog",sender.get().toString());
			else
				Log.e("sendLog","Null response returned");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		

	}



}

class LogSender extends AsyncTask<String, Void, HttpResponse>{
	private Context context;
	public LogSender(Context applicationContext) {
		context = applicationContext;
	}

	@Override
	protected HttpResponse doInBackground(String... arg0) {
		int res_id = Integer.parseInt(arg0[0]);
		
	    DatabaseHelper db = new DatabaseHelper(context);
	    Restaurant res = db.getRestaurant((long)res_id);

		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://services.snu.ac.kr:3111/new_call");
			ArrayList<BasicNameValuePair> value = new ArrayList<BasicNameValuePair>();
			value.add(new BasicNameValuePair("phoneNumber", res.getPhoneNumber()));
			value.add(new BasicNameValuePair("name", res.getName()));
			value.add(new BasicNameValuePair("device", "android"));
			value.add(new BasicNameValuePair("campus", Server.CAMPUS));
			
			try {
				httppost.setEntity(new UrlEncodedFormEntity(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			return httpclient.execute(httppost);

		}catch(Exception e){
			return null;
		}
	}

}
