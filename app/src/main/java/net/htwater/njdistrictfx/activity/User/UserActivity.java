package net.htwater.njdistrictfx.activity.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.view.SwitchView;
import net.htwater.njdistrictfx.view.SwitchView.OnStateChangedListener;

public class UserActivity extends BaseActivity {
//    private TextView name;
    private SwitchView mySwitch;
//    private TextView logout;
//    private RelativeLayout layout;
    private boolean isAutoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesUtil.isFloodSeason()) {
            setTheme(R.style.AppTheme_Orange);
        } else {
            setTheme(R.style.AppTheme_Blue);
        }
        setContentView(R.layout.activity_user);

        TextView name = (TextView) findViewById(R.id.name);
        mySwitch = (SwitchView) findViewById(R.id.mySwitch);
        TextView logout = (TextView) findViewById(R.id.logout);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout3);
        RelativeLayout back = (RelativeLayout)findViewById(R.id.back);

        name.setText(SharedPreferencesUtil.getUserName());
        if (SharedPreferencesUtil.isAutoLogin()) {
            mySwitch.setOpened(true);
            isAutoLogin = true;
        }

        mySwitch.setOnStateChangedListener(new OnStateChangedListener() {

            @Override
            public void toggleToOn(View view) {
                isAutoLogin = true;
            }

            @Override
            public void toggleToOff(View view) {
                isAutoLogin = false;
            }
        });
        logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "退出登录",Toast.LENGTH_SHORT).show();
                SharedPreferencesUtil.setUserName("");
                SharedPreferencesUtil.setPassword("");
                SharedPreferencesUtil.setAccount("");

                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mySwitch.isOpened()) {
                    mySwitch.setOpened(false);
                    isAutoLogin = false;
                } else {
                    mySwitch.setOpened(true);
                    isAutoLogin = true;
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.setAutoLogin(isAutoLogin);
    }

//    private void initActionBar() {
//        ActionBar actionBar = this.getActionBar();
//        actionBar.setCustomView(R.layout.action_bar_back);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        if (MyApplication.isFloodSeason) {
//            actionBar.setBackgroundDrawable(getResources().getDrawable(
//                    R.drawable.background_orange));
//        } else {
//            actionBar.setBackgroundDrawable(getResources().getDrawable(
//                    R.drawable.background_blue));
//        }
//
//        RelativeLayout back = (RelativeLayout) actionBar.getCustomView()
//                .findViewById(R.id.back);
//        back.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        TextView title = (TextView) actionBar.getCustomView().findViewById(
//                R.id.title);
//        title.setText("帐号与安全");
//    }
}
