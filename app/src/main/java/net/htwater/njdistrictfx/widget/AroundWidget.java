package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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
import net.htwater.njdistrictfx.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pc on 2018/11/23.
 */

public class AroundWidget extends Widget {
    private final Context context;
    private   TextView time;
    private AroundWidget.MyAdapter  adapter;
    public AroundWidget(Context context) {
        super(context, "shuiwei");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_around, null);
        ListView listView = (ListView) contentView.findViewById(R.id.listView);
        time = (TextView) contentView.findViewById(R.id.time);

        adapter = new AroundWidget.MyAdapter();
        listView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);
        simulationData();
        //request();
    }
    private void simulationData(){
        String s ="[{\"STCD\":\"60115000\",\"STNM\":\"大通\",\"LGTD\":\"117.6955\",\"LTTD\":\"30.811111\",\"Z\":\"6.51\",\"Q\":\"19100.0\",\"WRZ\":\"6\"},{\"STCD\":\"60116200\",\"STNM\":\"长江下关\",\"LGTD\":\"118.72728\",\"LTTD\":\"32.08332\",\"Z\":\"4.29\",\"Q\":\"0.0\",\"WRZ\":\"8.5\"},{\"STCD\":\"62914200\",\"STNM\":\"滁河晓桥\",\"LGTD\":\"118.54762\",\"LTTD\":\"32.15713\",\"Z\":\"6.98\",\"Q\":\"0.0\",\"WRZ\":\"9.5\"},{\"STCD\":\"62914800\",\"STNM\":\"滁河六合\",\"LGTD\":\"118.8403\",\"LTTD\":\"32.33979\",\"Z\":\"6.99\",\"Q\":\"0.0\",\"WRZ\":\"7.85\"}]";

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

    private void request() {
        time.setText(getTime());

        String tm = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd") + "+" + DateUtil.getTime();
        String url = Constants.ServerURL + "apiZbzd?tm=" + tm;
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
        //super.refresh();
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
            AroundWidget.MyAdapter.Holder holder;
            if (null == convertView) {
                holder = new AroundWidget.MyAdapter.Holder();
                convertView = View.inflate(context, R.layout.listitem_shuiwei_around, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                // holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);

                convertView.setTag(holder);
            } else {
                holder = (AroundWidget.MyAdapter.Holder) convertView.getTag();
            }

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                double z = Util.get2Double(jsonObject.getDouble("Z"));
                holder.tv2.setText(z + "");
                String wrz = jsonObject.getString("WRZ");
                if (wrz.equals("—")) {
                    holder.tv3.setText("—");

                    holder.tv2.setTextColor(Color.BLACK);
                } else {
                    double wrzdouble = Util.get2Double(jsonObject.getDouble("WRZ"));
                    holder.tv3.setText(wrzdouble + "");

                    if (z > wrzdouble) {
                        holder.tv2.setTextColor(Color.RED);
                    } else {
                        holder.tv2.setTextColor(Color.BLACK);
                    }
                }
                holder.tv4.setText(jsonObject.getString("Q"));

                /*holder.tv5.setText(jsonObject.getString("HNNM"));
                holder.tv6.setText(jsonObject.getString("TM"));*/
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
