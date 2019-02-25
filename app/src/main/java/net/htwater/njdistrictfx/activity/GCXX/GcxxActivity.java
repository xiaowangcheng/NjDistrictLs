package net.htwater.njdistrictfx.activity.GCXX;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by lzy on 2017/4/17
 */
public class GcxxActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    private TextView filterTv1;
    private TextView filterTv2;
    private TextView filterTv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcxx);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        TextView title = (TextView) findViewById(R.id.title);
        RelativeLayout filterLayout1 = (RelativeLayout) findViewById(R.id.filterLayout1);
        filterTv1 = (TextView) findViewById(R.id.filterTv1);
        EditText name = (EditText) findViewById(R.id.name);
        RelativeLayout filterLayout2 = (RelativeLayout) findViewById(R.id.filterLayout2);
        filterTv2 = (TextView) findViewById(R.id.filterTv2);
        RelativeLayout filterLayout3 = (RelativeLayout) findViewById(R.id.filterLayout3);
        filterTv3 = (TextView) findViewById(R.id.filterTv3);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        switch (MyApplication.CONSTANS_ENGINEERING_TYPE) {
            case Constants.CONSTANS_PUMP:
                title.setText("泵站");
                filterTv2.setText("级别：全部");
                break;
            case Constants.CONSTANS_GATE:
                title.setText("水闸");
                filterLayout3.setVisibility(View.VISIBLE);
                break;
            case Constants.CONSTANS_DIKE:
                title.setText("堤防");
                filterTv1.setText("防洪标准：全部");
                filterTv2.setText("工程级别：全部");
                break;
            case Constants.CONSTANS_RIVER:
                title.setText("河道");
                filterTv1.setText("管理单位：全部");
                break;
            default:
                break;
        }

        filterLayout1.setOnClickListener(filterListener1);
        filterLayout2.setOnClickListener(filterListener2);
        filterLayout3.setOnClickListener(filterListener3);
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

        query();
    }

    // http://218.2.110.162:8080/njfx/Queryres!SLGC
    private void query() {
        progressDialog.show();

        String method = null;
        switch (MyApplication.CONSTANS_ENGINEERING_TYPE) {
            case Constants.CONSTANS_RIVER:
                method = "QueryriverInfo";
                break;
            case Constants.CONSTANS_RES:
                method = "Queryres";
                break;
            case Constants.CONSTANS_DIKE:
                method = "Querydf";
                break;
            case Constants.CONSTANS_GATE:
                method = "Querygate";
                break;
            case Constants.CONSTANS_PUMP:
                method = "Querypumb";
                break;
        }
        String url = Constants.ServerIP + "/njfx/" + method + "!SLGC";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_GATE) {
                        filter2("闸");
                        filterTv2.setText("类型：闸");
                    } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
                        filter2("中型");
                        filterTv2.setText("类型：中型");
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

    private void filter1(String tv) {
        if (tv.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        String key = "COUNTY";
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
            key = "CITY";
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
            key = "DSFLST";
            tv = tv.substring(0, tv.length() - 1);
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
            key = "ADUNNM";
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(tv)) {
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
        String key = "ENTYNM";//ENCL
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_PUMP) {
            key = "ENCL";
            type = type.substring(0, 1);
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
            key = "DKCL";
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String typeValue = jsonObject.getString(key);
                if (typeValue.contains(type)) {
                    filterArray.put(jsonObject);
                }
            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filter3(String type) {
        if (type.equals("全部")) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        String key = "HLNM";
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String typeValue = jsonObject.getString(key);
                if (typeValue.equals("—")) {
                    continue;
                }
                if (typeValue.contains(type)) {
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

    private View.OnClickListener filterListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String[] array1;
            if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                array1 = Constants.government;
            } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
                array1 = Constants.fanghong;
            } else {
                array1 = Constants.districts;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(GcxxActivity.this);
            builder.setItems(array1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    restoreFilterText();
                    String text;
                    if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                        text = "管理单位：";
                    } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
                        text = "防洪标准：";
                    } else {
                        text = "地点：";
                    }
                    filterTv1.setText(text + array1[i]);
                    filter1(array1[i]);
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener filterListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String[] array1;
            if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
                array1 = Constants.riverTypes;
            } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RES) {
                array1 = Constants.resTypes;
            } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_GATE) {
                array1 = Constants.gateTypes;
            } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
                array1 = Constants.gcjb;
            } else {
                array1 = Constants.pumpTypes;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(GcxxActivity.this);
            builder.setItems(array1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    restoreFilterText();
                    String text;
                    if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_PUMP) {
                        text = "级别：";
                    } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
                        text = "工程级别：";
                    } else {
                        text = "类型：";
                    }
                    filterTv2.setText(text + array1[i]);
                    filter2(array1[i]);
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener filterListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String[] array1 = Constants.zk;
            AlertDialog.Builder builder = new AlertDialog.Builder(GcxxActivity.this);
            builder.setItems(array1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    restoreFilterText();
                    String text = "闸孔数：";
                    filterTv3.setText(text + array1[i]);
                    filter3(array1[i]);
                }
            });
            builder.show();
        }
    };

    private void restoreFilterText() {
        switch (MyApplication.CONSTANS_ENGINEERING_TYPE) {
            case Constants.CONSTANS_PUMP:
                filterTv1.setText("地点：全部");
                filterTv2.setText("级别：全部");
                break;
            case Constants.CONSTANS_DIKE:
                filterTv1.setText("防洪标准：全部");
                filterTv2.setText("工程级别：全部");
                break;
            case Constants.CONSTANS_RIVER:
                filterTv1.setText("管理单位：全部");
                filterTv2.setText("类型：全部");
                break;
            case Constants.CONSTANS_RES:
                filterTv1.setText("地点：全部");
                filterTv2.setText("类型：全部");
                break;
            case Constants.CONSTANS_GATE:
                filterTv1.setText("地点：全部");
                filterTv2.setText("类型：全部");
                filterTv3.setText("闸孔数：全部");
        }
    }
}
