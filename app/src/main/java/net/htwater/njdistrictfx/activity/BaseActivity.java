package net.htwater.njdistrictfx.activity;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;


import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.core.SystemBarTintManager;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 透明状态栏
         */
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // getWindow().addFlags(
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            if (SharedPreferencesUtil.isFloodSeason()) {
                tintManager.setStatusBarTintResource(R.color.theme_orange);
            } else {
                tintManager.setStatusBarTintResource(R.color.theme_blue);
            }
        }
        MyApplication.addActivity(this);
//        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.removeActivity(this);
    }
}
