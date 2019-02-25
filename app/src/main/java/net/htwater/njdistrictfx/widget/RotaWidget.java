package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
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

/**
 * Created by zth on 2017/5/21.
 */

public class RotaWidget extends Widget {
    private final Context context;
    //今日值班领导
    private TextView tv1;
    //今日值班组员
    private TextView tv2;
    private TextView tv5;
    private TextView tv9;//驾驶员
    //明日值班领导
    private TextView tv3;
    //明日值班组员
    private TextView tv4;
    private TextView tv7;
    private TextView tv10;//驾驶员

    public RotaWidget(Context context) {
        super(context, "rota");
        this.context = context;

        initView();
    }

    private void initView() {
        contentView = View.inflate(context, R.layout.layout_widget_rota, null);
        tv1 = (TextView) contentView.findViewById(R.id.tv1);
        tv2 = (TextView) contentView.findViewById(R.id.tv2);
        tv3 = (TextView) contentView.findViewById(R.id.tv3);
        tv4 = (TextView) contentView.findViewById(R.id.tv4);
        tv5 = (TextView) contentView.findViewById(R.id.tv5);
        tv7 = (TextView) contentView.findViewById(R.id.tv7);
        tv9 = (TextView) contentView.findViewById(R.id.tv9);
        tv10 = (TextView) contentView.findViewById(R.id.tv10);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(params);

        request();
    }

    private void request() {
        String url = Constants.ServerIP + "/njfx/queryFORyearmonth!Fxyw?year="
                + DateUtil.parseDate2String(new Date(), "yyyy") + "&month=" + DateUtil.parseDate2String(new Date(), "M") + "&day=2";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    handle(jsonArray);
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

    private void handle(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i == 0) {
                    List<String> members = getMemberHtml(jsonObject.getString("member_name"));
                    tv1.setText(jsonObject.getString("master_name"));
                    tv2.setText(Html.fromHtml(members.get(0)));
                    tv5.setText(Html.fromHtml(members.get(1)));
                    String jiashi_name = jsonObject.getString("jiashi_name");
                    if (null == jiashi_name || jiashi_name.length() == 0) {
                        tv9.setText("驾驶员：无");
                    } else {
                        tv9.setText("驾驶员：" + jiashi_name);
                    }
                } else if (i == 1) {
                    List<String> members = getMemberHtml(jsonObject.getString("member_name"));
                    tv3.setText(jsonObject.getString("master_name"));
                    tv4.setText(Html.fromHtml(members.get(0)));
                    tv7.setText(Html.fromHtml(members.get(1)));
                    String jiashi_name = jsonObject.getString("jiashi_name");
                    if (null == jiashi_name || jiashi_name.length() == 0) {
                        tv10.setText("驾驶员：无");
                    } else {
                        tv10.setText("驾驶员：" + jiashi_name);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getMemberHtml(String names) {
        List<String> rtn = new ArrayList<>();
        String[] namsArray = names.split(";");
        String rtn1 = "";
        String rtn2 = "";
        int i = 1;
        for (String s : namsArray) {
            if (i > 1) {
                if ((i % 2) == 1) {
                    if (!rtn1.equals("")) {
                        rtn1 = rtn1 + "<br />";
                    }
                    rtn1 = rtn1 + s;
//                    if (s.contains("★")) {
//                        rtn1 = rtn1 + "<u>" + s.replace("★", "") + "</u>";
//                    } else {
//                        rtn1 = rtn1 + s;
//                    }
                } else if ((i % 2) == 0) {
                    if (!rtn2.equals("")) {
                        rtn2 = rtn2 + "<br />";
                    }
                    rtn2 = rtn2 + s;
                }
            } else {
                rtn1 = rtn1 + s;
//                if (s.contains("★")) {
//                    rtn1 = rtn1 + "<u>" + s.replace("★", "") + "</u>";
//                } else {
//                    rtn1 = rtn1 + s;
//                }
            }
            i++;
        }
        rtn.add(rtn1);
        rtn.add(rtn2);
        return rtn;
    }

    @Override
    protected void refresh() {
        request();
    }
}
