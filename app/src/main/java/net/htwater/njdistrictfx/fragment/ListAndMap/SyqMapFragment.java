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
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.SYQ.RainfallDetailActivity;
import net.htwater.njdistrictfx.activity.SYQ.WaterLevelDetailActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lzy on 2017/3/31
 */
public class SyqMapFragment extends Fragment implements AMapLocationListener, AMap.OnMarkerClickListener, AMap
        .OnCameraChangeListener {
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private List<Marker> markerList = new ArrayList<>();
    private List<Text> textList = new ArrayList<>();
    private boolean isShowLables = false;
    private JSONArray jsonArray = new JSONArray();
    private float defaultZoom = 9;
    private float showTextZoom = 10.8f;

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
        LinearLayout legend = (LinearLayout) view.findViewById(R.id.legend);
        LinearLayout floodLegend = (LinearLayout) view.findViewById(R.id.floodLegend);

        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            legend.setVisibility(View.VISIBLE);
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            floodLegend.setVisibility(View.VISIBLE);
            defaultZoom = 11.5f;
            showTextZoom = 12.5f;
        }

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

        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            return true;
        }
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            int index = (int) marker.getObject();
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            JSONObject jsonObject = (JSONObject) adapter.getItem(position);
            Intent intent = new Intent(getActivity(), RainfallDetailActivity.class);
            try {
                intent.putExtra("stcd", jsonObject.getString("STCD"));
                intent.putExtra("stnm", jsonObject.getString("STNM"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER){
            int index = (int) marker.getObject();
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            JSONObject jsonObject = (JSONObject) adapter.getItem(position);
            Intent intent = new Intent(getActivity(), WaterLevelDetailActivity.class);
            try {
                intent.putExtra("stcd", jsonObject.getString("STCD"));
                intent.putExtra("stnm", jsonObject.getString("STNM"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Subscribe
    public void onEvent(JSONArray jsonArray) {
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER
                || MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RIVER_WATER
                || MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            this.jsonArray = handleData(jsonArray);
        } else {
            this.jsonArray = jsonArray;
        }
        drawMarker();
        if (isShowLables) {
            drawLable();
        }
    }

    /**
     * 把超警戒的站点排到最前面
     *
     * @param jsonArray
     * @return
     */
    private JSONArray handleData(JSONArray jsonArray) {
        String alertKey, zKey;
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
            alertKey = "CTZ";
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            alertKey = "ZUIGAOSHUIWEI";
        } else {
            alertKey = "WRZ";
        }
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            zKey = "PPDWZ";
        } else {
            zKey = "Z";
        }
        List<JSONObject> dataList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Object object = jsonObject.get(alertKey);
                Object zObject = jsonObject.get(zKey);
                if (object.toString().equals("—") || zObject.toString().equals("—")) {
                    dataList.add(jsonObject);
                    continue;
                }
                if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
                    String zString = jsonObject.getString(zKey);
                    String ctzString = jsonObject.getString(alertKey);
                    if (zString.isEmpty() || ctzString.isEmpty()) {
                        dataList.add(jsonObject);
                        continue;
                    }
                    double Z = Double.valueOf(zString);
                    double CTZ = Double.valueOf(ctzString);
                    if (Z > CTZ) {
                        jsonObject.put("alert", true);
                        dataList.add(0, jsonObject);
                    } else {
                        dataList.add(jsonObject);
                    }
                } else {
                    double Z = jsonObject.getDouble(zKey);
                    double CTZ = jsonObject.getDouble(alertKey);
                    if (Z > CTZ) {
                        jsonObject.put("alert", true);
                        dataList.add(0, jsonObject);
                    } else {
                        dataList.add(jsonObject);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray1 = new JSONArray();
        for (JSONObject jsonObject : dataList) {
            jsonArray1.put(jsonObject);
        }
        return jsonArray1;
    }

    private void drawMarker() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();

        MarkerOptions options = new MarkerOptions();
        int index = -1;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                index++;
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if ("—".equals(jsonObject.getString("LTTD")) || "—".equals(jsonObject.getString("LGTD"))) {
                    continue;
                }
                double[] array = Util.delta(Double.valueOf(jsonObject.getString("LTTD")), Double.valueOf(jsonObject.getString("LGTD")));
                LatLng location = new LatLng(array[0], array[1]);
                Marker marker = aMap.addMarker(options.position(location).icon(getBitmap(jsonObject)));
                marker.setObject(index);
                markerList.add(marker);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawLable() {
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
                StringBuilder stringBuilder = new StringBuilder();
                if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
                    stringBuilder.append(jsonObject.getString("DLMC")).append(":").append(CommonUtil.format2One(jsonObject.getString("DEPTH")));
                } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
                    stringBuilder.append(jsonObject.getString("STNM")).append(":").append(CommonUtil.format2One(jsonObject.getString("TVALUE")));
                } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_GATE_WATER) {
                    stringBuilder.append(jsonObject.getString("STNM"));
                } else {
                    stringBuilder.append(jsonObject.getString("STNM")).append(":").append(CommonUtil.format2Two(jsonObject.getString("Z")));
                }
                Text text = aMap.addText(options.position(location).text(stringBuilder.toString()));
                textList.add(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private com.amap.api.maps.model.BitmapDescriptor getBitmap(JSONObject jsonObject) {
        int id = R.mipmap.cw_normal_icon;
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            try {
                double TVALUE = Double.valueOf(jsonObject.getString("TVALUE"));
                if (TVALUE == 0) {
                    id = R.mipmap._6;
                } else if (TVALUE <= 10) {
                    id = R.mipmap._2;
                } else if (TVALUE <= 24.9) {
                    id = R.mipmap._3;
                } else if (TVALUE <= 49.9) {
                    id = R.mipmap._4;
                } else if (TVALUE <= 99.9) {
                    id = R.mipmap._1;
                } else {
                    id = R.mipmap._5;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            try {
                String VALUEString = jsonObject.getString("DEPTH");
                if (VALUEString.equals("—")) {
                    return BitmapDescriptorFactory.fromResource(R.mipmap._6);
                }
                double VALUE = Double.valueOf(jsonObject.getString("DEPTH"));
                if (VALUE >= 50) {
                    id = R.mipmap._5;
                } else if (VALUE >= 40) {
                    id = R.mipmap._1;
                } else if (VALUE >= 30) {
                    id = R.mipmap._3;
                } else if (VALUE >= 20) {
                    id = R.mipmap._2;
                } else {
                    id = R.mipmap._6;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("alert")) {
            id = R.mipmap.cw_guard_icon;
        }
        return BitmapDescriptorFactory.fromResource(id);
    }

    private void setUpMap() {
        aMap = mapView.getMap();

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
        if (cameraPosition.zoom >= showTextZoom) {
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
