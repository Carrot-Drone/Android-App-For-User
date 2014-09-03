package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpartners.shadal.MainActivity;
import com.lchpartners.shadal.R;

import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

/**
 * Created by Gwangrae Kim on 2014-09-02.
 */
public class FavoriteFragment extends Fragment implements ActionBarUpdater {
    private boolean updateActionBarOnCreateView = false;
    private MainActivity mActivity;

    public static class FavoritesAdapter extends ArrayAdapter<Restaurant> {
        private static class FavoriteViewHolder {
            public TextView restaurantName;
            public ImageView flyer, category, coupon, newRestaurant;
        }

        private final MainActivity mActivity;
        private final ArrayList<Restaurant> values;
        private final LayoutInflater mInflater;

        private int TAB_TO_ATTACH_MENU_FRAGMENT = MainActivity.TAB_MAIN;

        /**
         * By Default, this class will attach MenuFragment to MainActivity.TAB_MAIN
         */
        public FavoritesAdapter(MainActivity activity, ArrayList<Restaurant> restaurants) {
            super(activity, R.layout.listview_item_restaurant, restaurants);
            this.mActivity = activity;
            this.values = restaurants;
            this.mInflater = LayoutInflater.from(activity);
        }

        public FavoritesAdapter(MainActivity activity, ArrayList<Restaurant> restaurants,
                                int tabToAttachMenuFragment) {
            this (activity, restaurants);
            this.TAB_TO_ATTACH_MENU_FRAGMENT = tabToAttachMenuFragment;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView restaurantName;
            ImageView flyer, category, coupon, newRestaurant;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listview_item_favorite, null);
                restaurantName = (TextView) convertView.findViewById(R.id.restaurant_name);
                flyer = (ImageView) convertView.findViewById(R.id.flyer);
                category = (ImageView) convertView.findViewById(R.id.category);
                coupon = (ImageView) convertView.findViewById(R.id.coupon);
                newRestaurant = (ImageView) convertView.findViewById(R.id.newRestaurant);

                FavoriteViewHolder viewHolder = new FavoriteViewHolder();
                viewHolder.flyer = flyer;
                viewHolder.category = category;
                viewHolder.coupon = coupon;
                viewHolder.newRestaurant = newRestaurant;

                viewHolder.restaurantName = restaurantName;
                convertView.setTag(viewHolder);
            }
            else {
                FavoriteViewHolder viewHolder = (FavoriteViewHolder) convertView.getTag();
                restaurantName = viewHolder.restaurantName;
                flyer = viewHolder.flyer;
                category = viewHolder.category;
                coupon = viewHolder.coupon;
                newRestaurant = viewHolder.newRestaurant;
            }

            final Restaurant restaurant = getItem(position);
            int currentCategoryDrawable = CategoryFragment.getCategoryDrawableByName(restaurant.getCategory());
            category.setImageDrawable(mActivity.getResources().getDrawable(currentCategoryDrawable));

            restaurantName.setText(restaurant.name);
            if(restaurant.getFlyer()) {
                flyer.setVisibility(View.VISIBLE);
            }
            if(restaurant.getCoupon()) {
                coupon.setVisibility(View.VISIBLE);
            }
            if(restaurant.isNew()) {
                newRestaurant.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Log.e("tag", "called");
                    MainActivity.ShadalTabsAdapter adapter = mActivity.getAdapter();
                    adapter.push(TAB_TO_ATTACH_MENU_FRAGMENT,
                            new MainActivity.ShadalTabsAdapter.FragmentRecord(MenuFragment.class, restaurant.id));
                }
            });

            if(position %2 == 0) convertView.setBackgroundColor(0xfff2f2f2);
            else convertView.setBackgroundColor(0xfffcfcfc);

            return convertView;
        }

    }


    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        FavoritesAdapter adapter = new FavoritesAdapter(mActivity, dbHelper.getFavoriteRestaurant()
                                                          , MainActivity.TAB_FAVORITE);
        dbHelper.closeDB();

        if (updateActionBarOnCreateView)
            updateActionBar();
        ListView resultView = (ListView) inflater.inflate(R.layout.fragment_restaurant, container, false);
        resultView.setAdapter(adapter);
        return resultView;
    }

    public void invalidate() {
        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        FavoritesAdapter adapter = new FavoritesAdapter(mActivity, dbHelper.getFavoriteRestaurant()
                , MainActivity.TAB_FAVORITE);
        dbHelper.closeDB();

        ListView resultView = (ListView) getView();
        resultView.setAdapter(adapter);
    }

    public void setUpdateActionBarOnCreateView() {
        this.updateActionBarOnCreateView = true;
    }
    public void updateActionBar () {
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar_favorite, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());

        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }

}