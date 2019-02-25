package net.htwater.njdistrictfx.activity.SYQ;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
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
import net.htwater.njdistrictfx.activity.WebViewActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.view.MyDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by LZY on 2017/4/6.县市区面雨量
 */
public class CountryRainActivity extends BaseActivity {
    private String queryDate;
    private ProgressDialog progressDialog;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_rain);

        final TextView date = (TextView) findViewById(R.id.date);
        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        final ListView listView = (ListView) findViewById(R.id.listView);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        TextView rightButton = (TextView) findViewById(R.id.rightButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        queryDate = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd");

        date.setText(queryDate);
        final MyDatePickerDialog dateDialog = new MyDatePickerDialog(this, queryDate, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                queryDate = MyDatePickerDialog.transformFormat(year, monthOfYear, dayOfMonth);
                date.setText(queryDate);

                query();
            }
        });

        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RAIN;
                    Intent intent = new Intent(CountryRainActivity.this, SyqActivity.class);
                    JSONArray getArray = adapter.getArray();
                    if (getArray.length() > position) {
                        JSONObject object = getArray.getJSONObject(position);
                        String STNM = object.getString("STNM");
                        if (STNM.contains("南京")) {
                            intent.putExtra("iniType", "全部");
                        } else {
                            intent.putExtra("iniType", STNM);
                        }
                    }
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        title.setText("面雨量");
        rightButton.setText("等值面");

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog.show();
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CountryRainActivity.this, WebViewActivity.class);
                intent.putExtra("name", "等值面");
                intent.putExtra("url", "http://218.2.110.162/njfxphone_page/htmls/water-dengzhi.html");
                startActivity(intent);
            }
        });

        query();
    }

    private void query() {
        progressDialog.show();

        String url = Constants.ServerIP + "/njfx/queryRainArea!SYQ?tm=" + queryDate + "+" + DateUtil.getTime();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    // if (jsonArray.length() > 0) {
                    adapter.setData(jsonArray);
                    // }
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


    class MyAdapter extends BaseAdapter {
        private JSONArray array;
        private final Context context;

        public MyAdapter(Context context) {
            super();
            this.context = context;
            array = new JSONArray();
        }

        public void setData(JSONArray array) {
            this.array = array;
            notifyDataSetChanged();
        }

        public JSONArray getArray() {
            return array;
        }

        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, R.layout.listitem_area_water, null);
            TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
            TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
            TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
            TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
            TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);
            // TextView tv6 = (TextView) convertView.findViewById(R.id.tv6);

            try {
                JSONObject object = array.getJSONObject(position);
                tv1.setText(Html.fromHtml("<u>" + object.getString("STNM") + "</u>"));
                tv2.setText(object.getDouble("TVALUE") + "");
                tv3.setText(object.getDouble("YVALUE") + "");
                tv4.setText(object.getDouble("BYVALUE") + "");
                tv5.setText(object.getDouble("TTVALUE") + "");
                // tv6.setText(object.getInt("STVALUE") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
