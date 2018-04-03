package com.tql.lbs_based_amap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;
//import com.amap.api.location.AMapLocationClientOption;

public class MainActivity extends AppCompatActivity //implements View.OnClickListener
{

    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 1;

    Button start;
    Button stop;
    TextView mTextView;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明定位回调监听器
    //public AMapLocationListener mLocationListener = new AMapLocationListener();

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mTextView.setText("ok, get result");

                    String location_result = null;
                    location_result = String.format( "location type:%d,\nlatitude:%06f,\nlongitude:%06f, \n" +
                                    "address:%s,\n StreetNumber:%s,\nAdCode:%s,\n" +
                                    "AOIName:%s,\nBuildingId:%s,\nfloor:%s,\nGPSAccuracyStatus:%d",
                            amapLocation.getLocationType(),
                            amapLocation.getLatitude(),
                            amapLocation.getLongitude(),
                            amapLocation.getAddress(),
                            amapLocation.getStreetNum(),
                            amapLocation.getAdCode(),
                            amapLocation.getAoiName(),
                            amapLocation.getBuildingId(),
                            amapLocation.getFloor(),
                            amapLocation.getGpsAccuracyStatus()
                    );
                    mTextView.setText(location_result);


                    //解析定位结果
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    amapLocation.getFloor();//获取当前室内定位的楼层
                    amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);

                }
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                else {
                    Toast.makeText(MainActivity.this, "AmapError: location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /*
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    private Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle( "location")//getAppName(this) )
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

    //AMapLocationClientOption option = new AMapLocationClientOption();
    /**
     * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
     */
    /*option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
    if(null != mLocationClient){
        locationClient.setLocationOption(option);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        locationClient.stopLocation();
        locationClient.startLocation();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_location);
        stop = findViewById(R.id.stop_location);
        mTextView = findViewById(R.id.location_result);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "start_location clicked", Toast.LENGTH_SHORT ).show();

                //这里以ACCESS_COARSE_LOCATION为例
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "request permission", Toast.LENGTH_SHORT ).show();

                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                          WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
                } else {
                    Toast.makeText(MainActivity.this, "started location", Toast.LENGTH_SHORT).show();
                    //启动定位
                    mLocationClient.startLocation();
                    //异步获取定位结果
                }
            }
        });
       stop.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MainActivity.this,  "stopped location", Toast.LENGTH_SHORT).show();
               mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
           }
       });


        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);


        //关闭缓存机制
        //mLocationOption.setLocationCacheEnable(false);


        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);

        //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
        //mLocationClient.enableBackgroundLocation(2001, buildNotification());
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
       // mLocationClient.disableBackgroundLocation(true);

        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        mLocationOption = null;
        mLocationClient = null;
    }

  /* @Override
    public void onClick(View v){
        if( v.getId()  == R.id.start_location ) {

        }
        else if( v.getId() == R.id.stop_location ){

        }
    }
*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        if( requestCode == WRITE_COARSE_LOCATION_REQUEST_CODE) {
            if( grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //异步获取定位结果
                Toast.makeText(MainActivity.this, "started location", Toast.LENGTH_SHORT).show();
                //启动定位
                mLocationClient.startLocation();
            }
        }
    }
}
