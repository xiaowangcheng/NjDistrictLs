package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.SYQ.WaterLevelDetailActivity;
import net.htwater.njdistrictfx.bean.WaterBean;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lzy on 2017/6/5
 */
public class WaterMapFragment extends Fragment implements AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private List<Marker> markerList = new ArrayList<>();
    private List<Text> textList = new ArrayList<>();
    private boolean isShowLables = false;
    private List<WaterBean> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_map, null);
        mapView = (MapView) view.findViewById(R.id.map);
        ImageView setLayer = (ImageView) view.findViewById(R.id.setLayer);

        mapView.onCreate(savedInstanceState);
        setUpMap();

        setLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aMap.getMapType() == AMap.MAP_TYPE_SATELLITE) {
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                } else {
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                }
            }
        });

        LatLng location = new LatLng(CommonUtil.latitude, CommonUtil.longitude);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, CommonUtil.zoon));
        mlocationClient.startLocation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        EventBus.getDefault().unregister(this);

        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            MarkerOptions options = new MarkerOptions().anchor(0.5f, 0.5f);
            aMap.addMarker(options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker)));
        } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = (int) marker.getObject();
        WaterBean waterBean = list.get(index);
        Intent intent = new Intent(getActivity(), WaterLevelDetailActivity.class);
        intent.putExtra("stnm", waterBean.getSTNM());
        intent.putExtra("stcd", waterBean.getSTCD());
//                intent.putExtra("sttp", waterBean.getSttp());
        startActivity(intent);
        return true;
    }

    @Subscribe
    public void onEvent(List<WaterBean> list) {
//        if (list.size() == 0) {
//            return;
//        }
        this.list = list;
        drawMarker();
        if (isShowLables) {
            drawLable();
        }
    }

    private void drawMarker() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();

        MarkerOptions options = new MarkerOptions();
        int index = -1;
        for (WaterBean waterBean : list) {
            index++;
            if ("—".equals(waterBean.getLTTD()) || "—".equals(waterBean.getLGTD())) {
                continue;
            }
            double[] array = Util.delta(Double.valueOf(waterBean.getLTTD()), Double.valueOf(waterBean.getLGTD()));
            LatLng location = new LatLng(array[0], array[1]);
            Marker marker = aMap.addMarker(options.position(location).icon(getBitmap(waterBean)));
            marker.setObject(index);
            markerList.add(marker);
        }
    }

    private void drawLable() {
        for (Text text : textList) {
            text.remove();
        }
        textList.clear();

        TextOptions options = new TextOptions().fontSize(30).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP);
        for (WaterBean waterBean : list) {
            if ("—".equals(waterBean.getLTTD()) || "—".equals(waterBean.getLGTD())) {
                continue;
            }
            double[] array = Util.delta(Double.valueOf(waterBean.getLTTD()), Double.valueOf(waterBean.getLGTD()));
            LatLng location = new LatLng(array[0], array[1]);
            Text text = aMap.addText(options.position(location).text(waterBean.getSTNM()+":"+CommonUtil.format2Two(waterBean.getZ())));
            textList.add(text);
        }
    }

    private com.amap.api.maps.model.BitmapDescriptor getBitmap(WaterBean waterBean) {
        int id;
        if (Double.parseDouble(waterBean.getZ())>Double.parseDouble(waterBean.getWRZ())) {
            id = R.mipmap.cw_guard_icon;
        } else {
            id = R.mipmap.cw_normal_icon;
        }
        return BitmapDescriptorFactory.fromResource(id);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap = mapView.getMap();
        // 自定义系统定位图标
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
        aMap.setMyLocationStyle(myLocationStyle);
        // aMap.showMapText(false);

        aMap.setOnMarkerClickListener(this);
        aMap.setOnCameraChangeListener(this);

        UiSettings settings = aMap.getUiSettings();
        settings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        settings.setCompassEnabled(false);
        settings.setScaleControlsEnabled(true);
        settings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);

        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setOnceLocation(true);

        mlocationClient = new AMapLocationClient(getActivity());
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.setLocationListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (cameraPosition.zoom >= 12) {
            if (!isShowLables) {
                drawLable();
                isShowLables = true;
            }
        } else {
            if (isShowLables) {
                //hide labels
                for (Text text : textList) {
                    text.remove();
                }
                textList.clear();
                isShowLables = false;
            }
        }
        if (cameraPosition.zoom < 9.4) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 9.5));
        }
    }
}
