package com.lchpatners.shadal.recommend;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.util.LogUtils;

import java.text.DecimalFormat;

/**
 * Created by eunhyekim on 2015. 8. 27..
 */

public class RecommendedRestaurantFragment extends Fragment {
    private static final String TAG = LogUtils.makeTag(RecommendedRestaurantFragment.class);

    private static Activity mActivity;
    private static RecommendedRestaurant mRecommendedRestaurant;
    private static ImageButton upButton;
    private static ImageButton downButton;
    String restaurantName;
    String reason;
    String retention;
    String number_of_my_calls;
    String total_number_of_calls;
    String phone_number;
    String category_title;
    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.upButton) {
                RecommendedRestaurantController.changeCurrentPage(-1);
            } else if (view.getId() == R.id.downButton) {
                RecommendedRestaurantController.changeCurrentPage(1);
            }
        }
    };
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

    public static RecommendedRestaurantFragment create(RecommendedRestaurant recommendedRestaurant) {
        RecommendedRestaurantFragment fragment = new RecommendedRestaurantFragment();
        mRecommendedRestaurant = recommendedRestaurant;
        return fragment;
    }

    public static void setUpButtonInvisible() {
        upButton.setVisibility(View.INVISIBLE);
    }

    public static void setDownButtonInvisible() {
        downButton.setVisibility(View.INVISIBLE);
    }

    public static String numberOfCallsFormatString(int numberOfCalls) {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Restaurant restaurant = mRecommendedRestaurant.getRestaurant();

        restaurantName = restaurant.getName();
        reason = "<" + mRecommendedRestaurant.getReason() + ">";
        retention = String.valueOf(Math.round(restaurant.getRetention() * 100));
        number_of_my_calls = String.valueOf(restaurant.getNumber_of_my_calls());
        total_number_of_calls = numberOfCallsFormatString(restaurant.getTotal_number_of_calls());
        phone_number = String.valueOf(restaurant.getPhone_number());
        category_title = RecommendedRestaurantController.getCategoryTitle(mActivity, restaurant.getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new, container, false);
        setView(view);
        return view;
    }

    public void setView(View view) {
        tv_name = (TextView) view.findViewById(R.id.name);
        tv_reason = (TextView) view.findViewById(R.id.reason);
        tv_retention = (TextView) view.findViewById(R.id.retention);
        tv_number_of_my_calls = (TextView) view.findViewById(R.id.number_of_my_calls);
        tv_total_number_of_my_calls = (TextView) view.findViewById(R.id.total_number_of_calls);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        iv_new = (ImageView) view.findViewById(R.id.iv_new);
        iv_category = (ImageView) view.findViewById(R.id.iv_category);
        iv_flyer = (ImageView) view.findViewById(R.id.iv_flyer);
        iv_info = (ImageView) view.findViewById(R.id.iv_info);
        tv_phone_number = (TextView) view.findViewById(R.id.call);
        upButton = (ImageButton) view.findViewById(R.id.upButton);
        downButton = (ImageButton) view.findViewById(R.id.downButton);
        upButton.setOnClickListener(btnClickListener);
        downButton.setOnClickListener(btnClickListener);

        tv_name.setText(restaurantName);
        tv_reason.setText(reason);
        tv_retention.setText(retention);
        tv_number_of_my_calls.setText(number_of_my_calls);
        tv_total_number_of_my_calls.setText(total_number_of_calls);
        tv_phone_number.setText(phone_number);

        iv_category.setImageResource(getCategoryIcon(category_title));
        tv_category.setText(category_title);
        newIconVisible();

    }

    public void newIconVisible() {
        if (mRecommendedRestaurant.getReason().equals("캠달에 처음이에요")) {
            iv_new.setVisibility(View.VISIBLE);
        } else {
            iv_new.setVisibility(View.INVISIBLE);
        }
    }

    public int getCategoryIcon(String category_title) {
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
}