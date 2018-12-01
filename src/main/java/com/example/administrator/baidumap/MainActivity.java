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
    private MylocationListener mylistener;//重写监听类，获取我的位置
    private float myCurrentX;
    private Context context;
    private double myLatitude;//纬度，用于存储自己所在位置的纬度
    private double myLongitude;//经度，用于存储自己所在位置的经度
    private BitmapDescriptor myIconLocation1;
    private MyOrientationListener myOrientationListener; //方向感应类对象
    private MyLocationConfiguration.LocationMode locationMode;  //定位图层显示方式



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context=this;
        initView();//初始化窗口
        initLocation(); //获取到当前用户的位置
        button=(Button)findViewById(R.id.locate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationByLL(myLatitude, myLongitude);
            }
        });
    }
    /*
     * 此方法用于更新当前用户的位置信息
     * */
    private void initLocation() {
        locationMode=MyLocationConfiguration.LocationMode.NORMAL;
        mylocationClient = new LocationClient(this);
        mylistener = new MylocationListener();
        mylocationClient.registerLocationListener(mylistener);
        LocationClientOption mOption = new LocationClientOption(); //配置当前位置参数
        mOption.setCoorType("bd0911"); //设置坐标类型
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);//设置是否打开GPS
        mOption.setScanSpan(1000);//设置扫描时间间隔1000ms
        mylocationClient.setLocOption(mOption);
        myOrientationListener = new MyOrientationListener(context);

        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {

            }
        });
    }

    /*
    *初始化窗口
    * */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.baidumapView);
        bdMap = mMapView.getMap();//根据给定增量显示地图大小
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(18.0f);
        bdMap.setMapStatus(msu);
    }


    private class MylocationListener implements BDLocationListener {

        private boolean isFirstIn=true;
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            myLatitude = bdLocation.getLatitude();
            myLongitude = bdLocation.getLongitude();
            MyLocationData data = new MyLocationData.Builder()
                    .direction(myCurrentX)//设定图标方向
                    .accuracy(bdLocation.getRadius())//getRadius 获取定位精度,默认值0.0f
                    .latitude(myLatitude)//百度纬度坐标
                    .longitude(myLongitude)//百度经度坐标
                    .build();
            //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            bdMap.setMyLocationData(data);
            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                //根据当前所在位置经纬度前往
                getLocationByLL(myLatitude, myLongitude);
                isFirstIn = false;
                //提示当前所在地址信息
//                Toast.makeText(context, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    * 获取当前位置的经纬度
    * */
    public void getLocationByLL(double la, double lg)
    {
        //地理坐标的数据结构
        LatLng latLng = new LatLng(la, lg);
        //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        bdMap.setMapStatus(msu);
    }
}
