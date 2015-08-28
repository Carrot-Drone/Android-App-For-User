package com.lchpatners.shadal.recommend;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.lchpatners.shadal.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */

public class RecommendedRestaurantPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = LogUtils.makeTag(RecommendedRestaurantPagerAdapter.class);
    Context mContext;
    ArrayList<RecommendedRestaurant> mRecommendedRestaurants;
    private int COUNT;

    public RecommendedRestaurantPagerAdapter(FragmentManager fm, Context context, ArrayList<RecommendedRestaurant> recommendedRestaurants) {
        super(fm);
        mContext = context;
        mRecommendedRestaurants = recommendedRestaurants;
        COUNT = mRecommendedRestaurants.size();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "original position: " + position);
//        position = position % COUNT;
        Log.d(TAG, position + "");
        Log.d(TAG, mRecommendedRestaurants.get(position).getRestaurant().getName());

        return RecommendedRestaurantFragment.create(mRecommendedRestaurants.get(position));
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}

