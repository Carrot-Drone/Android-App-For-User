package com.lchpartners.android.adaptor;

import java.io.IOException;
import java.util.ArrayList;

import com.lchpartners.shadal.R;

import info.android.sqlite.model.Restaurant;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {
	private final Context context;
	private final ArrayList<Restaurant> values;
	final float scale = getContext().getResources().getDisplayMetrics().density;
 
	public RestaurantArrayAdapter(Context context, ArrayList<Restaurant> values) {
		super(context, R.layout.adapter_list, values);
		
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.adapter_list, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.restaurant_name);
		textView.setText(values.get(position).getName());
		textView.setTextSize((float) 15.0);
	    textView.setFocusable(false);
	    textView.setFocusableInTouchMode(false);
	    
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
