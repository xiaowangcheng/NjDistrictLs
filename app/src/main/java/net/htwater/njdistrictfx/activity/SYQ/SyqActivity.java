package net.htwater.njdistrictfx.activity.SYQ;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.fragment.ListAndMap.SyqListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.SyqMapFragment;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.view.MyDatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by lzy on 2017/4/10
 */
public class SyqActivity extends BaseActivity {
    private String queryDate;
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    public List<String> favoritesList = SharedPreferencesUtil.getFavoritesList();
    private boolean isShowAll = true;
    private TextView type;
    private TextView district;
    private String iniType = ""; // 初始化水库水情(专用)
    private TextView showAll;
    private TextView showMe;
    private int blue;
    private int white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syq);

        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        TextView title = (TextView) findViewById(R.id.title);
        RelativeLayout districtLayout = (RelativeLayout) findViewById(R.id.districtLayout);
        district = (TextView) findViewById(R.id.district);
        EditText name = (EditText) findViewById(R.id.name);
        showAll = (TextView) findViewById(R.id.showAll);
        showMe = (TextView) findViewById(R.id.showMe);
        RelativeLayout typeLayout = (RelativeLayout) findViewById(R.id.typeLayout);
        type = (TextView) findViewById(R.id.type);
        RelativeLayout importantLayout = (RelativeLayout) findViewById(R.id.importantLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            districtLayout.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
            title.setText("积淹点");
            importantLayout.setVisibility(View.GONE);
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
            typeLayout.setVisibility(View.VISIBLE);
            // district.setText("所在区：全部");
            type.setText("类型：全部");
            if (null != getIntent().getExtras() && null != getIntent().getStringExtra("iniType")) {
                iniType = getIntent().getStringExtra("iniType");
                if (null != iniType && iniType.length() > 0) {
                    if ("小型水库".equals(iniType)) {
                        iniType = "小一型";
                    } else if ("中型水库".equals(iniType)) {
                        iniType = "中型";
                    }
                }
            }
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            title.setVisibility(View.VISIBLE);
            title.setText("点雨量");
            importantLayout.setVisibility(View.GONE);
            if (null != getIntent().getExtras() && null != getIntent().getStringExtra("iniType")) {
                iniType = getIntent().getStringExtra("iniType");
            }
        }

        blue = getResources().getColor(R.color.theme_blue);
        white = Color.WHITE;

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

        date.setText("时间：" + queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText("时间：" + queryDate);
                simulationData();
                //query();
                //点击时间或筛选条件时恢复顶栏的全部按钮
                restoreTopButton();
                restoreFilterButton(1);
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.show();
            }
        });
        districtLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] districts = Constants.districts;
                AlertDialog.Builder builder = new AlertDialog.Builder(SyqActivity.this);
                builder.setItems(districts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text;
//                        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
//                            text = "所在区：";
//                        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
//                            text = "行政区：";
//                        } else {
                        text = "地点：";
//                        }
                        district.setText(text + districts[i]);
                        filter(districts[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                        restoreFilterButton(2);
                    }
                });
                builder.show();
            }
        });
        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] types = new String[]{"全部", "中型水库", "小一型水库", "小二型水库"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SyqActivity.this);
                builder.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        type.setText("类型：" + types[i]);
                        filter2(types[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                        restoreFilterButton(3);
                    }
                });
                builder.show();
            }
        });
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowAll) {
                    return;
                }
                isShowAll = true;
                //刷新数据
                EventBus.getDefault().post(jsonArray);

                showAll.setTextColor(blue);
                showAll.setBackgroundResource(R.drawable.tabswitch_left_normal);
                showMe.setTextColor(white);
                showMe.setBackgroundResource(R.drawable.tabswitch_right_focus);
            }
        });
        showMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowAll) {
                    return;
                }
                isShowAll = false;
                //刷新数据
                showFavorites();

                showAll.setTextColor(white);
                showAll.setBackgroundResource(R.drawable.tabswitch_left_focus);
                showMe.setTextColor(blue);
                showMe.setBackgroundResource(R.drawable.tabswitch_right_normal);
                //点击重要站点时恢复全部筛选条件
                district.setText("地点：全部");
                type.setText("类型：全部");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final FragmentManager manager = getFragmentManager();
        final Fragment listFragment = new SyqListFragment();
        final Fragment mapFragment = new SyqMapFragment();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listFragment.isHidden()) {
                    manager.beginTransaction().hide(mapFragment).show(listFragment).commit();
                    switchButton.setText("地图");
                } else {
                    manager.beginTransaction().hide(listFragment).show(mapFragment).commit();
                    switchButton.setText("列表");
                }
            }
        });

        manager.beginTransaction().add(R.id.contentLayout, mapFragment).hide(mapFragment)
                .add(R.id.contentLayout, listFragment).commit();
        simulationData();
        // query();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.setFavorites(favoritesList);
    }

    private void simulationData(){
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String url="";
                String  s ="";
                String method = "QueryMainwater", param = "TM";
                switch (MyApplication.CONSTANS_WATER_TYPE) {
                    case Constants.CONSTANS_RIVER_WATER:
                        method = "rvTdWater";
                        param = "tm";
                        url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                    case Constants.CONSTANS_RES_WATER:
                        method = "Queryreswater";
                        url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                    case Constants.CONSTANS_GATE_WATER:
                        method = "QueryGateWater";
                        url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                    case Constants.CONSTANS_PUMP_WATER:
                        method = "QueryPumpwater";
                        s="[{\"JYSID\":\"1\",\"DLMC\":\"毛纺厂路\",\"JYSJC\":\"毛纺厂路\",\"LGTD\":\"118.725174\",\"LTTD\":\"32.13558\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"9.72\"},{\"JYSID\":\"2\",\"DLMC\":\"七里河大街\",\"JYSJC\":\"七里河大街\",\"LGTD\":\"118.658233\",\"LTTD\":\"32.09113\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"17.84\"},{\"JYSID\":\"3\",\"DLMC\":\"浦滨路\",\"JYSJC\":\"浦滨路\",\"LGTD\":\"118.66193\",\"LTTD\":\"32.07336\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"4\",\"DLMC\":\"鼎泰家园\",\"JYSJC\":\"鼎泰家园\",\"LGTD\":\"118.693897\",\"LTTD\":\"32.121845\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"5\",\"DLMC\":\"杨庄转盘\",\"JYSJC\":\"杨庄转盘\",\"LGTD\":\"118.731883\",\"LTTD\":\"32.210011\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"10.3\"},{\"JYSID\":\"6\",\"DLMC\":\"柳新路铁路涵\",\"JYSJC\":\"柳新路铁路涵\",\"LGTD\":\"118.703333\",\"LTTD\":\"32.132975\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"7\",\"DLMC\":\"江山路\",\"JYSJC\":\"江山路\",\"LGTD\":\"118.73961\",\"LTTD\":\"32.136845\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"8\",\"DLMC\":\"协和加油站\",\"JYSJC\":\"南京工大南门东侧加油站\",\"LGTD\":\"118.639972\",\"LTTD\":\"32.074814\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"9\",\"DLMC\":\"行知路\",\"JYSJC\":\"行知路\",\"LGTD\":\"118.6045\",\"LTTD\":\"32.0254\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"13.35\"},{\"JYSID\":\"10\",\"DLMC\":\"葛新路宁启铁路桥洞\",\"JYSJC\":\"葛新路宁启铁路桥洞\",\"LGTD\":\"118.750094\",\"LTTD\":\"32.252492\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"11\",\"DLMC\":\"信息工程大学站\",\"JYSJC\":\"信息工程大学站\",\"LGTD\":\"118.721763\",\"LTTD\":\"32.204425\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"9.12\"}]";
                        url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                    case Constants.CONSTANS_FLOOD:
                        method = "Queryflood";
                        param = "tm";
                        s ="[{\"JYSID\":\"1\",\"DLMC\":\"毛纺厂路\",\"JYSJC\":\"毛纺厂路\",\"LGTD\":\"118.725174\",\"LTTD\":\"32.13558\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"9.72\"},{\"JYSID\":\"2\",\"DLMC\":\"七里河大街\",\"JYSJC\":\"七里河大街\",\"LGTD\":\"118.658233\",\"LTTD\":\"32.09113\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"17.84\"},{\"JYSID\":\"3\",\"DLMC\":\"浦滨路\",\"JYSJC\":\"浦滨路\",\"LGTD\":\"118.66193\",\"LTTD\":\"32.07336\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"4\",\"DLMC\":\"鼎泰家园\",\"JYSJC\":\"鼎泰家园\",\"LGTD\":\"118.693897\",\"LTTD\":\"32.121845\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"5\",\"DLMC\":\"杨庄转盘\",\"JYSJC\":\"杨庄转盘\",\"LGTD\":\"118.731883\",\"LTTD\":\"32.210011\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"10.3\"},{\"JYSID\":\"6\",\"DLMC\":\"柳新路铁路涵\",\"JYSJC\":\"柳新路铁路涵\",\"LGTD\":\"118.703333\",\"LTTD\":\"32.132975\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"7\",\"DLMC\":\"江山路\",\"JYSJC\":\"江山路\",\"LGTD\":\"118.73961\",\"LTTD\":\"32.136845\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"8\",\"DLMC\":\"协和加油站\",\"JYSJC\":\"南京工大南门东侧加油站\",\"LGTD\":\"118.639972\",\"LTTD\":\"32.074814\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"9\",\"DLMC\":\"行知路\",\"JYSJC\":\"行知路\",\"LGTD\":\"118.6045\",\"LTTD\":\"32.0254\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"13.35\"},{\"JYSID\":\"10\",\"DLMC\":\"葛新路宁启铁路桥洞\",\"JYSJC\":\"葛新路宁启铁路桥洞\",\"LGTD\":\"118.750094\",\"LTTD\":\"32.252492\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"0.0\"},{\"JYSID\":\"11\",\"DLMC\":\"信息工程大学站\",\"JYSJC\":\"信息工程大学站\",\"LGTD\":\"118.721763\",\"LTTD\":\"32.204425\",\"TM\":\"2019-01-10 14:25:32\",\"DEPTH\":\"9.12\"}]";
                        url =  Constants.ServerURL+"apiJys?"+ param + "=" + queryDate + "+" + DateUtil.getTime();
                        //url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                    case Constants.CONSTANS_RAIN:
                        method ="jiangbeiapi";// "queryRainSta";
                        param = "tm";
                        s="[{\"STCD\":\"60116200\",\"STNM\":\"长江下关\",\"RVNM\":\"null\",\"LGTD\":\"118.72728\",\"LTTD\":\"32.08332\",\"STLC\":\"null\",\"TVALUE\":\"16\",\"YVALUE\":\"1\",\"BYVALUE\":\"1\",\"TTVALUE\":\"18.0\"},{\"STCD\":\"60404800\",\"STNM\":\"路南塘坝\",\"RVNM\":\"null\",\"LGTD\":\"118.64954\",\"LTTD\":\"32.11055\",\"STLC\":\"路南塘坝                                          \",\"TVALUE\":\"12.5\",\"YVALUE\":\"1\",\"BYVALUE\":\"1\",\"TTVALUE\":\"14.5\"},{\"STCD\":\"62914800\",\"STNM\":\"滁河六合\",\"RVNM\":\"null\",\"LGTD\":\"118.8403\",\"LTTD\":\"32.33979\",\"STLC\":\"null\",\"TVALUE\":\"5.5\",\"YVALUE\":\"1\",\"BYVALUE\":\"0\",\"TTVALUE\":\"6.5\"},{\"STCD\":\"60401094\",\"STNM\":\"岳子河闸\",\"RVNM\":\"岳子河\",\"LGTD\":\"118.819479\",\"LTTD\":\"32.24323\",\"STLC\":\"岳子河闸                                          \",\"TVALUE\":\"8.5\",\"YVALUE\":\"1.5\",\"BYVALUE\":\"0.5\",\"TTVALUE\":\"10.5\"},{\"STCD\":\"62916400\",\"STNM\":\"葛塘\",\"RVNM\":\"马汊河\",\"LGTD\":\"118.737354\",\"LTTD\":\"32.251839\",\"STLC\":\"葛塘                                              \",\"TVALUE\":\"12\",\"YVALUE\":\"0\",\"BYVALUE\":\"0.5\",\"TTVALUE\":\"12.5\"},{\"STCD\":\"62914200\",\"STNM\":\"滁河晓桥\",\"RVNM\":\"null\",\"LGTD\":\"118.54762\",\"LTTD\":\"32.15713\",\"STLC\":\"null\",\"TVALUE\":\"13.5\",\"YVALUE\":\"1\",\"BYVALUE\":\"1\",\"TTVALUE\":\"15.5\"},{\"STCD\":\"62916401\",\"STNM\":\"小头李\",\"RVNM\":\"滁河\",\"LGTD\":\"118.67317\",\"LTTD\":\"32.25299\",\"STLC\":\"小头李                                            \",\"TVALUE\":\"10.5\",\"YVALUE\":\"0.5\",\"BYVALUE\":\"0.5\",\"TTVALUE\":\"11.5\"},{\"STCD\":\"60404300\",\"STNM\":\"黑扎营泵站\",\"RVNM\":\"null\",\"LGTD\":\"118.64134\",\"LTTD\":\"32.21091\",\"STLC\":\"黑扎营泵站                                        \",\"TVALUE\":\"10.5\",\"YVALUE\":\"0.5\",\"BYVALUE\":\"1\",\"TTVALUE\":\"12.0\"}]";
                        url = Constants.ServerURL+"apiRain?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                        break;
                }
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
                        if (iniType.length() > 0) {
                            type.setText("类型：" + iniType);
                            filter2(iniType);
                        } else {
                            type.setText("类型：中型");
                            filter2("中型");
                        }
                    } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RIVER_WATER) {
                        jsonArray = handle(jsonArray);
                        EventBus.getDefault().post(jsonArray);
                    } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
                        if (iniType.length() > 0) {
                            district.setText("地点：" + iniType);
                            filter(iniType);
                        } else {
                            EventBus.getDefault().post(jsonArray);
                        }
                    } else {
                        EventBus.getDefault().post(jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒




    }




    // http://218.2.110.162:8080/njfx/Queryreswater!SYQ?TM=2017-03-15+14%3A36
    private void query() {
        progressDialog.show();
        String url="";
        String method = "QueryMainwater", param = "TM";
        switch (MyApplication.CONSTANS_WATER_TYPE) {
            case Constants.CONSTANS_RIVER_WATER:
                method = "rvTdWater";
                param = "tm";
                url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
            case Constants.CONSTANS_RES_WATER:
                method = "Queryreswater";
                url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
            case Constants.CONSTANS_GATE_WATER:
                method = "QueryGateWater";
                url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
            case Constants.CONSTANS_PUMP_WATER:
                method = "QueryPumpwater";
                url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
            case Constants.CONSTANS_FLOOD:
                method = "Queryflood";
                param = "tm";
                url =  Constants.ServerURL+"apiJys?"+ param + "=" + queryDate + "+" + DateUtil.getTime();
                //url =  Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
            case Constants.CONSTANS_RAIN:
                method ="jiangbeiapi";// "queryRainSta";
                param = "tm";
                url = Constants.ServerURL+"apiRain?" + param + "=" + queryDate + "+" + DateUtil.getTime();
                break;
        }

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
                        if (iniType.length() > 0) {
                            type.setText("类型：" + iniType);
                            filter2(iniType);
                        } else {
                            type.setText("类型：中型");
                            filter2("中型");
                        }
                    } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RIVER_WATER) {
                        jsonArray = handle(jsonArray);
                        EventBus.getDefault().post(jsonArray);
                    } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
                        if (iniType.length() > 0) {
                            district.setText("地点：" + iniType);
                            filter(iniType);
                        } else {
                            EventBus.getDefault().post(jsonArray);
                        }
                    } else {
                        EventBus.getDefault().post(jsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);
    }

    // 点雨量STLC、河道水情水库水情CITY、泵站水情CITYNM
    private void filter(String district) {
        if (district.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        String key = "CITY";
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            key = "STLC";
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            key = "CITYNM";
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(district)) {
                    filterArray.put(jsonObject);
                }
            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filter2(String type) {
        if (type.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String typeValue = jsonObject.getString("RES_STTP");
                if (type.contains("大型") && typeValue.equals("0201")
                        || type.contains("中型") && typeValue.equals("0202")
                        || type.contains("小一型") && typeValue.equals("0203")
                        || type.contains("小二型") && typeValue.equals("0204")) {
                    filterArray.put(jsonObject);
                }
            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void search(String x) {
        if (x.equals("")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                switch (MyApplication.CONSTANS_WATER_TYPE) {
                    case Constants.CONSTANS_RIVER_WATER:
                        break;
                    case Constants.CONSTANS_RES_WATER:
                        break;
                    case Constants.CONSTANS_GATE_WATER:
                        break;
                    case Constants.CONSTANS_PUMP_WATER:
                        break;
                    case Constants.CONSTANS_FLOOD:
                        if (jsonObject.getString("DLMC").contains(x)) {
                            filterArray.put(jsonObject);
                        }
                        break;
                    case Constants.CONSTANS_RAIN:
                        if (jsonObject.getString("STNM").contains(x)) {
                            filterArray.put(jsonObject);
                        }
                        break;
                }


            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFavorites() {
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getBoolean("isfocus")) {
                    filterArray.put(jsonObject);
                }
            }
            if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RIVER_WATER) {
                EventBus.getDefault().post(handle2(filterArray));
            } else {
                EventBus.getDefault().post(filterArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray handle2(JSONArray jsonArray) {
        JSONObject[] array = new JSONObject[11];
        List<JSONObject> others = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String STNM = jsonObject.getString("STNM");
                switch (STNM) {
                    case "长江下关":
                        array[0] = jsonObject;
                        break;
                    case "晓桥":
                        array[1] = jsonObject;
                        break;
                    case "六合":
                        array[2] = jsonObject;
                        break;
                    case "前垾村":
                        array[3] = jsonObject;
                        break;
                    case "东山":
                        array[4] = jsonObject;
                        break;
                    case "水碧桥":
                        array[5] = jsonObject;
                        break;
                    case "高淳":
                        array[6] = jsonObject;
                        break;
                    case "蛇山闸":
                        array[7] = jsonObject;
                        break;
                    case "葛塘":
                        array[8] = jsonObject;
                        break;
                    case "陈家桥":
                        array[9] = jsonObject;
                        break;
                    case "灵岩河":
                        array[10] = jsonObject;
                        break;
                    default:
                        others.add(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray();
        for (JSONObject jsonObject : array) {
            if (null != jsonObject) {
                resultArray.put(jsonObject);
            }
        }
        for (JSONObject jsonObject : others) {
            resultArray.put(jsonObject);
        }
        return resultArray;
    }

    private void restoreTopButton() {
        //点击时间或筛选条件时恢复顶栏的全部按钮
        isShowAll = true;
        showAll.setTextColor(blue);
        showAll.setBackgroundResource(R.drawable.tabswitch_left_normal);
        showMe.setTextColor(white);
        showMe.setBackgroundResource(R.drawable.tabswitch_right_focus);
    }

    private void restoreFilterButton(int index) {
        switch (MyApplication.CONSTANS_WATER_TYPE) {
//            case Constants.CONSTANS_RIVER_WATER:
//                break;
            case Constants.CONSTANS_RES_WATER:
                switch (index) {
                    case 1:
                        district.setText("地点：全部");
                        type.setText("类型：全部");
                        break;
                    case 2:
                        type.setText("类型：全部");
                        break;
                    case 3:
                        district.setText("地点：全部");
                        break;
                }
                break;
//            case Constants.CONSTANS_GATE_WATER:
//                break;
//            case Constants.CONSTANS_PUMP_WATER:
//                break;
            case Constants.CONSTANS_FLOOD:
                break;
            case Constants.CONSTANS_RAIN:
                if (index == 1) {
                    district.setText("地点：全部");
                    //type.setText("类型：全部");
                }
                break;
        }
    }

    /**
     * 河道水情默认按水系排列
     *
     * @param jsonArray
     * @return
     */
    private JSONArray handle(JSONArray jsonArray) {
        List<JSONObject> list1 = new ArrayList<>();
        List<JSONObject> list2 = new ArrayList<>();
        List<JSONObject> list3 = new ArrayList<>();
        List<JSONObject> list4 = new ArrayList<>();
        List<JSONObject> list5 = new ArrayList<>();
        List<JSONObject> list6 = new ArrayList<>();
        List<JSONObject> list7 = new ArrayList<>();
        JSONArray resultArray = new JSONArray();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String HNNM = jsonObject.getString("HNNM");
                switch (HNNM) {
                    case "长江干流及沿江水系":
                        list1.add(jsonObject);
                        break;
                    case "秦淮河水系":
                        list2.add(jsonObject);
                        break;
                    case "滁河水系":
                        list3.add(jsonObject);
                        break;
                    case "水阳江水系":
                        list4.add(jsonObject);
                        break;
                    case "太湖水系":
                        list5.add(jsonObject);
                        break;
                    case "淮河水系":
                        list6.add(jsonObject);
                        break;
                    default:
                        list7.add(jsonObject);
                }
            }
            for (JSONObject jsonObject : list1) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list2) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list3) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list4) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list5) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list6) {
                resultArray.put(jsonObject);
            }
            for (JSONObject jsonObject : list7) {
                resultArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultArray;
    }
}
