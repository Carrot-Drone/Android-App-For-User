package com.lchpatners.shadal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.widget.Adapter Adapter} of {@link com.lchpatners.shadal.RestaurantListFragment
 * RestaurantListFragment}.
 */
public class RestaurantListAdapter extends BaseAdapter {
    /**
     * Indicates that you need to get data from bookmarks,
     * not by a specific category.
     */
    public static final String BOOKMARK = "bookmark";

    public static final String URLS = "urls";

    public static final String NAME = "name";
    public static final String RESTAURANT = "restaurant";

    public static final String PHONE_NUMBER = "phonenumber";

//    public static final String CAllSLIST = "callslist";


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

    /**
     * {@link android.content.Context Context} this belongs to.
     */
    private Context context;
    /**
     * Data source of the list. Could either be a certain
     * {@link com.lchpatners.shadal.Restaurant#category category}
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
    private List<String> headers;

    public RestaurantListAdapter(Context context, String category) {
        this.context = context;
        this.category = category;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        reloadData();
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

    /**
     * Reload all menu data from {@link #category source}.
     */
    public void reloadData() {
        data.clear();
        headers.clear();
        ArrayList<Restaurant> restaurants;
//        ArrayList<Call> calls;
//        if (!category.equals(CAllSLIST)) {
            restaurants = DatabaseHelper.getInstance(context).getRestaurantsByCategory(category);
            for (Restaurant restaurant : restaurants) {
                data.add(restaurant);
            }
        /*} else {
            calls = DatabaseHelper.getInstance(context).getRecentCallsList("NAME");
            String header = null;
            for (Call call : calls) {
            if (category.equals(CAllSLIST) && !restaurant.getCategory().equals(header)) {
                header = restaurant.getCategory();
                data.add(header);
                headers.add(header);
            }

                data.add(call);
            }
        }*/

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
                TextView name = (TextView) convertView.findViewById(R.id.name);
                name.setText(restaurant.getName());
                convertView.findViewById(R.id.recent).setVisibility(restaurant.isNew() ? View.VISIBLE : View.GONE);
                convertView.findViewById(R.id.coupon).setVisibility(restaurant.hasCoupon() ? View.VISIBLE : View.GONE);
                convertView.findViewById(R.id.flyer).setVisibility(restaurant.hasFlyer() ? View.VISIBLE : View.GONE);

                if (restaurant.hasFlyer()) {
                    final ImageButton imgBtn = (ImageButton) convertView.findViewById(R.id.flyer);
                    imgBtn.setClickable(true);
                    imgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Restaurant restaurant = (Restaurant) getItem(pos);
                            DatabaseHelper helper = DatabaseHelper.getInstance(context);
                            ArrayList<String> urls = helper.getFlyerUrlsByRestaurantServerId(restaurant.getServerId());
                            Intent intent = new Intent(context, FlyerActivity.class);
                            intent.putExtra(RESTAURANT, restaurant);
                            intent.putExtra(URLS, urls);
                            context.startActivity(intent);

                        }
                    });
                }
                convertView.findViewById(R.id.bookmark).setVisibility(restaurant.isFavorite()
                        && !category.equals("bookmark") ? View.VISIBLE : View.GONE);
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


