package com.hanjaea.locprovider;

import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.InputStream;

public class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

    private RemoteViews views;
    private int widgetID;
    private AppWidgetManager appWidgetManager;

    public DownloadBitmap(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager) {
        this.views = views;
        this.widgetID = appWidgetID;
        this.appWidgetManager = appWidgetManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        //다운로드 받을 이미지 주소
        String url = params[0];

        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Log.e("ImageDownload", "Download succeeded! " + params[0]);
            return bitmap;
        } catch (Exception e) {
            Log.e("ImageDownload", "Download failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //그 결과를 가지고 화면에 출력
        if(isCancelled()) {
            bitmap = null;
        }
        //views.setImageViewBitmap(R.id.imageView, bitmap);
        appWidgetManager.updateAppWidget(widgetID, views);

    }
}
