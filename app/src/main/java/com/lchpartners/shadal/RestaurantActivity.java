package com.lchpartners.shadal;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

import java.util.ArrayList;

import com.lchpartners.android.adaptor.RestaurantArrayAdapter;
import com.lchpartners.server.Server;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class RestaurantActivity extends ListActivity{
	private RestaurantArrayAdapter adapter;
	private ArrayList<Restaurant> results = new ArrayList<Restaurant>();
	private DatabaseHelper db;
	
	TextView selection;
    String category;
   
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_restaurant);

        // get current Res's category from Intent;
        Intent intent = new Intent(this.getIntent());
        int index  = intent.getIntExtra("category", 1);
        String [] categories = getResources().getStringArray(R.array.categories);
        category = categories[index];
		
        // Show the Up button in the action bar.
        // setupActionBar();
		
        Context context = getApplicationContext();
        db = new DatabaseHelper(context);
       
        openAndQueryDatabase();
        displayResultList();

        // update Restaurant In Category
        Server.context = getApplicationContext();
        Server server = new Server();
        server.updateRestaurantInCategory(category);
   }
   
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle("Restaurant");
		}
	}
   
   private void displayResultList() {
	   adapter = new RestaurantArrayAdapter(this, results);
	   setListAdapter(adapter);
	   
	   selection = (TextView)findViewById(R.id.selection);
   }
   
   private void openAndQueryDatabase() {
    
    System.out.println(category);

	results = db.getAllRestaurantsWithCategory(category);
   }
   
   @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
	   super.onListItemClick(l, v, position, id);
	   System.out.println(results.get(position).getName());
	   System.out.println(results.get(position).getId());
	   
		Intent moveToMenu = new Intent(RestaurantActivity.this, MenuActivity.class);
		moveToMenu.putExtra("res_id", (int)results.get(position).getId());
		
		startActivity(moveToMenu);
		onPause();
   }
}