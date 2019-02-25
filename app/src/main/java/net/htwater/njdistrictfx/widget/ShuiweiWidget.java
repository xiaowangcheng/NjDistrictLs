package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShuiweiWidget extends Widget {
    private final Context context;
    private MyAdapter adapter;
    private TextView time;

    public ShuiweiWidget(Context context) {
        super(context, "shuiwei");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_shuiwei, null);
        ListView listView = (ListView) contentView.findViewById(R.id.listView);
        time = (TextView) contentView.findViewById(R.id.time);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        time.setText(getTime());

        String tm = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd") + "+" + DateUtil.getTime();
        String url = Constants.ServerIP + "/njfx/impStaWater!SYQ?tm=" + tm;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.equals("")) {
                    return;
                }
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
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);
    }

    private String getTime() {
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        return "时间: " + time;
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
            Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(context, R.layout.listitem_shuiwei_widget, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                // holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.tv1.setText(jsonObject.getString("STNM"));
                holder.tv2.setText(jsonObject.getString("Z"));
                holder.tv3.setText(jsonObject.getString("WRZ"));
                String tv2_value = jsonObject.getString("Z");
                String tv3_value = jsonObject.getString("WRZ");
                holder.tv2.setTextColor(Color.BLACK);
                if (null != tv2_value && !tv2_value.equals("null") && !tv2_value.equals("—") && !tv2_value.equals("-")
                        && null != tv3_value && !tv3_value.equals("null") && !tv3_value.equals("—") && !tv3_value.equals("-")) {
                    try {
                        double dbl_tv2 = Double.parseDouble(tv2_value);
                        double dbl_tv3 = Double.parseDouble(tv3_value);
                        if (dbl_tv2 > dbl_tv3) {
                            holder.tv2.setTextColor(Color.RED);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                holder.tv4.setText(jsonObject.getString("HISHIGHEST"));
                // holder.tv5.setText(jsonObject.getString("HISHIGHEST"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class Holder {
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            // TextView tv5;
        }
    }
}
