package com.hanjaea.locprovider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanjaea.locprovider.model.GpsItem;
import com.hanjaea.locprovider.utils.LogUtil;
import com.hanjaea.locprovider.utils.Utils;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DaumMainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private static final String LOG_TAG = "DaumMainActivity";
    private ViewGroup mapViewContainer;
    private MapView mMapView;
    private String add_array="";

    private String networkLat;
    private double latitude;
    private String networkLong;
    private double longitude;
    private AQuery aq;
    public String currentLocation; // 그래서 최종 위치
    private List<GpsItem> gpsModelItem;


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_main);
        aq = new AQuery(this);
        /*
        Intent intent = getIntent();

        if(intent != null){
            networkLat = intent.getExtras().getString("networkLat");
            //tx1.setText(name);
            latitude = Double.parseDouble(networkLat);
            Log.d(">>> networkLat",networkLat);

            networkLong = intent.getExtras().getString("networkLong");
            //tx2.setText(String.valueOf(age));
            longitude = Double.parseDouble(networkLong);
            Log.d(">>> networkLong",networkLong);
         */

        /*
        String array[] = intent.getExtras().getStringArray("array");
        //
        for(int i=0;i<array.length;i++){
            if(i==0) {
                add_array += array[i] + ",";
            }else{
                add_array += array[i];
            }
        }
        */
        //}

        //ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mMapView = (MapView)findViewById(R.id.daum_map_view);
        //MapView mMapView = new MapView(this);
        mMapView.setCurrentLocationEventListener(this);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        //mMapView.setDaumMapApiKey("API KEY");

        aSyncinit();


        //dauminit();

    }

    private void dauminit(){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.5514579595, 126.951949155);
        currentLocation = Utils.getCompleteAddressString(this, 37.5514579595, 126.951949155);

//            txtCurrentMoney.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
//                    + "\n고도 : " + altitude + "\n정확도 : "  + accuracy);



        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(37.537229, 127.005515);
        MapPoint mapPoint3 = MapPoint.mapPointWithGeoCoord(37.545024, 127.03923);
        MapPoint mapPoint4 = MapPoint.mapPointWithGeoCoord(37.527896, 127.036245);
        MapPoint mapPoint5 = MapPoint.mapPointWithGeoCoord(37.541889, 127.095388);
        MapPoint mapPoint6 = MapPoint.mapPointWithGeoCoord(37.56671833682815,127.0312029910513);
        //MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        mMapView.setMapCenterPoint(mapPoint, true);
        //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
        //mapViewContainer.addView(mMapView);

        //Map list = new HashMap<String, String>();
        //list.put("latitude","37.537229");
        //list.put("longitude","127.005515");


        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(currentLocation);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker);

        MapPOIItem marker2 = new MapPOIItem();
        marker2.setItemName("GPS 수신위치2!!");
        marker2.setTag(1);
        marker2.setMapPoint(mapPoint2);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker2.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker2);

        MapPOIItem marker3 = new MapPOIItem();
        marker3.setItemName("GPS 수신위치3!!");
        marker3.setTag(3);
        marker3.setMapPoint(mapPoint3);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker3.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker3.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker3);

        MapPOIItem marker4 = new MapPOIItem();
        marker4.setItemName("GPS 수신위치4!!");
        marker4.setTag(4);
        marker4.setMapPoint(mapPoint4);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker4.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker4.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker4);

        MapPOIItem marker5 = new MapPOIItem();
        marker5.setItemName("GPS 수신위치5!!");
        marker5.setTag(5);
        marker5.setMapPoint(mapPoint5);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker5.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker5.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker5);

        MapPOIItem marker6 = new MapPOIItem();
        marker6.setItemName("GPS 수신위치6!!");
        marker6.setTag(6);
        marker6.setMapPoint(mapPoint6);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker6.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker6.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mMapView.addPOIItem(marker6);


        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

        // Polyline 좌표 지정.
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.5514579595, 126.951949155));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.537229, 127.005515));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.545024,127.03923));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.527896,127.036245));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(37.541889,127.095388));

        // Polyline 지도에 올리기.
        mMapView.addPolyline(polyline);

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        //mMapView.setShowCurrentLocationMarker(false);
    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }




    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(DaumMainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(DaumMainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(DaumMainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(DaumMainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(DaumMainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DaumMainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DaumMainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DaumMainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 서버에서 받아온 데이타를 지도 맵에 맞도록 형변환 처리
     * @param jsonArray
     */
    private void setMapData(JSONArray jsonArray){

        //LogUtil.d(">>>",jsonArray.toString());

            if(jsonArray != null){
                for (int i=0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jobj = jsonArray.getJSONObject(i);
                        ObjectMapper mapper = new ObjectMapper();
                        //GpsItem gpsitm = mapper.readValue(jobj, GpsItem.class);
                        GpsItem gpsitem = mapper.readValue(jobj.toString(), GpsItem.class);

                        gpsModelItem = mapper.readValue(jsonArray.toString(), new TypeReference<List<GpsItem>>() {});
                        //LogUtil.d(">>> getLatitude : ",gpsModelItem.get(i).getLatitude());

                    } catch (JSONException e){
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mapList(gpsModelItem);
            }

    }

    /**
     * 가공된 데이터를 이용해 지도에 위치를 표시 해 준다.
     */
    private void mapList(List<GpsItem> ListItem){

        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.


        for(GpsItem item : ListItem){
            double la = Double.parseDouble(item.getLatitude());
            double lo = Double.parseDouble(item.getLongitude());
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(la,lo);
            //currentLocation = Utils.getCompleteAddressString(this, la, lo);
            currentLocation = Utils.getAddress(this, la, lo);
            mMapView.setMapCenterPoint(mapPoint, true);
            //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
            //mapViewContainer.addView(mMapView);

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(currentLocation);
            marker.setTag(item.getId());
            marker.setMapPoint(mapPoint);
            // 기본으로 제공하는 BluePin 마커 모양.
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mMapView.addPOIItem(marker);

            // Polyline 좌표 지정.
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(la, lo));


            /*
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(currentLocation);
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            // 기본으로 제공하는 BluePin 마커 모양.
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mMapView.addPOIItem(marker);
            */
        }

        // Polyline 지도에 올리기.
        mMapView.addPolyline(polyline);

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

    }
    // 메시지 종류를 식별하기 위해, what 변수에 전달할 값을 상수로 정의.
    //private final int MSG_A = 0;
    //private final int MSG_B = 1;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MSG_A:

                    try {
                        JSONArray jsonArray = (JSONArray) msg.obj;
                        setMapData(jsonArray);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case Utils.MSG_B:
                    break;
                // TODO : add case.
            }
        }
    };


    /**
     *  DB에 저장된 위치데이터 가져오기
     */
    private void aSyncinit() {

        Utils.aSyncinitGet(getApplicationContext(), mHandler);

        /*
        String str_url = AppNetworkConstants.Url.ADDRESS_GET;

        ProgressDialog dialog = new ProgressDialog(getApplicationContext());
        //ProgressBar dialog = new ProgressBar(getApplicationContext());
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(null);
        dialog.setTitle("GPS정보 가져오기...");


        aq.progress(dialog).ajax(str_url, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                super.callback(url, json, status);
                //{"status":"success","insert_id":1,"data":{"user_type":"1","user_key":"787","major":"123","minor":"14587","rssi":"61"}}
                //LogUtil.Log(">>>>>> callback json : " + json.toString());
                if(json != null){
                    if(json.optString("status").equals("success")){
                        Toast.makeText(getApplicationContext(), "조회 완료!!", Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = json.getJSONArray("data");
                            setMapData(jsonArray);
                        } catch (JSONException e){
                            e.printStackTrace();
                        } catch (Exception e){
                            e.printStackTrace();
                        }


                    }else{
                        Toast.makeText(getApplicationContext(), "조회 에러!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "서버장애", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                }
            }
        });
        */

    }


}
