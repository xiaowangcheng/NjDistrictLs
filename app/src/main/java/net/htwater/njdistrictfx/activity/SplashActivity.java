package net.htwater.njdistrictfx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;


import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.User.LoginActivity;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            @SuppressLint("MissingPermission") String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            if (null != imei && !imei.isEmpty()) {
                SharedPreferencesUtil.setImei(imei);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        doSomething();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void doSomething() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jumpToLogin();
            }
        }).start();
    }

    private void jumpToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
