package net.htwater.njdistrictfx.activity.ENG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
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
import net.htwater.njdistrictfx.view.MyScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2018/11/19.
 */

public class FxjbActivity extends BaseActivity {
    private  MyScrollView scrollView;
    private  MyAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView listView=null;
    private  JSONArray jsonArray = new JSONArray();
    private  FrameLayout topBar;
    private  View topBarView =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fxjb_activity);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("防汛简报");
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        listView = (ListView) findViewById(R.id.listView);
        scrollView = (MyScrollView) findViewById(R.id.scrollView);
        topBar = (FrameLayout)findViewById(R.id.topBar);
        topBarView = View.inflate(FxjbActivity.this, R.layout.topbar_fxjb, null);

        EditText name = (EditText) findViewById(R.id.name);
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
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        simulationData();
        //reuqest();
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
                if (jsonObject.getString("STREETNAME").contains(x)) {
                    filterArray.put(jsonObject);
                }
            }
            adapter.setData(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void simulationData(){
        final String s ="[{\"ID\":\"19\",\"STREETNAME\":\"葛塘街道\",\"FLOODPREVENTION\":\"2018-11-29 10:38:56\",\"ACTION\":\"主要负责人在防汛一线，现场指挥严重积淹水点应急排水工作，排水管理单位、城管、市政人员冒雨行动，及时处置各处积淹水点，在积淹水点基本消除后继续安排专人值守，开展善后工作。\",\"PUMPTRUCK\":\"4\",\"PUMPSTATION\":\"2\",\"VEHICLE\":\"2\",\"ACTIONNUMBER\":\"60\",\"LODGINGTREE\":\"11\"},{\"ID\":\"20\",\"STREETNAME\":\"葛塘街道\",\"FLOODPREVENTION\":\"2018-12-28 16:15:38\",\"ACTION\":\"23232323\",\"PUMPTRUCK\":\"1\",\"PUMPSTATION\":\"1\",\"VEHICLE\":\"1\",\"ACTIONNUMBER\":\"1\",\"LODGINGTREE\":\"1\"},{\"ID\":\"14\",\"STREETNAME\":\"长芦街道\"," +
                "\"FLOODPREVENTION\":\"2018-11-29 15:55:15\",\"ACTION\":\"主要负责人在防汛一线，现场指挥严重积淹水点应急排水工作，排水管理单位、城管、市政人员冒雨行动，及时处置各处积淹水点，在积淹水点基本消除后继续安排专人值守，开展善后工作。\",\"PUMPTRUCK\":\"3\",\"PUMPSTATION\":\"3\",\"VEHICLE\":\"1\",\"ACTIONNUMBER\":\"70\",\"LODGINGTREE\":\"4\"},{\"ID\":\"13\",\"STREETNAME\":\"大厂街道\",\"FLOODPREVENTION\":\"2018-11-29 15:53:13\",\"ACTION\":\"主要负责人在防汛一线，现场指挥严重积淹水点应急排水工作，排水管理单位、城管、市政人员冒雨行动，及时处置各处积淹水点，在积淹水点基本消除后继续安排专人值守，开展善后工作。\",\"PUMPTRUCK\":\"2\",\"PUMPSTATION\":\"3\",\"VEHICLE\":\"2\",\"ACTIONNUMBER\":\"50\",\"LODGINGTREE\":\"10\"}]";
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
                    topBar.addView(topBarView);
                    adapter = new  MyAdapter(FxjbActivity.this,jsonArray);
                    listView.setAdapter(adapter);
                    listView.setOnTouchListener(new View.OnTouchListener() {
                        float x = 0;

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            scrollView.onTouchEvent(event);

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x = event.getX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    // isClick = Math.abs(event.getX() - x) <= 10;//? true : false;
                                    break;
                            }
                            return false;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },1000); // 延时1秒

    }


    private void reuqest(){
        progressDialog.show();
        String url = Constants.ServerURL + "apiPrereport";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                     jsonArray = new JSONArray(s.replace("null", "—"));
                    topBar.addView(topBarView);
                    adapter = new  MyAdapter(FxjbActivity.this,jsonArray);
                    listView.setAdapter(adapter);
                    listView.setOnTouchListener(new View.OnTouchListener() {
                        float x = 0;

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            scrollView.onTouchEvent(event);

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x = event.getX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    // isClick = Math.abs(event.getX() - x) <= 10;//? true : false;
                                    break;
                            }
                            return false;
                        }
                    });
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

        private final Context context;
        private JSONArray jsonArray = new JSONArray();
        public MyAdapter(Context context,JSONArray jsonArray) {
            super();
            this.context = context;
            this.jsonArray = jsonArray;
        }
        public void setData(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            this.notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return jsonArray.length();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAdapter.Holder holder;
            if (null == convertView) {
                holder = new  MyAdapter.Holder();
                convertView = View.inflate(context, R.layout.listitem_fxjb, null);
                holder.scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollView);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);
                holder.tv6 = (TextView) convertView.findViewById(R.id.tv6);
                holder.tv7 = (TextView) convertView.findViewById(R.id.tv7);
                holder.tv8 = (TextView) convertView.findViewById(R.id.tv8);
                scrollView.addToList(holder.scrollView);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.Holder) convertView.getTag();
            }
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.tv1.setText(jsonObject.getString("STREETNAME"));
                holder.tv2.setText(jsonObject.getString("FLOODPREVENTION"));
                holder.tv3.setText(jsonObject.getString("ACTION"));
                holder.tv4.setText(jsonObject.getString("PUMPTRUCK"));
                holder.tv5.setText(jsonObject.getString("PUMPSTATION"));

                holder.tv6.setText(jsonObject.getString("VEHICLE"));
                holder.tv7.setText(jsonObject.getString("ACTIONNUMBER"));
                holder.tv8.setText(jsonObject.getString("LODGINGTREE"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        class Holder {
            HorizontalScrollView scrollView;
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            TextView tv5;
            TextView tv6;
            TextView tv7;
            TextView tv8;
        }
    }

}
