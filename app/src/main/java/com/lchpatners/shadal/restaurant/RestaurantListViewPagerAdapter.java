package com.lchpatners.shadal.restaurant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lchpatners.shadal.dao.Category;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RestaurantListViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int CHICKEN = 0;
    private static final int PIZZA = 1;
    private static final int CHINESE = 2;
    private static final int KOREAN = 3;
    private static final int DOSIRAK = 4;
    private static final int BOSSAM = 5;
    private static final int NAENGMYEON = 6;
    private static final int ETC = 7;

    public RestaurantListViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return RestaurantListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        // this view pager adapter only handle 8 fragment (regard to category numbers)
        //TODO : make this function to flexible. (now, regard to length var in Category )
        return new Category().getLength();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String pageTitle;

        switch (position) {
            case CHICKEN:
                pageTitle = "치킨";
                break;
            case PIZZA:
                pageTitle = "피자";
                break;
            case CHINESE:
                pageTitle = "중국집";
                break;
            case KOREAN:
                pageTitle = "한식/분식";
                break;
            case DOSIRAK:
                pageTitle = "도시락/돈까스";
                break;
            case BOSSAM:
                pageTitle = "족발/보쌈";
                break;
            case NAENGMYEON:
                pageTitle = "냉면";
                break;
            case ETC:
            default:
                pageTitle = "기타";
                break;
        }
        return pageTitle;
    }
}
