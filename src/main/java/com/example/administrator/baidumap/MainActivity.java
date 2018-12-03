package com.example.administrator.baidumap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

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
    private Button button; //此按钮重置得到当前位置
    private MapView mMapView = null;
    private BaiduMap bdMap;
    private LocationClient mylocationClient; //定位服务
    private float myCurrentX;
    private Context context;
    private double myLatitude;//纬度，用于存储自己所在位置的纬度
    private double myLongitude;//经度，用于存储自己所在位置的经度
    private BitmapDescriptor myIconLocation1;
    private MyLocationConfiguration.LocationMode locationMode;  //定位图层显示方式
    private boolean isFirstLocation = true;
    private MyLocationListener myListener = new MyLocationListener();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context=this;
        initView();//初始化窗口
        button=(Button)findViewById(R.id.locate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    /*
     * 此方法用于更新当前用户的位置信息
     * */
    private void initLocation() {
        locationMode = MyLocationConfiguration.LocationMode.NORMAL;

        LocationClientOption mOption = new LocationClientOption(); //配置当前位置参数
        mOption.setCoorType("bd0911"); //设置坐标类型
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);//设置是否打开GPS
        mOption.setScanSpan(1000);//设置扫描时间间隔1000ms
        mylocationClient.setLocOption(mOption);
    }

    /*
    *初始化窗口
    * */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.baidumapView);
        bdMap = mMapView.getMap();//根据给定增量显示地图大小
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(18.0f);
        bdMap.setMapStatus(msu);
        bdMap.setMyLocationEnabled(true);
        mylocationClient=new LocationClient(this);
        mylocationClient.registerLocationListener(myListener);
        initLocation();
        mylocationClient.start();
    }
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
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
}
