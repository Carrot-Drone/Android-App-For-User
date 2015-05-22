package com.lchpatners.shadal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.widget.Adapter Adapter} of {@link com.lchpatners.shadal.MenuListActivity
 * MenuListActivity}. Displays menus.
 */
public class MenuListAdapter extends BaseAdapter {

    /**
     * {@link android.content.Context Context} this belongs to.
     */
    private Context context;
    /**
     * {@link com.lchpatners.shadal.Restaurant Restaurant}
     * of which {@link com.lchpatners.shadal.Menu Menu} information is
     * to be displayed.
     */
    private Restaurant restaurant;
    /**
     * List of all data, including both {@link #HEADER} and {@link #ITEM}.
     */
    private List<Object> data;
    /**
     * List of {@link #HEADER} data.
     */
    private List<String> headers;

    /**
     * Indicates the header view type.
     * Used for {@link com.lchpatners.shadal.Menu#section sections}.
     */
    private static final int HEADER = 0;
    /**
     * Indicates the item view type.
     * Used for {@link com.lchpatners.shadal.Menu#item items}.
     */
    private static final int ITEM = 1;
    /**
     * The number of view types.
     */
    private static final int VIEW_TYPE_COUNT = 2;

    public MenuListAdapter(Context context, Restaurant restaurant) {
        this.context = context;
        this.restaurant = restaurant;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        reloadData();
    }

    /**
     * Reload all menu data of {@link #restaurant}.
     */
    public void reloadData() {
        data.clear();
        List<Menu> menus = DatabaseHelper.getInstance(context)
                .getMenusByRestaurantServerId(restaurant.getServerId());
        String header = null;
        for (Menu menu : menus) {
            if (!menu.getSection().equals(header)) {
                header = menu.getSection();
                data.add(header);
                headers.add(header);
            }
            data.add(menu);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) instanceof String &&
                headers.contains(data.get(position)) ? HEADER : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
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
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            switch (getItemViewType(position)) {
                case HEADER:
                    convertView = inflater.inflate(R.layout.list_header_menu, parent, false);
                    break;
                case ITEM:
                    convertView = inflater.inflate(R.layout.list_item_menu, parent, false);
                    break;
            }
        }
        assert convertView != null;
        switch (getItemViewType(position)) {
            case HEADER:
                TextView header = (TextView)convertView.findViewById(R.id.header);
                header.setText((String)data.get(position));
                break;
            case ITEM:
                TextView item = (TextView)convertView.findViewById(R.id.item);
                item.setText(((Menu)data.get(position)).getItem());
                TextView price = (TextView)convertView.findViewById(R.id.price);
                int value = ((Menu)data.get(position)).getPrice();
                if (value == 0) {
                    price.setVisibility(View.INVISIBLE);
                }
                price.setText(value + context.getString(R.string.won));
                break;
        }
        return convertView;
    }
}
