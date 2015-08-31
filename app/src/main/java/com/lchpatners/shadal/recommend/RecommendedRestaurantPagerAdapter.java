package com.lchpatners.shadal.recommend;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.lchpatners.shadal.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */

public class RecommendedRestaurantPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = LogUtils.makeTag(RecommendedRestaurantPagerAdapter.class);
    Context mContext;
    List<RecommendedRestaurant> mRecommendedRestaurants;
    private int count;

    public RecommendedRestaurantPagerAdapter(FragmentManager fm, Context context, List<RecommendedRestaurant> recommendedRestaurants) {
        super(fm);
        this.count = recommendedRestaurants.size() - 1;
        mContext = context;
        mRecommendedRestaurants = recommendedRestaurants;
    }

    @Override
    public Fragment getItem(int position) {
        return RecommendedRestaurantFragment.newInstance(mRecommendedRestaurants.get(position), count, position);
    }

    @Override
    public int getCount() {
        return mRecommendedRestaurants.size();
    }

}

