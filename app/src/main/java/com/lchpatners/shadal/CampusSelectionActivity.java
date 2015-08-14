package com.lchpatners.shadal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Shows campuses list and gets the user's selection.
 */
public class CampusSelectionActivity extends ActionBarActivity {

    /**
     * A {@link android.widget.ListView} to display a campus list.
     */
    ListView listView;
    /**
     * A {@link org.json.JSONArray} of campus data.
     */
    private JSONArray campuses;
    /**
     * Indicates if the database file existed at the time when the application
     * started. If <code>true</code>, it is likely that the user is using the
     * app for the first time. Or he/she may have cleared the cache.
     */
    private boolean hasNoDatabase;
    /**
     * A confirm {@link android.widget.Button}.
     */
    private Button confirm;
    private Toolbar select, toolbar;

    private String from = "campus";
    private String id = null;
    private String nameKor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_selection);

        Intent intent = getIntent();
        if (intent.getStringExtra("from") != null) {
            from = intent.getStringExtra("from");
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("캠퍼스 선택하기");
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);

        TextView emptyView = new TextView(this);
        emptyView.setText("loading...");
        emptyView.setTextColor(getResources().getColor(R.color.white100));
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        emptyView.setLayoutParams(params);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        if (campuses != null) {
            setListView();
        }
        tryShowingCampusesFromServer();
        select = (Toolbar) findViewById(R.id.select);
        if ((from.equals("RSbyOwnerActivity")) || (from.equals("RSbyUserActivity"))) {
            select.setVisibility(View.GONE);
            select.setClickable(false);
        } else {

            select.setVisibility(View.VISIBLE);
            if (Preferences.getCampusKoreanShortName(this) != null) {
                select.setClickable(true);
            }
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Preferences.getCampusKoreanShortName(CampusSelectionActivity.this) == null) {
                        return;
                    }

                    boolean isFirst = initializeDatabase();

//                AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//                helper.sendEvent("UX", isFirst ? "select_campus_start" : "select_campus",
//                        Preferences.getCampusKoreanShortName(CampusSelectionActivity.this));

                    Intent intent = new Intent(CampusSelectionActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
//        helper.sendScreen("캠퍼스 선택하기 화면");
    }

    /**
     * Request the campus list to the server and set the view, as a callback behavior.
     *
     * @see com.lchpatners.shadal.Server.CampusesLoadingTask CampusesLoadingTask
     */
    public void showCampusesFromServer() {

        new Server.CampusesLoadingTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                campuses = results;
                Log.d("campuses", campuses.toString());
                setListView();
            }
        }.execute();
    }

    /**
     * Set the view of {@link #listView};
     */
    public void setListView() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return campuses.length();
            }

            @Override
            public JSONObject getItem(int position) {
                try {
                    return campuses.getJSONObject(position);
                } catch (JSONException e) {
                    return null;
                }
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.list_item_campus, parent, false);
                }
                TextView textView = (TextView) convertView;

                final JSONObject campus = getItem(position);
                try {
                    textView.setText(campus.getString("name_kor"));
                } catch (JSONException e) {
                    return null;
                } catch (NullPointerException e) {
                    return null;
                }
                String selected = Preferences.getCampusKoreanShortName(CampusSelectionActivity.this);
                if (textView.getText().equals(selected)) {
                    textView.setTextColor(getResources().getColor(R.color.white100));
                    textView.setBackgroundColor(getResources().getColor(R.color.primary_light));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.black87));
                    textView.setBackgroundColor(getResources().getColor(R.color.white100));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View child = listView.getChildAt(i);
                            child.setBackgroundColor(getResources().getColor(R.color.white100));
                            ((TextView) child).setTextColor(getResources().getColor(R.color.black87));
                        }
                        v.setBackgroundColor(getResources().getColor(R.color.primary_light));
                        ((TextView) v).setTextColor(getResources().getColor(R.color.white100));

                        if (confirm != null) {
                            confirm.setAlpha(1.0f);
                            confirm.setClickable(true);
                        }
                        if (from.equals("RSbyOwnerActivity") || from.equals("RSbyUserActivity")) {
                            Intent intent = new Intent();

                            try {
                                id = campus.getString("id");
                                nameKor = campus.getString("name_kor");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("id", id);
                            intent.putExtra("name", nameKor);
                            Log.d("campusselection", "id, name:" + id + nameKor);
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Preferences.setCampus(CampusSelectionActivity.this, campus);
                            Server server = new Server(CampusSelectionActivity.this);
                            server.sendUuid();
                        }
                    }
                });
                return textView;
            }
        };
        listView.setAdapter(adapter);
    }

    /**
     * Call {@link #showCampusesFromServer()} if connected to the network
     * or try again with an {@link android.app.AlertDialog AlertDialog};
     */
    public void tryShowingCampusesFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            showCampusesFromServer();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.please_check_connectivity)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tryShowingCampusesFromServer();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();
        }
    }

    /**
     * Initialize the database by getting an instance of
     * {@link com.lchpatners.shadal.DatabaseHelper DatabaseHelper}.
     * Delete the database file of old versions, if any.
     * And finally call {@link #tryLoadingFromServer()}.
     *
     * @return If the user uses the app for the first time.
     */
    public boolean initializeDatabase() {
        DatabaseHelper helper = DatabaseHelper.getInstance(CampusSelectionActivity.this);
        boolean isFirst = hasNoDatabase = !helper.checkDatabase(Preferences.getCampusEnglishName(this));
        if (hasNoDatabase) {
            Cursor cursor = null;
            try {
                if (getDatabasePath(DatabaseHelper.LEGACY_DATABASE_NAME).exists()) {
                    File legacyFile = getDatabasePath(DatabaseHelper.LEGACY_DATABASE_NAME);
                    SQLiteDatabase legacyDb = SQLiteDatabase.openDatabase(legacyFile.getPath(), null, 0); // EXCEPTION OCCURS
                    cursor = legacyDb.rawQuery("SELECT server_id FROM restaurants WHERE is_favorite = 1;", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("id"));

                        } while (cursor.moveToNext());
                    }
                    legacyFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            tryLoadingFromServer();
        }
        return isFirst;
    }

    /**
     * Get all restaurant data of the selected campus if online. Otherwise, try again.
     */
    public void tryLoadingFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Server(this).updateAll();
            hasNoDatabase = false;
            Toast.makeText(this, getString(R.string.initial_download_guide), Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.please_check_connectivity)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tryLoadingFromServer();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {

        if (from.equals("RSbyOwnerActivity") || from.equals("RSbyUserActivity")) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
