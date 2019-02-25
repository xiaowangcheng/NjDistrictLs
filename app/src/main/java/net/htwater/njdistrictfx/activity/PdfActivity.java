package net.htwater.njdistrictfx.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PdfActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jsonObject = (JSONObject) adapter.getItem(i);
                    String pdfname = jsonObject.getString("src");
                    String url = Constants.ServerIP + "/pdf/web/viewer.html?pdfurl=" + pdfname;
                    //String url = "http://59.83.223.39:8091/northOfYangtzeRiver/views/weatherForecast/professionalProductsForecast.html";
                    Intent intent = new Intent(PdfActivity.this, WebViewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        request();
    }

    private void request() {
        progressDialog.show();
        //http://122.227.159.82:8280/njfx/modules/yjya/queryFORfxya!Fxyw?type=
        String url = Constants.ServerIP + "/njfx/modules/yjya/queryFORfxya!Fxyw?type=";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    String id = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        if ("预案".equals(name)) {
                            id = jsonObject.getString("id");
                            break;
                        }
                    }
                    if (null == id) {
                        Toast.makeText(PdfActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String pId = jsonObject.getString("pId");
                        if (null != pId && id.equals(pId)) {
                            list.add(jsonObject);
                        }
                    }
                    adapter.setData(list);
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
        private List<JSONObject> list = new ArrayList<>();

        private void setData(List<JSONObject> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = new TextView(PdfActivity.this);

            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            tv.setLayoutParams(layoutParams);

            tv.setBackgroundColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(18, 0, 0, 0);
            try {
                tv.setText(list.get(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tv;
        }
    }
}
