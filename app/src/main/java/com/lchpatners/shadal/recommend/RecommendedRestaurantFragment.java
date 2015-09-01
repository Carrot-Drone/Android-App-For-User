package com.lchpatners.shadal.recommend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.call.CallLog.CallLogController;
import com.lchpatners.shadal.call.RecentCallController;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.RestaurantInfoActivity;
import com.lchpatners.shadal.restaurant.RestaurantListActivity;
import com.lchpatners.shadal.restaurant.flyer.Flyer;
import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;
import com.lchpatners.shadal.util.AnalyticsHelper;
import com.lchpatners.shadal.util.LogUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by eunhyekim on 2015. 8. 27..
 */

public class RecommendedRestaurantFragment extends Fragment {
    public static final String FLYER_URLS = "flyer_urls";
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String RESTAURANT_PHONE_NUMBER = "restaurant_phone_number";
    private static final String TAG = LogUtils.makeTag(RecommendedRestaurantFragment.class);

    private static Activity mActivity;

    private static ImageButton upButton;
    private static ImageButton downButton;

    private Restaurant mRestaurant;
    private String restaurantName;
    private String retention;
    private String number_of_my_calls;
    private String total_number_of_calls;
    private String phone_number;
    private String category_title;
    private LinearLayout ll_flyer;
    private TextView tv_name;
    private TextView tv_reason;
    private TextView tv_retention;
    private TextView tv_number_of_my_calls;
    private TextView tv_total_number_of_my_calls;
    private TextView tv_category;
    private ImageView iv_new;
    private ImageView iv_category;
    private ImageView iv_flyer;
    private ImageView iv_info;
    private TextView tv_phone_number;
    private Toolbar bottom_bar;
    private int count;
    private int position;
    private int restaurant_id;
    private String reason;
    private int goodCount = 0;
    private int badCount = 0;
    private View view;

    public static RecommendedRestaurantFragment newInstance(RecommendedRestaurant recommendedRestaurant, int count, int position) {
        RecommendedRestaurantFragment fragment = new RecommendedRestaurantFragment();
//        fragment.setRestaurant(recommendedRestaurant);
//        fragment.position = position;
//        fragment.count = count;

        Bundle args = new Bundle();
        args.putString("reason", recommendedRestaurant.getReason());
        args.putInt("restaurant_id", recommendedRestaurant.getRestaurant().getId());
        args.putInt("count", count);
        args.putInt("position", position);
        fragment.setArguments(args);

        return fragment;
    }

    public static void setUpButtonInvisible() {
        upButton.setVisibility(View.INVISIBLE);
    }

    public static void setDownButtonInvisible() {
        downButton.setVisibility(View.INVISIBLE);
    }

    private static String numberOfCallsFormatString(int numberOfCalls) {
        if ((numberOfCalls >= 10) && (numberOfCalls < 100)) {
            numberOfCalls = (numberOfCalls / 10) * 10;
        } else if ((numberOfCalls >= 100)) {
            numberOfCalls = (numberOfCalls / 100) * 100;
        }

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String strNumberOfCalls = formatter.format(numberOfCalls);
        return strNumberOfCalls;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRestaurant = RestaurantController.getRestaurant(mActivity, restaurant_id);
        setView(view);
        goodCount = mRestaurant.getTotal_number_of_goods();
        badCount = mRestaurant.getTotal_number_of_bads();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);
        this.view = view;

        setView(view);
        setUpDownButtonEvent(view);
        setRestaurantInfo(view);
        setCategory(view);
        setCallButton(view);
        setFlyer(view);

        if (this.position == 0) {
            setUpButtonInvisible();
        } else if (this.position == count) {
            setDownButtonInvisible();
        }
        return view;
    }

    public void setView(View view) {
        mRestaurant = RestaurantController.getRestaurant(mActivity, restaurant_id);
        count = getArguments().getInt("count");
        position = getArguments().getInt("position");
        restaurant_id = getArguments().getInt("restaurant_id");
        reason = getArguments().getString("reason");
        mRestaurant = RestaurantController.getRestaurant(mActivity, restaurant_id);
        goodCount = mRestaurant.getTotal_number_of_goods();
        badCount = mRestaurant.getTotal_number_of_bads();

        restaurantName = mRestaurant.getName();
        reason = "<" + reason + ">";
        retention = String.valueOf(Math.round(mRestaurant.getRetention() * 100));
        number_of_my_calls = RecentCallController.getRecentCallCountByRestaurantId(mActivity, mRestaurant.getId()) + "";
        total_number_of_calls = numberOfCallsFormatString(mRestaurant.getTotal_number_of_calls());
        phone_number = String.valueOf(mRestaurant.getPhone_number());
        category_title = RecommendedRestaurantController.getCategoryTitle(mActivity, mRestaurant.getId());

        tv_name = (TextView) view.findViewById(R.id.name);
        tv_reason = (TextView) view.findViewById(R.id.reason);
        tv_retention = (TextView) view.findViewById(R.id.retention);
        tv_number_of_my_calls = (TextView) view.findViewById(R.id.number_of_my_calls);
        tv_total_number_of_my_calls = (TextView) view.findViewById(R.id.total_number_of_calls);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        iv_new = (ImageView) view.findViewById(R.id.iv_new);
        tv_phone_number = (TextView) view.findViewById(R.id.call);
        upButton = (ImageButton) view.findViewById(R.id.upButton);
        downButton = (ImageButton) view.findViewById(R.id.downButton);
        bottom_bar = (Toolbar) view.findViewById(R.id.bottom_bar);
        ll_flyer = (LinearLayout) view.findViewById(R.id.flyerLayout);

        tv_name.setText(restaurantName);
        tv_reason.setText(reason);
        tv_retention.setText(retention);
        tv_number_of_my_calls.setText(number_of_my_calls);
        tv_total_number_of_my_calls.setText(total_number_of_calls);
        tv_phone_number.setText(phone_number);

        newIconVisible();
        setEvaluationBar(view);
    }

    private void setEvaluationBar(View view) {
        float percent = 0;

        TextView tv_like = (TextView) view.findViewById(R.id.tv_like);
        TextView tv_hate = (TextView) view.findViewById(R.id.tv_hate);
        TextView tv_percent = (TextView) view.findViewById(R.id.tv_percent);
        LinearLayout base_rating_bar = (LinearLayout) view.findViewById(R.id.base_rating_bar);
        LinearLayout rating_bar = (LinearLayout) view.findViewById(R.id.rating_bar);
        LinearLayout max_rating_bar = (LinearLayout) view.findViewById(R.id.max_rating_bar);
        RelativeLayout rl_percent = (RelativeLayout) view.findViewById(R.id.rl_percent);

        base_rating_bar.setVisibility(View.VISIBLE);
        max_rating_bar.setVisibility(View.INVISIBLE);
        rating_bar.setVisibility(View.VISIBLE);

        if (goodCount == 0 && badCount == 0) {
            percent = 50;
            rating_bar.getLayoutParams().width = (int) (base_rating_bar.getLayoutParams().width * (percent / 100));

            if (((ViewGroup) rl_percent).getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) ((ViewGroup) rl_percent).getLayoutParams()).leftMargin
                        = (rating_bar.getLayoutParams().width);
            }
        } else if (goodCount == 0) {
            if (((ViewGroup) rl_percent).getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) ((ViewGroup) rl_percent).getLayoutParams()).leftMargin
                        = 0;
            }

            rating_bar.setVisibility(View.INVISIBLE);
        } else if (badCount == 0) {
            rating_bar.setVisibility(View.INVISIBLE);
            max_rating_bar.setVisibility(View.VISIBLE);
            percent = 100;
            rating_bar.getLayoutParams().width = (int) (base_rating_bar.getLayoutParams().width);

            if (((ViewGroup) rl_percent).getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) ((ViewGroup) rl_percent).getLayoutParams()).leftMargin
                        = (rating_bar.getLayoutParams().width);
            }
        } else {
            percent = ((float) goodCount / ((float) goodCount + (float) badCount) * 100);
            rating_bar.getLayoutParams().width = (int) (base_rating_bar.getLayoutParams().width * (percent / 100));

            if (((ViewGroup) rl_percent).getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) ((ViewGroup) rl_percent).getLayoutParams()).leftMargin
                        = (rating_bar.getLayoutParams().width);
            }
        }

        tv_like.setText(goodCount + "");
        tv_hate.setText(badCount + "");
        tv_percent.setText(Math.round(percent) + "%");
    }

    private void newIconVisible() {
        if (reason.equals("<캠달에 처음이에요>")) {
            iv_new.setVisibility(View.VISIBLE);
        } else {
            iv_new.setVisibility(View.INVISIBLE);
        }
    }

    private int getCategoryIcon(String category_title) {
        int icon_id;
        switch (category_title) {
            case "치킨":
                icon_id = R.drawable.icon_card_food_chicken;
                break;
            case "피자":
                icon_id = R.drawable.icon_card_food_pizza;
                break;
            case "중국집":
                icon_id = R.drawable.icon_card_food_chinese_dishes;
                break;
            case "한식/분식":
                icon_id = R.drawable.icon_card_food_korean_dishes;
                break;
            case "도시락/돈까스":
                icon_id = R.drawable.icon_card_food_lunch;
                break;
            case "족발/보쌈":
                icon_id = R.drawable.icon_card_food_jokbal;
                break;
            case "냉면":
                icon_id = R.drawable.icon_card_food_noodles;
                break;
            default:
                icon_id = R.drawable.icon_card_food_etc;
                break;
        }
        return icon_id;
    }

    public void setFlyer(View view) {
        iv_flyer = (ImageView) view.findViewById(R.id.iv_flyer);
        ll_flyer = (LinearLayout) view.findViewById(R.id.flyerLayout);

        if (!mRestaurant.isHas_flyer()) {
            ll_flyer.setVisibility(View.GONE);
        } else {
            iv_flyer.setOnClickListener(new View.OnClickListener() {
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

                    AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                    analyticsHelper.sendEvent("UX", "flyer_in_recommend_clicked", mRestaurant.getName());
                }
            });
        }
    }

    public void setCallButton(View view) {
        bottom_bar = (Toolbar) view.findViewById(R.id.bottom_bar);
        bottom_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = "tel:" + mRestaurant.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                mActivity.startActivity(intent);

                RecentCallController.stackRecentCall(mActivity, mRestaurant.getId());
                CallLogController.sendCallLog(mActivity, mRestaurant.getId());

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                analyticsHelper.sendEvent("UX", "phonenumber_clicked", mRestaurant.getName());
                analyticsHelper.sendEvent("UX", "phonenumber_in_recommend_clicked", mRestaurant.getName());

//                RootActivity.updateNavigationView(mActivity);
//                RecommendedRestaurantActivity.updateNavigationView(mActivity);
            }
        });
    }

    public void setCategory(View view) {

        iv_category = (ImageView) view.findViewById(R.id.iv_category);
        iv_category.setImageResource(getCategoryIcon(category_title));
        tv_category.setText(category_title);
        iv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = RecommendedRestaurantController.getCategoryPosition(category_title);
                Intent intent = new Intent(getActivity(), RestaurantListActivity.class);
                intent.putExtra("category", category_title);
                intent.putExtra("position", position);
                startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                analyticsHelper.sendEvent("UX", "category_in_recommend_clicked", category_title);
            }
        });
    }

    public void setRestaurantInfo(View view) {
        iv_info = (ImageView) view.findViewById(R.id.iv_info);
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RestaurantInfoActivity.class);
                intent.putExtra(RESTAURANT_ID, mRestaurant.getId());
                mActivity.startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                analyticsHelper.sendEvent("UX", "res_in_recommend_clicked", mRestaurant.getName());
            }
        });
        iv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RestaurantInfoActivity.class);
                intent.putExtra(RESTAURANT_ID, mRestaurant.getId());
                mActivity.startActivity(intent);

                AnalyticsHelper analyticsHelper = new AnalyticsHelper(mActivity);
                analyticsHelper.sendEvent("UX", "res_in_recommend_clicked", mRestaurant.getName());
            }
        });
    }


    public void setUpDownButtonEvent(View view) {

        upButton = (ImageButton) view.findViewById(R.id.upButton);
        downButton = (ImageButton) view.findViewById(R.id.downButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecommendedRestaurantController.changeCurrentPage(-1);
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecommendedRestaurantController.changeCurrentPage(1);
            }
        });

    }

}