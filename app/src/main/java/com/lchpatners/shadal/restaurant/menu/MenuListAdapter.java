package com.lchpatners.shadal.restaurant.menu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lchpatners.shadal.DatabaseHelper;
import com.lchpatners.shadal.Menu;
import com.lchpatners.shadal.R;
import com.lchpatners.shadal.restaurant.Restaurant;
import com.lchpatners.shadal.SubMenu;
import com.lchpatners.shadal.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class MenuListAdapter extends BaseAdapter {
    private static final String TAG = LogUtils.makeTag(MenuListAdapter.class);

    private static final int SECTION = 0;
    private static final int ITEM = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private Context context;
    private Restaurant restaurant;
    private List<Object> data;
    private List<String> headers;

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
                .getMenusByRestaurantServerId(restaurant.getRestaurantId());
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
                headers.contains(data.get(position)) ? SECTION : ITEM;
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
                case SECTION:
                    convertView = inflater.inflate(R.layout.list_header_menu, parent, false);
                    break;
                case ITEM:
                    convertView = inflater.inflate(R.layout.list_item_menu, parent, false);
                    break;
            }
        }
        assert convertView != null;
        switch (getItemViewType(position)) {
            case SECTION:
                TextView header = (TextView) convertView.findViewById(R.id.header);
                header.setText((String) data.get(position));
                break;
            case ITEM:
                TextView item = (TextView) convertView.findViewById(R.id.item);
                item.setText(((Menu) data.get(position)).getItem());
                TextView price = (TextView) convertView.findViewById(R.id.price);
                int value = ((Menu) data.get(position)).getPrice();
                price.setText(value + context.getString(R.string.won));
                if (value == 0) {
                    ArrayList<SubMenu> subMenus = ((Menu) data.get(position)).getSubMenus();
                    Log.d("submenu size", subMenus.size() + "");
                    if (!subMenus.isEmpty() && subMenus.size() > 0) {
                        String subMenuString = "";
                        int count = 0;
                        for (SubMenu subMenu : subMenus) {
                            count++;
                            subMenuString += subMenu.getItem() + " : " + subMenu.getPrice() + context.getString(R.string.won);
                            if (count < subMenus.size()) {
                                subMenuString += "\n";
                            }
                        }
                        price.setText(subMenuString);
                    } else {
                        price.setVisibility(View.GONE);
                    }


                }


                TextView description = (TextView) convertView.findViewById(R.id.description);

                if (((Menu) data.get(position)).getDescription() != null &&
                        ((Menu) data.get(position)).getDescription().length() > 0 &&
                        !((Menu) data.get(position)).getDescription().equals("null")) {
                    description.setVisibility(View.VISIBLE);
                    description.setText(((Menu) data.get(position)).getDescription());
                } else {
                    description.setVisibility(View.GONE);
                }
                break;
        }

        return convertView;
    }
}
