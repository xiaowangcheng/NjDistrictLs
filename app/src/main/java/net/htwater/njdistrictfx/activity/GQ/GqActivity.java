package net.htwater.njdistrictfx.activity.GQ;

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
import net.htwater.njdistrictfx.bean.WaterBean;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.fragment.ListAndMap.GqListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.GqMapFragment;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.util.JsonUtil;
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
public class GqActivity extends BaseActivity {
    private String queryDate;
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    private boolean isShowAll = true;
    private TextView showAll;
    private TextView showMe;
    private int blue;
    private int white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gq);

        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        TextView title = (TextView) findViewById(R.id.title);
        RelativeLayout districtLayout = (RelativeLayout) findViewById(R.id.districtLayout);
        final TextView district = (TextView) findViewById(R.id.district);
        EditText name = (EditText) findViewById(R.id.name);
        showAll = (TextView) findViewById(R.id.showAll);
        showMe = (TextView) findViewById(R.id.showMe);
        RelativeLayout filterLayout3 = (RelativeLayout) findViewById(R.id.filterLayout3);
        TextView filter3 = (TextView) findViewById(R.id.filter3);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_PUMP) {
            title.setText("泵站工情");
            title.setVisibility(View.VISIBLE);
            districtLayout.setVisibility(View.GONE);
        }
//        else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_GATE) {
//            filterLayout3.setVisibility(View.VISIBLE);
//        }

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

        blue = getResources().getColor(R.color.theme_blue);
        white = Color.WHITE;

        date.setText("时间：" + queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText("时间：" + queryDate);

                query();
                //点击时间或筛选条件时恢复顶栏的全部按钮
                restoreTopButton();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(GqActivity.this);
                builder.setItems(districts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        district.setText("地点：" + districts[i]);
                        filter(districts[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                    }
                });
                builder.show();
            }
        });
        filterLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] districts = Constants.districts;
                AlertDialog.Builder builder = new AlertDialog.Builder(GqActivity.this);
                builder.setItems(districts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        district.setText("地点：" + districts[i]);
                        filter(districts[i]);
                        //点击时间或筛选条件时恢复顶栏的全部按钮
                        restoreTopButton();
                    }
                });
                builder.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
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
        final Fragment listFragment = new GqListFragment();
        final Fragment mapFragment = new GqMapFragment();

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
        RelativeLayout rl_tab =  findViewById(R.id.rl_tab);
        rl_tab.setVisibility(View.GONE);
        simulationData();
        //query();
    }

    private void simulationData(){
        progressDialog.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String s ="";
                String method = null;
                switch (MyApplication.CONSTANS_ENGINEERING_TYPE) {
                    case Constants.CONSTANS_GATE:
                        method = "QueryszgqPc";
                        break;
                    case Constants.CONSTANS_PUMP:
                        //method = "QueryPumpgqPC";
                        method ="apiPumpz";
                        s ="[{\"PUMPCODE\":\"1\",\"PUMPNAME\":\"赵桥河西雨水泵站\",\"LGTD\":\"118.810231\",\"LTTD\":\"32.280827\",\"FLOW\":\"15\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"13\",\"PUMPNAME\":\"三河泵站\",\"LGTD\":\"118.7053\",\"LTTD\":\"32.11705\",\"FLOW\":\"24\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"1\"},{\"PUMPCODE\":\"3\",\"PUMPNAME\":\"中心河雨水泵站\",\"LGTD\":\"118.8434\",\"LTTD\":\"32.26998\",\"FLOW\":\"30\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"1\"},{\"PUMPCODE\":\"11\",\"PUMPNAME\":\"双涵泵站\",\"LGTD\":\"118.663987\",\"LTTD\":\"32.083622\",\"FLOW\":\"12\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"12\",\"PUMPNAME\":\"联合泵站\",\"LGTD\":\"118.642978\",\"LTTD\":\"32.047787\",\"FLOW\":\"12\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"3\"},{\"PUMPCODE\":\"6\",\"PUMPNAME\":\"石佛农场泵站\",\"LGTD\":\"118.670303\",\"LTTD\":\"32.082664\",\"FLOW\":\"26\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"10\",\"PUMPNAME\":\"引水河泵站\",\"LGTD\":\"118.747162\",\"LTTD\":\"32.136118\",\"FLOW\":\"24\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"5\"},{\"PUMPCODE\":\"8\",\"PUMPNAME\":\"黄狼泵站\",\"LGTD\":\"118.6677\",\"LTTD\":\"32.08121\",\"FLOW\":\"20\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"1\"},{\"PUMPCODE\":\"5\",\"PUMPNAME\":\"坝子窑泵站\",\"LGTD\":\"118.700365\",\"LTTD\":\"32.085115\",\"FLOW\":\"36\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"1\"},{\"PUMPCODE\":\"7\",\"PUMPNAME\":\"五里泵站\",\"LGTD\":\"118.651226\",\"LTTD\":\"32.006602\",\"FLOW\":\"30\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"9\",\"PUMPNAME\":\"复兴泵站\",\"LGTD\":\"118.727922\",\"LTTD\":\"32.166295\",\"FLOW\":\"12\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"2\",\"PUMPNAME\":\"长丰河雨水泵站\",\"LGTD\":\"118.8296\",\"LTTD\":\"32.24887\",\"FLOW\":\"25\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"},{\"PUMPCODE\":\"4\",\"PUMPNAME\":\"通江集雨水泵站\",\"LGTD\":\"118.869084\",\"LTTD\":\"32.204931\",\"FLOW\":\"30\",\"CURRENTNUMBER\":\"5\",\"OPENNUM\":\"2\"}]";

                        break;
                }

                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    // jsonArray = handleKDS(jsonArray);
                    EventBus.getDefault().post(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒
    }


    // http://218.2.110.162:8080/njfx/QueryPumpgqPC!GQ?TM=2017-05-15+14%3A36
    private void query() {
        progressDialog.show();

        String method = null;
        switch (MyApplication.CONSTANS_ENGINEERING_TYPE) {
            case Constants.CONSTANS_GATE:
                method = "QueryszgqPc";
                break;
            case Constants.CONSTANS_PUMP:
                //method = "QueryPumpgqPC";
                method ="apiPumpz";
                findViewById(R.id.rl_tab).setVisibility(View.GONE);
                break;
        }
       String url = Constants.ServerURL+ method+ "?tm=" + queryDate + "+" + DateUtil.getTime();
        //   String url = Constants.ServerIP + "/njfx/" + method + "!GQ?tm=" + queryDate + "+" + DateUtil.getTime();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    // jsonArray = handleKDS(jsonArray);
                    EventBus.getDefault().post(jsonArray);
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

    private JSONArray handleKDS(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String KDS = handle2(jsonObject.getString("KDS"));
                jsonObject.put("KDS", KDS);
                if (KDS.startsWith("0")) {
                    list.add(jsonObject);
                } else {
                    list.add(0, jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray();
        for (JSONObject jsonObject : list) {
            resultArray.put(jsonObject);
        }
        return resultArray;
    }

    private String handle2(String kds) {
        String[] array = kds.split(",");
        int number = 0;
        for (String x : array) {
            if (!x.equals("0")) {
                number++;
            }
        }
        return number + "/" + array.length;
    }

    private void filter(String district) {
        if (district.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("CITY").contains(district)) {
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
                if (jsonObject.getString("PUMPNAME").contains(x)) {
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
}
