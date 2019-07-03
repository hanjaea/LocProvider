package com.hanjaea.locprovider.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hanjaea.locprovider.AppNetworkConstants;
import com.hanjaea.locprovider.DaumMainActivity;
import com.hanjaea.locprovider.utils.LocPreference;
import com.hanjaea.locprovider.utils.LogUtil;
import com.hanjaea.locprovider.R;
import com.hanjaea.locprovider.utils.Utils;
import com.hanjaea.locprovider.db.DatabaseOpenHelper;
import com.hanjaea.locprovider.db.GpsInfoQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static final String TAG = "NewAppWidget";
    //private static final int WIDGET_UPDATE_INTERVAL = 1000 * 60 * 10; // 10분
    private static final int WIDGET_UPDATE_INTERVAL = 1000 * 60 * 5; // 5분
    private static PendingIntent mSender;
    private static AlarmManager mManager;
    private static AppWidgetManager mAppWidgetManager;

    // 커스텀 액션
    public static String PENDING_ACTION = "com.hanjaea.locprovider.Pending_Action";
    public static final String CUSTOM_REFRESH_ACTION = "au.com.codeka.weather.UpdateAction";

    //private String Latitude = "1.1";
    //private String Longitude = "1.1";
    private String oLatitude;
    private String oLongitude;
    private Context mContext;
    private DatabaseOpenHelper mDbHelper;
    private Location mLocation;
    public static boolean isService = true;
    private LocPreference eventPrefs;
    public static boolean mExec = true;


    public static void notifyRefresh(Context context) {
        Intent i = new Intent(context, NewAppWidget.class);
        i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        i.putExtra(CUSTOM_REFRESH_ACTION, 1);
        context.sendBroadcast(i);
    }

    private void prefInit(Context context){

        if(eventPrefs == null) {
            eventPrefs = new LocPreference(context);
            String lat = eventPrefs.get_Latitude();
            String lon = eventPrefs.get_Longitude();
            if(lat==null) {
                eventPrefs.set_Latitude("1.1");
                eventPrefs.set_Longitude("1.1");
            }
        }
    }

    /**
     * 내부 custom 함수
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        LogUtil.Log(">>> updateAppWidget appWidgetId","appWidgetId : " + appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        LogUtil.Log(">>> updateAppWidget isService : ", String.valueOf(isService));

        if(isService) {
            //views.setImageViewResource(R.id.button3, android.R.drawable.ic_media_pause);
            gpsDataSendChk(context, Utils.getLocationInfo(context));
        }else{
        //    views.setImageViewResource(R.id.button3, android.R.drawable.ic_media_play);
        }

        //if(mExec) {
        //views.setOnClickPendingIntent(R.id.button1, getPendingIntent(context, R.id.button1, true));
        views.setOnClickPendingIntent(R.id.button2, getPendingIntent(context, R.id.button2, true));
        //views.setOnClickPendingIntent(R.id.button3, getPendingIntent(context, R.id.button3,false));
        //views.setOnClickPendingIntent(R.id.button4, getPendingIntent(context, R.id.button4,false));
        //views.setOnClickPendingIntent(R.id.button5, getPendingIntent(context, R.id.button5,false));
        //}

        Toast.makeText(context,"updateAppWidget ",Toast.LENGTH_SHORT).show();


        // Construct the RemoteViews object
        Date now = new Date();
        //views.setTextViewText(R.id.appwidget_text, "[" + String.valueOf(isService) + "]" );
        String msg = "[발신] " + now.toLocaleString();
        views.setTextViewText(R.id.widgettext, msg);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        //if(!isService){
        //    return;
        //}

        int appWidgetId = intent.getIntExtra("viewId", 0);
        boolean state = intent.getBooleanExtra("state",true);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        int lid = rv.getLayoutId();
        //LinearLayout linearLayout = (LinearLayout)rv.getLayoutId();


        // 수신한 인텐트로부터 액션값을 읽음
        String action = intent.getAction();
        // AppWidget의 기본 Action 들
        if (action.equals(PENDING_ACTION)) {

            /*
            if(appWidgetId == R.id.button1){
                Toast.makeText(context,"Toast",Toast.LENGTH_SHORT).show();
                int id = rv.getLayoutId();

                LogUtil.Log(">>> button1 id","id : " + id);

                try {
                    Utils.backupDatabase(context);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            */

            if(appWidgetId == R.id.button2){
                int id = rv.getLayoutId();
                LogUtil.Log(">>> button2 id","id : " + id);

                Intent intent2 = new Intent(context, DaumMainActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);

                //Toast.makeText(context,"button2 ",Toast.LENGTH_SHORT).show();
                context.startActivity(intent2);

            }
            /*
            else if(appWidgetId == R.id.button3){

                //isService = !isService;

                Toast.makeText(context,"alram stop isServiced " + String.valueOf(isService),Toast.LENGTH_SHORT).show();
                LogUtil.Log(">>> onReceive button3 isService ", String.valueOf(isService));
                LogUtil.Log(">>> onReceive button3 state","state : " + String.valueOf(state));

                int id = rv.getLayoutId();

                LogUtil.Log(">>> id button3","id : " + id);

                isService = !isService;

                if(!isService) {
                    //isService = true;
                    rv.setImageViewResource(R.id.button3, android.R.drawable.ic_media_play);
                    //rv.setViewVisibility(R.id.button3, View.VISIBLE);
                    //rv.setViewVisibility(R.id.button3, View.GONE);

                    //rv.setString(R.id.button3, "state","pause");
                    //CharSequence widgetText = context.getString(R.string.appwidget_text_loading);
                    //Button btn3 = (Button)findViewById(R.id.button3);

                    removePreviousAlarm();
                    //long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
                    //mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
                    //mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    //mManager.set(AlarmManager.RTC, firstTime, mSender);
                    //}
                    Toast.makeText(context,"isService = false",Toast.LENGTH_SHORT).show();
                }else{
                    //isService = true;
                    rv.setImageViewResource(R.id.button3, android.R.drawable.ic_media_pause);

                    removePreviousAlarm();

                    long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
                    mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
                    mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    mManager.set(AlarmManager.RTC, firstTime, mSender);

                    //updateAppWidget(context, mAppWidgetManager,appWidgetId);

                    gpsDataSendChk(context, Utils.getLocationInfo(context));
                    Toast.makeText(context,"isService = true",Toast.LENGTH_SHORT).show();

                }
            }
            */

            LogUtil.Log(">>> onReceive End ~~~ isService ", String.valueOf(isService));
        }

        //if(mExec) {
            //rv.setOnClickPendingIntent(R.id.button1, getPendingIntent(context, R.id.button1));
            //rv.setOnClickPendingIntent(R.id.button2, getPendingIntent(context, R.id.button2));
            //rv.setOnClickPendingIntent(R.id.button3, getPendingIntent(context, R.id.button3));
        //}

        if(action.equals("android.appwidget.action.APPWIDGET_UPDATE"))
        {
            LogUtil.w(">>> action.equals ", "android.appwidget.action.APPWIDGET_UPDATE");
            if(isService) {
                removePreviousAlarm();

                long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
                mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
                mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mManager.set(AlarmManager.RTC, firstTime, mSender);
            }
            Toast.makeText(context,"APPWIDGET_UPDATE",Toast.LENGTH_SHORT).show();
        }
        //
        else if(action.equals("android.appwidget.action.APPWIDGET_DISABLED"))
        {
            LogUtil.w(">>> action.equals ", "android.appwidget.action.APPWIDGET_DISABLED");
            removePreviousAlarm();
            Toast.makeText(context,"APPWIDGET_DISABLED",Toast.LENGTH_SHORT).show();
        }

        rv.setTextViewText(R.id.appwidget_text, "[" + String.valueOf(isService) + "]" );


        // 위젯 화면 갱신
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName cpName = new ComponentName(context, NewAppWidget.class);
        //appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.updateAppWidget(cpName, rv);

        //int[] appWidgetIds = {appWidgetId};

        //this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
        //super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

         this.mAppWidgetManager = appWidgetManager;
        // preference null check
        prefInit(context);

        //Toast.makeText(context,"onUpdate~~ ",Toast.LENGTH_SHORT).show();

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //Toast.makeText(context, "onUpdate(): [" + String.valueOf(appWidgetId) + "] " + String.valueOf(appWidgetId), Toast.LENGTH_SHORT).show();
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /*
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        removePreviousAlarm();
    }
    */

    /*
        onReceive()는 브로드캐스트리시버에서 배웠기에 넘어가겠습니다
        onUpdate()는 위젯 갱신 주기에 따라 위젯을 갱신할때 호출됩니다
        onEnabled()는 위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출됩니다
        onDisabled()는 위젯의 마지막 인스턴스가 제거될때 호출됩니다
        onDeleted()는 위젯이 사용자에 의해 제거될때 호출됩니다
     */

    /**
     * 설정된 알람 취소
     */
    public void removePreviousAlarm()
    {
        if(mManager != null && mSender != null)
        {
            mSender.cancel();
            mManager.cancel(mSender);
        }
    }


    /**
     * 타이머에서 현재 앱에 저장된 로컬 DB 데이터 체크하는 함수
     * @param context
     * @param location
     */
    private void gpsDataSendChk(Context context, Location location){
        DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getInstance(context);    //db getInstance

        JSONArray list = GpsInfoQuery.getGpsInfo(context, dbHelper);
        //JSONObject obj = GpsInfoQuery.getGpsInfo(context, dbHelper);
        if(list != null && 0 < list.length()){
            // 배열을 서버로 던진다.
            aSyncinitArray(context, list, location);
            return;
        }

        //List<GpsInfo> list = GpsInfoQuery.getGpsInfoList(context,dbHelper);
        //if(list != null && 0 < list.size()){
            // 배열을 서버로 던진다.
        //    aSyncinitArray(context,)
        //}
        serverSend(context, location, dbHelper);


    }


    /**
     * 현재 위경도 정보를 서어버에 던지는 함수
     * @param context
     * @param location
     * @param dbHelper
     */
    private void serverSend(Context context, Location location, DatabaseOpenHelper dbHelper){
        LogUtil.Log(">>> ", "serverSend ");
        // preference null check
        prefInit(context);

        if(location!=null) {
            oLatitude = Double.toString(location.getLatitude());
            oLongitude = Double.toString(location.getLongitude());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            if (oLatitude != null && oLongitude != null) { //&& !Latitude.equals("0") && !Longitude.equals("0")
                String msg = "oLatitude : " + oLatitude + " Latitude : " + eventPrefs.get_Latitude() + " oLongitude : " + oLongitude + " Longitude : " + eventPrefs.get_Longitude();
                LogUtil.Log(">>> serverSend", msg);

                String scoLat = oLatitude;
                String scolong = oLongitude;

                String sccoLat = scoLat.replace(".", "");
                String sccolong = scolong.replace(".", "");

                String eLat = scoLat;
                String elong = scolong;
                if(!eventPrefs.get_Latitude().isEmpty()){
                    eLat = eventPrefs.get_Latitude();
                }
                if(!eventPrefs.get_Longitude().isEmpty()){
                    elong = eventPrefs.get_Longitude();
                }

                String ecLat = eLat.replace(".", "");
                String eclong = elong.replace(".", "");

                Double sLatitude = Double.parseDouble(oLatitude);
                Double sLongitude = Double.parseDouble(oLongitude);
                //Double sLatitude = Double.parseDouble("37.557543");
                //Double sLongitude = Double.parseDouble("126.943795");
                Double eLatitude = Double.parseDouble(eLat);
                Double eLongitude = Double.parseDouble(elong);

                String msg2 = "sccoLat : " + sccoLat + " sccolong : " + sccolong + " ecLat : " + ecLat + " eclong : " + eclong;
                LogUtil.Log(">>> serverSend", msg2);

                String distance = Utils.calcDistance(sLatitude,sLongitude,eLatitude,eLongitude);

                //int dist = Utils.DistanceByDegree(sLatitude,sLongitude,eLatitude,eLongitude);

                LogUtil.Log(">>> distance ", "distance : "+distance);

                //int adist = Utils.DistanceByDegreeAndroid(sLatitude,sLongitude,eLatitude,eLongitude);

                //LogUtil.Log(">>> adist ", "adist : "+adist);
                if(distance.contains("km")){
                    //Toast.makeText(context,"원거리일 경우 return distance " + distance,Toast.LENGTH_SHORT).show();
                    //return;
                    String val = distance.replace("km", "").trim();
                    val = val.replace(".", "").trim();
                    LogUtil.Log(">>> val ", val);
                    //Toast.makeText(context,">>> val : " + val,Toast.LENGTH_SHORT).show();

                    int dist = Integer.parseInt(val);
                    LogUtil.Log(">>> dist ", "dist : "+dist);
                    if(dist > 1) {
                        LogUtil.Log(">>> dist ", "dist : " + dist);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("latitude", oLatitude);
                            obj.put("longitude", oLongitude);
                            obj.put("up_dt", dateFormat.format(date).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String lat = oLatitude;
                        String ong = oLongitude;
                        //AppNetworkConstants.Latitude = lat;
                        //AppNetworkConstants.Longitude = ong;
                        eventPrefs.set_Latitude(lat);
                        eventPrefs.set_Longitude(ong);

                        // 서버전송
                        Utils.aSyncinit(context, obj, dbHelper);
                        mContext = null;
                        mLocation = null;
                        mDbHelper = null;
                    }


                    return;
                }

                if(distance.indexOf("m") > -1){

                    String val = distance.replace("m", "").trim();
                    val = val.replace(".", "").trim();
                    LogUtil.Log(">>> val ", val);
                    //Toast.makeText(context,">>> val : " + val,Toast.LENGTH_SHORT).show();

                    int dist = Integer.parseInt(val);
                    LogUtil.Log(">>> dist ", "dist : "+dist);
                    if(dist > 15) {
                        LogUtil.Log(">>> dist ", "dist : " + dist);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("latitude", oLatitude);
                            obj.put("longitude", oLongitude);
                            obj.put("up_dt", dateFormat.format(date).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String lat = oLatitude;
                        String ong = oLongitude;
                        //AppNetworkConstants.Latitude = lat;
                        //AppNetworkConstants.Longitude = ong;
                        eventPrefs.set_Latitude(lat);
                        eventPrefs.set_Longitude(ong);

                        // 서버전송
                        Utils.aSyncinit(context, obj, dbHelper);
                        mContext = null;
                        mLocation = null;
                        mDbHelper = null;


                        return;
                    }
                //}else{
                //    LogUtil.Log(">>> 기존 위경도 거리에 비해 너무 멀거나 가까울 때", "distance : "+Integer.parseInt(distance));
                //    Toast.makeText(context,">>> 기존 위경도 거리에 비해 너무 멀거나 가까울 때 distance : " + Integer.parseInt(distance),Toast.LENGTH_SHORT).show();
                }

                LogUtil.Log(">>> 거리계산 ", "" + distance);

            } else {
                LogUtil.Log(">>> serverSend", "oLatitude == Latitude");
            }
        }else{
            LogUtil.Log(">>> serverSend", "location == null");
        }
    }

    // 호출한 객체에 PendingIntent를 부여
    private static PendingIntent getPendingIntent(Context context, int id, boolean state) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(PENDING_ACTION);
        intent.putExtra("viewId", id);
        intent.putExtra("state", state);

        // 중요!!! getBroadcast를 이용할 때 동일한 Action명을 이용할 경우 서로 다른 request ID를 이용해야함
        // 아래와 같이 동일한 request ID를 주면 서로 다른 값을 putExtra()하더라도 제일 처음 값만 반환됨
        // return PendingIntent.getBroadcast(context, 0, intent, 0);
        return PendingIntent.getBroadcast(context, id, intent, 0);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MSG_A:

                    try {
                        //JSONArray jsonArray = (JSONArray) msg.obj;
                        //setMapData(jsonArray);
                        Toast.makeText(mContext, "DB정보 전송 완료!!", Toast.LENGTH_SHORT).show();
                        GpsInfoQuery.DelGpsAll(mContext);
                        GpsInfoQuery.initSeqReset(mContext, mDbHelper);
                        serverSend(mContext, mLocation, mDbHelper);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Utils.MSG_B:

                    serverSend(mContext, mLocation, mDbHelper);

                    break;
                // TODO : add case.
            }
        }
    };


    /**
     * 네크워크 오류로 현재 위치가 서버로 전송이 안된상태에서 호출 되는 함수
     * @param context
     * @param jsonArray
     * @param location
     */
    private void aSyncinitArray(final Context context, final JSONArray jsonArray, final Location location) {

        if(mContext == null) {
            mContext = context;
        }
        mDbHelper = DatabaseOpenHelper.getInstance(context);
        mLocation = location;
        Utils.aSyncinitArray(context, jsonArray, location, mHandler);
    }



}

