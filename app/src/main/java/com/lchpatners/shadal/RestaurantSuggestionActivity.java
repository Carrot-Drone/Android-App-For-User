package com.lchpatners.shadal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class RestaurantSuggestionActivity extends ActionBarActivity {

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.RSbyUser) {
                Intent intent = new Intent(RestaurantSuggestionActivity.this, RSbyUserActivity.class);
                startActivity(intent);
            } else if (view.getId() == R.id.RSbyOwner) {
                Intent intent = new Intent(RestaurantSuggestionActivity.this, RSbyOwnerActivity.class);
                startActivity(intent);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_suggestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        String title = getString(R.string.title_activity_restaurant_suggestion);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button byUser = (Button) findViewById(R.id.RSbyUser);
        Button byOwner = (Button) findViewById(R.id.RSbyOwner);

        byUser.setOnClickListener(btnListener);
        byOwner.setOnClickListener(btnListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
