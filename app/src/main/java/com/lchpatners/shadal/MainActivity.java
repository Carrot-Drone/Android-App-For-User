package com.lchpatners.shadal;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.restaurant.RestaurantListFragment;
import com.lchpatners.shadal.setting.SeeMoreActivity;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The main {@link android.app.Activity Activity}.
 * Displays the {@link android.support.v4.app.Fragment Fragments}.
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(MainActivity.class);
    public static boolean ready = true;
    /**
     * Indicates the last instance of {@link RestaurantListFragment
     * RestaurantListFragment}.
     */
    public RestaurantListFragment restaurantListFragmentCurrentlyOn;
    /**
     * The main {@link android.support.v4.view.ViewPager ViewPager}.
     */
    protected ViewPager viewPager;
    private Menu menu;
    private String title;
    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private ViewPagerAdapter adapter;
    private DrawerLayout drawerLayout;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.drawer_ic_1 || view.getId() == R.id.drawer_1) {
                drawerLayout.closeDrawers();
            } else if (view.getId() == R.id.drawer_ic_2 || view.getId() == R.id.drawer_2) {
                Intent intent = new Intent(MainActivity.this, RecommendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (view.getId() == R.id.drawer_ic_3 || view.getId() == R.id.drawer_3) {
                Intent intent = new Intent(MainActivity.this, SeeMoreActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
    private ActionBarDrawerToggle drawerToggle;
    private TextView drawerTitle;
    private SearchView searchView;

    public static void showToast(Context context) {
        Toast.makeText(context, "완료되었습니다!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawerTitle = (TextView) findViewById(R.id.drawer_title);

        new Server(this).checkAppMinimumVersion();


        // If no campus is selected, have the user select one.
        if (Preferences.getCampusId(this) == null) {
            ready = false;
            startActivity(new Intent(this, LoginCampusSelectActivity.class));
            finish();
        } else {
            drawerTitle.setText(Preferences.getCampusKoreanName(this));
            // new Server(this).getPopupList();
        }


        // If no database, get data from the server and update.
        if (!DatabaseHelper.getInstance(this).checkDatabase(Preferences.getCampusEnglishName(this))) {
            ready = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Server(this).updateAll();
            }
        }


        updateCampusMetaData();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.findViewById(R.id.drawer_ic_1).setOnClickListener(clickListener);
        navigationView.findViewById(R.id.drawer_ic_2).setOnClickListener(clickListener);
        navigationView.findViewById(R.id.drawer_ic_3).setOnClickListener(clickListener);
        navigationView.findViewById(R.id.drawer_1).setOnClickListener(clickListener); //주문하기
        navigationView.findViewById(R.id.drawer_2).setOnClickListener(clickListener); //추천
        navigationView.findViewById(R.id.drawer_3).setOnClickListener(clickListener); //더보기

        TextView lastDay = (TextView) navigationView.findViewById(R.id.last_day); //마지막 주문한날로부터
        TextView categoryName = (TextView) navigationView.findViewById(R.id.category); //가장 많이 주문한 음식
        TextView myCalls = (TextView) navigationView.findViewById(R.id.my_calls); //내 주문수
        TextView administrator = (TextView) navigationView.findViewById(R.id.administrator);

        DatabaseHelper helper = DatabaseHelper.getInstance(MainActivity.this);
        if (helper.getLastDay() < 0) {
            lastDay.setText("");
        } else {
            lastDay.setText(helper.getLastDay() + "일");
        }
        myCalls.setText(helper.getTotalNumberOfMyCalls() + "회");
        categoryName.setText(helper.getTheMostOrderedFood());
        administrator.setText(Preferences.getCampusKoreanName(MainActivity.this) +
                "\n주변음식점 정보의 수정 및 관리는 \n" +
                Preferences.getCampusAdministrator(MainActivity.this) + "에서 전담합니다.");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        title = getString(R.string.drawer_order);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.icon_action_bar_menu);

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this, ViewPagerAdapter.MAIN_ACTIVITY);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setBackgroundColor(getResources().getColor(R.color.primary));
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white100);
            }
        });
        tabs.setViewPager(viewPager);

        if (getIntent().getStringExtra("setcurrentpage") != null) {
            viewPager.setCurrentItem(1);
            Log.d("setcurrentpage", "getintentnotnull");
        }

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                toolbar.setTitle(title);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        switch (item.getItemId()) {
            case R.id.action_search:
                menu.findItem(R.id.action_restaurant_suggestion).setVisible(false);
                return true;
            case R.id.action_restaurant_suggestion:
                Intent intent = new Intent(this, RestaurantSuggestionActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        } else {
            super.onBackPressed();
        }
    }

    /**
     * Update campus meta data of currently selected campus.
     *
     * @see com.lchpatners.shadal.Server.CampusesLoadingTask CampusesLoadingTask
     */
    public void updateCampusMetaData() {
        new Server.CampusesLoadingTask() {
            @Override
            public void onPostExecute(Void aVoid) {
                if (results == null) return;
                for (int i = 0; i < results.length(); i++) {
                    try {
                        JSONObject result = results.getJSONObject(i);
                        if (result.getString("id").equals(
                                Preferences.getCampusEnglishName(MainActivity.this))) {
                            Preferences.setCampus(MainActivity.this, result);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            protected Void doInBackground(Void... params) {

                try {
                    serviceCall = Server.makeServiceCall(
                            Server.BASE_URL + Server.CAMPUSES, Server.GET, null);
                    if (serviceCall == null) {
                        return null;
                    }
                    results = new JSONArray(serviceCall);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_restaurant_suggestion).setVisible(true);
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("원하시는 음식점, 메뉴를 입력해주세요");

        TextView searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setTextSize(15);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int count = 0;
                ArrayList<Object> list = new ArrayList<>();
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(MainActivity.this);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select count(*) from restaurants where name like '%" + query + "%'", null);
                cursor.moveToFirst();
                count = cursor.getInt(0);
                if (count <= 0) {
                    Toast.makeText(MainActivity.this, "검색된 데이터가 없어요 ", Toast.LENGTH_LONG).show();
                    return false;
                }

                cursor.close();
                Cursor result = db.rawQuery("select * from restaurants where name like '%" + query + "%'", null);
                if (result != null && result.moveToFirst()) {
                    do {
                        list.add(new Restaurant(result));
                        Log.d("name", result.getString(1));
                        Toast.makeText(MainActivity.this, result.getString(1), Toast.LENGTH_SHORT).show();
                    } while (result.moveToNext());
                }
                result.close();
                searchView.setQuery("", false);
                searchView.setIconified(true);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.list_view, null, false);
                ListView listView = (ListView) view.findViewById(R.id.list_view);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}