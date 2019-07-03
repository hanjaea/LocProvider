package com.hanjaea.locprovider;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hanjaea.locprovider.utils.LogUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BuildConfig;
import butterknife.ButterKnife;
import butterknife.OnClick;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private List<String > listProviders;
    private TextView tvGpsEnable, tvNetworkEnable, tvPassiveEnable, tvGpsLatitude, tvGpsLongitude;
    private TextView tvNetworkLatitude, tvNetworkLongitude, tvPassiveLatitude, tvPassivekLongitude;
    private String TAG = "LocationProvider";
    private Button btnSendActivity;
    private EditText editTextInput;

    public BackgroundService gpsService;
    public boolean mTracking = false;
    /**
     * 루팅체크
     */
    private boolean isRootingFlag = false;
    private Context mContext;
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "";
    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";
    public String[] RootFilesPath = new String[] { ROOT_PATH + ROOTING_PATH_1, ROOT_PATH + ROOTING_PATH_2, ROOT_PATH + ROOTING_PATH_3, ROOT_PATH + ROOTING_PATH_4 };

    @BindView(R.id.btn_start_tracking)
    Button btnStartTracking;

    @BindView(R.id.btn_stop_tracking)
    Button btnStopTracking;

    //@BindView(R.id.txt_status)
    //TextView txtStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (!isRootingFlag) {
            isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
        }

        mContext = this;

        LogUtil.d(TAG, "isRootingFlag = " + isRootingFlag);
        if (isRootingFlag && LogUtil.ROOTING == true) {
            /* 종료 핸들러 */
            Handler killHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message m) {
                            LogUtil.d(TAG, "sam end handler : " + m);
                            switch (m.what) {
                                case 9999: {
                                    LogUtil.d(TAG, "handler");
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                    //am.killBackgroundProcesses(getPackageName());
                                    am.killBackgroundProcesses(getPackageName());
                                    System.exit(0);
                                    System.gc();
                                    break;
                                }
                            }
                        }
                    };
                    //sam.end(handler);
                    Handler killTimeoutHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            LogUtil.d(TAG, "killTimeoutHandler");
                            try {
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                am.killBackgroundProcesses(getPackageName());
                                System.exit(0);
                                System.gc();
                            } catch (Exception e) {

                            }
                        }
                    };
                    killTimeoutHandler.sendEmptyMessageDelayed(0, 1000);
                    // kill();
                    return false;
                }
            });
            MocaDialog dialog = new MocaDialog(mContext);
            dialog.setTitle(R.string.lbl_notify);
            dialog.setMessage(R.string.no_rooting);
            dialog.setActionButton(R.string.btn_comfirm, killHandler);
            dialog.show();
            return;
        }

        /*
        final Intent intent = new Intent(this.getApplication(), BackgroundService.class);
        this.getApplication().startService(intent);
//        this.getApplication().startForegroundService(intent);
        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        */

        tvGpsEnable = (TextView)findViewById(R.id.tvGpsEnable);
        tvNetworkEnable = (TextView)findViewById(R.id.tvNetworkEnable);
        tvPassiveEnable = (TextView)findViewById(R.id.tvPassiveEnable);
        tvGpsLatitude = (TextView)findViewById(R.id.tvGpsLatitude);
        tvGpsLongitude = (TextView)findViewById(R.id.tvGpsLongitude);
        tvNetworkLatitude = (TextView)findViewById(R.id.tvNetworkLatitude);
        tvNetworkLongitude = (TextView)findViewById(R.id.tvNetworkLongitude);
        tvPassiveLatitude = (TextView)findViewById(R.id.tvPassiveLatitude);
        tvPassivekLongitude = (TextView)findViewById(R.id.tvPassivekLongitude);
        btnSendActivity = (Button)findViewById(R.id.btnMapAcivivity);
        btnSendActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Option option = new Option("010xxxxxxxx","서울특별시xxxx"); /*송신 할 클래스*/

                //double networkLat = Double.parseDouble(tvNetworkLatitude.getText().toString());
                //double networkLong = Double.parseDouble(tvNetworkLongitude.getText().toString());
                //double[] array = {networkLat,networkLong}; /*송신 할 배열*/

                //String networkLat = tvGpsLatitude.getText().toString();
                //String networkLong = tvGpsLongitude.getText().toString();

                //String networkLat = tvNetworkLatitude.getText().toString();
                //String networkLong = tvNetworkLongitude.getText().toString();

                //String[] array = {networkLat,networkLong}; /*송신 할 배열*/

                //Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                Intent intent = new Intent(getApplicationContext(), DaumMainActivity.class);

                //intent.putExtra("networkLat",networkLat); /*송신*/
                //intent.putExtra("networkLong",networkLong);
                //intent.putExtra("array",array);
                //intent.putExtra("class",option);

                startActivity(intent);
            }
        });

        editTextInput = findViewById(R.id.edit_text_input);

        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvGpsLatitude.setText(Double.toString(lat ));
            tvGpsLongitude.setText((Double.toString(lng)));
        }
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvNetworkLatitude.setText(Double.toString(lat ));
            tvNetworkLongitude.setText((Double.toString(lng)));
        }

        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLongitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
            tvPassiveLatitude.setText(":: " + Double.toString(lat ));
            tvPassivekLongitude.setText((":: " + Double.toString(lng)));
        }

        listProviders = locationManager.getAllProviders();
        boolean [] isEnable = new boolean[3];
        for(int i=0; i<listProviders.size();i++) {
            if(listProviders.get(i).equals(LocationManager.GPS_PROVIDER)) {
                isEnable[0] = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                tvGpsEnable.setText(": " +  String.valueOf(isEnable[0]));

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else if(listProviders.get(i).equals(LocationManager.NETWORK_PROVIDER)) {
                isEnable[1] = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                tvNetworkEnable.setText(": " +  String.valueOf(isEnable[1]));

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else if(listProviders.get(i).equals(LocationManager.PASSIVE_PROVIDER)) {
                isEnable[2] = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
                tvPassiveEnable.setText(": " +  String.valueOf(isEnable[2]));

                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
            }

        }

        Log.d(TAG, listProviders.get(0) + '/' + String.valueOf(isEnable[0]));
        Log.d(TAG, listProviders.get(1) + '/' + String.valueOf(isEnable[1]));
        Log.d(TAG, listProviders.get(2) + '/' + String.valueOf(isEnable[2]));

    }


    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = 0.0;
        double longitude = 0.0;

        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvGpsLatitude.setText(Double.toString(latitude ));
            tvGpsLongitude.setText((Double.toString(longitude)));
            Log.d(TAG + " GPS : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }

        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvNetworkLatitude.setText(Double.toString(latitude ));
            tvNetworkLongitude.setText((Double.toString(longitude)));
            Log.d(TAG + " NETWORK : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }

        if(location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvPassiveLatitude.setText(": " + Double.toString(latitude ));
            tvPassivekLongitude.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " PASSIVE : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }

    }


    public void startService(View v) {
        String input = editTextInput.getText().toString();

        //Intent serviceIntent = new Intent(this, ExampleService.class);
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("inputExtra", input);

        ContextCompat.startForegroundService(this, serviceIntent);
        //startService(serviceIntent);
    }

    public void stopService(View v) {
        //Intent serviceIntent = new Intent(this, ExampleService.class);
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }



    @OnClick(R.id.btn_start_tracking)
    public void startLocationButtonClick() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        gpsService.startTracking();
                        mTracking = true;
                        toggleButtons();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @OnClick(R.id.btn_stop_tracking)
    public void stopLocationButtonClick() {
        mTracking = false;
        gpsService.stopTracking();
        toggleButtons();
    }

    private void toggleButtons() {
        btnStartTracking.setEnabled(!mTracking);
        btnStopTracking.setEnabled(mTracking);
        //txtStatus.setText( (mTracking) ? "TRACKING" : "GPS Ready" );
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("BackgroundService")) {
                gpsService = ((BackgroundService.LocationServiceBinder) service).getService();
                btnStartTracking.setEnabled(true);
                //txtStatus.setText("GPS Ready");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    /**
     * 루팅파일 의심 Path를 가진 파일들을 생성 한다.
     */
    private File[] createFiles(String[] sfiles) {
        File[] rootingFiles = new File[sfiles.length];
        for (int i = 0; i < sfiles.length; i++) {
            rootingFiles[i] = new File(sfiles[i]);
        }
        return rootingFiles;
    }

    /**
     * 루팅파일 여부를 확인 한다.
     */
    private boolean checkRootingFiles(File... file) {
        boolean result = false;

        for (File f : file) {
            if (f != null && f.exists() && f.isFile()) {
                result = true;
                break;
            } else {
                result = false;
            }
        }
        return result;
    }



    private void kill(){
        Handler timer = new Handler(); //Handler 생성
        timer.postDelayed(new Runnable(){ //2초후 쓰레드를 생성하는 postDelayed 메소드

            public void run(){
                //intent 생성
                //Intent intent = new Intent(MainActivity.this, NFC_WAIT.class);
                //startActivity(intent); //다음 액티비티 이동
                finish(); // 이 액티비티를 종료
            }
        }, 2000); //2000은 2초를 의미한다.

    }
}
