package net.htwater.njdistrictfx.adapter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.DownloadActivity;
import net.htwater.njdistrictfx.activity.ENG.FxjbActivity;
import net.htwater.njdistrictfx.activity.GCXX.GcxxActivity;
import net.htwater.njdistrictfx.activity.GCXX.PumpActivity;
import net.htwater.njdistrictfx.activity.GCXX.ResActivity;
import net.htwater.njdistrictfx.activity.GCXX.RiverActivity;
import net.htwater.njdistrictfx.activity.GQ.GqActivity;
import net.htwater.njdistrictfx.activity.GQ.PumpHistoryActivity;
import net.htwater.njdistrictfx.activity.Pdf2Activity;
import net.htwater.njdistrictfx.activity.SYQ.CountryRainActivity;
import net.htwater.njdistrictfx.activity.SYQ.FlowActivity;
import net.htwater.njdistrictfx.activity.SYQ.ResWaterActivity;
import net.htwater.njdistrictfx.activity.SYQ.SyqActivity;
import net.htwater.njdistrictfx.activity.SYQ.WaterActivity;
import net.htwater.njdistrictfx.activity.TxlActivity;
import net.htwater.njdistrictfx.activity.WebViewActivity;
import net.htwater.njdistrictfx.activity.XqQueryActivity;
import net.htwater.njdistrictfx.activity.XqReportActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by LZY on 2017/5/27.
 */

public class FunctionAdapter extends BaseAdapter {
    private Context context;
    private final List<String> list;
    private boolean isJuzhang = false;

    public FunctionAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public FunctionAdapter(Context context, List<String> list, boolean isJuzhang) {
        this.context = context;
        this.list = list;
        this.isJuzhang = isJuzhang;
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.griditem_function, null);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);
        TextView tv = (TextView) convertView.findViewById(R.id.textView);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView);

        int x = MyApplication.widthPixels / 3;
        int y;
        if (isJuzhang) {
            y = MyApplication.widthPixels / 4;
        } else {
            y = MyApplication.widthPixels / 3;
        }
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(x, y);
        convertView.setLayoutParams(layoutParams);

        String text = list.get(position);
        tv.setText(text);
//        if (text.equals("公告栏")) {
//            tv.setTextColor(context.getResources().getColor(R.color.text_gray));
//        }
        iv.setBackgroundResource(DataUtil.getIconId(list.get(position)));

        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (list.get(position)) {
                    case "水情":
                        intent = new Intent(context, WaterActivity.class);
                        break;
                    case "河道水情":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RIVER_WATER;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "潮位":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_TIDE;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "水库水情":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RES_WATER;
                        intent = new Intent(context, ResWaterActivity.class);
                        intent.putExtra("iniType", "中型");
                        break;
                    case "泵站水情":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_PUMP_WATER;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "积淹点水情":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_FLOOD;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "点雨量":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RAIN;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "面雨量":
                        intent = new Intent(context, CountryRainActivity.class);
                        break;
                    case "水闸水情":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_GATE_WATER;
                        intent = new Intent(context, SyqActivity.class);
                        break;
                    case "水闸工情":
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_GATE;
                        intent = new Intent(context, GqActivity.class);
                        break;
                    case "泵站工情":
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_PUMP;
                        intent = new Intent(context, GqActivity.class);
                        break;
                    case "周边站点":
                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_FLOW;
                        intent = new Intent(context, FlowActivity.class);
                        break;
                    case "历史机泵":
                        intent = new Intent(context, PumpHistoryActivity.class);
                        break;
                    case "水库":
                        intent = new Intent(context, ResActivity.class);
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_RES;
                        break;
                    /*case "水质":
                        intent = new Intent(context, QaterQualityActivity.class);
                        break;*/
                    case "泵站":
                        intent = new Intent(context, PumpActivity.class);
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_PUMP;
                        break;
                    case "水闸":
                        intent = new Intent(context, GcxxActivity.class);
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_GATE;
                        break;
                    case "堤防":
                        intent = new Intent(context, GcxxActivity.class);
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_DIKE;
                        break;
                    case "河道":
                        intent = new Intent(context, RiverActivity.class);
                        MyApplication.CONSTANS_ENGINEERING_TYPE = Constants.CONSTANS_RIVER;
                        break;
                    case "重要视频点":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                            intent.putExtra("type", "important");
                            intent.putExtra("userName", SharedPreferencesUtil.getAccount());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "流域视频点":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                            intent.putExtra("type", "river");
                            intent.putExtra("userName", SharedPreferencesUtil.getAccount());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "区域视频点":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                            intent.putExtra("type", "district");
                            intent.putExtra("userName", SharedPreferencesUtil.getAccount());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "来源视频点":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                            intent.putExtra("type", "source");
                            intent.putExtra("userName", SharedPreferencesUtil.getAccount());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "工程视频点":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                            intent.putExtra("type", "project");
                            intent.putExtra("userName", SharedPreferencesUtil.getAccount());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "视频监控":
                        if (hasApp(Constants.VIDEO_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.VIDEO_PACKAGE_NAME, "com.login.LoginActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("url", MyApplication.VideoUrl);
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载视频监控插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.VIDEOAPPID);
                        }
                        break;
                    case "联动会商":
                        if (hasApp(Constants.LDHS_PACKAGE_NAME)) {
                            intent = new Intent();
                            ComponentName localComponentName = new ComponentName(
                                    Constants.LDHS_PACKAGE_NAME, "com.jutong.live.FirstActivity");
                            intent.setComponent(localComponentName);
                            intent.setAction("android.intent.action.VIEW");
                            intent.putExtra("realname", SharedPreferencesUtil.getUserName());
                            intent.putExtra("type", SharedPreferencesUtil.getType());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "请下载联动会商插件后安装使用。", Toast.LENGTH_SHORT).show();
                            getPlugin(Constants.LDHSAPPID);
                        }
                        break;
                    case "卫星云图":
                    case "雷达图":
                    case "天气预报":
                    case "专业预报":
                    case "气象云图":
                    case "降水预报":
                    case "台风信息":
                    case "短临降雨":
                    case "三天天气图":
                    case "公告栏":
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("name", list.get(position));
                        break;
                    case "防汛简报":
                        intent = new Intent(context, FxjbActivity.class);
                        break;
                    case "防汛预案":
                        intent = new Intent(context, Pdf2Activity.class);
                        break;
                    case "水系图":
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("name", list.get(position));
                        break;
                   /* case "调查评价成果":
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("name", list.get(position));
                        break;*/
                    case "险工险段上报":
                        intent = new Intent(context, XqReportActivity.class);
                        break;
                    case "险工险段查询":
                        intent = new Intent(context, XqQueryActivity.class);
                        break;
                    case "防汛值班":
                        intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("name", list.get(position));
                        break;
                    case "通讯录":
                        intent = new Intent(context, TxlActivity.class);
                        break;
                    default:
                        Toast.makeText(context.getApplicationContext(), "功能暂未开放", Toast.LENGTH_SHORT).show();
                }
                try {
                    if (null != intent) {
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context.getApplicationContext(), "未安装该功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    private void getPlugin(String appid) {
        String url = MyApplication.UpdateServerIP + ":10010/htmarket/getAppVersion!public?appid=" + appid;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                if (null == response || response.length() <= 0) {
                    return;
                }
                String apkfile = null;
                try {
                    JSONObject object = new JSONObject(response);
                    apkfile = object.getString("apkfile");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = MyApplication.UpdateServerIP + ":10010/htmarket_files/apk/" + apkfile;
                Intent intent = new Intent(context, DownloadActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        MyApplication.mQueue.add(request);
    }

    /**
     * 检测是否安装插件包
     */
    private boolean hasApp(String packageName) {
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.versionName == null) {
                continue;
            }
            if (packageName.equals(p.packageName)) {
                return true;
            }
        }
        return false;
    }
}
