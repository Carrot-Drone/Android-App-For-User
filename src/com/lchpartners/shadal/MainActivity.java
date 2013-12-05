package com.lchpartners.shadal;

import java.io.IOException;

import info.android.sqlite.helper.DatabaseHelper;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity{
	DatabaseHelper mDbHelper;
	
	String [] categories = { "치킨", "피자", "중국집", "한식/분식", "도시락/돈까스", "족발/보쌈", "냉면", "기타"};
	TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categories);
		setListAdapter(adapter);
		
		// 처음 설치시 assets/databases/Shadal 파일로 디비 설정
		try{
	        mDbHelper = new DatabaseHelper(getApplicationContext());
	        boolean isExist = mDbHelper.isDataBaseExist();

	        SQLiteDatabase db;
	        if(!isExist){
	            //get database, we will override it in next steep
	            //but folders containing the database are created
	            db = mDbHelper.getWritableDatabase();
	            db.close();
	            //copy database
	            mDbHelper.copyDataBase();                        
	        }                                              
	        db = mDbHelper.getWritableDatabase();
		}catch(SQLException eSQL){
	        Log.e("log_tag","Can not open database");
		}
		catch (IOException IOe) {
	        Log.e("log_tag","Can not copy initial database");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent moveToRestaurant = new Intent(MainActivity.this, RestaurantActivity.class);
		moveToRestaurant.putExtra("category", position);
		startActivity(moveToRestaurant);
		onPause();
	}
}
