package com.lchpatners.shadal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class RestaurantListAdapter extends BaseAdapter {

    public static final String BOOKMARK = "bookmark";

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    private Context context;
    private String category;
    private ArrayList<Object> data = new ArrayList<>();
    private ArrayList<String> headers = new ArrayList<>();

    public RestaurantListAdapter(Context context, String category) {
        this.context = context;
        this.category = category;
        reloadData();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof String
                && headers.contains(getItem(position)) ? HEADER : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void add(Restaurant restaurant) {
        data.add(restaurant);
        notifyDataSetChanged();
    }

    public void remove(Restaurant restaurant) {
        data.remove(restaurant);
        notifyDataSetChanged();
    }

    public void reloadData() {
        data.clear();
        headers.clear();
        ArrayList<Restaurant> restaurants;
        if (!category.equals(BOOKMARK)) {
            restaurants = DatabaseHelper.getInstance(context).getRestaurantsByCategory(category);
        } else {
            restaurants = DatabaseHelper.getInstance(context).getFavoriteRestaurants();
        }
        String header = null;
        for (Restaurant restaurant : restaurants) {
            if (category.equals(BOOKMARK) && !restaurant.getCategory().equals(header)) {
                header = restaurant.getCategory();
                data.add(header);
                headers.add(header);
            }
            data.add(restaurant);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch (getItemViewType(position)) {
                case HEADER:
                    convertView = inflater.inflate(R.layout.list_header_menu, parent, false);
                    break;
                case ITEM:
                    convertView = inflater.inflate(R.layout.list_item_restaurant, parent, false);
                    break;
            }
        }
        assert convertView != null;
        switch (getItemViewType(position)) {
            case HEADER:
                TextView header = (TextView)convertView.findViewById(R.id.header);
                header.setText((String)data.get(position));
                break;
            case ITEM:
                Restaurant restaurant = (Restaurant)data.get(position);
                TextView name = (TextView)convertView.findViewById(R.id.name);
                name.setText(restaurant.getName());
                convertView.findViewById(R.id.recent).setVisibility(restaurant.isNew() ? View.VISIBLE : View.GONE);
                convertView.findViewById(R.id.coupon).setVisibility(restaurant.hasCoupon() ? View.VISIBLE : View.GONE);
                convertView.findViewById(R.id.flyer).setVisibility(restaurant.hasFlyer() ? View.VISIBLE : View.GONE);
                convertView.findViewById(R.id.bookmark).setVisibility(restaurant.isFavorite()
                        && !category.equals("bookmark") ? View.VISIBLE : View.GONE);
                break;
        }
        return convertView;
    }
}
