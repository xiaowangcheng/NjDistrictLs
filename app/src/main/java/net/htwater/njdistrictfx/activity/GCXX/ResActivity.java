package net.htwater.njdistrictfx.activity.GCXX;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by lzy on 2018/3/22.工程信息-水库
 */
public class ResActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    private TextView filterTv1;
    private TextView filterTv2;
    private int filterIndex1 = 0;//筛选条件1序号
    private int filterIndex2 = 0;//筛选条件2序号
    private String[] array1 = Constants.districts;
    private String[] array2 = Constants.resTypes;
    private JSONArray curArray = new JSONArray();//当前界面显示的数据（按名称过滤之前）
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcxx);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        TextView title = (TextView) findViewById(R.id.title);
        LinearLayout filterLayout = (LinearLayout) findViewById(R.id.filterLayout);
        RelativeLayout filterLayout1 = (RelativeLayout) findViewById(R.id.filterLayout1);
        filterTv1 = (TextView) findViewById(R.id.filterTv1);
        name = (EditText) findViewById(R.id.name);
        RelativeLayout filterLayout2 = (RelativeLayout) findViewById(R.id.filterLayout2);
        filterTv2 = (TextView) findViewById(R.id.filterTv2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");
        filterLayout.setVisibility(View.GONE);

        filterLayout1.setOnClickListener(filterListener1);
        filterLayout2.setOnClickListener(filterListener2);
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
        final Fragment listFragment = new GcxxListFragment();
        final Fragment mapFragment = new GcxxMapFragment();

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
        final String s ="[{\"GQBM\":\"BFAB4B002E5LA\",\"GQMC\":\"大顶山水库\",\"SPMC\":\"大顶山水库\",\"LV\":\"小（2）\",\"STREET\":\"泰山\",\"DRBSAR\":\"1.24000001\"," +
                "\"BLDT\":\"1970\",\"DSFLST\":\"20\",\"CHFLST\":\"200\",\"TTST\":\"32.56999969\",\"EFST\":\"15.25\"," +
                "\"CHFLLV\":\"45.00999832\",\"NRWTLV\":\"43.5\",\"XUNXLV\":\"43.5\",\"DMTPWD\":\"12.0\",\"MXDMHG\":\"9.0\",\"DMTPLN\":\"227.0\",\"BDHG\":\"43.5\"," +
                "\"YHDWD\":\"12.0\"}]";
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    EventBus.getDefault().post(jsonArray);
                    curArray = jsonArray;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                filterTv2.setText("类型：" + array2[1]);
                filterIndex2 = 1;
                try {
                    filter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒

    }

    // http://218.2.110.162:8080/njfx/Queryres!SLGC
    private void query() {
        progressDialog.show();

        String method = "Queryres";
        //String url = Constants.ServerIP + "/njfx/" + method + "!SLGC";
       String url = Constants.ServerURL+ "/apiRes/";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    EventBus.getDefault().post(jsonArray);
                    curArray = jsonArray;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                filterTv2.setText("类型：" + array2[1]);
                filterIndex2 = 1;
                try {
                    filter();
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
        JSONArray filterArray = new JSONArray();
        if (filterIndex1 != 0) {
            String key = "CITY";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(array1[filterIndex1])) {
                    filterArray.put(jsonObject);
                }
            }
        } else {
            filterArray = jsonArray;
        }
        JSONArray filterArray2 = new JSONArray();
        if (filterIndex2 != 0) {
            String key = "ENTYNM";
            for (int i = 0; i < filterArray.length(); i++) {
                JSONObject jsonObject = filterArray.getJSONObject(i);
                String typeValue = jsonObject.getString(key);
                if (typeValue.contains(array2[filterIndex2])) {
                    filterArray2.put(jsonObject);
                }
            }
        } else {
            filterArray2 = filterArray;
        }
        EventBus.getDefault().post(filterArray2);
        curArray = filterArray2;

        name.setText("");
    }

    private void search(String x) {
        if (x.equals("")) {
            EventBus.getDefault().post(curArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < curArray.length(); i++) {
                JSONObject jsonObject = curArray.getJSONObject(i);
                if (jsonObject.getString("GQMC").contains(x)) {
                    filterArray.put(jsonObject);
                }
            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener filterListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ResActivity.this);
            builder.setItems(array1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv1.setText("地点：" + array1[i]);
                    filterIndex1 = i;
                    try {
                        filter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener filterListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ResActivity.this);
            builder.setItems(array2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv2.setText("类型：" + array2[i]);
                    filterIndex2 = i;
                    try {
                        filter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.show();
        }
    };

}
