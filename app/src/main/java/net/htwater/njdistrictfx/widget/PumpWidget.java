package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PumpWidget extends Widget {
    private final Context context;
    private MyAdapter adapter;

    public PumpWidget(Context context) {
        super(context, "pump");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_pump, null);
        ListView listView = (ListView) contentView.findViewById(R.id.listView);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        String tm = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd") + "+" + DateUtil.getTime();
        String url = Constants.ServerIP + "/njfx/QueryPumpgqPC!GQ?tm=" + tm;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    adapter.setData(handleData(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);
    }

    private JSONArray handleData(JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                boolean isfocus = jsonObject.getBoolean("isfocus");
                if (isfocus) {
                    jsonObjects.add(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray();
        for (JSONObject jsonObject : jsonObjects) {
            resultArray.put(jsonObject);
        }
        return resultArray;
    }

    @Override
    protected void refresh() {
        request();
    }

    class MyAdapter extends BaseAdapter {
        private JSONArray jsonArray = new JSONArray();

        private void setData(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, R.layout.listitem_pump_widget, null);
            TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
            TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
            TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
            TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
            TextView tv5 = (TextView) convertView.findViewById(R.id.tv5);

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                tv1.setText(jsonObject.getString("PNM"));
                tv2.setText(jsonObject.getString("CN"));
                tv3.setText(jsonObject.getString("CNT"));
                tv4.setText(jsonObject.getString("RTFLOW"));
                tv5.setText(jsonObject.getString("ZLL"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }
}
