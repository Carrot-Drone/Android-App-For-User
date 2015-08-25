package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lchpatners.shadal.restaurant.flyer.FlyerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.widget.Adapter Adapter} of {@link com.lchpatners.shadal.RestaurantListFragment
 * RestaurantListFragment}.
 */
public class RestaurantListAdapter extends BaseAdapter {

    public static final String URLS = "urls";

    public static final String RESTAURANT = "restaurant";


    /**
     * Indicates the header view type.
     * Used for {@link com.lchpatners.shadal.Restaurant#category categories},
     * only when the data source is bookmarks.
     */
    private static final int HEADER = 0;
    /**
     * Indicates the item view type.
     * Used for {@link com.lchpatners.shadal.Restaurant#name names}.
     */
    private static final int ITEM = 1;
    /**
     * The number of view types.
     */
    private static final int VIEW_TYPE_COUNT = 2;
    int flag;
    /**
     * {@link android.content.Context Context} this belongs to.
     */
    private Context context;
    private int categoryId;
    /**
     * List of all data, including both {@link #HEADER} and {@link #ITEM}.
     */
    private List<Object> data;
    /**
     * List of {@link #HEADER} data.
     */
    private List<String> headers;

    public RestaurantListAdapter(Context context, int categoryId, int flag) {
        Log.d("RestaurantAdapter", "called");
        this.context = context;
        this.categoryId = categoryId;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        reloadData(flag);
        this.flag = flag;
    }


    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof String
                && headers.contains(getItem(position)) ? HEADER : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    public void reloadData() {
        Log.d("reloadData", "called");
        data.clear();
        headers.clear();
        ArrayList<Restaurant> restaurants = DatabaseHelper.getInstance(context).getRestaurantsByCategory(categoryId);
        for (Restaurant restaurant : restaurants) {
            data.add(restaurant);
        }
        notifyDataSetChanged();
    }

    public void reloadData(int flag) {
        Log.d("reloadData", "called");
        data.clear();
        headers.clear();
        ArrayList<Restaurant> restaurants = DatabaseHelper.getInstance(context).getRestaurantsByCategory(categoryId); //flag 0 default
        if (flag == 1) {

        } else if (flag == 2) {
        } else if (flag == 3) {
            restaurants = DatabaseHelper.getInstance(context).getRestaurantsByCategoryFilteredFlyer(categoryId);
        }

        for (Restaurant restaurant : restaurants) {
            data.add(restaurant);
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
            switch (getItemViewType(position)) {
                case HEADER:
                    convertView = inflater.inflate(R.layout.list_header_menu, parent, false);
                    break;
                case ITEM:
                    convertView = inflater.inflate(R.layout.list_item_restaurant, parent, false);
                    break;
            }
        }
        assert convertView != null;
        switch (getItemViewType(position)) {
            case HEADER:
                TextView header = (TextView) convertView.findViewById(R.id.header);
                header.setText((String) data.get(position));
                break;
            case ITEM:
                Restaurant restaurant = (Restaurant) data.get(position);
                TextView retention = (TextView) convertView.findViewById(R.id.retention);
                retention.setText(Math.round(restaurant.getRetention() * 100) + "");
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(restaurant.getName());
                convertView.findViewById(R.id.flyer).setVisibility(restaurant.hasFlyer() ? View.VISIBLE : View.GONE);

                if (restaurant.hasFlyer()) {
                    final ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.flyer);
                    imgBtn.setClickable(true);
                    imgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Restaurant restaurant = (Restaurant) getItem(pos);
                            DatabaseHelper helper = DatabaseHelper.getInstance(context);
                            ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getRestaurantId());
                            Intent intent = new Intent(context, FlyerActivity.class);
                            intent.putExtra(RESTAURANT, restaurant);
                            intent.putExtra(URLS, urls);
                            Log.d("RestaurantListAdapturls", urls.toString());
                            context.startActivity(intent);

                        }
                    });
                }
                break;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(pos) instanceof Restaurant) {
                    Restaurant restaurant = (Restaurant) getItem(pos);

//                    AnalyticsHelper helper = new AnalyticsHelper(context);
//                    helper.sendEvent("UX", "res_clicked", restaurant.getName());

                    Intent intent = new Intent(context, MenuListActivity.class);
                    intent.putExtra("RESTAURANT", restaurant);
                    intent.putExtra("REFERRER", "RestaurantListFragment");
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }
}


