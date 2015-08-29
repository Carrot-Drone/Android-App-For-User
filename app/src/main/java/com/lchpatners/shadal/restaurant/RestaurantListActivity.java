package com.lchpatners.shadal.restaurant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RestaurantListViewPagerAdapter;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.SlidingTabLayout;


public class RestaurantListActivity extends ActionBarActivity {
    //TODO : delete this disgusting variables
    private static final int CHICKEN = 0;
    private static final int PIZZA = 1;
    private static final int CHINESE = 2;
    private static final int KOREAN = 3;
    private static final int DOSIRAK = 4;
    private static final int BOSSAM = 5;
    private static final int NAENGMYEON = 6;
    private static final int ETC = 7;
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private RestaurantListViewPagerAdapter mRestaurantListViewPagerAdapter;
    private ViewPager mViewPager;
    private CharSequence category;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        position = intent.getIntExtra("position", 1);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(category);

        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mRestaurantListViewPagerAdapter = new RestaurantListViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mRestaurantListViewPagerAdapter);
        mViewPager.setCurrentItem(position);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSlidingTabLayout();
    }

    private void setSlidingTabLayout() {
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white100);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
                mToolbar.setTitle(pageTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                onBackPressed();
            } else {
                super.onBackPressed();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RestaurantListActivity.this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //TODO: set back button destination
        //intent.putExtra("setcurrentpage", "s");
        startActivity(intent);
    }
}
