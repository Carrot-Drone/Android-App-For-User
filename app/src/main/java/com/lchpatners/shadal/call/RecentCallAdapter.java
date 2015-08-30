package com.lchpatners.shadal.call;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.RootActivity;
import com.lchpatners.shadal.call.CallLog.CallLogController;
import com.lchpatners.shadal.recommend.RecommendedRestaurantActivity;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.restaurant.RestaurantController;
import com.lchpatners.shadal.restaurant.RestaurantInfoActivity;

import java.util.List;

/**
 * Created by eunhyekim on 2015. 8. 22..
 */
public class RecentCallAdapter extends BaseAdapter {
    public static final String RESTAURANT_ID = "restaurant_id";
    private Activity mActivity;
    private String mOrder;
    private List<RecentCall> mRecentCallList;

    public RecentCallAdapter(Activity activity, String order) {
        this.mActivity = activity;
        this.mOrder = order;
        loadData(mOrder);
    }

    public void loadData(String orderBy) {
        mRecentCallList = RecentCallController.getRecentCallList(mActivity, orderBy);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mRecentCallList.get(position);
    }

    @Override
    public int getCount() {
        return mRecentCallList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        final RecentCall recentCall = mRecentCallList.get(position);

        convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_call, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView count = (TextView) convertView.findViewById(R.id.call_count);
        name.setText(recentCall.getRestaurant_name());
        count.setText(recentCall.getCall_count() + "");

        ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.iv_call);
        imgBtn.setClickable(true);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Restaurant restaurant = RestaurantController.getRestaurant(
                        mActivity, recentCall.getRestaurant_id());

                SystemClock.sleep(5 * 100);
                RecentCallController.stackRecentCall(mActivity, restaurant.getId());
                CallLogController.sendCallLog(mActivity, restaurant.getId());
                String number = "tel:" + restaurant.getPhone_number();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                mActivity.startActivity(intent);
                loadData(mOrder);

                RootActivity.updateNavigationView(mActivity);
                RecommendedRestaurantActivity.updateNavigationView(mActivity);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(pos) instanceof RecentCall) {
                    RecentCall recentCall1 = mRecentCallList.get(pos);
                    // Restaurant restaurant = (Restaurant) data.get(pos);
//                    AnalyticsHelper helper = new AnalyticsHelper(mContext);
//                    helper.sendEvent("UX", "res_clicked", restaurant.getName());

                    Intent intent = new Intent(mActivity, RestaurantInfoActivity.class);
                    intent.putExtra(RESTAURANT_ID, recentCall.getRestaurant_id());
                    intent.putExtra("REFERRER", "RecentCall");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mActivity.startActivity(intent);
                }
            }
        });

        return convertView;
    }
}
