package com.hanjaea.locprovider;

public class AppNetworkConstants {

    private static String HOST = "http://15.164.100.147:3000";
    private static String API_PREFIX = "/gps/sendgps?jsonStr=";
    private static String API_GET_PREFIX = "/gps/getgps";
    private static String API_ARRAY_PREFIX = "/gps/sendgpsArray";

    //public static String Latitude = "1.1";
    //public static String Longitude = "1.1";

    public static class Url {
        // API ROOT<사용중>
        public static final String ADDRESS_SEND = HOST + API_PREFIX;        // 위,경도 정보 서버로 던지기
        public static final String ADDRESS_GET = HOST + API_GET_PREFIX;     // 저장된 위,경도 정보 가져오기
        public static final String ADDRESS_ARRAY = HOST + API_ARRAY_PREFIX; // 로컬 DB에 저장된 위,경도 정보 서버로 던지기

        /*
         * COMMON
         */
        // public static final String URL_DATA_SYNC = ADDRESS +
        // "/data_sync.php";
        //public static final String URL_DATA_SYNC = API_PREFIX2 + "/data_sync_20150921.php";
    }
}
