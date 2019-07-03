package com.hanjaea.locprovider.utils;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.util.Log;

/**
 *************************************************************************
 * @source  : LogUtil.java
 * @desc    : app 내 로그 출력관련 클래스
 *------------------------------------------------------------------------*
 * CSR No             DATE         AUTHOR  DESCRIPTION                    *
 *------------------------------------------------------------------------*
 * C20130802_81263    2016.04.05   한재아 
 * -----------------------------------------------------------------------
 * Copyright(c) 2016 BluenMobile,  All rights reserved.
 *************************************************************************
 */
public class LogUtil {

	public static final int MODE_DEBUG = 0;
	public static final int MODE_PUBLISH = 1;
	public static final int MODE_HAREX = 2;
	public static final int MODE;
	public static final int DEBUG_LOG_LEVEL;
	public static final int PUBLISH_LOG_LEVEL;
	public static final int HAREX_LOG_LEVEL;
	public static int LOG_LEVEL;
	public static final boolean ROOTING;

	static {
		MODE = MODE_PUBLISH;
		//MARKET = MARKET_GOOGLE;
		//USIM = false;
		ROOTING = true;
		DEBUG_LOG_LEVEL = android.util.Log.ERROR;
		PUBLISH_LOG_LEVEL = android.util.Log.ERROR;
		HAREX_LOG_LEVEL = android.util.Log.VERBOSE;
		LOG_LEVEL = MODE;

//		LOG_LEVEL = MODE == MODE_DEBUG ? DEBUG_LOG_LEVEL
//							: PUBLISH_LOG_LEVEL;
		if(MODE == MODE_DEBUG) {
			LOG_LEVEL = DEBUG_LOG_LEVEL;
		} else if(MODE == MODE_PUBLISH) {
			LOG_LEVEL = PUBLISH_LOG_LEVEL;
		} else if(MODE == MODE_HAREX) {
			LOG_LEVEL = HAREX_LOG_LEVEL;
		}
	}

	/**
	 * 로그 태그명
	 * @since 1.0.0
	 */
	private static boolean TYPE = true;	//true: 로그활성화  false:비활성화
	public static String TAG = "bluenmobile";

	public static void Log(String log)
	{
		Log(log , true);
	}

	public static void Log(String log, boolean flag)
	{
		if(!TYPE) return;
		if(flag)
		{
			if (log == null || log.equals("")) log = " ";
			Log.d(TAG, log);
		}
	}

	public static void Log(String tag, String log)
	{
		if(!TYPE) return;
		Log(tag, log, true);
	}

	public static void Log(String tag, String log, boolean flag)
	{
		if(!TYPE) return;
		if(true)
		{
			if (log == null || log.equals("")) log = " ";
			Log.d(TAG + " " + tag, log);
		}
	}

	//클래스의 변수명과 값을 로그로 찍어준다
	public static void Log(Object obj) {
		if(!TYPE) return;

		Field[] fields = obj.getClass().getFields();
		for(Field field : fields) {
			try {
				Log(field.getName(), String.valueOf(field.get(obj)) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	//클래스의 변수명과 값을 로그로 찍어준다
	public static <E> void Log(ArrayList<E> arrList) {
		if(!TYPE) return;

		LogUtil.Log("makeLogThisList");

		Field[] fields = null;

		LogUtil.Log(arrList.size() + "");

//				for ( E element : arrList) {
		for ( int i = 0; i < arrList.size(); i++ ) {
			fields = arrList.get(i).getClass().getFields();
			for(Field field : fields) {
				try {
					Log(field.getName(), String.valueOf(field.get(arrList.get(i))) );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void v(String tag, String msg) {
		v(tag, msg, null);
	}

	public static void v(String tag, String msg, Throwable t) {
		if (LOG_LEVEL <= android.util.Log.VERBOSE) {
			if (t != null) {
				android.util.Log.v(tag, msg, t);
			} else {
				android.util.Log.v(tag, msg);
			}
		}
	}

	public static void d(String tag, String msg) {
		d(tag, msg, null);
	}

	public static void d(String tag, String msg, Throwable t) {
		//if (LOG_LEVEL <= android.util.Log.DEBUG)
		{
			if (t != null) {
				android.util.Log.d(tag, msg, t);
			} else {
				android.util.Log.d(tag, msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		i(tag, msg, null);
	}

	public static void i(String tag, String msg, Throwable t) {
		if (LOG_LEVEL <= android.util.Log.INFO) {
			if (t != null) {
				android.util.Log.i(tag, msg, t);
			} else {
				android.util.Log.i(tag, msg);
			}
		}
	}

	public static void w(String tag, String msg) {
		w(tag, msg, null);
	}

	public static void w(String tag, String msg, Throwable t) {
		if (LOG_LEVEL <= android.util.Log.WARN) {
			if (t != null) {
				android.util.Log.w(tag, msg, t);
			} else {
				android.util.Log.w(tag, msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		e(tag, msg, null);
	}

	public static void e(String tag, String msg, Throwable t) {
		if (LOG_LEVEL <= android.util.Log.ERROR) {
			if (t != null) {
				android.util.Log.e(tag, msg, t);
			} else {
				android.util.Log.e(tag, msg);
			}
		}
	}

}
