package com.hanjaea.locprovider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.hanjaea.locprovider.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;
    private Timer mGpsServerSendTimer;
    private AQuery aq;

    private final int LOCATION_INTERVAL = 500;
    private final int LOCATION_DISTANCE = 10;
    private String Latitude = "0";
    private String Longitude = "0";
    private String oLatitude;
    private String oLongitude;


    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener
    {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation = location;
            Log.d(TAG, "LocationChanged getLatiude : "+location.getLatitude() + " getLongitude : " + location.getLongitude());

            Intent intent = new Intent(getApplicationContext(), DaumMainActivity.class);
            //Latitude = Double.toString(location.getLatitude());
            //Longitude = Double.toString(location.getLongitude());

            oLatitude = Double.toString(location.getLatitude());
            oLongitude = Double.toString(location.getLongitude());

            intent.putExtra("networkLat", Latitude);
            intent.putExtra("networkLong",Longitude);

            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //getApplication().startActivity(intent);
            //권한 체크
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) {
                double lng = lastKnownLocation.getLongitude();
                double lat = lastKnownLocation.getLatitude();
                Log.i(TAG, "longtitude : " + lng + ", latitude : " + lat);
                //tvNetworkLatitude.setText(Double.toString(lat ));
                //tvNetworkLongitude.setText((Double.toString(lng)));
            }

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        aq = new AQuery(getApplicationContext());
        startForeground(12345678, getNotification());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
                if (mGpsServerSendTimer != null) {
                    mGpsServerSendTimer.cancel();
                    mGpsServerSendTimer = null;
                }
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        initializeLocationManager();
        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener );
            beaconSendTimer();
        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    public void beaconSendTimer(){

        TimerTask mGpsSendTask = new TimerTask() {
            @Override
            public void run() {
                // 서버상태정보 호출 로직
                gpsDataSendChk();
            }
        };
        mGpsServerSendTimer = new Timer();
        mGpsServerSendTimer.schedule(mGpsSendTask, 0, 1000 * 60 * 10);
    }

    /**
     * 기존 GPS 정보와 데이터 비교후 서버에 던지게 처리 한다.
     * 서버로 던지기 전에 기존에 던졌던 데이터와 비교후 다르면 던지게 로직 수정
     */
    private void gpsDataSendChk(){
        JSONObject obj = new JSONObject();


        if (oLatitude != null && oLongitude != null ) { //&& !Latitude.equals("0") && !Longitude.equals("0")
            String msg = "oLatitude : " + oLatitude+ " Latitude : " + Latitude + " oLongitude : " + oLongitude + " Longitude : " + Longitude;
            Log.d(TAG, msg);
            if(!oLatitude.equals(Latitude) && !oLongitude.equals(Longitude)) {
                try {
                    obj.put("latitude", oLatitude);
                    obj.put("longitude", oLongitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String lat = oLatitude;
                String ong = oLongitude;
                Latitude = lat;
                Longitude = ong;
                // 서버전송
                aSyncinit(getApplicationContext(), obj.toString());
            }
        }
    }


    private void aSyncinit(Context context, String json) {
        //String str_url = "http://192.168.0.99:3000/beacon/sendgps?jsonStr=";
        String str_url = "http://192.168.0.16:3000/beacon/sendgps?jsonStr=";
        //String str_url = "http://bluemobile1.cafe24.com/wp-json/beacon/insert/?jsonStr=";

        // http://bluemobile1.cafe24.com/?jsonStr={%22user_type%22:%221%22,%22user_key%22:%22787%22,%22beacon_data%22:{%22major%22:%22123%22,%22minor%22:%2214587%22,%22rssi%22:%2261%22}}
        // http://bluemobile1.cafe24.com/wp-json/beacon/insert/?jsonStr=%7B%22user_type%22%3A%221%22%2C%22user_key%22%3A%22A-0001%22%2C%22beacon_data%22%3A%22%7B%5C%22major%5C%22%3A%5C%2222413%5C%22%2C%5C%22minor%5C%22%3A%5C%2245789%5C%22%2C%5C%22rssi%5C%22%3A%5C%22-65%5C%22%7D%22%7D

        //LogUtil.Log("~~~ str_url : " + str_url);
        //LogUtil.Log(">>>>>> aSyncinit json : " + json.toString());
        Map<String, Object> params = new HashMap<String, Object>();

        String query = null;
        try {
            query = URLEncoder.encode(json, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String url = "http://stackoverflow.com/search?q=" + query;
        str_url += query;
        //str_url += json.toString();
        LogUtil.Log(">>>>>> aSyncinit str_url : " + str_url);


        /*
        ProgressDialog dialog = new ProgressDialog(getApplicationContext());
        //ProgressBar dialog = new ProgressBar(getApplicationContext());

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(null);
        dialog.setTitle("GPS정보 전송중...");
        */
        //LogUtil.setProgressDialog(getApplicatisonContext());

        //AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setCancelable(false); // if you want user to wait for some process to finish,
        //builder.setView(R.layout.layout_loading_dialog);
        //AlertDialog dialog = builder.create();

        //AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //View view = getLayoutInflater().inflate(R.layout.progress);
        //builder.setView(R.layout.progress);
        //Dialog dialog = builder.create();
        //dialog.show();
        //if (show)dialog.show();
        //else dialog.dismiss();

        aq.progress(R.layout.progress).ajax(str_url, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                super.callback(url, json, status);
                //{"status":"success","insert_id":1,"data":{"user_type":"1","user_key":"787","major":"123","minor":"14587","rssi":"61"}}
                //LogUtil.Log(">>>>>> callback json : " + json.toString());
                if(json != null){
                    if(json.optString("status").equals("success")){
                        Toast.makeText(getApplicationContext(), "전송 완료!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                        //Latitude = "0";
                        //Longitude = "0";
                    }else{
                        Toast.makeText(getApplicationContext(), "전송 에러!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "서버장애", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                }
            }
        });


		/* POST 방식으로 보내기 (hashMap 을 이용한 key , value를 추가하여 파라메터를 만들어 던지는 방법)
		aq.progress(R.id.progressCircle).ajax(str_url, params, JSONObject.class, new AjaxCallback<JSONObject>(){
			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {

				if(json != null) {
					if (json.optInt("result") == 1) {   // 저장완료
						Toast.makeText(getApplicationContext(), "beacon list 정보 서버전송 완료!!", Toast.LENGTH_SHORT).show();
						//BeaconQuery.truncateAllTable(getApplicationContext());
					} else {  // 저장실패
						Toast.makeText(getApplicationContext(), "beacon list 정보 서버전송 중 장애!!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		*/


    }
}
