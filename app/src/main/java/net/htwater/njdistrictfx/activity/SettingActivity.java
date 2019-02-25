package net.htwater.njdistrictfx.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.fragment.SettingFragment;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        Fragment settingFragment = new SettingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, settingFragment).commit();
    }
}
