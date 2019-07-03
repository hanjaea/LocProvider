package com.hanjaea.locprovider;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private String host = "https://www.google.com/maps/@";
    private String url = "https://www.google.com/maps/@37.5677948,127.0329719,15z";
    private String add_array="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent(); /*데이터 수신*/

        String networkLat = intent.getExtras().getString("networkLat");
        //tx1.setText(name);
        Log.d(">>> networkLat",networkLat);

        String networkLong = intent.getExtras().getString("networkLong");
        //tx2.setText(String.valueOf(age));
        Log.d(">>> networkLong",networkLong);

        String array[] = intent.getExtras().getStringArray("array"); /*배열*/
        //
        for(int i=0;i<array.length;i++){
            if(i==0) {
                add_array += array[i] + ",";
            }else{
                add_array += array[i];
            }
        }
        //tx3.setText(add_array);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url + add_array);
                return true;
            }
        });
        Log.d(">>> address",host + add_array);
        Log.d("",host.concat(networkLat).concat(",").concat(networkLong));

        mWebView.loadUrl(host + add_array);
        //mWebView.loadUrl(host);
        //mWebView.loadUrl("https://www.google.com/maps/@37.5682809,127.0323136,17z");


    }
}
