package com.lchpatners.shadal.restaurant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;
import com.lchpatners.shadal.restaurant.menu.RestaurantMenu;
import com.lchpatners.shadal.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RestaurantListAdapter extends BaseAdapter {
    private static final String TAG = LogUtils.makeTag(RestaurantListAdapter.class);

    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";

    private Context mContext;
    private List<Restaurant> mRestaurantList;

    public RestaurantListAdapter(Context context, List<Restaurant> restaurantList) {
        this.mContext = context;
        this.mRestaurantList = restaurantList;
        sortRestaurantMenu();
    }

    private void sortRestaurantMenu() {
        List<Restaurant> tempRestaurantList = new ArrayList<Restaurant>();

        for (Restaurant restaurant : mRestaurantList) {
            tempRestaurantList.add(restaurant);
        }

        Collections.sort(tempRestaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurant, Restaurant t1) {
                String compare = restaurant.getName();
                String compareT = t1.getName();

                //ascending order
                return compare.compareTo(compareT);
            }
        });

        mRestaurantList = tempRestaurantList;
    }

    @Override
    public int getCount() {
        return mRestaurantList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRestaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        final Restaurant restaurant = mRestaurantList.get(pos);

        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_restaurant, null);

        TextView retention = (TextView) convertView.findViewById(R.id.retention);
        retention.setText(Math.round(restaurant.getRetention() * 100) + "");

        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(restaurant.getName());

        convertView.findViewById(R.id.flyer).setVisibility(restaurant.isHas_flyer() ? View.VISIBLE : View.GONE);

        if (restaurant.isHas_flyer()) {
            final ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.flyer);
            imgBtn.setClickable(true);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> flyer_urls = new ArrayList<String>();
                    RealmList<Flyer> flyers = restaurant.getFlyers();

                    for (Flyer flyer : flyers) {
                        flyer_urls.add(flyer.getUrl());
                    }

                    Intent intent = new Intent(mContext, FlyerActivity.class);
                    intent.putExtra(FLYER_URLS, flyer_urls);
                    intent.putExtra(RESTAURANT_ID, restaurant.getId());
                    intent.putExtra(RESTAURANT_PHONE_NUMBER, restaurant.getPhone_number());
                    mContext.startActivity(intent);
                }
            });
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AnalyticsHelper helper = new AnalyticsHelper(context);
//                helper.sendEvent("UX", "res_clicked", restaurant.getName());
                Intent intent = new Intent(mContext, RestaurantInfoActivity.class);
                intent.putExtra(RESTAURANT_ID, restaurant.getId());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}


