package com.lchpatners.shadal.restaurant.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RestaurantCorrectionActivity;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;
import com.lchpatners.shadal.util.LogUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class MenuListActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeTag(MenuListActivity.class);
    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";

    private Intent mIntent;
    private MenuListController mMenuListController;
    private Restaurant mRestaurant;

    private TextView click_evaluation;
    private TextView tvEvaluation;
    private ImageView ivEvaluation;
    private LinearLayout btn_divider;
    private ImageButton btnLike;
    private ImageButton btnHate;

//    View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (view.getId() == R.id.click_share) {
//                try {
//                    Log.d("button clicked", "");
//                    final KakaoLink kakaoLink = KakaoLink.getKakaoLink(MenuListActivity.this);
//                    final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//                    String text = mRestaurant.getName() + "\n(" + Preferences.getCampusKoreanName(MenuListActivity.this) + ")\n" + mRestaurant.getPhoneNumber();
//                    final String linkContent =
//                            kakaoTalkLinkMessageBuilder
//                                    .addText(text)
//                                    .addAppButton("캠퍼스:달 앱으로 이동").build();
//                    kakaoLink.sendMessage(linkContent, MenuListActivity.this);
//                } catch (KakaoParameterException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (view.getId() == R.id.click_evaluation) {
//
//                click_evaluation.setVisibility(View.GONE);
//                tvEvaluation.setVisibility(View.GONE);
//                ivEvaluation.setVisibility(View.GONE);
//                btn_divider.setVisibility(View.GONE);
//
//                btnHate.setVisibility(View.VISIBLE);
//                btnLike.setVisibility(View.VISIBLE);
//            }
//        }
//    };

    private boolean likeBtnChecked = false;
    private boolean hateBtnChecked = false;

//    View.OnClickListener onButtonClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (view.getId() == R.id.btn_like) {
//                if (!likeBtnChecked) {
//                    likeBtnChecked = !likeBtnChecked;
//                    btnLike.setImageResource(R.drawable.icon_detail_page_btn_check);
//                } else {
//                    likeBtnChecked = !likeBtnChecked;
//                    btnLike.setImageResource(R.drawable.icon_detail_page_btn_like);
//                }
//                btnHate.setImageResource(R.drawable.icon_detail_page_btn_hate);
//
//            } else if (view.getId() == R.id.btn_hate) {
//                if (!hateBtnChecked) {
//                    hateBtnChecked = !hateBtnChecked;
//                    btnHate.setImageResource(R.drawable.icon_detail_page_btn_check_selected);
//                } else {
//                    hateBtnChecked = !hateBtnChecked;
//                    btnHate.setImageResource(R.drawable.icon_detail_page_btn_hate);
//                }
//                btnLike.setImageResource(R.drawable.icon_detail_page_btn_like);
//
//            }
//        }
//    };

    // ALERT: this is android.view.Menu, not com.lchpartners.shadal.Menu
    private Menu menu;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        mIntent = getIntent();
        mMenuListController = new MenuListController(MenuListActivity.this);
        mRestaurant = mMenuListController.getRestaurant(mIntent.getIntExtra(RESTAURANT_ID, 0));

        setView();
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
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setView() {
        //TODO : set referrer

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(mRestaurant.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton flyer = (ImageButton) findViewById(R.id.flyer);
        RelativeLayout divider = (RelativeLayout) findViewById(R.id.divider_layout);
        divider.setVisibility((mRestaurant.isHas_flyer()) ? View.VISIBLE : View.GONE);
        flyer.setVisibility((mRestaurant.isHas_flyer()) ? View.VISIBLE : View.GONE);

        flyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> flyer_urls = new ArrayList<String>();
                RealmList<Flyer> flyers = mRestaurant.getFlyers();

                for (Flyer flyer : flyers) {
                    flyer_urls.add(flyer.getUrl());
                }

                Intent intent = new Intent(MenuListActivity.this, FlyerActivity.class);
                intent.putExtra(FLYER_URLS, flyer_urls);
                intent.putExtra(RESTAURANT_ID, mRestaurant.getId());
                intent.putExtra(RESTAURANT_PHONE_NUMBER, mRestaurant.getPhone_number());
                MenuListActivity.this.startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.menu_list);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.menu_header, null, false);
        View footer = inflater.inflate(R.layout.list_footer, null, false);

        TextView retention = (TextView) header.findViewById(R.id.retention);
        TextView numberOfMyCalls = (TextView) header.findViewById(R.id.number_of_my_calls);
        TextView totalNumberOfCalls = (TextView) header.findViewById(R.id.total_number_of_calls);
        TextView notice = (TextView) header.findViewById(R.id.notice);

        TextView click_share = (TextView) header.findViewById(R.id.click_share);
        click_evaluation = (TextView) header.findViewById(R.id.click_evaluation);
        ivEvaluation = (ImageView) header.findViewById(R.id.iv_evaluation);
        tvEvaluation = (TextView) header.findViewById(R.id.tv_evaluation);
        btn_divider = (LinearLayout) header.findViewById(R.id.btn_divider);

        btnHate = (ImageButton) header.findViewById(R.id.btn_hate);
        btnLike = (ImageButton) header.findViewById(R.id.btn_like);

//        click_share.setOnClickListener(clickListener);
//        click_evaluation.setOnClickListener(clickListener);
//
//        btnHate.setOnClickListener(onButtonClickListener);
//        btnLike.setOnClickListener(onButtonClickListener);

        if (mRestaurant.getNotice() != null && mRestaurant.getNotice().length() > 0) {
            notice.setText(mRestaurant.getNotice());
        } else {
            notice.setVisibility(View.GONE);
        }

        retention.setText(Math.round(mRestaurant.getRetention() * 100) + "");
        //TODO : GET my call count
//        numberOfMyCalls.setText(mRestaurant.getNumberOfCalls(MenuListActivity.this, restaurant.getRestaurantId()) + "");
        totalNumberOfCalls.setText(numberOfCallsFormatString(mRestaurant.getTotal_number_of_calls()));

        //for prevent duplicate header and footer
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(header, null, false);
        }
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footer, null, false);
        }

//        MenuListAdapter adapter = new MenuListAdapter(this, mRestaurant);
//        listView.setAdapter(adapter);

        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        phoneNumber.setText(mRestaurant.getPhone_number());
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                    helper.sendEvent("UX", "phonenumber_clicked", restaurant.getName());

                //TODO: update call db
//                Call.updateCallLog(MenuListActivity.this, restaurant);
                String number = "tel:" + mRestaurant.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(intent);
            }
        });

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

    public String hourFormatString() {
        float open_hours = mRestaurant.getOpening_hours();
        float close_hours = mRestaurant.getClosing_hours();

        String open;
        String close;

        int open_hour = (int) open_hours;
        if (open_hour < 10 && open_hour != 0) {
            open_hour += 24;
        }
        if (open_hours > open_hour) {
            open = open_hour + ":30";
        } else {
            open = open_hour + ":00";
        }

        int close_hour = (int) close_hours;
        if (close_hour < 10 && close_hour != 0) {
            close_hour += 24;
        }
        if (close_hours > close_hour) {
            close = close_hour + ":30";
        } else {
            close = close_hour + ":00";
        }

        if (open_hours == 0 && close_hours == 0) {
            return "";
        } else {
            return open + " ~ " + close;
        }
    }

    public String numberOfCallsFormatString(int numberOfCalls) {
        if ((numberOfCalls >= 10) && (numberOfCalls < 100)) {
            numberOfCalls = (numberOfCalls / 10) * 10;
        } else if ((numberOfCalls >= 100)) {
            numberOfCalls = (numberOfCalls / 100) * 100;
        }

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String strNumberOfCalls = formatter.format(numberOfCalls);
        return strNumberOfCalls;
    }

}
