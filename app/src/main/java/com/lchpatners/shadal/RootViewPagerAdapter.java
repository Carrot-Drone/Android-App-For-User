package com.lchpatners.shadal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lchpatners.shadal.call.RecentCallFragment;
import com.lchpatners.shadal.restaurant.category.CategoryListFragment;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RootViewPagerAdapter extends FragmentStatePagerAdapter {
    public static final int RECENT_CALL = 0;
    public static final int CATEGORY = 1;

    public RootViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment mFragment = null;
        switch (position) {
            case RECENT_CALL:
                mFragment = RecentCallFragment.newInstance();
                break;
            case CATEGORY:
                mFragment = CategoryListFragment.newInstance();
                break;
        }
        return mFragment;
    }

    @Override
    public int getCount() {
        // this view pager adapter only handle two fragment (recent call, category)
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String pageTitle = "";
        switch (position) {
            case RECENT_CALL:
                pageTitle = "최근주문";
                break;
            case CATEGORY:
                pageTitle = "음식점";
                break;
        }
        return pageTitle;
    }
}
