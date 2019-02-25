package net.htwater.njdistrictfx.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by laizhiyi on 2016/05/10
 */
public class MyApplication extends Application {
    //mate8,dpi2.75
    public static float density;
    /**
     * 屏幕横向宽度，单位dp
     */
    public static int width;
    public static int height;
    public static int heightPixels;
    public static int widthPixels;
    public static SharedPreferences sp;
    public static RequestQueue mQueue;
    public static String UpdateServerIP = "http://www.htwater.net";
    // public static String ServerIP = "http://typhoon.nbwater.gov.cn";
    public static String VideoUrl = "http://61.153.21.221:52581";
    private static final List<Activity> activities = new ArrayList<>();
    public static int versionCode;
    public static String versionName;
    public static int CONSTANS_ENGINEERING_TYPE = 0;
    public static int CONSTANS_WATER_TYPE = 0;
    public static int CONSTANS_TYPE_RAIN = 0;//记录点雨量中当前显示的是哪天
    private static String IMEI;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        density = metrics.density;
        width = (int) (metrics.widthPixels / density);
        heightPixels = metrics.heightPixels;
        widthPixels = metrics.widthPixels;
        height = (int) (heightPixels / density);

        sp = getSharedPreferences("configuration", MODE_PRIVATE);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //可能会因为获取不到权限而奔溃
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTheme(R.style.AppTheme_Blue);

//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.register(new IUmengRegisterCallback() {
//            @Override
//            public void onSuccess(String deviceToken) {
//                SharedPreferencesUtil.setdeviceToken(deviceToken);
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//
//            }
//        });
//
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                String tag = null;
//                try {
//                    JSONObject jsonObject = new JSONObject(msg.custom);
//                    tag = jsonObject.getString("tag");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String videoroomid = msg.extra.get("videoroomid");
//
//                if ("sphs".equals(tag)) {
//                    Intent intent = new Intent();
//                    ComponentName localComponentName = new ComponentName(
//                            Constants.LDHS_PACKAGE_NAME, "com.jutong.live.FirstActivity");
//                    intent.setComponent(localComponentName);
//                    intent.setAction("android.intent.action.VIEW");
//                    intent.putExtra("realname", SharedPreferencesUtil.getUserName());
//                    intent.putExtra("type", SharedPreferencesUtil.getType());
//                    intent.putExtra("videoroomid", videoroomid);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//
//            }
//        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);


        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);//是否输出debug日志，开启debug会影响性能


        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            SharedPreferencesUtil.setVersionCode(packageInfo.versionCode);
            SharedPreferencesUtil.setVersionName(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //避免7.0系统以上传递URI崩溃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
