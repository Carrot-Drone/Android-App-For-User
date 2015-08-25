package com.lchpatners.shadal.restaurant.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lchpatners.shadal.R;

/**
 * Created by youngkim on 2015. 8. 25..
 */
public class CategoryListAdapter extends BaseAdapter {

    public static final int CHICKEN = 0;
    public static final int PIZZA = 1;
    public static final int CHINESE = 2;
    public static final int KOREAN = 3;
    public static final int DOSIRAK = 4;
    public static final int BOSSAM = 5;
    public static final int NAENGMYEON = 6;
    public static final int ETC = 7;

    public static String[] categories;
    private Context context;

    public CategoryListAdapter(Context context) {
        this.context = context;
        categories = context.getResources().getStringArray(R.array.categories);
    }

    @Override
    public int getCount() {
        return categories.length;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_item_category, parent, false);
        ImageView icon = (ImageView) convertView.findViewById(R.id.category_icon);
        TextView text = (TextView) convertView.findViewById(R.id.category_text);

        int iconId;

        switch (position) {
            case CHICKEN:
                iconId = R.drawable.icon_list_food_chicken;
                break;
            case PIZZA:
                iconId = R.drawable.icon_list_food_pizza;
                break;
            case CHINESE:
                iconId = R.drawable.icon_list_food_chinese_dishes;
                break;
            case KOREAN:
                iconId = R.drawable.icon_list_food_korean_dishes;
                break;
            case DOSIRAK:
                iconId = R.drawable.icon_list_food_lunch;
                break;
            case BOSSAM:
                iconId = R.drawable.icon_list_food_jokbal;
                break;
            case NAENGMYEON:
                iconId = R.drawable.icon_list_food_cold_noodles;
                break;
            case ETC:
            default:
                iconId = R.drawable.icon_list_food_etc;
                break;
        }
        icon.setImageResource(iconId);
        text.setText(categories[position]);

        return convertView;
    }
}
