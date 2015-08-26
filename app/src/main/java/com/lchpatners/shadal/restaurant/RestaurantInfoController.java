package com.lchpatners.shadal.restaurant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;
import com.lchpatners.shadal.util.LogUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by YoungKim on 2015. 8. 26..
 */
public class RestaurantInfoController {
    private static final String TAG = LogUtils.makeTag(RestaurantInfoActivity.class);
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

    public RestaurantInfoController(Activity activity, Restaurant restaurant) {
        this.mActivity = activity;
        this.mRestaurant = restaurant;
        this.mRestaurantEvaluationController = new RestaurantEvaluationController(mActivity);
    }

    public void setHeader() {
        setHeaderWidget();
        checkPreEvaluateByUser();
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
        //TODO : GET my call count
//        numberOfMyCalls.setText(mRestaurant.getNumberOfCalls(MenuListActivity.this, restaurant.getRestaurantId()) + "");
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

                //TODO: update call db
                updateCallLog();
//                Call.updateCallLog(MenuListActivity.this, restaurant);
                String number = "tel:" + mRestaurant.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                mActivity.startActivity(intent);
            }
        });
    }

    private void updateCallLog() {

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
                RealmList<Flyer> flyers = mRestaurant.getFlyers();

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

        final Button click_evaluation = (Button) mHeader.findViewById(R.id.click_evaluation);
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
    }

    private void setHeaderButtonListener() {
        final Button click_share = (Button) mHeader.findViewById(R.id.click_share);
        final Button click_evaluation = (Button) mHeader.findViewById(R.id.click_evaluation);

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
                    }
                } else if (view.getId() == R.id.btn_hate) {
                    if (hateBtnChecked != true) {
                        mRestaurantEvaluationController.evaluate(BAD, mRestaurant.getId());
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
                    }
                }
            }
        };

        btnLike.setOnClickListener(listener);
        btnHate.setOnClickListener(listener);
    }
}
