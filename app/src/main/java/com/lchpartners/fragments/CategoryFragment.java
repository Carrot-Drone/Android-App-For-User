package com.lchpartners.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lchpartners.shadal.R;

/**
 * Created by Gwangrae Kim on 2014-08-27.
 */
public class CategoryFragment extends Fragment implements ActionBarUpdater {
    private static class CategoryAdapter extends ArrayAdapter <String> {
        private int mOneLineLayout = 0;
        private Drawable[] mCategoryDrawables;
        private String[] mCategoryNames;

        public CategoryAdapter(Context context, int mOneLineLayout, String[] categoryNames, int[] categoryDrawableIDs) {
            super(context, mOneLineLayout, categoryNames);

            if(categoryNames.length != categoryDrawableIDs.length)
                throw new IllegalArgumentException("The number of category drawables and names are not same!");

            this.mOneLineLayout = mOneLineLayout;
            this.mCategoryNames = categoryNames;
            this.mCategoryDrawables = new Drawable[categoryDrawableIDs.length];
            Resources resources = context.getResources();

            for (int i = 0; i < categoryDrawableIDs.length ; i++) {
                this.mCategoryDrawables[i] = resources.getDrawable(categoryDrawableIDs[i]);
            }
        }

        private static class CategoryViewHolder {
            public ImageView categoryImageView;
            public TextView categotyTextView;
        }

        @Override
        public View getView (final int position, View convertView, ViewGroup parent) {
            TextView categotyTextView;
            ImageView categoryImageView;

            if (convertView == null) {
                convertView = View.inflate(this.getContext(), mOneLineLayout, null);
                CategoryViewHolder viewHolder = new CategoryViewHolder();
                viewHolder.categoryImageView = (ImageView) convertView.findViewById(R.id.image_view_category);
                viewHolder.categotyTextView = (TextView) convertView.findViewById(R.id.text_view_category_name);
                categoryImageView = viewHolder.categoryImageView;
                categotyTextView = viewHolder.categotyTextView;
                convertView.setTag(viewHolder);
            }
            else {
                CategoryViewHolder viewHolder = (CategoryViewHolder) convertView.getTag();
                categoryImageView = viewHolder.categoryImageView;
                categotyTextView = viewHolder.categotyTextView;
            }

            if(position %2 == 0) convertView.setBackgroundColor(0xfff2f2f2);
            else convertView.setBackgroundColor(0xfffcfcfc);

            categotyTextView.setText(mCategoryNames[position]);
            categoryImageView.setImageDrawable(mCategoryDrawables[position]);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    /*Intent moveToRestaurant = new Intent(getContext(), RestaurantActivity.class);
                    moveToRestaurant.putExtra("category", position);
                    getContext().startActivity(moveToRestaurant);*/
                    Toast.makeText(getContext(), "TODO", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }

    private final static String TAG = "CategoryFragment";
    private final static String[] mCategoryNames
        = { "치킨", "피자", "중국집", "한식/분식", "도시락/돈까스", "족발/보쌈", "냉면", "기타"};
    private final static int [] mCategoryDrawables =
            { R.drawable.ic_chic, R.drawable.ic_pizza, R.drawable.ic_chinese, R.drawable.ic_bob,
            R.drawable.ic_dosirak, R.drawable.ic_bossam, R.drawable.ic_noodle, R.drawable.ic_etc };

    private Activity mActivity;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        ListView categoryListView = (ListView) inflater.inflate(R.layout.fragment_category,null);
        categoryListView.setAdapter
                (new CategoryAdapter(getActivity(),R.layout.listview_item_category,mCategoryNames,mCategoryDrawables));
        return categoryListView;
    }

    public void updateActionBar () {
        //Setting up the action bar
        ActionBar actionBar = mActivity.getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        ViewGroup titleBar = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.action_bar, null);
        titleBar.setLayoutParams(actionBar.getCustomView().getLayoutParams());
        actionBar.setCustomView(titleBar);
        mActivity.invalidateOptionsMenu();
    }
}