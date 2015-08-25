package com.lchpatners.shadal.campus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lchpatners.shadal.R;

import java.util.List;

/**
 * Created by youngkim on 2015. 8. 21..
 */
public class CampusAdapter extends BaseAdapter {
    private Context mContext;
    private List<Campus> mCampuses;
    private int checkedItem = -1;

    public CampusAdapter(Context context, List<Campus> campuses) {
        mContext = context;
        mCampuses = campuses;
    }

    @Override
    public int getCount() {
        return mCampuses.size();
    }

    @Override
    public Object getItem(int position) {
        return mCampuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCheckedItem(int position) {
        this.checkedItem = position;
    }

    private void markSelectedItem(ImageView imageView, int position) {
        if (checkedItem != -1) {
            if (checkedItem == position) {
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Campus campus = mCampuses.get(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_campus, null);

        ImageView iconImageView;
        TextView campusName;

        iconImageView = (ImageView) convertView.findViewById(R.id.selected_icon);
        campusName = (TextView) convertView.findViewById(R.id.campus_name);

        campusName.setText(campus.getName());
        markSelectedItem(iconImageView, position);

        return convertView;
    }
}
