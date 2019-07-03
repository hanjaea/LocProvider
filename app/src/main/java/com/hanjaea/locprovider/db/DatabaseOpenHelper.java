package com.hanjaea.locprovider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.hanjaea.locprovider.utils.LogUtil;

import java.io.File;

/**
 * 생성자 : 한재아
 * 설명 : 앱 내 데이터베이스 생성, 업그레이드
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    protected static final String DB_NAME = "GPS_Tracker.db";
    protected static int DB_VERSION = 1;
    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
    private static boolean EXTERNAL_MODE = true; // 내부 앱 영역의 DB = false, SD 영역의 DB = true
    private static DatabaseOpenHelper _instance = null; // Helper의 단일 인스턴스
    private Context mContext;

    static final String EXTERNAL_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    //static final String APP_ROOR_DIR = EXTERNAL_ROOT_DIR + File.separator + "bluenmobile";
    //static final String DATABASE_DIR = APP_ROOR_DIR + File.separator	+ "databases";
    static final String APP_ROOR_DIR = EXTERNAL_ROOT_DIR + File.separator + "hanjaea";
    static final String DATABASE_DIR = APP_ROOR_DIR + File.separator	+ "databases";

    static final String TBL_STATION_LOC_INFO = "TBL_STATION_LOC_INFO";
    static final String TBL_USER_INFO = "TBL_STATION_LOC_INFO";


    /**
     * <pre>
     * 데이터베이스 객체 생성자
     * </pre>
     *
     * @param context
     *            컨텍스트
     */
    public DatabaseOpenHelper(Context context) {
        // TODO Auto-generated constructor stub
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DB_VERSION=" + Integer.toString(DB_VERSION));
        mContext = context;
    }

    /**
     * <pre>
     * DB가 최초로 생성될때 호출되는 메소드이며 테이블을 생성한다.
     * </pre>
     *
     * @param db
     *            안드로이드폰내 DB파일 명칭
     * @return void
     */
    //@Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //db.execSQL(CREATE_TB_STATION_LOC_INFO);
        //db.execSQL(CREATE_TB_USER_INFO);

        createGPSTable(db);
        //createStationTable(db);
        //createBoardingTable(db);
        //createBoardingTempTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        LogUtil.Log(TAG," >>>>>>> onUpgrade ~~~ oldVersion : " + oldVersion + " newVersion : " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + GpsTableFeildConstants.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + UserInfoConstants.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + BoardingConstants.TABLE_NAME);
        this.onCreate(db);
    }

    /**
     * # 탑승자명단 테이블
     * UUID      고유번호
     * MAJOR     원생분류코드로 사용 할 필드
     * MINOR     원생분류코드로 사용 할 필드
     * NO        명찰 NO
     */
    /*
    public static final String CREATE_TB_USER_INFO =
            " CREATE TABLE [TBL_USER_INFO] ( "
                    +			"[UUID]  VARCHAR2(50), "
                    +			"[MAJOR] VARCHAR2(50), "
                    +			"[MINOR] VARCHAR2(50), "
                    +			"[NO] VARCHAR2(50), "
                    +			"[REGDT] VARCHAR2(20));";
    */


    /**
     * 정류장위치정보
     * LAN   위도
     * LON   경도
     * RADI  정류장 인식 반경거리
     * ORD   운행순서
     * SNAME 정류장명
     */
    /*
    public static final String CREATE_TB_STATION_LOC_INFO =
            " CREATE TABLE [TBL_LOC_INFO] ( "
                    +			"[LAN] VARCHAR2(50), "
                    +			"[LON] VARCHAR2(50), "
                    +			"[RADI] VARCHAR2(50), "
                    +			"[ORD] VARCHAR2(50), "
                    +			"[SNAME] VARCHAR2(50)";
    */


    /**
     * 버스상태정보 테이블
     * STATION_CODE  정류장코드
     * BUS_STEAT     근접/정차/출발
     */
    /*
    public static final String CREATE_TB_BUS_STATE =
            " CREATE TABLE [TBL_BUS_STATE] ( "
                    +			"[STATION_CODE]  VARCHAR2(50), "
                    +			"[BUS_STEAT] VARCHAR2(5) ";
    */

    /**
     * 버스위치 정보 테이블
     * SPEED  버스속도
     * LAN    위도
     * LON    경도
     * CYCLE  전송주기
     */
    /*
    public static final String CREATE_TB_LOC_INFO =
            " CREATE TABLE [TBL_LOC_INFO] ( "
                    +			"[SPEED]  REAL, "
                    +			"[LAN] VARCHAR2(50), "
                    +			"[LON] VARCHAR2(50), "
                    +			"[CYCLE] VARCHAR2(20));";
    */

    /**
     * 탑승자승하차정보 테이블
     * SPEED  명찰NO
     * LAN    승차/하차
     * LON    승/하차 시간
     */
    /*
    public static final String CREATE_TB_TAKE_OUT_INFO =
            " CREATE TABLE [TBL_TAKE_OUT_INFO] ( "
                    +			"[NO]  VARCHAR2(50), "
                    +			"[TO] VARCHAR2(50), "
                    +			"[TO_TIME] VARCHAR2(50) ";
    */


    /**
     * 원생정보 테이블
     * @param db    id, latitude, longitude, up_dt
     */
    private void createGPSTable(SQLiteDatabase db) {
        String userInfoCreateSQL = "CREATE TABLE " + GpsTableFeildConstants.TABLE_NAME + "(" +
                GpsTableFeildConstants._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GpsTableFeildConstants.LATITUDE                + " TEXT NOT NULL , " +          // TEXT 위도
                GpsTableFeildConstants.LONGITITUDE             + " TEXT NOT NULL , " +          // TEXT 경도
                GpsTableFeildConstants.UP_DT                   + " TEXT NOT NULL " +          // TEXT GPS 수집 후 등록일
                " );";
        Log.d(TAG, "TABLE CREATION createGPSTableSQL " + userInfoCreateSQL);
        db.execSQL(userInfoCreateSQL);
    }

    /**
     * <pre>
     * 데이터베이스 파일 저장 위치 경로 획득
     * </pre>
     *
     * @return
     */
    public static String getDatabaseFilePath() {
        if (EXTERNAL_MODE) {
            File dbFile = null;
            // Logger.d(TAG, "EXTERNAL DB MODE");

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                dbFile = new File(DATABASE_DIR + File.separator + DB_NAME);
                return dbFile.getAbsolutePath();
            } else {
                // Logger.d(TAG, "Not mount SDCARD");
                return new String("");
            }

        } else {
            // Logger.d(TAG, "INTERNAL DB MODE");
            return DB_NAME;
        }
    }

    /**
     * <pre>
     * SingleTon pattern으로 정의된 DatabaseHelper getter. 어플리케이션에서 단 하나의 DBHelper에서 Dagabase를 얻어와서 써야 Tracsaction 충돌이 일어나지 않는다.
     * (상이한 Helper에서 생성된 DB들은 서로 Transaction을 간섭하여 프로그램 오류를 발생시키므로 반드시 SingleTon을 유지하여야 한다.)
     * </pre>
     *
     * @param context
     *            안드로이드 컨텍스트
     * @return DatabaseOpenHelper _instance 데이터 베이스 인스턴스
     */
    public synchronized static DatabaseOpenHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new DatabaseOpenHelper(context.getApplicationContext());
        }
        return _instance;
    }


    /**
     * <pre>
     * 기존 DB Table을 제거하고 재생성
     * </pre>
     *
     * @param
     * @return void
     */
    public void initializeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        cleanUpDB(db);
        this.onCreate(db);
    }


    /**
     * <pre>
     * DB클린 메소드, 모든 테이블과 Trigger를 삭제하여 초기 상태로 만든다.
     * </pre>
     *
     * @param db
     *            데이터베이스
     * @return void
     */
    private void cleanUpDB(SQLiteDatabase db) {
    }

    /**
     * <pre>
     * DB 파일의 물리적인 삭제 기능
     * </pre>
     *
     * @param context
     *            컨텍스트
     */
    public static void removeDatabaseFile(Context context) {
        LogUtil.Log(TAG, "====DatabaseOpenHelper ======removeDatabaseFile");
        LogUtil.Log(TAG, "====getDatabaseFilePath======================== : " + getDatabaseFilePath());

        boolean ret = context.getApplicationContext().deleteDatabase(DB_NAME);
        if (ret == true) {
            LogUtil.Log("====SQLite 파일 삭제 ======", "성공 -- 버전 업데이트시");
        } else {
            LogUtil.Log("====SQLite 파일 삭제 ======", "실패 -- 최초 실행인 경우");
        }
    }


}
