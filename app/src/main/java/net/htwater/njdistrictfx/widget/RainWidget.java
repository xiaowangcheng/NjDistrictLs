package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RainWidget extends Widget {
    private final Context context;
    // private WebView webView;
    private MyAdapter myAdapter;

    public RainWidget(Context context) {
        super(context, "rain");
        this.context = context;
        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_rain, null);
        // webView = (WebView) contentView.findViewById(R.id.summary1);
        GridView gridView = (GridView) contentView.findViewById(R.id.gridView);

        myAdapter = new MyAdapter(context);
        gridView.setAdapter(myAdapter);

//        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8

//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                String[] array = message.split("-");
//                Intent intent = new Intent(context, WaterDetailActivity.class);
//                intent.putExtra("stnm", array[1]);
//                intent.putExtra("stcd", array[2]);
//                context.startActivity(intent);
//
//                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
//                return true;
//            }
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//
//            }
//        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        StringRequest stringRequest = new StringRequest(Constants.ServerIP + "/njfx/getRainSta!SYQ",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        if (null == response || response.length() <= 0) {
                            return;
                        }
                        try {
                            JSONObject object = new JSONObject(response);
                            String statics = object.getString("statics");
                            // String summary = object.getString("summary2");

                            // webView.loadData("<h5>" + summary + "</>", "text/html; charset=UTF-8", null);

                            String[] array = statics.replace("[", "").replace("]", "").split(",");
                            myAdapter.setData(array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(stringRequest);
    }

    class MyAdapter extends BaseAdapter {
        private final Context context;
        private List<String> list = new ArrayList<>();

        public MyAdapter(Context context) {
            super();
            this.context = context;
        }

        public void setData(String[] array) {
//            list.add("雨量(mm)");
//            list.add("今天");
//            list.add("昨天");
//            list.add("前天");
//            list.add("三天");
            list.add("≥200");
            list.add(array[0]);
            list.add(array[1]);
            list.add(array[2]);
            list.add(array[3]);
            list.add("100~200");
            list.add(array[5]);
            list.add(array[6]);
            list.add(array[7]);
            list.add(array[8]);
            list.add("50~100");
            list.add(array[10]);
            list.add(array[11]);
            list.add(array[12]);
            list.add(array[13]);
            list.add("30~50");
            list.add(array[15]);
            list.add(array[16]);
            list.add(array[17]);
            list.add(array[18]);
            list.add("0~30");
            list.add(array[20]);
            list.add(array[21]);
            list.add(array[22]);
            list.add(array[23]);
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

            if (!list.isEmpty()) {
                textView.setText(list.get(position));
            }
            return textView;
        }

    }
}
