package com.lchpatners.shadal;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.lchpatners.shadal.restaurant.category.CategoryListFragment;
import com.lchpatners.shadal.unused.CategoryItem;

/**
 * Created by eunhyekim on 2015. 7. 28..
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Indicates the main {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.CategoryListFragment CategoryListFragment
     */

    public static final int TREND = 0;
    public static final int NEW = 1;

    public static final int CATEGORY = 1;
    /**
     * Indicates the call-list {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.CallListFragment CallListFragment
     */
    public static final int RECENT = 0;

    /**
     * The number of {@link android.support.v4.app.Fragment Fragments}.
     */
    public static final String RESTAURANT_ACTIVITY = "restaurant";
    public static final String MAIN_ACTIVITY = "main";
    public static final String RECOMMEND_ACTIVITY = "recommend";
    public static final int MAX_PAGE = 2;
    //public static final String[] categories = {"치킨", "피자", "중국집", "한식/분식", "도시락/돈까스", "족발/보쌈", "냉면", "기타"};
    Context context;
    String type;
    CategoryItem[] categories;

    public ViewPagerAdapter(FragmentManager fm, Context context, String type) {
        super(fm);
        this.type = type;
        this.context = context;
        if (this.type.equals(RESTAURANT_ACTIVITY)) {
            this.categories = CategoryItem.getCategory(context);
        }
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (type.equals(MAIN_ACTIVITY)) {
            switch (position) {
                case CATEGORY:
                    fragment = CategoryListFragment.newInstance();
                    break;
                case RECENT:
                    fragment = CallListFragment.newInstance();
                    break;
            }
        } else if (type.equals(RESTAURANT_ACTIVITY)) {
            fragment = RestaurantListFragment.newInstance(position);

            Log.d("ViewPagerAdapter", "getItem getTitle: " + categories[position].getTitle());
        } else if (type.equals(RECOMMEND_ACTIVITY)) {
            switch (position) {
                case TREND:
                    fragment = TrendFragment.newInstance();
                    break;
                case NEW:
                    fragment = NewFragment.newInstance();
                    break;
            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        int count = 1;
        if (type.equals(MAIN_ACTIVITY)) {
            count = MAX_PAGE;
        } else if (type.equals(RESTAURANT_ACTIVITY)) {
            count = categories.length;
        } else if (type.equals(RECOMMEND_ACTIVITY)) {
            count = 2;
        }
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (type.equals(MAIN_ACTIVITY)) {
            switch (position) {
                case CATEGORY:
                    title = "음식점";
                    break;
                case RECENT:
                    title = "최근주문";
                    break;
            }
        } else if (type.equals(RESTAURANT_ACTIVITY)) {
            CategoryItem categoryItems = categories[position];
            title = categoryItems.getTitle();
            Log.d("getPageTitle", categoryItems.getTitle());
        } else if (type.equals(RECOMMEND_ACTIVITY)) {
            switch (position) {
                case TREND:
                    title = "우리학교 트렌드";
                    break;
                case NEW:
                    title = "캠달에 처음왔어요";
                    break;
            }
        }

        return title;
    }



}


