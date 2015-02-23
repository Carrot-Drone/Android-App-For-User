package com.lchpatners.shadal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guanadah on 2015-01-26.
 */
public class SeeMoreListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> data;
    private ArrayList<String> headers;

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    public SeeMoreListAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        headers = new ArrayList<>();
        addHeader(context.getString(R.string.participate_in));
        addItem(context.getString(R.string.facebook_page));
        addItem(context.getString(R.string.report_restaurant));
        addItem(context.getString(R.string.report_to_camdal));
        addHeader(context.getString(R.string.settings));
        addItem(context.getString(R.string.change_campus));
    }

    public void addItem(String item) {
        data.add(item);
    }

    public void addHeader(String header) {
        data.add(header);
        headers.add(header);
    }

    @Override
    public int getItemViewType(int position) {
        return headers.contains(data.get(position)) ? HEADER : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
                header.setText(data.get(position));
                break;
            case ITEM:
                TextView item = (TextView)convertView.findViewById(R.id.item);
                item.setText(data.get(position));
                convertView.findViewById(R.id.price).setVisibility(View.INVISIBLE);
                break;
        }
        return convertView;
    }
}
