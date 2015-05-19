package com.lchpatners.shadal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by Guanadah on 2015-02-22.
 */
public class CampusSelectionActivity extends Activity {

    private static JSONArray campuses;

    private boolean hasNoDatabase;

    ListView listView;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_selection);

        listView = (ListView)findViewById(R.id.list_view);

        TextView emptyView = new TextView(this);
        emptyView.setText("loading...");
        emptyView.setTextColor(getResources().getColor(R.color.white100));
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        emptyView.setLayoutParams(params);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        if (campuses != null) {
            setListView();
        }
        tryShowingCampusesFromServer();

        confirm = (Button)findViewById(R.id.confirm);
        if (Preferences.getCampusKoreanShortName(this) != null) {
            confirm.setAlpha(1.0f);
            confirm.setClickable(true);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.getCampusKoreanShortName(CampusSelectionActivity.this) == null) {
                    return;
                }

                boolean isFirst = initializeDatabase();

                AnalyticsHelper helper = new AnalyticsHelper(getApplication());
                helper.sendEvent("UX", isFirst ? "select_campus_start" : "select_campus",
                        Preferences.getCampusKoreanShortName(CampusSelectionActivity.this));

                Intent intent = new Intent(CampusSelectionActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsHelper helper = new AnalyticsHelper(getApplication());
        helper.sendScreen("캠퍼스 선택하기 화면");
    }
    public void showCampusesFromServer() {
        new CampusesLoadingTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                setListView();
            }
        }.execute();
    }
    public static class CampusesLoadingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String serviceCall = Server.makeServiceCall(
                        Server.BASE_URL + Server.CAMPUSES, Server.GET, null);
                if (serviceCall == null) {
                    return null;
                }
                campuses = new JSONArray(serviceCall);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

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
                    LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.list_item_campus, parent, false);
                }
                TextView textView = (TextView)convertView;

                final JSONObject campus = getItem(position);
                try {
                    textView.setText(campus.getString("name_kor_short"));
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
                        Preferences.setCampus(CampusSelectionActivity.this, campus);

                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View child = listView.getChildAt(i);
                            child.setBackgroundColor(getResources().getColor(R.color.white100));
                            ((TextView)child).setTextColor(getResources().getColor(R.color.black87));
                        }
                        v.setBackgroundColor(getResources().getColor(R.color.primary_light));
                        ((TextView)v).setTextColor(getResources().getColor(R.color.white100));

                        if (confirm != null) {
                            confirm.setAlpha(1.0f);
                            confirm.setClickable(true);
                        }

                        Server server = new Server(CampusSelectionActivity.this);
                        server.sendUuid();
                    }
                });
                return textView;
            }
        };
        listView.setAdapter(adapter);
    }

    public void tryShowingCampusesFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public boolean initializeDatabase() {
        boolean isFirst = hasNoDatabase = !DatabaseHelper.getInstance(CampusSelectionActivity.this).checkDatabase();
        if (hasNoDatabase) {
            Cursor cursor = null;
            try {
                if (getDatabasePath(DatabaseHelper.LEGACY_DATABASE_NAME).exists()) {
                    File legacyFile = getDatabasePath(DatabaseHelper.LEGACY_DATABASE_NAME);
                    SQLiteDatabase legacyDb = SQLiteDatabase.openDatabase(legacyFile.getPath(), null, 0); // EXCEPTION OCCURS
                    cursor = legacyDb.rawQuery("SELECT server_id FROM restaurants WHERE is_favorite = 1;", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            int id = cursor.getInt(cursor.getColumnIndex("server_id"));
                            DatabaseHelper.legacyBookmarks.add(id);
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

    public void tryLoadingFromServer() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
