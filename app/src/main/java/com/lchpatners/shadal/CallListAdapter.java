package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.widget.Adapter Adapter} of {@link RestaurantListFragment
 * RestaurantListFragment}.
 */
public class CallListAdapter extends BaseAdapter {
    /**
     * Indicates that you need to get data from bookmarks,
     * not by a specific category.
     */
    public static final String BOOKMARK = "bookmark";

    public static final String CAllSLIST = "callslist";


    /**
     * Indicates the header view type.
     * Used for {@link Restaurant#category categories},
     * only when the data source is bookmarks.
     */
    private static final int HEADER = 0;
    /**
     * Indicates the item view type.
     * Used for {@link Restaurant#name names}.
     */
    private static final int ITEM = 1;
    /**
     * The number of view types.
     */
    private static final int VIEW_TYPE_COUNT = 2;

    /**
     * {@link Context Context} this belongs to.
     */
    private Context context;
    /**
     * Data source of the list. Could either be a certain
     * {@link Restaurant#category category}
     * or bookmarks if this equals {@link #BOOKMARK}.
     */
    private String category;
    /**
     * List of all data, including both {@link #HEADER} and {@link #ITEM}.
     */
    private List<Object> data;

    /**
     * List of {@link #HEADER} data.
     */

    public CallListAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        reloadData();
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Reload all menu data from {@link #category source}.
     */
    public void reloadData() {
        data.clear();
        ArrayList<Call> calls;

        calls = DatabaseHelper.getInstance(context).getRecentCallsList();

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
        View view;
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
                DBhelper.insertRecentCalls(restaurant.getId());

                Server server = new Server(context);
                server.sendCallLog(restaurant);
                String number = "tel:" + restaurant.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                context.startActivity(intent);
                reloadData();
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


