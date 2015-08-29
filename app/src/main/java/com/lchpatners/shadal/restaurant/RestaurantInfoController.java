package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.call.CallLog.CallLogController;
import com.lchpatners.shadal.call.RecentCallController;
import com.lchpatners.shadal.login.LoginCampusSelectActivity;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;
import com.lchpatners.shadal.util.LogUtils;
import com.lchpatners.shadal.util.Preferences;
import com.lchpatners.shadal.util.RetrofitConverter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by YoungKim on 2015. 8. 26..
 */
public class RestaurantInfoController {
    private static final String TAG = LogUtils.makeTag(RestaurantInfoActivity.class);
    private static final String BASE_URL = "http://www.shadal.kr:3000";
    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";
    public static final int GOOD = 1;
    public static final int BAD = 0;

    private Activity mActivity;
    private Restaurant mRestaurant;
    private View mHeader;
    private RestaurantEvaluationController mRestaurantEvaluationController;

    private boolean likeBtnChecked = false;
    private boolean hateBtnChecked = false;
    private int goodCount = 0;
    private int badCount = 0;

    public RestaurantInfoController(Activity activity) {
        this.mActivity = activity;
        this.mRestaurantEvaluationController = new RestaurantEvaluationController(mActivity);
        // TODO: set Restaurant Info at constructor
    }

    public Restaurant getRestaurant(int restaurant_id) {
        Restaurant restaurant = null;

        Realm realm = Realm.getInstance(mActivity);
        realm.beginTransaction();
        try {
            RealmQuery<Restaurant> query = realm.where(Restaurant.class);
            restaurant = query.equalTo("id", restaurant_id).findFirst();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        } finally {
            realm.close();
        }

        mRestaurant = restaurant;
        updateCurrentRestaurant();

        return restaurant;
    }

    private void updateCurrentRestaurant() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new RetrofitConverter().createRestaurantConverter()))
                .setEndpoint(BASE_URL) // The base API endpoint.
                .build();

        RestaurantAPI restaurantAPI = restAdapter.create(RestaurantAPI.class);

        restaurantAPI.getRestaurant(mRestaurant.getId(), new Callback<Restaurant>() {
            @Override
            public void success(Restaurant restaurant, Response response) {
                Realm realm = Realm.getInstance(mActivity);
                realm.beginTransaction();
                try {
                    realm.copyToRealmOrUpdate(restaurant);
                    realm.commitTransaction();
                } catch (Exception e) {
                    realm.cancelTransaction();
                    e.printStackTrace();
                } finally {
                    realm.close();
                }

                setHeaderData();
                mRestaurant = restaurant;
                goodCount = restaurant.getTotal_number_of_goods();
                badCount = restaurant.getTotal_number_of_bads();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                error.printStackTrace();
            }
        });
    }

    public void setHeader() {
        setHeaderWidget();
        setHeaderData();
        setHeaderButtonListener();
        setEvaluateButtonListener();
        attachHeader();
    }

    public void setFooter() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footer = inflater.inflate(R.layout.list_footer, null, false);
        ListView listView = (ListView) mActivity.findViewById(R.id.menu_list);

        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footer, null, false);
        }
    }

    private void setHeaderWidget() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeader = inflater.inflate(R.layout.menu_header, null, false);
    }

    private void setHeaderData() {
        checkPreEvaluateByUser();
        //TODO : update Evaluation

        TextView retention = (TextView) mHeader.findViewById(R.id.retention);
        TextView numberOfMyCalls = (TextView) mHeader.findViewById(R.id.number_of_my_calls);
        TextView totalNumberOfCalls = (TextView) mHeader.findViewById(R.id.total_number_of_calls);
        TextView notice = (TextView) mHeader.findViewById(R.id.notice);

        if (mRestaurant.getNotice() != null && mRestaurant.getNotice().length() > 0) {
            notice.setText(mRestaurant.getNotice());
        } else {
            notice.setVisibility(View.GONE);
        }

        retention.setText(Math.round(mRestaurant.getRetention() * 100) + "");
        numberOfMyCalls.setText(RecentCallController.getRecentCallCountByRestaurantId(mActivity, mRestaurant.getId()) + "");
        totalNumberOfCalls.setText(numberOfCallsFormatString(mRestaurant.getTotal_number_of_calls()));
    }

    private String numberOfCallsFormatString(int numberOfCalls) {
        if ((numberOfCalls >= 10) && (numberOfCalls < 100)) {
            numberOfCalls = (numberOfCalls / 10) * 10;
        } else if ((numberOfCalls >= 100)) {
            numberOfCalls = (numberOfCalls / 100) * 100;
        }

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String strNumberOfCalls = formatter.format(numberOfCalls);
        return strNumberOfCalls;
    }

    public void attachHeader() {
        ListView listView = (ListView) mActivity.findViewById(R.id.menu_list);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(mHeader, null, false);
        }
    }

    public void setCallButtonListener() {
        TextView phoneNumber = (TextView) mActivity.findViewById(R.id.phone_number);
        phoneNumber.setText(mRestaurant.getPhone_number());
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                    helper.sendEvent("UX", "phonenumber_clicked", restaurant.getName());
                String number = "tel:" + mRestaurant.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                mActivity.startActivity(intent);
                updateCallLog();
            }
        });
    }

    private void updateCallLog() {
        SystemClock.sleep(5 * 100);
        RecentCallController.stackRecentCall(mActivity, mRestaurant.getId());
        CallLogController.sendCallLog(mActivity, mRestaurant.getId());
        setHeaderData();
    }

    public void setFlyerButtonListener() {
        ImageButton flyer = (ImageButton) mActivity.findViewById(R.id.flyer);
        RelativeLayout divider = (RelativeLayout) mActivity.findViewById(R.id.divider_layout);
        divider.setVisibility((mRestaurant.isHas_flyer()) ? View.VISIBLE : View.GONE);
        flyer.setVisibility((mRestaurant.isHas_flyer()) ? View.VISIBLE : View.GONE);

        flyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> flyer_urls = new ArrayList<String>();
                List<Flyer> flyers = mRestaurant.getFlyers();

                for (Flyer flyer : flyers) {
                    flyer_urls.add(flyer.getUrl());
                }

                Intent intent = new Intent(mActivity, FlyerActivity.class);
                intent.putExtra(FLYER_URLS, flyer_urls);
                intent.putExtra(RESTAURANT_ID, mRestaurant.getId());
                intent.putExtra(RESTAURANT_PHONE_NUMBER, mRestaurant.getPhone_number());
                mActivity.startActivity(intent);
            }
        });
    }

    private void checkPreEvaluateByUser() {
        int score = mRestaurantEvaluationController.getEvaluationByCurrentUser(mRestaurant.getId());

        final TextView click_evaluation = (TextView) mHeader.findViewById(R.id.click_evaluation);
        final ImageView ivEvaluation = (ImageView) mHeader.findViewById(R.id.iv_evaluation);
        final TextView tvEvaluation = (TextView) mHeader.findViewById(R.id.tv_evaluation);
        final LinearLayout btn_divider = (LinearLayout) mHeader.findViewById(R.id.btn_divider);

        final ImageButton btnHate = (ImageButton) mHeader.findViewById(R.id.btn_hate);
        final ImageButton btnLike = (ImageButton) mHeader.findViewById(R.id.btn_like);

        if (score != -1) {
            if (score == GOOD) {
                likeBtnChecked = true;
                btnLike.setImageResource(R.drawable.icon_detail_page_btn_check);
                btnHate.setImageResource(R.drawable.icon_detail_page_btn_hate);
            } else if (score == BAD) {
                hateBtnChecked = true;
                btnHate.setImageResource(R.drawable.icon_detail_page_btn_check_selected);
                btnLike.setImageResource(R.drawable.icon_detail_page_btn_like);
            }
            click_evaluation.setVisibility(View.GONE);
            tvEvaluation.setVisibility(View.GONE);
            ivEvaluation.setVisibility(View.GONE);
            btn_divider.setVisibility(View.GONE);

            btnHate.setVisibility(View.VISIBLE);
            btnLike.setVisibility(View.VISIBLE);
        }

        goodCount = mRestaurant.getTotal_number_of_goods();
        badCount = mRestaurant.getTotal_number_of_bads();
    }

    private void setHeaderButtonListener() {
        final TextView click_share = (TextView) mHeader.findViewById(R.id.click_share);
        final TextView click_evaluation = (TextView) mHeader.findViewById(R.id.click_evaluation);

        final ImageView ivEvaluation = (ImageView) mHeader.findViewById(R.id.iv_evaluation);
        final TextView tvEvaluation = (TextView) mHeader.findViewById(R.id.tv_evaluation);

        final LinearLayout btn_divider = (LinearLayout) mHeader.findViewById(R.id.btn_divider);

        final ImageButton btnHate = (ImageButton) mHeader.findViewById(R.id.btn_hate);
        final ImageButton btnLike = (ImageButton) mHeader.findViewById(R.id.btn_like);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.click_share) {
                    try {
                        final KakaoLink kakaoLink = KakaoLink.getKakaoLink(mActivity);
                        final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder
                                = kakaoLink.createKakaoTalkLinkMessageBuilder();

                        String text = "아직";
                        final String linkContent =
                                kakaoTalkLinkMessageBuilder
                                        .addText(text)
                                        .addAppButton("캠퍼스:달 앱으로 이동").build();
                        kakaoLink.sendMessage(linkContent, mActivity);
                    } catch (KakaoParameterException e) {
                        e.printStackTrace();
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

        if (likeBtnChecked != false || hateBtnChecked != false) {
            click_evaluation.setVisibility(View.GONE);
        }
    }

    private void setEvaluateButtonListener() {
        final ImageButton btnHate = (ImageButton) mHeader.findViewById(R.id.btn_hate);
        final ImageButton btnLike = (ImageButton) mHeader.findViewById(R.id.btn_like);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_like) {
                    if (likeBtnChecked != true) {
                        mRestaurantEvaluationController.evaluate(GOOD, mRestaurant.getId());
                        UserPreferenceController.sendUserPreference(mRestaurant.getId(), GOOD, Preferences.getDeviceUuid(mActivity));
                        if (!likeBtnChecked) {
                            likeBtnChecked = !likeBtnChecked;
                            hateBtnChecked = !likeBtnChecked;
                            btnLike.setImageResource(R.drawable.icon_detail_page_btn_check);
                        } else {
                            likeBtnChecked = !likeBtnChecked;
                            hateBtnChecked = !likeBtnChecked;
                            btnLike.setImageResource(R.drawable.icon_detail_page_btn_like);
                        }
                        btnHate.setImageResource(R.drawable.icon_detail_page_btn_hate);
                        setHeaderData();
                    }
                } else if (view.getId() == R.id.btn_hate) {
                    if (hateBtnChecked != true) {
                        mRestaurantEvaluationController.evaluate(BAD, mRestaurant.getId());
                        UserPreferenceController.sendUserPreference(mRestaurant.getId(), BAD, Preferences.getDeviceUuid(mActivity));
                        if (!hateBtnChecked) {
                            hateBtnChecked = !hateBtnChecked;
                            likeBtnChecked = !hateBtnChecked;
                            btnHate.setImageResource(R.drawable.icon_detail_page_btn_check_selected);
                        } else {
                            hateBtnChecked = !hateBtnChecked;
                            likeBtnChecked = !hateBtnChecked;
                            btnHate.setImageResource(R.drawable.icon_detail_page_btn_hate);
                        }
                        btnLike.setImageResource(R.drawable.icon_detail_page_btn_like);
                        setHeaderData();
                    }
                }
            }
        };

        btnLike.setOnClickListener(listener);
        btnHate.setOnClickListener(listener);
    }

    public Restaurant getUpdatedRestaurant() {
        return mRestaurant;
    }
}
