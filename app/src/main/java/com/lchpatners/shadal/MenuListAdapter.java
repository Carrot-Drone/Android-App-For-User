package com.lchpatners.shadal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class MenuListAdapter extends BaseAdapter {

    private Context context;
    private Restaurant restaurant;
    private ArrayList<Object> data;
    private ArrayList<String> headers;

    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public MenuListAdapter(Context context, Restaurant restaurant) {
        this.context = context;
        this.restaurant = restaurant;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        reloadData();
    }

    public void reloadData() {
        data.clear();
        ArrayList<Menu> menus = DatabaseHelper.getInstance(context).getMenusByRestaurantServerId(restaurant.getServerId());
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
                price.setText(((Menu)data.get(position)).getPrice() + context.getString(R.string.won));
                break;
        }
        return convertView;
    }
}
