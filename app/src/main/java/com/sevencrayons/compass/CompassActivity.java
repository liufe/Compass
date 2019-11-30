package com.sevencrayons.compass;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elvishew.xlog.XLog;

import java.io.File;
import java.util.List;


public class CompassActivity extends AppCompatActivity {

    ///location
    public static final int LOCATION_CODE = 301;
    private static final String TAG = "CompassActivity";
    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //不为空,显示地理位置经纬度
                XLog.i(location.getLongitude() + " " + location.getLatitude() + "");
                longitude.setText(getString(R.string.sotw_longitude)+":"+location.getLongitude());
                latitude.setText(getString(R.string.sotw_latitude)+":"+location.getLatitude());
            }
        }
    };
    private Compass compass;
    private ImageView arrowView;
    private TextView sotwLabel, horizontalLabel, verticalLabel, longitude, latitude;
    private float currentAzimuth;
    private SOTWFormatter sotwFormatter;
    private LocationManager locationManager;
    private String locationProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        sotwFormatter = new SOTWFormatter(this);

        arrowView = findViewById(R.id.main_image_hands);
        sotwLabel = findViewById(R.id.sotw_label);
        horizontalLabel = findViewById(R.id.horizontal_label);
        verticalLabel = findViewById(R.id.vertical_label);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        setupCompass();
        getLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        XLog.i("start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        XLog.i("stop compass");
        compass.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //  Toast.makeText(this, "申请权限", Toast.LENGTH_LONG).show();
                    try {
                        List<String> providers = locationManager.getProviders(true);
                        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//如果是Network
                            locationProvider = LocationManager.NETWORK_PROVIDER;
                        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
//如果是GPS
                            locationProvider = LocationManager.GPS_PROVIDER;
                        }
//监视地理位置变化
                        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if (location != null) {
                            XLog.i("Permissions granted:     longitude" + location.getLongitude() + " latitude:" + location.getLatitude() + "");
                            longitude.setText(getString(R.string.sotw_longitude)+":"+location.getLongitude());
                            latitude.setText(getString(R.string.sotw_latitude)+":"+location.getLatitude());
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    XLog.i("Permissions denied");
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
//监视地理位置变化
                locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
//输入经纬度
    XLog.i("longitude" + location.getLongitude() + " latitude:" + location.getLatitude() + "");
                    Toast.makeText(this, location.getLongitude() + " " + location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                    longitude.setText(getString(R.string.sotw_longitude)+":"+location.getLongitude());
                    latitude.setText(getString(R.string.sotw_latitude)+":"+location.getLatitude());
                }
            }
        } else {
//监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                //不为空,显示地理位置经纬度
                XLog.i("longitude" + location.getLongitude() + " latitude:" + location.getLatitude() + "");
                longitude.setText(getString(R.string.sotw_longitude)+":"+location.getLongitude());
                latitude.setText(getString(R.string.sotw_latitude)+":"+location.getLatitude());
            }
        }
    }

    private void setupCompass() {
        compass = new Compass(this);
        Compass.CompassListener cl = getCompassListener();
        compass.setListener(cl);
    }

    ///转动指针
    private void adjustArrow(float azimuth) {

        XLog.i("will set rotation from " + currentAzimuth + " to "
                + azimuth);
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    ///设置方向角
    private void adjustSotwLabel(float azimuth) {
        String tempSotwLabel = sotwFormatter.format(azimuth);
        XLog.i("sotw:" + tempSotwLabel);
        sotwLabel.setText(tempSotwLabel);
    }

    ///设置水平角度
    private void adjustHorizontalLabel(String horizontal) {
        XLog.i("horizontal:" + horizontal);
        horizontalLabel.setText(getString(R.string.sotw_horizontal) + ":" + horizontal);
    }

    ///设置垂直角度
    private void adjustVerticalLabel(String vertical) {
        XLog.i("vertical:" + vertical);
        verticalLabel.setText(getString(R.string.sotw_vertical) + ":" + vertical);
    }

    ///获取位置回调
    private Compass.CompassListener getCompassListener() {
        return new Compass.CompassListener() {
            @Override
            public void onNewAzimuth(final float azimuth, final String horizontal, final String vertical) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustArrow(azimuth);
                        adjustSotwLabel(azimuth);
                        adjustHorizontalLabel(horizontal);
                        adjustVerticalLabel(vertical);
                    }
                });
            }
        };
    }

    /**
     * history view
     *
     * @param view
     */
    public void goHistory(View view) {
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }

}
