package com.lchpatners.shadal.restaurant;

import android.app.Activity;
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
    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";
    private static final String TAG = LogUtils.makeTag(RestaurantListAdapter.class);
    private Activity mActivity;
    private int mCategoryNumber;
    private String mOrder;
    private List<Restaurant> mRestaurantList;

    public RestaurantListAdapter(Activity activity, int categoryNumber, String order_by) {
        this.mActivity = activity;
        this.mCategoryNumber = categoryNumber;
        this.mOrder = order_by;
        loadData(mOrder);
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
//        notifyDataSetChanged();
    }

    public void loadData(String orderBy) {
        mRestaurantList = RestaurantController.getRestaurantList(mActivity, mCategoryNumber, orderBy);
        if (mRestaurantList != null) {
            sortRestaurantMenu();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mRestaurantList != null) {
            return mRestaurantList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mRestaurantList != null) {
            return mRestaurantList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        final Restaurant restaurant = mRestaurantList.get(pos);

        convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_restaurant, null);

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

                    Intent intent = new Intent(mActivity, FlyerActivity.class);
                    intent.putExtra(FLYER_URLS, flyer_urls);
                    intent.putExtra(RESTAURANT_ID, restaurant.getId());
                    intent.putExtra(RESTAURANT_PHONE_NUMBER, restaurant.getPhone_number());
                    mActivity.startActivity(intent);
                }
            });
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AnalyticsHelper helper = new AnalyticsHelper(context);
//                helper.sendEvent("UX", "res_clicked", restaurant.getName());
                Intent intent = new Intent(mActivity, RestaurantInfoActivity.class);
                intent.putExtra(RESTAURANT_ID, restaurant.getId());
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }
}


