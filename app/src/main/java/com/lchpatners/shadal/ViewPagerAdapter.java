package com.lchpatners.shadal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by eunhyekim on 2015. 7. 28..
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Indicates the main {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.CategoryListFragment CategoryListFragment
     */
    public static final int CATEGORY = 1;
    /**
     * Indicates the call-list {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.CallListFragment CallListFragment
     */
    public static final int RECENT = 0;
    /**
     * Indicates the see-more {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.SeeMoreFragment SeeMoreFragment
     */
    public static final int SEE_MORE = 2;
    /**
     * Indicates the random {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.RandomFragment RandomFragment
     */
    // public static final int RANDOM = 2;
    /**
     * The number of {@link android.support.v4.app.Fragment Fragments}.
     */
    public static final String RESTAURANTACTIVITY = "restaurant";
    public static final String MAINACTIVITY = "main";
    public static final int MAX_PAGE = 2;
    //public static final String[] categories = {"치킨", "피자", "중국집", "한식/분식", "도시락/돈까스", "족발/보쌈", "냉면", "기타"};
    Context context;
    String type;
    Category[] categories;

    public ViewPagerAdapter(FragmentManager fm, Context context, String type) {
        super(fm);
        this.type = type;
        this.context = context;
        if (this.type.equals(RESTAURANTACTIVITY)) {
            this.categories = Category.getCategory(context);
        }
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (type.equals(MAINACTIVITY)) {
            switch (position) {
                case CATEGORY:
                    fragment = CategoryListFragment.newInstance();
                    break;
                case RECENT:
                    fragment = CallListFragment.newInstance();
                    break;
            }
        } else if (type.equals(RESTAURANTACTIVITY)) {
            fragment = RestaurantListFragment.newInstance(position);

            Log.d("ViewPagerAdapter", "getItem getTitle: " + categories[position].getTitle());
        }
        return fragment;
    }

    @Override
    public int getCount() {
        int count = 1;
        if (type.equals(MAINACTIVITY)) {
            count = MAX_PAGE;
        } else if (type.equals(RESTAURANTACTIVITY)) {
            count = categories.length;
        }
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (type.equals(MAINACTIVITY)) {
            switch (position) {
                case CATEGORY:
                    title = "음식점";
                    break;
                case RECENT:
                    title = "최근주문";
                    break;
            }
        } else if (type.equals(RESTAURANTACTIVITY)) {
            Category category = categories[position];
            title = category.getTitle();
            Log.d("getPAgeTitle", category.getTitle());
        }

        return title;
    }

    public Drawable getPageIcon(int page, boolean selected) {
        Drawable icon = null;
        switch (page) {
            case CATEGORY:
                icon = context.getResources().getDrawable(
                        selected ? R.drawable.tab_main_selected : R.drawable.tab_main_unselected
                );
                break;
            case RECENT:
                icon = context.getResources().getDrawable(
                        selected ? R.drawable.tab_star_selected : R.drawable.tab_star_unselected
                );
                break;

        }
        return icon;
    }


}


