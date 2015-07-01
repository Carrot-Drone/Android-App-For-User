package com.lchpatners.shadal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

/**
 * Created by eunhyekim on 2015. 6. 8..
 */
public class Popup extends Fragment {

    private Activity activity;
    private String currentURL;
    private String pid;
    private String link;

    public static Popup newInstance() {
        return new Popup();
    }

    public void getDatafromServer() {
        Bundle b = new Bundle();
        String pid = b.getString("pid");

    }

    public void init(String pid) {
        currentURL = Server.POPUP_URL + "/" + pid;
        this.pid = pid;
    }

    public void init(String pid, String link) {
        currentURL = Server.POPUP_URL + "/" + pid;
        this.pid = pid;
        this.link = link;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout wrapper = new RelativeLayout(activity);;
        View popup = inflater.inflate(R.layout.popup, null, false);

        WebView webView = (WebView) popup.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setClickable(true);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.loadUrl(currentURL);
        webView.setWebViewClient(new myWebViewClient());
        wrapper.addView(popup);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("accept Pid", pid);
                Log.d("onTouch Webview", link + currentURL);
                Server server = new Server(getActivity());
                server.acceptPopup(pid);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

                return false;
            }
        });

        if (link != null) {
            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    Log.d("accept Pid", pid);
                    Log.d("onTouch Webview", link + currentURL);
                    Server server = new Server(getActivity());
                    server.acceptPopup(pid);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();

                    return false;
                }
            });
        }
        return wrapper;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}
