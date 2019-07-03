package com.hanjaea.locprovider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.hanjaea.locprovider.utils.LogUtil;
import com.hanjaea.locprovider.utils.Utils;
import com.hanjaea.locprovider.model.GpsData;
import com.hanjaea.locprovider.model.GpsInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GpsInfoQuery {

    /**
     * sqlite sequence 초기화
     * @param context
     */
    public static int initSeqReset(Context context, DatabaseOpenHelper instance){
        //SQLiteDatabase db = DatabaseOpenHelper.getInstance(context)
        //        .getReadableDatabase();
        SQLiteDatabase db = instance.getWritableDatabase();

        int cnt = 0;
        Cursor cursor = null;

        ContentValues cv = new ContentValues();
        cv.put("seq", "0");

        db.beginTransaction();
        try {
            cnt = db.update("SQLITE_SEQUENCE", cv, null, null);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //LogUtil.Log("===insertStationInfo SQLException", e.toString());
            cnt = -1;
        } finally {
            db.endTransaction();
            //if (db != null) {
            //    db.close();
            //}
        }


        return cnt;
    }


    /**
     * 통신 장애시 로컬 db에 수신된 GPS 정보를 저장
     * @param context
     * @param instance
     * @param gpsinfo
     * @return
     */
    public static long insertUserInfo(Context context, DatabaseOpenHelper instance, GpsData gpsinfo){
        //SQLiteDatabase db = DatabaseOpenHelper.getInstance(context)
        //        .getWritableDatabase();
        SQLiteDatabase db = instance.getWritableDatabase();

        Cursor cursor = null;
        long cnt = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        db.beginTransaction();

        try {

            //for(int i=0; i < userdata.size(); i++){
                ContentValues values = new ContentValues();
                //UserInfoData data = userdata.get(i);

                values.put(GpsTableFeildConstants.LATITUDE, gpsinfo.getLatitude());         // TEXT 위도
                values.put(GpsTableFeildConstants.LONGITITUDE, gpsinfo.getLongitude());     // TEXT 경도
                //values.put(GpsTableFeildConstants.UP_DT, gpsinfo.getUp_dt());               // TEXT GPS 수신 날짜시간
                values.put(GpsTableFeildConstants.UP_DT, dateFormat.format(date));
                //values.put("REGDT", dateFormat.format(date));


                /*
            values.put(GpsTableFeildConstants.LATITUDE, gpsinfo.getLatitude());         // TEXT 위도
            values.put(GpsTableFeildConstants.LONGITITUDE, gpsinfo.getLongitude());     // TEXT 경도
            values.put(GpsTableFeildConstants.UP_DT, gpsinfo.getUp_dt());               // TEXT GPS 수신 날짜시간
            //values.put(GpsTableFeildConstants.UP_DT, dateFormat.format(date));
            */

                cnt = db.insertOrThrow(GpsTableFeildConstants.TABLE_NAME, null,
                        values);
            //}

            db.setTransactionSuccessful();

        } catch (SQLException e) {
            LogUtil.Log("===insertUserInfo SQLException", e.toString());
            cnt = -1;
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return cnt;
    }

    /**
     * 네트워크 장애시 서버에 던지지 못했던 GPS정볼를 DB에서 조회해서 json array 로 만들어 리턴 한다.
     * @param context
     * @param instance
     * @return
     */
    public static JSONArray getGpsInfo(Context context, DatabaseOpenHelper instance) {
        //SQLiteDatabase db = DatabaseOpenHelper.getInstance(context)
        //        .getWritableDatabase();
        SQLiteDatabase db = instance.getReadableDatabase();

        int cnt = 0;
        Cursor cursor = null;
        GpsInfo retJson = null;
        JSONArray array = new JSONArray();
        JSONObject jobj = new JSONObject();

        try {
            if (db != null) {
                String sql = "SELECT " +
                        GpsTableFeildConstants.LATITUDE + ", " +
                        GpsTableFeildConstants.LONGITITUDE  + ", " +
                        GpsTableFeildConstants.UP_DT +
                        " FROM " + GpsTableFeildConstants.TABLE_NAME;
                LogUtil.Log(">>>> getGpsInfo sql : " + sql);

                cursor = db.rawQuery(sql, null);
                //cursor.moveToFirst();

                if(cursor != null && cursor.getCount()>0){
                    cursor.moveToFirst();
                   array = Utils.getGpsInfoDbQueryJsonArray(context, cursor);
                    //jobj = Utils.getGpsInfoDbQueryJsonArray(context, cursor);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            //LogUtil.Log("===insertStationInfo SQLException", e.toString());
        } finally {
            if(cursor != null){
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return array;
    }


    /**
     * 서버에 미 전송된 데이터 조회
     * @param context
     * @param instance
     * @return
     */
    public static List<GpsInfo> getGpsInfoList(Context context, DatabaseOpenHelper instance) {
        //SQLiteDatabase db = DatabaseOpenHelper.getInstance(context)
        //        .getWritableDatabase();
        SQLiteDatabase db = instance.getReadableDatabase();
        List<GpsInfo> list = new ArrayList<GpsInfo>();


        int cnt = 0;
        Cursor cursor = null;
        GpsInfo retJson = null;
        JSONArray array = new JSONArray();

        try {
            if (db != null) {
                String sql = "SELECT * FROM " + GpsTableFeildConstants.TABLE_NAME;

                LogUtil.Log(">>>> getGpsInfoList sql : " + sql);

                cursor = db.rawQuery(sql, null);
                //cursor.moveToFirst();

                if(cursor != null && cursor.getCount()>0){
                    cursor.moveToFirst();
                    list = Utils.getGpsInfoDbQueryList(context, cursor);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            //LogUtil.Log("===insertStationInfo SQLException", e.toString());
        } finally {
            //if(cursor != null){
            //    cursor.close();
            //}
            //if (db != null) {
            //    db.close();
            //}
        }

        return list;
    }


    /**
     * 테이블에 등록된 모든 내용 삭제
     * @param context
     * @return
     */
    public static int DelGpsAll(Context context){
        SQLiteDatabase db = DatabaseOpenHelper.getInstance(context)
                .getWritableDatabase();

        int cnt = 0;

        //ContentValues cv = new ContentValues();
        //cv.put(StationInfoConstants.USE_YN, val);

        db.beginTransaction();
        try {
            //cnt = db.update(StationInfoConstants.TABLE_NAME, null, null, null);
            cnt = db.delete(GpsTableFeildConstants.TABLE_NAME, null, null);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            //LogUtil.Log("===insertStationInfo SQLException", e.toString());
            cnt = -1;
        } finally {
            db.endTransaction();
            //if (db != null) {
            //    db.close();
            //}
        }

        return cnt;

    }






}
