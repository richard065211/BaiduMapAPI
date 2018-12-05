package com.example.administrator.baidumap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private TextView longitude,latitude;
    private Button button; //此按钮重置得到当前位置
    private MapView mMapView = null; //获取XML地图控件
    private BaiduMap bdMap;  //定义一个百度MAP对象
    private LocationClient mylocationClient; //定位服务
    private float myCurrentX;
    private Context context;
    private double myLatitude;//纬度，用于存储自己所在位置的纬度
    private double myLongitude;//经度，用于存储自己所在位置的经度
    private BitmapDescriptor myIconLocation1;
    private MyLocationConfiguration.LocationMode locationMode;  //定位图层显示方式
    private boolean isFirstLocation = true;
    private MyLocationListener myListener = new MyLocationListener();
    private  LatLng latLng;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context=this;
        initView();//初始化窗口
        button=(Button)findViewById(R.id.locate);
        latitude=(TextView)findViewById(R.id.latitude);
        longitude=(TextView)findViewById(R.id.longitude);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyLocation();
            }
        });
    }
    /*
    * 点击按钮获取当前位置
    *
    * */
    private void showMyLocation() {
        bdMap.setMyLocationEnabled(true); //开启定位图层
        mylocationClient=new LocationClient(getApplicationContext());//声明Location类
        initLocation();
        mylocationClient.registerLocationListener(myListener);
        mylocationClient.start();//开启定位
        mylocationClient.requestLocation();
    }

    /*
     * 此方法用于配置mylocationClient
     * */
    private void initLocation() {
        locationMode = MyLocationConfiguration.LocationMode.NORMAL;
        LocationClientOption mOption = new LocationClientOption(); //配置当前位置参数
        mOption.setCoorType("bd09ll"); //设置坐标类型
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);//设置是否打开GPS
        mOption.setScanSpan(1000);//设置扫描时间间隔1000ms
        mOption.setLocationNotify(true);

        mylocationClient.setLocOption(mOption);
    }

    /*
    *初始化窗口
    * */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.baidumapView);
        bdMap = mMapView.getMap();//根据给定增量显示地图大小
        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);   //设置地图类型
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(18.0f);
        bdMap.setMapStatus(msu);
        bdMap.setMyLocationEnabled(true);
    }
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            myLongitude=location.getLongitude();
            myLatitude=location.getLatitude();
            longitude.setText(String.valueOf(myLongitude));
            latitude.setText(String.valueOf(myLatitude));
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //构造定位数据
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            bdMap.setMyLocationData(data);
            if (isFirstLocation) {
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                bdMap.animateMapStatus(msu); //动画效果
                isFirstLocation = false;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
