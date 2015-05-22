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
 * {@link android.widget.Adapter Adapter} of {@link com.lchpatners.shadal.SeeMoreFragment
 * SeeMoreFragment}.
 */
public class SeeMoreListAdapter extends BaseAdapter {

    /**
     * {@link android.content.Context Context} this belongs to.
     */
    private Context context;
    /**
     * List of all data, including both {@link #HEADER} and {@link #ITEM}.
     */
    private List<String> data;
    /**
     * List of {@link #HEADER} data.
     */
    private List<String> headers;

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

    public SeeMoreListAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        headers = new ArrayList<>();
    }

    /**
     * Add an item.
     * @param item Item to add.
     */
    public void addItem(String item) {
        data.add(item);
    }

    /**
     * Add a header.
     * @param header Header to add.
     */
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
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
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
