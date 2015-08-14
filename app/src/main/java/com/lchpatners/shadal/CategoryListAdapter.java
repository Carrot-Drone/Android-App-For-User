package com.lchpatners.shadal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link android.widget.Adapter Adapter} of {@link com.lchpatners.shadal.CategoryListFragment
 * CategoryListFragment}.
 */
public class CategoryListAdapter extends BaseAdapter {

    /**
     * Indicates the "치킨" category.
     */
    public static final int CHICKEN = 0;
    /**
     * Indicates the "피자" category.
     */
    public static final int PIZZA = 1;
    /**
     * Indicates the "중식" category.
     */
    public static final int CHINESE = 2;
    /**
     * Indicates the "한식/분식" category.
     */
    public static final int KOREAN = 3;
    /**
     * Indicates the "도시락/돈까스" category.
     */
    public static final int DOSIRAK = 4;
    /**
     * Indicates the "족발/보쌈" category.
     */
    public static final int BOSSAM = 5;
    /**
     * Indicates the "냉면" category.
     */
    public static final int NAENGMYEON = 6;
    /**
     * Indicates the "기타" category.
     */
    public static final int ETC = 7;
    /**
     * The number of all categories.
     */
    public static final int CATEGORIES_NUMBER = 8;

    /**
     * An array of categories.
     */
    public static String[] categories;

    ArrayList<Restaurant> restaurants;

    private Context context;

    /**
     * Set {@link #context} and {@link #categories}.
     *
     * @param context {@link android.content.Context}
     */
    public CategoryListAdapter(Context context) {
        this.context = context;
        categories = context.getResources().getStringArray(R.array.categories);
    }

    public CategoryListAdapter(Context context, ArrayList<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @Override
    public int getCount() {

        return CATEGORIES_NUMBER;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_category, parent, false);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.category_icon);
        TextView text = (TextView) convertView.findViewById(R.id.category_text);
        int iconId;
        switch (position) {
            case CHICKEN:
                iconId = R.drawable.ic_chic;
                break;
            case PIZZA:
                iconId = R.drawable.ic_pizza;
                break;
            case CHINESE:
                iconId = R.drawable.ic_chinese;
                break;
            case KOREAN:
                iconId = R.drawable.ic_bob;
                break;
            case DOSIRAK:
                iconId = R.drawable.ic_dosirak;
                break;
            case BOSSAM:
                iconId = R.drawable.ic_bossam;
                break;
            case NAENGMYEON:
                iconId = R.drawable.ic_noodle;
                break;
            case ETC:
            default:
                iconId = R.drawable.ic_etc;
                break;
        }
        icon.setImageResource(iconId);
        text.setText(categories[position]);
        return convertView;
    }
}
