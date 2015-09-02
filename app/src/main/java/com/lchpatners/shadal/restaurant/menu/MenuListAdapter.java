package com.lchpatners.shadal.restaurant.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lchpatners.shadal.R;
import com.lchpatners.shadal.dao.Restaurant;
import com.lchpatners.shadal.restaurant.RestaurantMenuController;
import com.lchpatners.shadal.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by YoungKim on 2015. 8. 25..
 */
public class MenuListAdapter extends BaseAdapter {
    private static final String TAG = LogUtils.makeTag(MenuListAdapter.class);

    private static final int SECTION = 0;
    private static final int ITEM = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private Context mContext;
    private RestaurantMenuController mRestaurantMenuController;
    private List<RestaurantMenu> mRestaurantMenus;
    private ArrayList mMenuData = new ArrayList();

    public MenuListAdapter(Context context, Restaurant restaurant) {
        this.mContext = context;
        this.mRestaurantMenuController = new RestaurantMenuController(context, restaurant);
        this.mRestaurantMenus = mRestaurantMenuController.getRestaurantMenus();
        convertMenuList();
    }

    private void convertMenuList() {
        sortMenuList();

        if (mRestaurantMenus.size() != 0) {
            String currentSection = mRestaurantMenus.get(0).getSection();
            mMenuData.add(currentSection);

            for (RestaurantMenu menu : mRestaurantMenus) {
                if (!currentSection.equals(menu.getSection())) {
                    currentSection = menu.getSection();
                    mMenuData.add(currentSection);
                }
                mMenuData.add(menu);
            }
        } else {
            mMenuData.add("메뉴가 없어요!");
        }
    }

    private void sortMenuList() {
        List<RestaurantMenu> tempMenuList = new ArrayList<RestaurantMenu>();

        for (RestaurantMenu restaurantMenu : mRestaurantMenus) {
            tempMenuList.add(restaurantMenu);
        }

        Collections.sort(tempMenuList, new Comparator<RestaurantMenu>() {
            @Override
            public int compare(RestaurantMenu restaurantMenu, RestaurantMenu t1) {
                String compare = restaurantMenu.getSection();
                String compareT = t1.getSection();

                //ascending order
                return compare.compareTo(compareT);
            }
        });

        mRestaurantMenus = tempMenuList;
    }

    @Override
    public int getItemViewType(int position) {
        return mMenuData.get(position) instanceof String ? SECTION : ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return mMenuData.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

//        if (convertView != null) {
        switch (getItemViewType(position)) {
            case SECTION:
                convertView = inflater.inflate(R.layout.list_header_menu, null);
                break;
            case ITEM:
                convertView = inflater.inflate(R.layout.list_item_menu, null);
                break;
        }
//        }

        switch (getItemViewType(position)) {
            case SECTION:
                TextView header = (TextView) convertView.findViewById(R.id.header);
                header.setText((String) mMenuData.get(position));
                break;
            case ITEM:
                TextView item = (TextView) convertView.findViewById(R.id.item);
                item.setText(((RestaurantMenu) mMenuData.get(position)).getName());
                TextView price = (TextView) convertView.findViewById(R.id.price);

                if (((RestaurantMenu) mMenuData.get(position)).getSubMenus().size() != 0) {
                    RealmList<RestaurantSubMenu> subMenus = ((RestaurantMenu) mMenuData.get(position)).getSubMenus();
                    String subMenuString = "";
                    int count = 0;
                    for (RestaurantSubMenu subMenu : subMenus) {
                        count++;
                        subMenuString += subMenu.getName() + " : " + subMenu.getPrice() + mContext.getString(R.string.won);
                        if (count < subMenus.size()) {
                            subMenuString += "\n";
                        }
                    }
                    price.setText(subMenuString);
                } else {
                    int value = ((RestaurantMenu) mMenuData.get(position)).getPrice();
                    if (value == 0) {
                        price.setVisibility(View.GONE);
                    } else {
                        price.setText(value + mContext.getString(R.string.won));
                    }
                }

                TextView description = (TextView) convertView.findViewById(R.id.description);

                if (((RestaurantMenu) mMenuData.get(position)).getDescription() != null &&
                        ((RestaurantMenu) mMenuData.get(position)).getDescription().length() > 0 &&
                        !((RestaurantMenu) mMenuData.get(position)).getDescription().equals("null")) {
                    description.setVisibility(View.VISIBLE);
                    description.setText(((RestaurantMenu) mMenuData.get(position)).getDescription());
                } else {
                    description.setVisibility(View.GONE);
                }
                break;
        }

        return convertView;
    }
}
