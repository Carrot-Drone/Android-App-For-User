package com.lchpartners.android.adaptor;

import java.util.ArrayList;

import com.lchpartners.shadal.R;

import info.android.sqlite.model.Restaurant;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {
	private final Context context;
	private final ArrayList<Restaurant> values;
 
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
		textView.setTextSize((float) 20.0);
	    textView.setFocusable(false);
	    textView.setFocusableInTouchMode(false);

		return rowView;
	}

}
