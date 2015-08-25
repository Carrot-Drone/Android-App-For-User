package com.lchpatners.shadal.unused;

import android.content.Context;
import android.database.Cursor;

import com.lchpatners.shadal.DatabaseHelper;
import com.lchpatners.shadal.R;

import java.util.ArrayList;

/**
 * Created by eunhyekim on 2015. 8. 4..
 */
public class CategoryItem {
    private int categoryId;
    private int campusId;
    private String title;
    private int CategoryIcon;

    public CategoryItem(Cursor cursor) {
        categoryId = cursor.getInt(cursor.getColumnIndex("id"));
        campusId = cursor.getInt(cursor.getColumnIndex("campus_id"));
        title = cursor.getString(cursor.getColumnIndex("title"));
    }

    public static CategoryItem[] getCategory(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        ArrayList<CategoryItem> data = helper.getCategoryList();
        CategoryItem[] categories = new CategoryItem[data.size()];
        for (int i = 0; i < data.size(); i++) {
            categories[i] = data.get(i);
        }
        return categories;
    }

    public int getCampusId() {
        return campusId;
    }

    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryIcon(String title) {
        int iconId;
        switch (title) {
            case "치킨":
                iconId = R.drawable.icon_list_food_chicken;
                break;
            case "피자":
                iconId = R.drawable.icon_list_food_pizza;
                break;
            case "중국집":
                iconId = R.drawable.icon_list_food_chinese_dishes;
                break;
            case "한식/분식":
                iconId = R.drawable.icon_list_food_korean_dishes;
                break;
            case "도시락/돈까스":
                iconId = R.drawable.icon_list_food_lunch;
                break;
            case "족발/보쌈":
                iconId = R.drawable.icon_list_food_jokbal;
                break;
            case "냉면":
                iconId = R.drawable.icon_list_food_cold_noodles;
                break;
            case "기타":
            default:
                iconId = R.drawable.icon_list_food_etc;
                break;
        }
        return iconId;
    }
}