package com.lchpatners.shadal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Guanadah on 2015-01-23.
 */
public class CategoryListFragment extends Fragment {

    private static Context context;

    public static CategoryListFragment newInstance(Context context) {
        CategoryListFragment.context = context;
        return new CategoryListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_view);
        final CategoryListAdapter adapter = new CategoryListAdapter(context);
        listView.setAdapter(adapter);
        // known issue : duplicated onItemClick when double tapping
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = ((TextView)view.findViewById(R.id.category_text)).getText().toString();
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .add(R.id.fragment_list_frame, RestaurantListFragment.newInstance(context, category))
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

}
