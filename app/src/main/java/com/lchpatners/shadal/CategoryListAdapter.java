package com.lchpatners.shadal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Guanadah on 2015-01-22.
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
    public static final int CATEGORIES_NUMBER = 8;

    public static String[] categories = new String[CATEGORIES_NUMBER];

    private Context context;

    public CategoryListAdapter(Context context) {
        this.context = context;
        categories = context.getResources().getStringArray(R.array.categories);
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
        ImageView icon = (ImageView)convertView.findViewById(R.id.category_icon);
        TextView text = (TextView)convertView.findViewById(R.id.category_text);
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
            case ETC: default:
                iconId = R.drawable.ic_etc;
                break;
        }
        icon.setImageResource(iconId);
        text.setText(categories[position]);
        return convertView;
    }
}
