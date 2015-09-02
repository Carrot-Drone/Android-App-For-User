package com.lchpatners.shadal.recommend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lchpatners.shadal.NavigationDrawerController;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.dao.Campus;
import com.lchpatners.shadal.setting.SeeMoreActivity;
import com.lchpatners.shadal.util.AnalyticsHelper;


public class RecommendedRestaurantActivity extends ActionBarActivity {
    static final int REQUEST_CODE = 0;
    static NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    TextView drawerTitle;
    Campus mCampus;
    ImageView icon_drawer_1;
    ImageView icon_drawer_2;
    ImageView icon_drawer_3;
    TextView tv_drawer_1;
    TextView tv_drawer_2;
    TextView tv_drawer_3;
    private ActionBarDrawerToggle mDrawerToggle;
    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.drawer_ic_1 || view.getId() == R.id.drawer_1) {
                setDrawerStyleItem1Clicked();
                setDrawerStyleItem2NotClicked();

                Intent intent = new Intent(RecommendedRestaurantActivity.this, RootActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else if (view.getId() == R.id.drawer_ic_2 || view.getId() == R.id.drawer_2) {
                mDrawerLayout.closeDrawers();

            } else if (view.getId() == R.id.drawer_ic_3 || view.getId() == R.id.drawer_3) {

                setDrawerStyleItem3Clicked();
                setDrawerStyleItem2NotClicked();

                Intent intent = new Intent(RecommendedRestaurantActivity.this, SeeMoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    };

    public static void updateNavigationView(Activity activity) {
        NavigationDrawerController.updateNavigationDrawer(activity, navigationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsHelper analyticsHelper = new AnalyticsHelper(RecommendedRestaurantActivity.this);
        analyticsHelper.sendScreen("추천 화면");
        setNavigationDrawer();
        updateNavigationView(RecommendedRestaurantActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setDrawerStyleItem2NotClicked();
                setDrawerStyleItem2Clicked();
                setDrawerStyleItem3NotClicked();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.recommend));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        RecommendedRestaurantController mRecommendedRestaurantController = new RecommendedRestaurantController(RecommendedRestaurantActivity.this, getSupportFragmentManager());
        mRecommendedRestaurantController.getRecommendedRestaurantListFromServer();

        mCampus = mRecommendedRestaurantController.getCampus();
        setNavigationDrawer();

    }

    private void setNavigationDrawer() {
        drawerTitle = (TextView) findViewById(R.id.drawer_title);
        drawerTitle.setText(mCampus.getName());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.recommended_restaurant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open, R.string.close) {
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

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

        setDrawerStyleItem2Clicked();

        TextView lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        TextView categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        TextView myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수
        TextView administrator = (TextView) navigationView.findViewById(R.id.administrator);

        //TODO: get recent call information
        administrator.setText(mCampus.getName() + "\n주변음식점 정보의 수정 및 관리는\n" +
                mCampus.getAdministrator() + "에서 전담합니다.");
        updateNavigationView(RecommendedRestaurantActivity.this);
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
            setResult(RESULT_OK);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
