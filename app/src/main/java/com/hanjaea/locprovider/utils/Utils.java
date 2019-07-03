package com.hanjaea.locprovider.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanjaea.locprovider.AppNetworkConstants;
import com.hanjaea.locprovider.R;
import com.hanjaea.locprovider.db.DatabaseOpenHelper;
import com.hanjaea.locprovider.db.GpsInfoQuery;
import com.hanjaea.locprovider.db.GpsTableFeildConstants;
import com.hanjaea.locprovider.model.GpsData;
import com.hanjaea.locprovider.model.GpsInfo;
import com.hanjaea.locprovider.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.codehaus.jackson.map.ObjectMapper


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class Utils {

    public static final int MSG_A = 0 ;
    public static final int MSG_B = 1 ;

    public static void setProgressDialog(Context context) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText("전송중 ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    /**
     * 위도,경도로 주소구하기
     * @param lat
     * @param lng
     * @return 주소
     */
    public static String getAddress(Context mContext,double lat, double lng) {
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List <Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;

                }
            }

        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }



    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");


                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }

        // "대한민국 " 글자 지워버림
        strAdd = strAdd.substring(5);

        return strAdd;
    }


    public static Location getLocationInfo(Context context) {


        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }


        Location networklocation = null; // netWorklocation
        Location gpslocation = null; // gpslocation
        Location location = null; // location
        double latitude = 0; // latitude
        double longitude = 0; // longitude

        int gpsEnableCnt = 0;
        int networkEnableCnt = 0;

        try {


            LocationManager locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                boolean canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {

                    if (locationManager != null) {
                        networklocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (networklocation != null) {
                            latitude = networklocation.getLatitude();
                            longitude = networklocation.getLongitude();

                            String lat = String.valueOf(latitude);
                            String lon = String.valueOf(longitude);

                            LogUtil.d(">>>>> NetWork Location ","latitude : " + lat + " longitude : " + lon);

                            networkEnableCnt++;

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (gpslocation == null) {

                        if (locationManager != null) {
                            gpslocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (gpslocation != null) {
                                latitude = gpslocation.getLatitude();
                                longitude = gpslocation.getLongitude();

                                String lat = String.valueOf(latitude);
                                String lon = String.valueOf(longitude);

                                LogUtil.d(">>>>> GPS Location ","latitude : " + lat + " longitude : " + lon);

                                gpsEnableCnt++;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            return gpslocation;
        }


        if(gpsEnableCnt > 0 && networkEnableCnt > 0){
            location = networklocation;
        }else if(gpsEnableCnt == 0 || networkEnableCnt > 0){
            location = networklocation;
        }else if(gpsEnableCnt > 0 || networkEnableCnt == 0){
            location = gpslocation;
        }else{
            location = gpslocation;
        }

        return location;
    }


    /**
     * 사용안함
     * @param context
     * @param jarray
     * @return
     */
    public static boolean aServerSendArray(final Context context, final JSONArray jarray) {
        boolean retval = true;
        DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getInstance(context);
        AQuery aq = new AQuery(context);
        final String jsonString = jarray.toString();
        LogUtil.Log(">>> aSyncinitArray", jsonString);
        String str_url = "http://192.168.0.15:3000/beacon/sendgps?jsonStr=";
        //String str_url = "http://192.168.0.99:3000/beacon/sendgps?jsonStr=";
        //String str_url = "http://bluemobile1.cafe24.com/wp-json/beacon/insert/?jsonStr=";

        JSONObject jobj = new JSONObject();
        try{
            //jobj.put("array",jsonString);
            jobj.put("array",jarray);
        }catch (JSONException e){
            e.printStackTrace();
        }

        final String jarrayString = jobj.toString();
        LogUtil.Log(">>> jarrayString", jarrayString);
        Map<String, Object> params = new HashMap<String, Object>();
        String query = null;
        try {
            query = URLEncoder.encode(jarrayString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String url = "http://stackoverflow.com/search?q=" + query;
        str_url += query;
        //str_url += json.toString();
        LogUtil.Log(">>>>>> aSyncinitArray str_url : " + str_url);


        ProgressDialog dialog = new ProgressDialog(context);
        //ProgressBar dialog = new ProgressBar(getApplicationContext());

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(null);
        dialog.setTitle("GPS정보 전송중...");

        aq.progress(R.layout.progress);
        aq.ajax(str_url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                super.callback(url, json, status);
                //{"status":"success","insert_id":1,"data":{"user_type":"1","user_key":"787","major":"123","minor":"14587","rssi":"61"}}
                LogUtil.Log(">>>>>> callback json : " + json);
                boolean error = false;
                if (json != null) {
                    if (json.optString("status").equals("success")) {
                        Toast.makeText(context, "전송 완료!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "전송 에러!!", Toast.LENGTH_SHORT).show();
                        error = true;
                    }
                } else {
                    Toast.makeText(context, "서버장애", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                //retval = error;
            }
        });

        return retval;
    }


    /**
     * 통신이 원할 할 경우 서버로 현재 위치를 던지는 함수
     * @param context
     * @param jobj
     * @param instance
     */
    public static void aSyncinit(final Context context, final JSONObject jobj, final DatabaseOpenHelper instance) {
        //DatabaseOpenHelper dbHelper = DatabaseOpenHelper.getInstance(context);
        AQuery aq = new AQuery(context);
        final String jsonString = jobj.toString();
        LogUtil.Log(">>> aSycninit", jsonString);

        String str_url = AppNetworkConstants.Url.ADDRESS_SEND;
       //String str_url = "http://192.168.0.99:3000/beacon/sendgps?jsonStr=";
        //String str_url = "http://bluemobile1.cafe24.com/wp-json/beacon/insert/?jsonStr=";

        Map<String, Object> params = new HashMap<String, Object>();
        String query = null;
        try {
            query = URLEncoder.encode(jsonString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String url = "http://stackoverflow.com/search?q=" + query;
        str_url += query;
        //str_url += json.toString();
        LogUtil.Log(">>>>>> aSyncinit str_url : " + str_url);


        ProgressDialog dialog = new ProgressDialog(context);
        //ProgressBar dialog = new ProgressBar(getApplicationContext());

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(null);
        dialog.setTitle("GPS정보 전송중...");

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


        aq.progress(R.layout.progress);
        aq.ajax(str_url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                super.callback(url, json, status);
                //{"status":"success","insert_id":1,"data":{"user_type":"1","user_key":"787","major":"123","minor":"14587","rssi":"61"}}
                LogUtil.Log(">>>>>> aSyncinit callback json : " + json);
                boolean error = false;
                if (json != null) {
                    if (json.optString("status").equals("success")) {
                        Toast.makeText(context, "전송 완료!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                        //Latitude = "0";
                        //Longitude = "0";
                    } else {
                        Toast.makeText(context, "전송 에러!!", Toast.LENGTH_SHORT).show();
                        error = true;
                        //dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, "서버장애", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                    error = true;
                }

                // 서버통신 중 장애 발생 시 해당 내용 db에 저장한다.
                if (error) {
                    if (jobj != null) {
                        ObjectMapper objectMapper = new ObjectMapper();

                        try {
                            //GpsInfo ginfo = objectMapper.readValue(jsonString, GpsInfo.class);
                            GpsData ginfo = objectMapper.reader().forType(GpsData.class).readValue(jsonString);

                            long cnt = GpsInfoQuery.insertUserInfo(context, instance, ginfo);
                            LogUtil.Log(">>> insertUserInfo result","cnt : "+ Long.toString(cnt));

                        } catch (JsonGenerationException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                    }
                }
            }
        });



    }


    /**
     * 루팅된 폰인지 확
     * @return
     */
    public static boolean isRooted() {
        return findBinary("su");
    }


    /**
     * device에 저장된 계정 binary 확인
     * @param binaryName
     * @return
     */
    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if ( new File( where + binaryName ).exists() ) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }


    private static boolean isSDCardWriteable(Context context) {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }

    public static void exportDB(Context context){ //, String dbpath
        final String DB_NAME = "GPS_Tracker.db";
        //File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        //File data = Environment.getExternalStorageDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        //String currentDBPath = "/data/"+ "com.bluenmobile.ohnaebus" +"/databases/"+DB_NAME;
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpsTracker/";
        String currentDBPath = "/data/user/0/com.hanjaea.locprovider/databases/GPS_Tracker.db";
        //String currentDBPath = dbpath;
        String backupDBPath = DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void backupDatabase(Context context) throws IOException {
        String DB_FILEPATH = "/data/user/0/com.hanjaea.locprovider/databases/GPS_Tracker.db";

        if (isSDCardWriteable(context)) {
            // Open your local db as the input stream
            String inFileName = DB_FILEPATH;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/GPS_Tracker";
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            // Close the streams
            output.flush();
            output.close();
            fis.close();
        }
    }

    public static void sqliteExport(Context context){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/storage/emulated/0/com.hanjaea.locprovider/databases/GPS_Tracker.db";
                String backupDBPath = "GPS_Tracker";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                if(backupDB.exists()){
                    Toast.makeText(context, "DB Export Complete!!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * 로컬 DB에서 조회 된 내용을 json array 로 형변환 시켜서 되돌려 준다.
     * @param context
     * @param cursor
     * @return
     */
    public static List<GpsInfo> getGpsInfoDbQueryList(Context context, Cursor cursor) {
        String retJson = "";

        int dataCnt = cursor.getCount();
        JSONArray array = new JSONArray();
        List<GpsInfo> list = new ArrayList<GpsInfo>();

        if (dataCnt > 0) {
            while (!cursor.isAfterLast()) {
                JSONObject obj = new JSONObject();

                String latitude = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.LATITUDE));            // TEXT 위도
                String longititude = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.LONGITITUDE));       // TEXT 경도
                String up_dt = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.UP_DT));           // TEXT GPS 수신날짜시간
                double distance = 0;

                try {
                    obj.put(GpsTableFeildConstants.LATITUDE, latitude);
                    obj.put(GpsTableFeildConstants.LONGITITUDE, longititude);
                    obj.put(GpsTableFeildConstants.UP_DT, up_dt);
                    array.put(obj);


                    GpsInfo info = new GpsInfo(latitude,longititude,up_dt);
                    list.add(info);

                } catch (Exception e) {
                    LogUtil.Log("JSON Combine", ":::::array Error " + e.toString());
                }
                cursor.moveToNext();
            }
        }


        //jobj.getJSONObject("latitude");
        /*
        try {
            String latitude = jobj.getString("latitude");
            String longitude = jobj.getString("longitude");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        //obj.put("latitude", oLatitude);
        //obj.put("longitude", oLongitude);

        return list;

    }


    public static JSONArray getGpsInfoDbQueryJsonArray(Context context, Cursor cursor) {
        String retJson = "";

        int dataCnt = cursor.getCount();
        JSONArray array = new JSONArray();
        JSONObject jobj = new JSONObject();
        List<GpsInfo> list = new ArrayList<GpsInfo>();

        if (dataCnt > 0) {
            while (!cursor.isAfterLast()) {
                JSONObject obj = new JSONObject();

                String latitude = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.LATITUDE));            // TEXT 위도
                String longititude = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.LONGITITUDE));       // TEXT 경도
                String up_dt = cursor.getString(cursor.getColumnIndex(GpsTableFeildConstants.UP_DT));           // TEXT GPS 수신날짜시간

                try {
                    obj.put(GpsTableFeildConstants.LATITUDE, latitude);
                    obj.put(GpsTableFeildConstants.LONGITITUDE, longititude);
                    obj.put(GpsTableFeildConstants.UP_DT, up_dt);
                    array.put(obj);

                } catch (Exception e) {
                    LogUtil.Log("JSON Combine", ":::::array Error " + e.toString());
                }
                cursor.moveToNext();
            }


            //try{
            //    jobj.put("array_list",array);
            //}catch (JSONException e){
            //    e.printStackTrace();
            //}
        }

        return array;

    }


    /**
     *  DB에 저장된 위치데이터 가져오기
     */
    public static void aSyncinitGet(final Context context, final Handler handler) {
        String str_url = AppNetworkConstants.Url.ADDRESS_GET;
        AQuery aq = new AQuery(context);
        final Message message = handler.obtainMessage() ;


        ProgressDialog dialog = new ProgressDialog(context);
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
                        Toast.makeText(context, "조회 완료!!", Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray jsonArray = json.getJSONArray("data");
                            // fill the message object.
                            message.what = MSG_A;
                            message.obj = jsonArray;

                            // send message object.
                            handler.sendMessage(message);
                            //setMapData(jsonArray);
                        } catch (JSONException e){
                            e.printStackTrace();
                        } catch (Exception e){
                            e.printStackTrace();
                        }


                    }else{
                        Toast.makeText(context, "조회 에러!!", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                        // fill the message object.
                        message.what = MSG_B;

                        // send message object.
                        handler.sendMessage(message);
                    }
                }else{
                    Toast.makeText(context, "서버장애", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                    message.what = MSG_B;

                    // send message object.
                    handler.sendMessage(message);
                }
            }
        });

    }




    /**
     * 네크워크 오류로 현재 위치가 서버로 전송이 안된상태에서 호출 되는 함수
     * @param context
     * @param jsonArray
     * @param location
     */
    public static void aSyncinitArray(final Context context, final JSONArray jsonArray, final Location location, final Handler handler) {
        AQuery aq = new AQuery(context);
        final Message message = handler.obtainMessage() ;
        final String jsonString = jsonArray.toString();
        LogUtil.Log(">>> aSyncinitArray", jsonString);
        String str_url = AppNetworkConstants.Url.ADDRESS_ARRAY;

        JSONObject jobj = new JSONObject();
        try{
            //jobj.put("array",jsonString);
            jobj.put("array",jsonArray);
        }catch (JSONException e){
            e.printStackTrace();
        }

        final String jarrayString = jobj.toString();
        LogUtil.Log(">>> jarrayString", jarrayString);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array_list",jarrayString);
        String query = null;
        try {
            query = URLEncoder.encode(jarrayString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String url = "http://stackoverflow.com/search?q=" + query;
        //str_url += query;
        //str_url += json.toString();
        LogUtil.Log(">>>>>> aSyncinitArray str_url : " + str_url);


        ProgressDialog dialog = new ProgressDialog(context);
        //ProgressBar dialog = new ProgressBar(getApplicationContext());

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(null);
        dialog.setTitle("GPS정보 전송중...");

        aq.progress(R.layout.progress);
        aq.ajax(str_url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                super.callback(url, json, status);
                //{"status":"success","insert_id":1,"data":{"user_type":"1","user_key":"787","major":"123","minor":"14587","rssi":"61"}}
                LogUtil.Log(">>>>>> aSyncinitArray callback json : " + json);
                boolean error = false;
                if (json != null) {
                    if (json.optString("status").equals("success")) {
                        Toast.makeText(context, "DB정보 전송 완료!!", Toast.LENGTH_SHORT).show();
                        //GpsInfoQuery.DelGpsAll(context);
                        //GpsInfoQuery.initSeqReset(context, dbHelper);
                        //serverSend(context, location, dbHelper);

                        message.what = MSG_A;
                        // send message object.
                        handler.sendMessage(message);

                    } else {
                        Toast.makeText(context, "DB 정보 전송 에러!!", Toast.LENGTH_SHORT).show();
                        error = true;
                        message.what = MSG_B;
                        // send message object.
                        handler.sendMessage(message);
                    }
                } else {
                    Toast.makeText(context, "DB 정보 전송시 서버장애", Toast.LENGTH_SHORT).show();
                    error = true;
                    message.what = MSG_B;
                    // send message object.
                    handler.sendMessage(message);
                }
                //retval = error;
            }
        });

    }

    /**
     * 시작 위경도 정보와 마지막 위경도 정보를 이용한 거리계산
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static String calcDistance(double lat1, double lon1, double lat2, double lon2){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1000);
        String result = rslt + " km";
        if(rslt == 0) result = Math.round(ret) +" m";

        return result;
    }

    //두지점(위도,경도) 사이의 거리
    public static int DistanceByDegree(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        double theta, dist;
        int val = 0;
        theta = _longitude1 - _longitude2;
        dist = Math.sin(DegreeToRadian(_latitude1)) * Math.sin(DegreeToRadian(_latitude2)) + Math.cos(DegreeToRadian(_latitude1))
                * Math.cos(DegreeToRadian(_latitude2)) * Math.cos(DegreeToRadian(theta));
        dist = Math.acos(dist);
        dist = RadianToDegree(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환
        val = (int) Math.round(dist);

        return val;
    }

    //안드로이드 - 두지점(위도,경도) 사이의 거리
    public static int DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);
        int val = (int) Math.round(distance);

        return val;
    }

    //degree->radian 변환
    public static double DegreeToRadian(double degree){
        return degree * Math.PI / 180.0;
    }

    //randian -> degree 변환
    public static double RadianToDegree(double radian){
        return radian * 180d / Math.PI;
    }




}
