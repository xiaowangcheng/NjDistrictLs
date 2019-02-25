package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmergencyWidget extends Widget {
    private final Context context;
    private MyAdapter adapter;
    private TextView level;
    private TextView stlc;

    public EmergencyWidget(Context context) {
        super(context, "emergency");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_emergency, null);
        GridView gridView = (GridView) contentView.findViewById(R.id.gridView);
        level = (TextView) contentView.findViewById(R.id.level);
        stlc = (TextView) contentView.findViewById(R.id.stlc);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        String url = Constants.ServerIP + "/njfx/getEmergencyResp!Fxyw";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONArray jsonArray1 = new JSONArray();
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        jsonArray1.put(jsonObject);
                    }
                    adapter.setData(jsonArray1);
                    setFirstData(jsonArray.getJSONObject(0));
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

    private void setFirstData(JSONObject jsonObject) {
        try {
            stlc.setText(jsonObject.getString("STLC"));
            String value = "";
            int color = Color.BLACK;
            switch (jsonObject.getInt("LEVEL")) {
                case 0:
                    value = "未启动";
                    color = Color.parseColor("#676767");
                    break;
                case 1:
                    value = "Ⅰ级";
                    color = Color.parseColor("#9D1F23");
                    break;
                case 2:
                    value = "Ⅱ级";
                    color = Color.parseColor("#E9961C");
                    break;
                case 3:
                    value = "Ⅲ级";
                    color = Color.parseColor("#AEB122");
                    break;
                case 4:
                    value = "Ⅳ级";
                    color = Color.parseColor("#229DED");
                    break;
            }
            level.setText(value);
            level.setTextColor(color);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            View view = View.inflate(context, R.layout.griditem_emergency, null);
            TextView level = (TextView) view.findViewById(R.id.level);
            TextView stlc = (TextView) view.findViewById(R.id.stlc);

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                stlc.setText(jsonObject.getString("STLC"));
                String value = "";
                int color = Color.BLACK;
                switch (jsonObject.getInt("LEVEL")) {
                    case 0:
                        value = "未启动";
                        color = Color.parseColor("#676767");
                        level.setTextSize(12f);
                        break;
                    case 1:
                        value = "Ⅰ级";
                        color = Color.parseColor("#9D1F23");
                        break;
                    case 2:
                        value = "Ⅱ级";
                        color = Color.parseColor("#E9961C");
                        break;
                    case 3:
                        value = "Ⅲ级";
                        color = Color.parseColor("#AEB122");
                        break;
                    case 4:
                        value = "Ⅳ级";
                        color = Color.parseColor("#229DED");
                        break;
                }
                level.setText(value);
                level.setTextColor(color);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }

    }
}
