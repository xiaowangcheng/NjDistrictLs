package net.htwater.njdistrictfx.activity.SYQ;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import net.htwater.njdistrictfx.bean.WaterBean;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.fragment.ListAndMap.WaterListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.WaterMapFragment;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.util.JsonUtil;
import net.htwater.njdistrictfx.view.MyDatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by lzy on 2017/6/5
 */
public class WaterActivity extends BaseActivity {
    private String queryDate;
    private ProgressDialog progressDialog;
    private boolean isShowAll = true;
    private TextView showAll;
    private TextView showMe;
    private int blue;
    private int white;
    private List<WaterBean> dataList = new ArrayList<>();
    private TextView district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
        TextView title = (TextView) findViewById(R.id.title);
        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        RelativeLayout rl_tab = (RelativeLayout) findViewById(R.id.rl_tab);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        RelativeLayout districtLayout = (RelativeLayout) findViewById(R.id.districtLayout);
        districtLayout.setVisibility(View.GONE);
        district = (TextView) findViewById(R.id.district);
        showAll = (TextView) findViewById(R.id.showAll);
        showMe = (TextView) findViewById(R.id.showMe);
        EditText name = (EditText) findViewById(R.id.name);
        rl_tab.setVisibility(View.GONE);
        title.setText("水情");
        title.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

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
                //  query();
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


        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowAll) {
                    return;
                }
                isShowAll = true;
                //刷新数据
                EventBus.getDefault().post(dataList);

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
                search(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final FragmentManager manager = getFragmentManager();
        final Fragment mapFragment = new WaterMapFragment();
        final Fragment listFragment = new WaterListFragment();


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
        //query();
    }

    private void simulationData(){
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String    s ="[{\"STCD\":\"60115000\",\"STNM\":\"大通\",\"RVNM\":\"null\",\"STLC\":\"null\",\"LGTD\":\"117.6955\",\"LTTD\":\"30.811111\",\"Z\":\"6.51\",\"Q\":\"19100.0\",\"WRZ\":\"6\"},{\"STCD\":\"60116200\",\"STNM\":\"长江下关\",\"RVNM\":\"null\",\"STLC\":\"null\",\"LGTD\":\"118.72728\",\"LTTD\":\"32.08332\",\"Z\":\"4.31\",\"Q\":\"0.0\",\"WRZ\":\"8.5\"},{\"STCD\":\"60401094\",\"STNM\":\"岳子河闸\",\"RVNM\":\"岳子河\",\"STLC\":\"岳子河闸                                          \",\"LGTD\":\"118.819479\",\"LTTD\":\"32.24323\",\"Z\":\"6.99\",\"Q\":\"0.0\",\"WRZ\":\"6\"},{\"STCD\":\"60404800\",\"STNM\":\"路南塘坝\",\"RVNM\":\"null\",\"STLC\":\"路南塘坝                                          \",\"LGTD\":\"118.64954\",\"LTTD\":\"32.11055\",\"Z\":\"26.42\",\"Q\":\"0.0\",\"WRZ\":\"6\"},{\"STCD\":\"62914200\",\"STNM\":\"滁河晓桥\",\"RVNM\":\"null\",\"STLC\":\"null\",\"LGTD\":\"118.54762\",\"LTTD\":\"32.15713\",\"Z\":\"6.98\",\"Q\":\"0.0\",\"WRZ\":\"9.5\"},{\"STCD\":\"62914800\",\"STNM\":\"滁河六合\",\"RVNM\":\"null\",\"STLC\":\"null\",\"LGTD\":\"118.8403\",\"LTTD\":\"32.33979\",\"Z\":\"6.99\",\"Q\":\"0.0\",\"WRZ\":\"7.85\"},{\"STCD\":\"62916400\",\"STNM\":\"葛塘\",\"RVNM\":\"马汊河\",\"STLC\":\"葛塘                                              \",\"LGTD\":\"118.737354\",\"LTTD\":\"32.251839\",\"Z\":\"4.14\",\"Q\":\"0.0\",\"WRZ\":\"6\"},{\"STCD\":\"62916900\",\"STNM\":\"划子口闸\",\"RVNM\":\"划子口河\",\"STLC\":\"划子口闸                                          \",\"LGTD\":\"118.951505\",\"LTTD\":\"32.192352\",\"Z\":\"6.97\",\"Q\":\"4.3\",\"WRZ\":\"null\"}]";
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    dataList = JsonUtil.jsonToList(s, WaterBean.class);
                    EventBus.getDefault().post(dataList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒
    }

    private void query() {

        progressDialog.show();
        String url = Constants.ServerURL+"apiWater?tm=" + queryDate + "+" + DateUtil.getTime();
        //String url = Constants.ServerIP + "/njfx/allWaterRegime!SYQ?tm=" + queryDate + "+" + DateUtil.getTime();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                 if (null == s || s.equals("")) {
                    return;
                }
                try {
                    dataList = JsonUtil.jsonToList(s, WaterBean.class);
                    EventBus.getDefault().post(dataList);
                } catch (Exception e) {
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

    private void filter(String district) {
//        if (district.equals("全部")) {
//            EventBus.getDefault().post(dataList);
//            return;
//        }
//        List<WaterBean> resultList = new ArrayList<>();
//        for (WaterBean waterBean : dataList) {
//            if (waterBean.getCITY().contains(district)) {
//                resultList.add(waterBean);
//            }
//        }
//        EventBus.getDefault().post(resultList);
    }

    /**
     * 显示超警戒站点
     */
    private void showFavorites() {
        List<WaterBean> resultList = new ArrayList<>();
        for (WaterBean waterBean : dataList) {
            if (Double.parseDouble(waterBean.getZ())>Double.parseDouble(waterBean.getWRZ())) {
                resultList.add(waterBean);
            }
        }
        EventBus.getDefault().post(resultList);
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
        if (index == 1) {
            district.setText("地点：全部");
        }
    }

    private void search(String x) {
        if (x.equals("")) {
            EventBus.getDefault().post(dataList);
            return;
        }
        List<WaterBean> resultList = new ArrayList<>();
        for (WaterBean waterBean : dataList) {
            if (waterBean.getSTNM().contains(x)) {
                resultList.add(waterBean);
            }
        }
        EventBus.getDefault().post(resultList);
    }
}
