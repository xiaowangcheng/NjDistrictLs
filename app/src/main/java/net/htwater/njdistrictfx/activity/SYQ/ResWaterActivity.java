package net.htwater.njdistrictfx.activity.SYQ;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.Date;
import java.util.List;


/**
 * Created by lzy on 2018/3/23
 */
public class ResWaterActivity extends SyqActivity {
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
    private String[] districts = Constants.districts;
    private String[] types = new String[]{"全部", "中型水库", "小一型水库", "小二型水库"};
    private int filterIndex1 = 0;
    private int filterIndex2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syq);

        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        RelativeLayout districtLayout = (RelativeLayout) findViewById(R.id.districtLayout);
        district = (TextView) findViewById(R.id.district);
        EditText name = (EditText) findViewById(R.id.name);
        showAll = (TextView) findViewById(R.id.showAll);
        showMe = (TextView) findViewById(R.id.showMe);
        RelativeLayout typeLayout = (RelativeLayout) findViewById(R.id.typeLayout);
        type = (TextView) findViewById(R.id.type);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        typeLayout.setVisibility(View.VISIBLE);
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

        blue = getResources().getColor(R.color.theme_blue);
        white = Color.WHITE;

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

        date.setText("时间：" + queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText("时间：" + queryDate);

                query();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ResWaterActivity.this);
                builder.setItems(districts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = "地点：";
                        district.setText(text + districts[i]);
                        filterIndex1 = i;
                        try {
                            filter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // filter1(districts[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                        // restoreFilterButton(2);
                    }
                });
                builder.show();
            }
        });
        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResWaterActivity.this);
                builder.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        type.setText("类型：" + types[i]);
                        filterIndex2 = i;
                        try {
                            filter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // filter2(types[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                        // restoreFilterButton(3);
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

        query();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.setFavorites(favoritesList);
    }

    // http://218.2.110.162:8080/njfx/Queryreswater!SYQ?TM=2017-03-15+14%3A36
    private void query() {
        progressDialog.show();

        String method = "Queryreswater", param = "TM";
        String url = Constants.ServerIP + "/njfx/" + method + "!SYQ?" + param + "=" + queryDate + "+" + DateUtil.getTime();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    if (iniType.length() > 0) {
                        type.setText("类型：" + iniType);
                        filter2(iniType);
                        filterIndex2 = 1;
                    } else {
                        type.setText("类型：中型");
                        filter2("中型");
                        filterIndex2 = 1;
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

    private void filter() throws JSONException {
        if (filterIndex1 == 0 && filterIndex2 == 0) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        String key = "CITY";
        JSONArray filterArray = new JSONArray();
        if (filterIndex1 != 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(districts[filterIndex1])) {
                    filterArray.put(jsonObject);
                }
            }
        } else {
            filterArray = jsonArray;
        }
        JSONArray filterArray2 = new JSONArray();
        if (filterIndex2 != 0) {
            for (int i = 0; i < filterArray.length(); i++) {
                JSONObject jsonObject = filterArray.getJSONObject(i);
                String typeValue = jsonObject.getString("RES_STTP");
                if (types[filterIndex2].contains("大型") && typeValue.equals("0201")
                        || types[filterIndex2].contains("中型") && typeValue.equals("0202")
                        || types[filterIndex2].contains("小一型") && typeValue.equals("0203")
                        || types[filterIndex2].contains("小二型") && typeValue.equals("0204")) {
                    filterArray2.put(jsonObject);
                }
            }
        } else {
            filterArray2 = filterArray;
        }
        EventBus.getDefault().post(filterArray2);
    }

    // 点雨量STLC、河道水情水库水情CITY、泵站水情CITYNM
    private void filter1(String district) {
        if (district.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        String key = "CITY";
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
                if (jsonObject.getString("_s").contains(x)) {
                    filterArray.put(jsonObject);
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
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        filterIndex1 = 0;
        filterIndex2 = 0;
    }

}
