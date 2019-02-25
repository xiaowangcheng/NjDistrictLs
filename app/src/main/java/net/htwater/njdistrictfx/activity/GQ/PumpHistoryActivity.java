package net.htwater.njdistrictfx.activity.GQ;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.adapter.PumpAdapter;
import net.htwater.njdistrictfx.adapter.PumpHistoryAdapter;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.view.MyDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lzy on 2018/7/30.历史机泵
 */
public class PumpHistoryActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private PumpHistoryAdapter adapter;
    private String queryDate;
    private AlertDialog pumpDialog;
    private String stcd;//当前选中的站点
    private TextView pump;
    // private TextView district;
    // private AlertDialog districtDialog;
    // private String curDistrict = "";//当前选中的区县
    // private Map<String, List<JSONObject>> pumpMap = new HashMap<>();
    private List<JSONObject> pumpList = new ArrayList<>();
    private boolean isShowPumpList = false;
    private PumpAdapter pumpAdapter;
    private TextView rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_history);

        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        ListView listView = (ListView) findViewById(R.id.listView);
        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        // district = (TextView) findViewById(R.id.district);
        pump = (TextView) findViewById(R.id.pump);
        rightButton = (TextView) findViewById(R.id.rightButton);
        final LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
        final LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout2);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final ListView pumpLv = (ListView) findViewById(R.id.pumpLv);

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

        date.setText(queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText(queryDate);

                requestData();
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.show();
            }
        });

        adapter = new PumpHistoryAdapter(this);
        listView.setAdapter(adapter);

        pumpAdapter = new PumpAdapter(this);
        pumpLv.setAdapter(pumpAdapter);
        pumpLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject jsonObject = (JSONObject) pumpAdapter.getItem(position);
                    stcd = jsonObject.getString("STCD");
                    requestData();
                    pump.setText(jsonObject.getString("STNM"));

                    rightButton.performClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        progressDialog = new ProgressDialog(PumpHistoryActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        title.setText("历史机泵");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rightButton.setText("模糊\n查询");
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPumpList) {
                    layout2.setVisibility(View.GONE);
                    layout1.setVisibility(View.VISIBLE);
                    isShowPumpList = false;

                    editText.setText("");

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
                } else {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                    isShowPumpList = true;

                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);

                    pumpAdapter.setData(pumpList);
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String x = s.toString().trim();
                List<JSONObject> resultList = new ArrayList<>();
                try {
                    for (JSONObject jsonObject : pumpList) {
                        String stnm = jsonObject.getString("STNM");
                        if (stnm.contains(x)) {
                            resultList.add(jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pumpAdapter.setData(resultList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(Constants.simpledistricts, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                curDistrict = Constants.simpledistricts[which];
//                district.setText(curDistrict);
//
//                createPumpDialog(pumpMap.get(curDistrict));
//            }
//        });
//        builder.setNegativeButton("取消", null);
//        districtDialog = builder.create();

//        district.setText(Constants.simpledistricts[0]);
//        district.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                districtDialog.show();
//            }
//        });
        pump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == pumpDialog) {
                    return;
                }
                pumpDialog.show();
            }
        });

        requestPump();
    }

    @Override
    public void onBackPressed() {
        if (isShowPumpList) {
            rightButton.performClick();
        } else {
            super.onBackPressed();
        }
    }

    private void requestPump() {
        progressDialog.show();

        String url = Constants.ServerIP + "/njfx/getBasicInfo!GQ";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    final JSONArray pumpArray = new JSONArray(s);
                    pumpList.clear();
                    String[] pumps = new String[pumpArray.length()];
                    for (int i = 0; i < pumpArray.length(); i++) {
                        JSONObject jsonObject = pumpArray.getJSONObject(i);
                        pumpList.add(jsonObject);
                        pumps[i] = jsonObject.getString("STNM");
                        if (i == 0) {
                            stcd = jsonObject.getString("STCD");
                            requestData();
                            pump.setText(jsonObject.getString("STNM"));
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(PumpHistoryActivity.this);
                    builder.setItems(pumps, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                stcd = pumpList.get(which).getString("STCD");
                                requestData();
                                pump.setText(pumpList.get(which).getString("STNM"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    pumpDialog = builder.create();
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

//    private void requestPump() {
//        progressDialog.show();
//
//        String url = Constants.ServerIP + "/njfx/getBasicInfo!GQ";
//        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                 try {
//                    final JSONArray pumpArray = new JSONArray(s);
//                    for (String x : Constants.simpledistricts) {
//                        List<JSONObject> list = new ArrayList<>();
//                        for (int i = 0; i < pumpArray.length(); i++) {
//                            JSONObject jsonObject = pumpArray.getJSONObject(i);
//                            String STLC = jsonObject.getString("STLC");
//                            if (STLC.contains(x)) {
//                                list.add(jsonObject);
//                            }
//                        }
//                        pumpMap.put(x, list);
//                    }
//                    createPumpDialog(pumpMap.get(Constants.simpledistricts[0]));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                progressDialog.dismiss();
//                volleyError.printStackTrace();
//            }
//        });
//        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
//        MyApplication.mQueue.add(request);
//    }

//    private void createPumpDialog(final List<JSONObject> childList) {
//        String[] pumps = new String[childList.size()];
//        try {
//            for (int i = 0; i < childList.size(); i++) {
//                JSONObject jsonObject = childList.get(i);
//                pumps[i] = jsonObject.getString("STNM");
//                if (i == 0) {
//                    stcd = jsonObject.getString("STCD");
//                    requestData();
//                    pump.setText(jsonObject.getString("STNM"));
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(PumpHistoryActivity.this);
//        builder.setItems(pumps, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    stcd = childList.get(which).getString("STCD");
//                    requestData();
//                    pump.setText(childList.get(which).getString("STNM"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        builder.setNegativeButton("取消", null);
//        pumpDialog = builder.create();
//    }

    private void requestData() {
        if (null == stcd) {
            return;
        }
        String url = Constants.ServerIP + "/njfx/QueryPumpLineByDay!GQ?stcd=" + stcd + "&tm=" + queryDate;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    adapter.setData(jsonArray);
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
}
