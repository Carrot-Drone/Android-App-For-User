package com.lchpatners.shadal;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lchpatners.shadal.recommend.RecommendedRestaurantActivity;
import com.lchpatners.shadal.setting.SeeMoreActivity;
import com.lchpatners.shadal.util.LogUtils;

/**
 * Created by youngkim on 2015. 8. 24..
 */
public class RootActivity extends ActionBarActivity {
    static final int REQUEST_CODE = 1;
    private static final String TAG = LogUtils.makeTag(RootActivity.class);
    ImageView icon_drawer_1;
    ImageView icon_drawer_2;
    ImageView icon_drawer_3;
    TextView tv_drawer_1;
    TextView tv_drawer_2;
    TextView tv_drawer_3;
    private RootController mController;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private RootViewPagerAdapter mViewPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private TextView drawerTitle;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Menu mMenu;
    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.drawer_ic_1 || view.getId() == R.id.drawer_1) {
                mDrawerLayout.closeDrawers();
            } else if (view.getId() == R.id.drawer_ic_2 || view.getId() == R.id.drawer_2) {

                setDrawerStyleItem2Clicked();
                setDrawerStyleItem1NotClicked();

                Intent intent = new Intent(RootActivity.this, RecommendedRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CODE);
            } else if (view.getId() == R.id.drawer_ic_3 || view.getId() == R.id.drawer_3) {

                setDrawerStyleItem3Clicked();
                setDrawerStyleItem1NotClicked();

                Intent intent = new Intent(RootActivity.this, SeeMoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setDrawerStyleItem1Clicked();
                setDrawerStyleItem3NotClicked();
                setDrawerStyleItem2NotClicked();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        //TODO:checkAppMinimunVersion

        mController = new RootController(RootActivity.this);

        //TODO:check previous Database

        //TODO: set toast only one time

        //TODO : get popup list

        setNavigationDrawer();

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("주문하기");
        mToolbar.setNavigationIcon(R.drawable.icon_action_bar_menu);

        setSlidingTabBar();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mToolbar.setTitle("주문하기");
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void setNavigationDrawer() {
        drawerTitle = (TextView) findViewById(R.id.drawer_title);
        drawerTitle.setText(mController.getCampus().getName());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        icon_drawer_1 = (ImageView) navigationView.findViewById(R.id.drawer_ic_1);
        icon_drawer_2 = (ImageView) navigationView.findViewById(R.id.drawer_ic_2);
        icon_drawer_3 = (ImageView) navigationView.findViewById(R.id.drawer_ic_3);
        tv_drawer_1 = (TextView) navigationView.findViewById(R.id.drawer_1);
        tv_drawer_2 = (TextView) navigationView.findViewById(R.id.drawer_2);
        tv_drawer_3 = (TextView) navigationView.findViewById(R.id.drawer_3);

        icon_drawer_1.setOnClickListener(navigationClickListener);
        icon_drawer_2.setOnClickListener(navigationClickListener);
        icon_drawer_3.setOnClickListener(navigationClickListener);
        tv_drawer_1.setOnClickListener(navigationClickListener); //주문하기
        tv_drawer_2.setOnClickListener(navigationClickListener); //추천
        tv_drawer_3.setOnClickListener(navigationClickListener); //더보기

        setDrawerStyleItem1Clicked();
        setDrawerStyleItem2NotClicked();
        setDrawerStyleItem3NotClicked();

        TextView lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        TextView categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        TextView myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수
        TextView administrator = (TextView) navigationView.findViewById(R.id.administrator);

        //TODO: get recent call information
        administrator.setText(mController.getCampus().getName() + "\n주변음식점 정보의 수정 및 관리는\n" +
                mController.getCampus().getAdministrator() + "에서 전담합니다.");
        myCalls.setText(NavigationDrawerController.getTotalCallCount(RootActivity.this) + "회");
        lastDay.setText(NavigationDrawerController.getLastDay(RootActivity.this));
        categoryName.setText(NavigationDrawerController.getTheMostOrderedFood(RootActivity.this));
    }

    private void setSlidingTabBar() {
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPagerAdapter = new RootViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

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
    }

    private void setDrawerStyleItem1NotClicked() {
        tv_drawer_1.setTypeface(null, Typeface.NORMAL);
        tv_drawer_1.setTextColor(getResources().getColor(R.color.light_grey));
        icon_drawer_1.setImageResource(R.drawable.icon_drawer_list_menu_call_normal);

    }

    private void setDrawerStyleItem2NotClicked() {
        tv_drawer_2.setTypeface(null, Typeface.NORMAL);
        tv_drawer_2.setTextColor(getResources().getColor(R.color.light_grey));
        icon_drawer_2.setImageResource(R.drawable.icon_drawer_list_menu_recommand_normal);
    }

    private void setDrawerStyleItem3NotClicked() {
        tv_drawer_3.setTypeface(null, Typeface.NORMAL);
        tv_drawer_3.setTextColor(getResources().getColor(R.color.light_grey));
        icon_drawer_3.setImageResource(R.drawable.icon_drawer_list_menu_more_normal);
    }

    private void setDrawerStyleItem1Clicked() {
        tv_drawer_1.setTypeface(Typeface.DEFAULT_BOLD);
        tv_drawer_1.setTextColor(getResources().getColor(R.color.primary));
        icon_drawer_1.setImageResource(R.drawable.icon_drawer_list_menu_call_selected);

    }

    private void setDrawerStyleItem2Clicked() {
        tv_drawer_2.setTypeface(Typeface.DEFAULT_BOLD);
        tv_drawer_2.setTextColor(getResources().getColor(R.color.primary));
        icon_drawer_2.setImageResource(R.drawable.icon_drawer_list_menu_recommand_selected);
    }

    private void setDrawerStyleItem3Clicked() {
        tv_drawer_3.setTypeface(Typeface.DEFAULT_BOLD);
        tv_drawer_3.setTextColor(getResources().getColor(R.color.primary));
        icon_drawer_3.setImageResource(R.drawable.icon_drawer_list_menu_more_selected);
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();

        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_root, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        switch (item.getItemId()) {
            case R.id.action_search:
                mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(false);
                return true;
//            case R.id.action_restaurant_suggestion:
//                Intent intent = new Intent(this, RestaurantSuggestionActivity.class);
//                startActivity(intent);
//                return true;
            case android.R.id.home:
                mMenu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
