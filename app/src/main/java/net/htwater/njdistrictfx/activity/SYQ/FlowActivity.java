package net.htwater.njdistrictfx.activity.SYQ;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.util.Util;
import net.htwater.njdistrictfx.view.MyDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lzy on 2017/5/25.流量
 */
public class FlowActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private MyAdapter adapter;
    private String queryDate;
    private JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        ListView listView = (ListView) findViewById(R.id.listView);
        final TextView date = (TextView) findViewById(R.id.date);
        RelativeLayout dateLayout = (RelativeLayout) findViewById(R.id.dateLayout);
        EditText name = (EditText) findViewById(R.id.name);

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

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

        date.setText("时间：" + queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText("时间：" + queryDate);

                request();
            }
        });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.show();
            }
        });

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        progressDialog = new ProgressDialog(FlowActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject jsonObject = (JSONObject) adapter.getItem(position);
                    Intent intent = new Intent(FlowActivity.this, WaterLevelDetailActivity.class);
                    intent.putExtra("stnm", jsonObject.getString("STNM"));
                    intent.putExtra("stcd", jsonObject.getString("STCD"));
                    intent.putExtra("wrz", jsonObject.getString("WRZ"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        title.setText("周边站点");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        simulationData();
        //request();
    }

    private  void simulationData(){
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String s ="[{\"STCD\":\"60115000\",\"STNM\":\"大通\",\"LGTD\":\"117.6955\",\"LTTD\":\"30.811111\",\"Z\":\"6.46\",\"Q\":\"19500.0\",\"WRZ\":\"6\"},{\"STCD\":\"60116200\",\"STNM\":\"长江下关\",\"LGTD\":\"118.72728\",\"LTTD\":\"32.08332\",\"Z\":\"4.02\",\"Q\":\"0.0\",\"WRZ\":\"8.5\"},{\"STCD\":\"62914200\",\"STNM\":\"滁河晓桥\",\"LGTD\":\"118.54762\",\"LTTD\":\"32.15713\",\"Z\":\"7.03\",\"Q\":\"0.0\",\"WRZ\":\"9.5\"},{\"STCD\":\"62914800\",\"STNM\":\"滁河六合\",\"LGTD\":\"118.8403\",\"LTTD\":\"32.33979\",\"Z\":\"7.01\",\"Q\":\"0.0\",\"WRZ\":\"7.85\"}]";
                try {
                    progressDialog.dismiss();
                    jsonArray = handle(new JSONArray(s.replace("null", "—")));
                    adapter.setData(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒

    }

    private void request() {
        progressDialog.show();

        String url = Constants.ServerURL + "apiZbzd?tm=" + queryDate + "+" + DateUtil.getTime();
        //String url = Constants.ServerIP + "/njfx/QueryFlow!SYQ?TM=" + queryDate + "+" + DateUtil.getTime();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                try {
                    jsonArray = handle(new JSONArray(s.replace("null", "—")));
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

    private JSONArray handle(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        try {
            for (int i = jsonArray.length() - 1; i > -1; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(0, jsonObject);
               /* String HNNM = jsonObject.getString("HNNM");
                if (HNNM.equals("—")) {
                    list.add(jsonObject);
                } else {
                    list.add(0, jsonObject);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray();
        for (JSONObject jsonObject : list) {
            resultArray.put(jsonObject);
        }
        return resultArray;
    }

    private void search(String x) {
        if (x.equals("")) {
            adapter.setData(jsonArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("STNM").contains(x)) {
                    filterArray.put(jsonObject);
                }
            }
            adapter.setData(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyAdapter extends BaseAdapter {
        private JSONArray jsonArray = new JSONArray();

        public void setData(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(FlowActivity.this, R.layout.listitem_syq, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);
                holder.tv6 = (TextView) convertView.findViewById(R.id.tv6);
                holder.tv7 = (TextView) convertView.findViewById(R.id.tv7);

                holder.tv7.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                double z = Util.get2Double(jsonObject.getDouble("Z"));
                holder.tv2.setText(CommonUtil.format2Two(z + ""));
                String wrz = jsonObject.getString("WRZ");
                if (wrz.equals("—")) {
                    holder.tv3.setText("—");

                    holder.tv2.setTextColor(Color.BLACK);
                } else {
                    double wrzdouble = Util.get2Double(jsonObject.getDouble("WRZ"));
                    holder.tv3.setText(CommonUtil.format2Two(wrzdouble+""));

                    if (z > wrzdouble) {
                        holder.tv2.setTextColor(Color.RED);
                    } else {
                        holder.tv2.setTextColor(Color.BLACK);
                    }
                }
                holder.tv4.setText(jsonObject.getString("Q"));

                holder.tv5.setVisibility(View.GONE);
                holder.tv6.setVisibility(View.GONE);
                /*holder.tv5.setText(jsonObject.getString("HNNM"));
                holder.tv6.setText(jsonObject.getString("TM"));*/
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        //"TM": "2017-05-25 09:00:00"
        private String transformTime(String time) {
            int index1 = time.indexOf("-");
            int index2 = time.lastIndexOf(":");
            return time.substring(index1 + 1, index2);
        }
    }

    private class Holder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        TextView tv5;
        TextView tv6;
        TextView tv7;
    }
}
