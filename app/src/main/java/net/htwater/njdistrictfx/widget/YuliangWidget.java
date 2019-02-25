package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class YuliangWidget extends Widget {
    private final Context context;
    private MyAdapter adapter;
    private TextView time;

    public YuliangWidget(Context context) {
        super(context, "yuliang");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_yuliang, null);
        GridView gridView = (GridView) contentView.findViewById(R.id.gridView);
        time = (TextView) contentView.findViewById(R.id.time);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        time.setText(getTime());

        String tm = DateUtil.parseDate2String(new Date(), "yyyy-MM-dd") + "+" + DateUtil.getTime();
        String url = Constants.ServerIP + "/njfx/RainStatistic!SYQ?tm=" + tm;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "0"));
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

    private List<String> handleData(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString("STNM"));
                list.add(getValue(jsonObject.getString("MAX")));
                list.add(jsonObject.getDouble("TVALUE") + "");
                list.add(getName(jsonObject.getString("MAX")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //627B5460-桃花坝(石湫)(18.5),627B5480-独山(17.0)
    private String getValue(String x) {
        x = x.split(",")[0];
        int index1 = x.lastIndexOf("(");
        int index2 = x.lastIndexOf(")");
        return x.substring(index1 + 1, index2);
    }

    private String getName(String x) {
        x = x.split(",")[0];
        int index1 = x.indexOf("-");
        int index2 = x.indexOf("(");
        return x.substring(index1 + 1, index2).replace("无", "—");
    }

    private String getTime() {
        int x = Integer.valueOf(new SimpleDateFormat("H").format(new Date()));
        String startTime = "";
        if (x < 8) {
            //昨日八点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            startTime = new SimpleDateFormat("M-d").format(calendar.getTime()) + " 8:00";
        } else {
            //今日八点
            startTime = new SimpleDateFormat("M-d").format(new Date()) + " 8:00";
        }
        String endTime = new SimpleDateFormat("M-d H:mm").format(new Date());
        return "统计时段: " + startTime + " 至 " + endTime;
    }

    @Override
    protected void refresh() {
        request();
    }

    class MyAdapter extends BaseAdapter {
        private List<String> list = new ArrayList<>();

        public MyAdapter() {
            super();
        }

        private void setData(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
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
            convertView = View.inflate(context, R.layout.griditem_rain, null);
            TextView textView = (TextView) convertView;

            textView.setText(list.get(position));

            return textView;
        }

    }
}
