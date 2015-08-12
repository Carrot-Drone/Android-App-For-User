package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class CallListAdapter extends BaseAdapter {

    private Context context;

    private List<Object> data;

    private String orderby;

    public CallListAdapter(Context context, String orderby) {
        this.context = context;
        data = new ArrayList<>();
        reloadData(orderby);
        this.orderby = orderby;
    }

    public void reloadData(String orderby) {
        data.clear();
        ArrayList<Call> calls;
        calls = DatabaseHelper.getInstance(context).getRecentCallsList(orderby);

        for (Call call : calls) {
            data.add(call);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_call, parent, false);
        }
        assert convertView != null;


        final Call call = (Call) data.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView count = (TextView) convertView.findViewById(R.id.call_count);
        name.setText(call.getRestaurantName());
        count.setText(call.getCount() + "");

        ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.iv_call);
        imgBtn.setClickable(true);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                Restaurant restaurant = helper.getRestaurantFromId(call.getRestaurantId());

                DatabaseHelper DBhelper = DatabaseHelper.getInstance(context);
                DBhelper.insertRecentCalls(restaurant.getRestaurantId(), restaurant.getCategoryId());

                Server server = new Server(context);
                //server.sendCallLog(restaurant);
                String number = "tel:" + restaurant.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                context.startActivity(intent);
                SystemClock.sleep(5000);

                reloadData(orderby);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(pos) instanceof Call) {
                    Call call = (Call) getItem(pos);
                    // Restaurant restaurant = (Restaurant) data.get(pos);
//                    AnalyticsHelper helper = new AnalyticsHelper(context);
//                    helper.sendEvent("UX", "res_clicked", restaurant.getName());
                    DatabaseHelper helper = DatabaseHelper.getInstance(context);
                    Restaurant restaurant = helper.getRestaurantFromId(call.getRestaurantId());

                    Intent intent = new Intent(context, MenuListActivity.class);
                    intent.putExtra("RESTAURANT", restaurant);
                    intent.putExtra("REFERRER", "CallListFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }
}


