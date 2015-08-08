package com.lchpatners.shadal;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by eunhyekim on 2015. 8. 4..
 */
public class Category {
    Context context;
    private int categoryId;
    private int campusId;
    private String title;
    private int CategoryIcon;

    public Category(Cursor cursor) {
        categoryId = cursor.getInt(cursor.getColumnIndex("id"));
        campusId = cursor.getInt(cursor.getColumnIndex("campus_id"));
        title = cursor.getString(cursor.getColumnIndex("title"));
    }

    public static Category[] getCategory(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        ArrayList<Category> data = helper.getCategoryList();
        Category[] categories = new Category[data.size()];
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
                iconId = R.drawable.ic_chic;
                break;
            case "피자":
                iconId = R.drawable.ic_pizza;
                break;
            case "중국집":
                iconId = R.drawable.ic_chinese;
                break;
            case "한식/분식":
                iconId = R.drawable.ic_bob;
                break;
            case "도시락/돈까스":
                iconId = R.drawable.ic_dosirak;
                break;
            case "족발/보쌈":
                iconId = R.drawable.ic_bossam;
                break;
            case "냉면":
                iconId = R.drawable.ic_noodle;
                break;
            case "기타":
            default:
                iconId = R.drawable.ic_etc;
                break;
        }
        return iconId;
    }
}