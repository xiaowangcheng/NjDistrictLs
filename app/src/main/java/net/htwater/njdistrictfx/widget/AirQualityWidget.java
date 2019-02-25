package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityWidget extends Widget {
    private final Context context;
    private TextView pm;
    private TextView primarypollutant;
    private TextView AQI;
    private TextView affect;
    private ImageView level;
    private TextView action;

    public AirQualityWidget(Context context) {
        super(context, "air quality");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_air_quality, null);
        pm = (TextView) contentView.findViewById(R.id.pm);
        primarypollutant = (TextView) contentView.findViewById(R.id.primarypollutant);
        AQI = (TextView) contentView.findViewById(R.id.AQI);
        affect = (TextView) contentView.findViewById(R.id.affect);
        level = (ImageView) contentView.findViewById(R.id.level);
        action = (TextView) contentView.findViewById(R.id.action);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        String url = MyApplication.UpdateServerIP + ":8080/SmartWaterServices/QueryPM25!WEATHER?city=nanjing";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                if (null == response || response.length() <= 0) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(response);

                    switch (object.getString("level")) {
                        case "优":
                            level.setBackground(context.getResources().getDrawable(R.mipmap.zhsl_you));
                            break;
                        case "良":
                            level.setBackground(context.getResources().getDrawable(R.mipmap.zhsl_liang));
                            break;
                        case "差":
                            level.setBackground(context.getResources().getDrawable(R.mipmap.zhsl_cha));
                            break;
                    }
                    pm.setText("PM2.5:" + object.getString("PM2.5/1h") + ",PM10:" + object.getString("PM10/1h"));
                    primarypollutant.setText(object.getString("primarypollutant"));
                    AQI.setText("空气质量指数：" + object.getString("AQI"));
                    affect.setText("对健康的影响：" + object.getString("affect"));
                    action.setText("建议采取措施：" + object.getString("action"));
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

    @Override
    protected void refresh() {
        request();
    }
}
