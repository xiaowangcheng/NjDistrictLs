package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.parseColor;

/**
 * Created by lzy on 2017/4/17
 */
public class GcxxMapFragment extends Fragment implements AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private List<Marker> markerList = new ArrayList<>();
    private List<Text> textList = new ArrayList<>();
    private boolean isShowLables = false;
    private JSONArray jsonArray = new JSONArray();
    private List<com.amap.api.maps.model.Polyline> lineList = new ArrayList<>();
    private String dataString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_map_gcxx, null);
        mapView = (MapView) view.findViewById(R.id.map);
        ImageView setLayer = (ImageView) view.findViewById(R.id.setLayer);
        LinearLayout legend = (LinearLayout) view.findViewById(R.id.legend);
        LinearLayout legend2 = (LinearLayout) view.findViewById(R.id.legend2);

        mapView.onCreate(savedInstanceState);
        setUpMap();

        /*if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
            legend.setVisibility(View.VISIBLE);
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
            legend2.setVisibility(View.VISIBLE);
        }*/

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

        LatLng location = null;

        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES){
            location =new LatLng(CommonUtil.latitudeShuiku, CommonUtil.longitudeShuiku);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, CommonUtil.zoonshuiku));
        } else {
            location =new LatLng(CommonUtil.latitude, CommonUtil.longitude);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, CommonUtil.zoon));
        }
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
        return true;
    }

    @Subscribe
    public void onEvent(JSONArray jsonArray) {
//        if (jsonArray.length() == 0) {
//            return;
//        }
        this.jsonArray = jsonArray;
        drawMarker();
        if (isShowLables) {
            drawLable();
        }
    }

    private void drawMarker() {
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER
                || MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
            return;
        }
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();

        MarkerOptions options = new MarkerOptions();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if ("—".equals(jsonObject.getString("LTTD")) || "—".equals(jsonObject.getString("LGTD"))) {
                    continue;
                }
                double[] array = Util.delta(Double.valueOf(jsonObject.getString("LTTD")), Double.valueOf(jsonObject.getString("LGTD")));
                LatLng location = new LatLng(array[0], array[1]);
                Marker marker = aMap.addMarker(options.position(location).icon(getBitmap()));
                marker.setObject(i);
                markerList.add(marker);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawLable() {
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
            return;
        }
        for (Text text : textList) {
            text.remove();
        }
        textList.clear();

        TextOptions options = new TextOptions().fontSize(30).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if ("—".equals(jsonObject.getString("LTTD")) || "—".equals(jsonObject.getString("LGTD"))) {
                    continue;
                }
                double[] array = Util.delta(Double.valueOf(jsonObject.getString("LTTD")), Double.valueOf(jsonObject.getString("LGTD")));
                LatLng location = new LatLng(array[0], array[1]);
                Text text = aMap.addText(options.position(location).text(jsonObject.getString("GQMC")));
                textList.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private com.amap.api.maps.model.BitmapDescriptor getBitmap() {
        int id = R.mipmap.st_sk_icon;
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_PUMP) {
            id = R.mipmap.cw_guard_icon;
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_GATE) {
            id = R.mipmap.s2_sz;
        }
        return BitmapDescriptorFactory.fromResource(id);
    }

    private void readData() {
        String fileName = "river.json";
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
            fileName = "river.json";
        } else  if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
            fileName = "shuiku.json";
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            AssetManager assetManager = getActivity().getAssets();
            InputStream is = assetManager.open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String str;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
            dataString = stringBuilder.toString();
            if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                drawRiver();
            } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
                drawRiver();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawDike() {
        for (com.amap.api.maps.model.Polyline line : lineList) {
            line.remove();
        }
        lineList.clear();
        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject x = features.getJSONObject(i);
                JSONObject attributes = x.getJSONObject("attributes");
                String DSFLST = attributes.getString("DSFLST");
                JSONObject geometry = x.getJSONObject("geometry");
                JSONArray paths = geometry.getJSONArray("paths");
                for (int j = 0; j < paths.length(); j++) {
                    JSONArray jsonArray = paths.getJSONArray(j);
                    drawLine(jsonArray, getDikeColor(DSFLST));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawLine(JSONArray jsonArray, int color) {
        LatLng northeast = aMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng southwest = aMap.getProjection().getVisibleRegion().latLngBounds.southwest;

        try {
            PolylineOptions options = new PolylineOptions().width(6).color(color);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                double[] location = Util.delta(array.getDouble(1), array.getDouble(0));
                if (location[0] < southwest.latitude || location[0] > northeast.latitude
                        || location[1] < southwest.longitude || location[1] > northeast.longitude) {
                    continue;
                }
                options.add(new LatLng(location[0], location[1]));
            }
            if (!options.getPoints().isEmpty()) {
                com.amap.api.maps.model.Polyline line = aMap.addPolyline(options);
                lineList.add(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getDikeColor(String DSFLST) {
        if (null == DSFLST || DSFLST.equals("null")) {
            return parseColor("#FFD5D5");
        }
        int x = Integer.valueOf(DSFLST);
        if (x >= 100) {
            return parseColor("#FF0000");
        } else if (x >= 50) {
            return parseColor("#FF7E7E");
        } else if (x >= 10) {
            return parseColor("#FFB978");
        } else {
            return parseColor("#FFD5D5");
        }
    }

    private void drawDikeText() {
        for (Text text : textList) {
            text.remove();
        }
        textList.clear();
        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject x = features.getJSONObject(i);
                JSONObject attributes = x.getJSONObject("attributes");
                String GQMC = attributes.getString("GQMC");
                JSONObject geometry = x.getJSONObject("geometry");
                JSONArray paths = geometry.getJSONArray("paths");
                for (int j = 0; j < paths.length(); j++) {
                    JSONArray jsonArray = paths.getJSONArray(j);
                    JSONArray jsonArray1 = jsonArray.getJSONArray(jsonArray.length() / 2);
                    double[] location = Util.delta(jsonArray1.getDouble(1), jsonArray1.getDouble(0));
                    TextOptions options = new TextOptions().fontSize(30).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP);
                    Text text = aMap.addText(options.position(new LatLng(location[0], location[1])).text(GQMC));
                    textList.add(text);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawRiver() {
        for (com.amap.api.maps.model.Polyline line : lineList) {
            line.remove();
        }
        lineList.clear();
        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject x = features.getJSONObject(i);
                JSONObject attributes = x.getJSONObject("attributes");
                //String RVGD = attributes.getString("RVGD");
                JSONObject geometry = x.getJSONObject("geometry");
                JSONArray rings = geometry.getJSONArray("rings");
                drawLine(rings.getJSONArray(0), parseColor("#73B2FF"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getRiverColor(String RVGD) {
        if (null == RVGD || RVGD.equals("null")|| RVGD.equals(" ")) {
            return parseColor("#1E8FFF");
        }
        int x = Integer.valueOf(RVGD);
        int color;
        switch (x) {
            case 1:
            case 2:
                color = Color.parseColor("#1E8FFF");
                break;
            case 3:
                color = Color.parseColor("#FFFF00");
                break;
            case 4:
                color = Color.parseColor("#A120F1");
                break;
            case 5:
                color = Color.parseColor("#FF0000");
                break;
            default:
                color = Color.parseColor("#1E8FFF");
        }
        return color;
    }

    private void drawRiverText() {
        for (Text text : textList) {
            text.remove();
        }
        textList.clear();
        try {
            JSONObject jsonObject = new JSONObject(dataString);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject x = features.getJSONObject(i);
                JSONObject attributes = x.getJSONObject("attributes");
                String GQMC = attributes.getString("GQMC");
                JSONObject geometry = x.getJSONObject("geometry");
                JSONArray rings = geometry.getJSONArray("rings").getJSONArray(0);
                JSONArray jsonArray1 = rings.getJSONArray(rings.length() / 2);
                double[] location = Util.delta(jsonArray1.getDouble(1), jsonArray1.getDouble(0));
                TextOptions options = new TextOptions().fontSize(30).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP);
                Text text = aMap.addText(options.position(new LatLng(location[0], location[1])).text(GQMC));
                textList.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        if (cameraPosition.zoom < 9.4) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 9.5));
        }
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER
                || MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
            if (null == dataString) {
                readData();
            } else {
                if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                    drawRiver();
                }
            }
            if (cameraPosition.zoom >= 13) {
                if (!isShowLables) {
                    if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                        drawRiverText();
                    }
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
            return;
        }
        if (cameraPosition.zoom >= 10.8) {
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
    }
}
