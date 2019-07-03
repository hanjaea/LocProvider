package com.hanjaea.locprovider.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocPreference {
    private static final String TAG = "LOC_PREFER_DATA" ;
    private static final String PREFS_LAT__DATA = "LOC_LAT_DATA";
    private static final String PREFS_LONG_DATA = "LOC_LONG_DATA";
    private static final String PREFS_IS_SERVICE = "LOC_IS_SERVICE";
    //private static final String PREFS_EVENT_CLOSETYPE = "EVENT_CLOSE_YPE" ;
    //private static final String PREFS_EVENT_CLOSEDATE = "EVENT_CLOSE_DATE" ;
    //private static final String PREFS_EVENT_TITLE = "EVENT_TITLE" ;
    //private static final String PREFS_EVENT_ID = "EVENT_ID" ;
    private SharedPreferences mPrefs ;

    /*
    AppNetworkConstants.Latitude = lat;
    AppNetworkConstants.Longitude = ong;
     */

    public LocPreference(Context context) {
        mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /*
     * 	 위도정보
     */
    public void set_Latitude (String data) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREFS_LAT__DATA, data);
        editor.commit();
    }

    public String get_Latitude() {
        String str = null ;
        str = mPrefs.getString(PREFS_LAT__DATA, "");
        return str ;
    }

    /*
     * 	 경도정보
     */
    public void set_Longitude(String data) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREFS_LONG_DATA, data);
        editor.commit();
    }

    public String get_Longitude() {
        String str = null ;
        str = mPrefs.getString(PREFS_LONG_DATA, "");
        return str ;
    }



    //서비스 실해 여부
    public void set_isService ( boolean data ) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREFS_IS_SERVICE, data);
        editor.commit();
    }

    public boolean get_isService ( ) {
        boolean str = false ;
        str = mPrefs.getBoolean(PREFS_IS_SERVICE, false);
        return str ;
    }

    /*
    // 이벤트 종료 타입에 보여주지 않기로 한 날짜 값
    public void set_CloseDate ( long data ) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong(PREFS_EVENT_CLOSEDATE, data);
        editor.commit();
    }

    public long get_CloseDate ( ) {
        long str = 0 ;
        str = mPrefs.getLong(PREFS_EVENT_CLOSEDATE, 0);
        return str ;
    }
    */
}
