package com.lchpatners.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lchpatners.shadal.MainActivity;
import com.lchpatners.shadal.R;

import java.util.ArrayList;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.model.Restaurant;

/**
 * Created by Gwangrae Kim on 2014-09-02.
 */
public class FavoriteFragment extends Fragment implements ActionBarUpdater, Locatable {
    private boolean updateActionBarOnCreateView = false;
    private MainActivity mActivity;
    private static final String TAG = "FavoriteFragment";
    public String tag() {
        return TAG;
    }

    private int attachedPage = -1;
    public int getAttachedPage() {
        return attachedPage;
    }
    public void setAttachedPage(int page) {
        this.attachedPage = page;
    }

    public static class FavoritesAdapter extends ArrayAdapter<Restaurant> {
        private static class FavoriteViewHolder {
            public TextView restaurantName;
            public ImageView flyer, category, coupon, newRestaurant;
        }

        private final MainActivity mActivity;
        private final ArrayList<Restaurant> values;
        private final LayoutInflater mInflater;

        private int TAB_TO_ATTACH_MENU_FRAGMENT = MainActivity.TAB_FAVORITE;

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
            if(restaurant.hasFlyer()) {
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
        ArrayList<Restaurant> restaurants = dbHelper.getFavoriteRestaurant();
        dbHelper.closeDB();

        if (updateActionBarOnCreateView)
            updateActionBar();

        View resultView = inflater.inflate(R.layout.fragment_favorite, container, false);
        ListView favoritesListView = (ListView) resultView.findViewById(R.id.list_favorite_view);
        View noFavoritesView = resultView.findViewById(R.id.image_view_star_null);

        if (restaurants.size() == 0) {
            favoritesListView.setVisibility(View.INVISIBLE);
            noFavoritesView.setVisibility(View.VISIBLE);
        }
        else {
            favoritesListView.setVisibility(View.VISIBLE); // null
            noFavoritesView.setVisibility(View.INVISIBLE);
            FavoritesAdapter adapter = new FavoritesAdapter(mActivity, restaurants, MainActivity.TAB_FAVORITE);
            favoritesListView.setAdapter(adapter);
        }
        return resultView;
    }

    public void invalidateContent() {
        View resultView = getView();
        View noFavoritesView = resultView.findViewById(R.id.image_view_star_null);
        ListView favoritesListView = (ListView) resultView.findViewById(R.id.list_favorite_view);

        DatabaseHelper dbHelper = new DatabaseHelper(mActivity);
        ArrayList<Restaurant> restaurants = dbHelper.getFavoriteRestaurant();
        dbHelper.closeDB();

        if (restaurants.size() == 0) {
            favoritesListView.setVisibility(View.GONE);
            noFavoritesView.setVisibility(View.VISIBLE);
        }
        else {
            favoritesListView.setVisibility(View.VISIBLE);
            noFavoritesView.setVisibility(View.GONE);
            FavoritesAdapter adapter = new FavoritesAdapter(mActivity, restaurants, MainActivity.TAB_FAVORITE);
            favoritesListView.setAdapter(adapter);
        }
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