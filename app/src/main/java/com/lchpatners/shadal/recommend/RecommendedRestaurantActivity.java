package com.lchpatners.shadal.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.campus.Campus;
import com.lchpatners.shadal.setting.SeeMoreActivity;


public class RecommendedRestaurantActivity extends ActionBarActivity {
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    TextView drawerTitle;
    Campus mCampus;

    private ActionBarDrawerToggle mDrawerToggle;
    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.drawer_ic_1 || view.getId() == R.id.drawer_1) {
                Intent intent = new Intent(RecommendedRestaurantActivity.this, RootActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (view.getId() == R.id.drawer_ic_2 || view.getId() == R.id.drawer_2) {
                mDrawerLayout.closeDrawers();
            } else if (view.getId() == R.id.drawer_ic_3 || view.getId() == R.id.drawer_3) {
                Intent intent = new Intent(RecommendedRestaurantActivity.this, SeeMoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.findViewById(R.id.drawer_ic_1).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_ic_2).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_ic_3).setOnClickListener(navigationClickListener);
        navigationView.findViewById(R.id.drawer_1).setOnClickListener(navigationClickListener); //주문하기
        navigationView.findViewById(R.id.drawer_2).setOnClickListener(navigationClickListener); //추천
        navigationView.findViewById(R.id.drawer_3).setOnClickListener(navigationClickListener); //더보기

        TextView lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        TextView categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        TextView myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수
        TextView administrator = (TextView) navigationView.findViewById(R.id.administrator);

        //TODO: get recent call information
        administrator.setText(mCampus.getName() + "\n주변음식점 정보의 수정 및 관리는\n" +
                mCampus.getAdministrator() + "에서 전담합니다.");


    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();

        } else {
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
