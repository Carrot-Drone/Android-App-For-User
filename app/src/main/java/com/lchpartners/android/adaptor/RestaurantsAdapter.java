package com.lchpartners.android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lchpartners.shadal.R;

import java.util.ArrayList;

import info.android.sqlite.model.Restaurant;

public class RestaurantsAdapter extends ArrayAdapter<Restaurant> {

	private final Context context;
	private final ArrayList<Restaurant> values;
	final float scale = getContext().getResources().getDisplayMetrics().density;

	public RestaurantsAdapter(Context context, ArrayList<Restaurant> values) {
		super(context, R.layout.listview_item_restaurant, values);

		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View rowView = inflater.inflate(R.layout.listview_item_restaurant, null);
		TextView textView = (TextView) rowView.findViewById(R.id.restaurant_name);
		textView.setText(values.get(position).getName());
		textView.setTextSize((float) 15.0);
	    textView.setFocusable(false);
	    textView.setFocusableInTouchMode(false);

        rowView.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Intent moveToMenu = new Intent(RestaurantActivity.this, MenuActivity.class);
                //moveToMenu.putExtra("res_id", (int) mResults.get(position).getId());

                //startActivity(moveToMenu);
                //onPause();
                Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show();
            }
        });

	    int width = (int)(20*scale + 0.5f);
	    int height = (int)(20*scale + 0.5f);

	    ImageView flyer = (ImageView) rowView.findViewById(R.id.flyer);
	    flyer.setFocusable(false);
	    flyer.setFocusableInTouchMode(false);
	    flyer.setBackgroundColor(0);
	    flyer.getLayoutParams().width = width;
	    flyer.getLayoutParams().height = height;

	    ImageView coupon = (ImageView) rowView.findViewById(R.id.coupon);
	    coupon.setFocusable(false);
	    coupon.setFocusableInTouchMode(false);
	    coupon.setBackgroundColor(0);
	    coupon.getLayoutParams().width = (int)(width * 0.9);
	    coupon.getLayoutParams().height = (int)(height * 0.9);

	    Restaurant restaurant = values.get(position);
	    if(!restaurant.getFlyer()){
	    	flyer.setVisibility(View.INVISIBLE);
	    	if(!restaurant.getCoupon()){
	    		coupon.setVisibility(View.INVISIBLE);
	    	}else{
	    		// right align coupon View
	    	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)coupon.getLayoutParams();
	    	    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	    coupon.setLayoutParams(params);
	    	}
	    }else{
	    	if(!restaurant.getCoupon()){
	    		coupon.setVisibility(View.INVISIBLE);
	    	}
	    }
		return rowView;
	}

}
