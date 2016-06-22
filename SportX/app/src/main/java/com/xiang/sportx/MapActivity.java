package com.xiang.sportx;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xiang.Util.Constant;
import com.xiang.Util.LocationUtil;

import me.drakeet.materialdialog.MaterialDialog;

public class MapActivity extends BaseAppCompatActivity implements BDLocationListener {

    private MapView mapView;

    private String gymName;
    private float latitude;
    private float longitude;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);
    }

    @Override
    protected void initData() {
        gymName = getIntent().getStringExtra(Constant.GYM_NAME);
        latitude = getIntent().getFloatExtra(Constant.LATITUDE, 39.963175f);
        longitude = getIntent().getFloatExtra(Constant.LONGITUDE, 116.400244f);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(this);    //注册监听函数
        mLocationClient.setLocOption(LocationUtil.getLocationClientOption());
        mLocationClient.start();
    }

    @Override
    protected void configView() {
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_on);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Location location = new Location("baidu");
        location.setLongitude(bdLocation.getLongitude());
        location.setLatitude(bdLocation.getLatitude());

        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果

        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果

        } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            location = null;
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            location = null;
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            location = null;
        }

        if (null != location){
            Log.d("location", location.getLatitude() + "" + location.getLongitude());
        } else{
            final MaterialDialog materialDialog = new MaterialDialog(this);
            materialDialog.setCanceledOnTouchOutside(true);
            materialDialog.setTitle("提示");
            materialDialog.setMessage("请您打开“设置->应用->SportX->权限管理”，并允许使用手机定位，以便给您提供更好的服务。");
            materialDialog.setPositiveButton("好的", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }

        BaiduMap baiduMap = mapView.getMap();
        LatLng myPoint = new LatLng(location.getLatitude(), location.getLongitude());
        //构建Marker图标
        BitmapDescriptor myBitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.mypoint);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions myOption = new MarkerOptions()
                .position(myPoint)
                .icon(myBitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(myOption);

        mLocationClient.stop();
    }
}
