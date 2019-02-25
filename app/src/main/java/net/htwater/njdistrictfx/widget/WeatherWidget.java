package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class WeatherWidget extends Widget {
    private final Context context;
    private TextView temp;
    private TextView info;
    private TextView date2, info2, temp2, wind2, date3, info3, temp3, wind3, meiyu;
    private ImageView image;

    public WeatherWidget(Context context) {
        super(context, "weather");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_weather, null);
        temp = (TextView) contentView.findViewById(R.id.temp);
        info = (TextView) contentView.findViewById(R.id.info);
        date2 = (TextView) contentView.findViewById(R.id.date2);
        info2 = (TextView) contentView.findViewById(R.id.info2);
        temp2 = (TextView) contentView.findViewById(R.id.temp2);
        wind2 = (TextView) contentView.findViewById(R.id.wind2);
        date3 = (TextView) contentView.findViewById(R.id.date3);
        info3 = (TextView) contentView.findViewById(R.id.info3);
        temp3 = (TextView) contentView.findViewById(R.id.temp3);
        wind3 = (TextView) contentView.findViewById(R.id.wind3);
        image = (ImageView) contentView.findViewById(R.id.image);
        meiyu = (TextView) contentView.findViewById(R.id.meiyu);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        String tm = DateUtil.parseDate2String(new Date(), DateUtil.DATETIME_SHORT_FORMAT4);
        String url = Constants.ServerIP + "/WeatherImgServices/getWeatherInfo!img?tm=" + tm + "&stanum=58238&type=3d";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(response.replace("null", "—"));
                    handle(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(stringRequest);

        String tm2 = DateUtil.parseDate2String(new Date(), "yyyy");
        String url2 = Constants.ServerIP + "/njfx/getRumeiAPP!SYQ?year=" + tm2;
        StringRequest stringRequest2 = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String days = jsonObject.getString("days");
                    if (null != days && !days.equals("null")) {
                        meiyu.setText("入梅第" + days + "天");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(stringRequest2);
    }

    private void handle(JSONArray jsonArray) {
        try {
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            JSONObject jsonObject2 = jsonArray.getJSONObject(1);
            JSONObject jsonObject3 = jsonArray.getJSONObject(2);

            temp.setText(transform(jsonObject1.getString("minTemp24h")) + "-" + transform(jsonObject1.getString("maxTemp24h")) + "℃");
            info.setText(jsonObject1.getString("stationName") + " " + DateUtil.parseDate2String(new Date(), DateUtil.DATE_FORMAT4)
                    + " " + jsonObject1.getString("weather12h").replace("—", "多云"));
            if (!jsonObject1.getString("weather12h").equals("—")) {
                String imageUrl = Constants.ServerIP + "/weathericon/" + jsonObject1.getString("weather12h") + ".png";
                Picasso.with(context).load(imageUrl).into(image);
            }

            date2.setText(DateUtil.parseDate2String(DateUtil.getDiffDate(new Date(), "D+1"), DateUtil.DATE_FORMAT4));
            info2.setText(jsonObject2.getString("weather12h").replace("—", "多云"));
            temp2.setText(transform(jsonObject2.getString("minTemp24h")) + "-" + transform(jsonObject2.getString("maxTemp24h")) + "℃");
            wind2.setText(jsonObject2.getString("windDirection12h") + jsonObject2.getString("maxWindSpeed12h"));

            date3.setText(DateUtil.parseDate2String(DateUtil.getDiffDate(new Date(), "D+2"), DateUtil.DATE_FORMAT4));
            info3.setText(jsonObject3.getString("weather12h").replace("—", "多云"));
            temp3.setText(transform(jsonObject3.getString("minTemp24h")) + "-" + transform(jsonObject3.getString("maxTemp24h")) + "℃");
            wind3.setText(jsonObject3.getString("windDirection12h") + jsonObject3.getString("maxWindSpeed12h"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int transform(String x) {
        int index = x.indexOf(".");
        if (index == -1) {
            return Integer.valueOf(x);
        } else {
            return Integer.valueOf(x.substring(0, index));
        }
    }

    @Override
    protected void refresh() {
        request();
    }
}
