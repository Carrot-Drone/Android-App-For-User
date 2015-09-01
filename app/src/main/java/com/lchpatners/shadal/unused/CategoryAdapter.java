//package com.lchpatners.shadal.unused;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.lchpatners.shadal.R;
//
///**
// * Created by eunhyekim on 2015. 8. 6..
// */
//public class CategoryAdapter extends BaseAdapter {
//
//    private Context mContext;
//    private CategoryItem[] mCategories;
//
//    public CategoryAdapter(Context context, CategoryItem[] categories) {
//        mContext = context;
//        mCategories = categories;
//
//    }
//
//    @Override
//    public int getCount() {
//        return mCategories.length;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mCategories[position];
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup viewGroup) {
//
//        ImageView iconImageView; //public by default
//        TextView categoryTitle;
//
//        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_category, null);
//
//        iconImageView = (ImageView) convertView.findViewById(R.id.category_icon);
//        categoryTitle = (TextView) convertView.findViewById(R.id.category_text);
//
//
//        CategoryItem category = mCategories[position];
//
//        iconImageView.setImageResource(category.getCategoryIcon(category.getTitle()));
//        categoryTitle.setText(category.getTitle());
//        return convertView;
//    }
//
//}
