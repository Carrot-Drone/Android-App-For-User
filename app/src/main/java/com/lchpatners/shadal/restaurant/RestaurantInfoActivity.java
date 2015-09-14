package com.lchpatners.shadal.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.campus.CampusController;
import com.lchpatners.shadal.dao.Campus;
import com.lchpatners.shadal.dao.Restaurant;
import com.lchpatners.shadal.restaurant.menu.MenuListAdapter;
import com.lchpatners.shadal.restaurant_correction.RestaurantCorrectionActivity;
import com.lchpatners.shadal.util.AnalyticsHelper;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.RetrofitConverter;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class RestaurantInfoActivity extends ActionBarActivity {
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String KAKAO_RESTAURANT_ID = "kakao_restaurant_id";
    public static final String KAKAO_CAMPUS = "kakao_restaurant_in_campus";
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    private static final String TAG = LogUtils.makeTag(RestaurantInfoActivity.class);
    private Intent mIntent;
    private RestaurantInfoController mRestaurantInfoController;
    private Restaurant mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Campus campus = CampusController.getCurrentCampus(RestaurantInfoActivity.this);
        int restaurant_id;
        String currentCampusShortName = "";
        if (campus != null) {
            currentCampusShortName = campus.getName_kor_short();
        }
        String kakaoCampusName;

        mIntent = getIntent();
        kakaoCampusName = mIntent.getStringExtra(KAKAO_CAMPUS);
        mRestaurantInfoController = new RestaurantInfoController(RestaurantInfoActivity.this);

        if (kakaoCampusName == null) {
            restaurant_id = mIntent.getIntExtra(RESTAURANT_ID, 0);
            mRestaurant = mRestaurantInfoController.getRestaurant(restaurant_id);
            setView();
        } else {
            restaurant_id = mIntent.getIntExtra(KAKAO_RESTAURANT_ID, 0);
            if (kakaoCampusName.equals(currentCampusShortName)) {
                mRestaurant = mRestaurantInfoController.getRestaurant(restaurant_id);
                setView();
            } else {
                getKAKAORestaurant(restaurant_id, kakaoCampusName);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsHelper analyticsHelper = new AnalyticsHelper(RestaurantInfoActivity.this);
        analyticsHelper.sendScreen("음식점 화면");
    }

    private void getKAKAORestaurant(int restaurant_id, final String kakaoCampusName) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createRestaurantConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.getRestaurant(restaurant_id, new Callback<Restaurant>() {
            @Override
            public void success(Restaurant restaurant, Response response) {
                mRestaurant = restaurant;
                mRestaurantInfoController.setKAKAORestaurant(restaurant);
                setKAKAOView(kakaoCampusName);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.pen) {
            Intent intent = new Intent(this, RestaurantCorrectionActivity.class);
            intent.putExtra(RESTAURANT_ID, mRestaurant.getId());
            startActivity(intent);

            AnalyticsHelper analyticsHelper = new AnalyticsHelper(RestaurantInfoActivity.this);
            analyticsHelper.sendEvent("UX", "restaurant_correction", CampusController.getCurrentCampus(RestaurantInfoActivity.this).getName_kor_short());

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setKAKAOView(final String kakaoCampusName) {
        setRestaurantStaticInfo();
        mRestaurantInfoController.setFlyerButtonListener();
        mRestaurantInfoController.setCallButtonListener();
        mRestaurantInfoController.setFooter();
        mRestaurantInfoController.setHeader();

        final TextView click_share = (TextView) findViewById(R.id.click_share);
        final TextView click_evaluation = (TextView) findViewById(R.id.click_evaluation);

        final ImageView ivEvaluation = (ImageView) findViewById(R.id.iv_evaluation);
        final TextView tvEvaluation = (TextView) findViewById(R.id.tv_evaluation);

        final LinearLayout btn_divider = (LinearLayout) findViewById(R.id.btn_divider);

        final ImageButton btnHate = (ImageButton) findViewById(R.id.btn_hate);
        final ImageButton btnLike = (ImageButton) findViewById(R.id.btn_like);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.click_share) {
                    try {
                        final KakaoLink kakaoLink = KakaoLink.getKakaoLink(RestaurantInfoActivity.this);
                        final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder
                                = kakaoLink.createKakaoTalkLinkMessageBuilder();
                        String campus_name = kakaoCampusName;
                        String text = mRestaurant.getName() + "(" + campus_name + ")" + "\n" + mRestaurant.getPhone_number();

                        final String linkContent =
                                kakaoTalkLinkMessageBuilder
                                        .addText(text)
                                        .addAppButton("캠퍼스:달 바로가기",
                                                new AppActionBuilder()
                                                        .addActionInfo(AppActionInfoBuilder
                                                                .createAndroidActionInfoBuilder()
                                                                .setExecuteParam("restaurant_id=" + mRestaurant.getId() + "&" + "campusName=" + campus_name)
                                                                .setMarketParam("referrer=kakaotalklink")
                                                                .build())
                                                        .addActionInfo(AppActionInfoBuilder
                                                                .createiOSActionInfoBuilder()
                                                                .setExecuteParam("restaurant_id=" + mRestaurant.getId() + "&" + "campusName=" + campus_name)
                                                                .build())
                                                        .build())
                                        .build();
                        kakaoLink.sendMessage(linkContent, (RestaurantInfoActivity.this));
                    } catch (KakaoParameterException e) {
                        e.printStackTrace();
                    } finally {
                        AnalyticsHelper analyticsHelper = new AnalyticsHelper(RestaurantInfoActivity.this);
                        analyticsHelper.sendEvent("UX", "share_kakao_clicked", mRestaurant.getName());
                    }

                } else if (view.getId() == R.id.click_evaluation) {
                    click_evaluation.setVisibility(View.GONE);
                    tvEvaluation.setVisibility(View.GONE);
                    ivEvaluation.setVisibility(View.GONE);
                    btn_divider.setVisibility(View.GONE);

                    btnHate.setVisibility(View.VISIBLE);
                    btnLike.setVisibility(View.VISIBLE);
                }
            }
        };

        click_evaluation.setOnClickListener(listener);
        click_share.setOnClickListener(listener);

        ListView listView = (ListView) findViewById(R.id.menu_list);
        MenuListAdapter adapter = new MenuListAdapter(this, mRestaurant);
        listView.setAdapter(adapter);
    }

    public void setView() {
        setRestaurantStaticInfo();
        mRestaurantInfoController.setFlyerButtonListener();
        mRestaurantInfoController.setCallButtonListener();
        mRestaurantInfoController.setFooter();
        mRestaurantInfoController.setHeader();

        ListView listView = (ListView) findViewById(R.id.menu_list);
        MenuListAdapter adapter = new MenuListAdapter(this, mRestaurant);
        listView.setAdapter(adapter);
    }

    private void setRestaurantStaticInfo() {
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(mRestaurant.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView officeHours = (TextView) findViewById(R.id.office_hours);
        officeHours.setText(hourFormatString());

        TextView minimum = (TextView) findViewById(R.id.minimum);
        TextView labelMinimum = (TextView) findViewById(R.id.label_minimum);
        if (mRestaurant.getMinimum_price() != 0) {
            minimum.setText(mRestaurant.getMinimum_price() + "원");
        } else {
            minimum.setVisibility(View.GONE);
            labelMinimum.setVisibility(View.GONE);
        }
    }

    private String hourFormatString() {
        float open_hours = mRestaurant.getOpening_hours();
        float close_hours = mRestaurant.getClosing_hours();

        String open;
        String close;

        int open_hour = (int) open_hours;
        if (open_hour < 10 && open_hour != 0) {
            open = "0" + open_hour;
        } else {
            open = open_hour + "";
        }
        if (open_hours > open_hour) {
            open = open + ":30";
        } else {
            open = open + ":00";
        }

        int close_hour = (int) close_hours;
        if (close_hour < 10 && close_hour != 0) {
            close = "0" + close_hour;
        } else {
            close = close_hour + "";
        }
        if (close_hours > close_hour) {
            close = close + ":30";
        } else {
            close = close + ":00";
        }

        if (open_hours == 0 && close_hours == 0) {
            return "정보없음";
        } else {
            return open + " ~ " + close;
        }
    }
}
