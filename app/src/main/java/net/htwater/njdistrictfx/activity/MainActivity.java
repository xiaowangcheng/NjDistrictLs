package net.htwater.njdistrictfx.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.CheckUpdateEvent;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.core.TabFragmentHost;
import net.htwater.njdistrictfx.fragment.FunctionFragment;
import net.htwater.njdistrictfx.fragment.HomeFragment;
import net.htwater.njdistrictfx.fragment.SettingFragment;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends BaseActivity {
    private long pressTime = 0;
    private final String[] mTextArray = {"首页", "功能", "设置"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabFragmentHost mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
        final TextView title = (TextView) findViewById(R.id.title);
        TextView enter = (TextView) findViewById(R.id.enter);

        initMenu();

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);//去掉tab之间的分割线
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("设置")) {
                    title.setVisibility(View.GONE);
                } else {
                    title.setVisibility(View.VISIBLE);
                }
            }
        });

        Class[] mFragmentArray = {HomeFragment.class, FunctionFragment.class, SettingFragment.class};
        for (int i = 0; i < mFragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i])
                    .setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
        }

        if (SharedPreferencesUtil.isLeader()) {
            enter.setVisibility(View.VISIBLE);
        }
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JuzhangActivity.class);
                startActivity(intent);
            }
        });

        verifyPermissions();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - pressTime < 2000) {
            MyApplication.finishAll();
        } else {
            pressTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onEvent(CheckUpdateEvent event) {
        checkNewVersion(false);
    }

    private void initMenu() {
        String menu = SharedPreferencesUtil.getMenu();
        if (menu.isEmpty()) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(menu);
            JSONArray module = jsonObject.getJSONArray("module");
            StringBuilder functionId = new StringBuilder();
            StringBuilder functionName = new StringBuilder();
            for (int i = 0; i < module.length(); i++) {
                JSONObject jsonObject1 = module.getJSONObject(i);
                functionId.append(jsonObject1.getString("class_id")).append(",");
                functionName.append(jsonObject1.getString("menu")).append(",");
            }
            String functionIdStr = functionId.toString();
            String functionNameStr = functionName.toString();
            functionIdStr = functionIdStr.substring(0, functionIdStr.length() - 1);
            functionNameStr = functionNameStr.substring(0, functionNameStr.length() - 1);
            SharedPreferencesUtil.setFunctionIdList(functionIdStr);
            SharedPreferencesUtil.setFunctionNameList(functionNameStr);

            String[] ids = SharedPreferencesUtil.getFunctionIdList().split(",");
            String[] keys = new String[ids.length];
            for (int i = 0; i < ids.length; i++) {
                keys[i] = "class_id#" + ids[i];
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : keys) {
                if (!jsonObject.has(key)) {
                    continue;
                }
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    stringBuilder.append(jsonObject1.getString("menu")).append(",");
                }
            }
            String y = stringBuilder.toString();
            SharedPreferencesUtil.setFunctionSubitemList(y.substring(0, y.length() - 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View getTabItemView(int index) {
        int[] mImageArray = {R.drawable.ic_home_selector, R.drawable.ic_function_selector,
                R.drawable.ic_setting_selector};

        View view = View.inflate(this, R.layout.item_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        imageView.setImageResource(mImageArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(mTextArray[index]);

        return view;
    }

    public void checkNewVersion(final boolean isAutoCheck) {
        String url = MyApplication.UpdateServerIP + ":10010/htmarket/getAppVersion!public?appid=" + Constants.APPID;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(response);
                    int version = object.getInt("version");
                    final String apkfile = object.getString("apkfile");
                    if (version > MyApplication.versionCode) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("检测到新版本，是否升级？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = MyApplication.UpdateServerIP + ":10010/htmarket_files/apk/" + apkfile;
                                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                                // downloadApk(url);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    } else {
                        if (!isAutoCheck) {
                            Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
                        }
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
        MyApplication.mQueue.add(request);
    }

    /**
     * 检测是否安装插件包
     */
    private boolean hasApp(String packageName) {
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
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

    private int getVerCode(String packageName) {
        int verCode = -1;
        try {
            verCode = getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    private void verifyPermissions() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO};
        for (String permission : permissions) {
            int x = ActivityCompat.checkSelfPermission(this, permission);
            if (x != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

}
