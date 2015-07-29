package com.lchpatners.shadal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by eunhyekim on 2015. 7. 28..
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * Indicates the main {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.CategoryListFragment CategoryListFragment
     */
    public static final int MAIN = 1;
    /**
     * Indicates the bookmark {@link android.support.v4.app.Fragment Fragment}.
     *
     * @see com.lchpatners.shadal.BookmarkFragment BookmarkFragment
     */
    public static final int BOOKMARK = 0;
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
    public static final int MAX_PAGE = 2;
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MAIN:
                fragment = CategoryListFragment.newInstance();
                break;
            case BOOKMARK:
                fragment = CallListFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return MAX_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case MAIN:
                title = "음식점";
                break;
            case BOOKMARK:
                title = "최근주문";
                break;
        }
        return title;
    }

    public Drawable getPageIcon(int page, boolean selected) {
        Drawable icon = null;
        switch (page) {
            case MAIN:
                icon = context.getResources().getDrawable(
                        selected ? R.drawable.tab_main_selected : R.drawable.tab_main_unselected
                );
                break;
            case BOOKMARK:
                icon = context.getResources().getDrawable(
                        selected ? R.drawable.tab_star_selected : R.drawable.tab_star_unselected
                );
                break;

        }
        return icon;
    }


}


