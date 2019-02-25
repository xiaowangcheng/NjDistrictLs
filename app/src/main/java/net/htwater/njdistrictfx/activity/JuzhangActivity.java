package net.htwater.njdistrictfx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.adapter.FunctionAdapter;
import net.htwater.njdistrictfx.util.DataUtil;

public class JuzhangActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juzhang);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        GridView gridView1 = (GridView) findViewById(R.id.gridView1);
        GridView gridView2 = (GridView) findViewById(R.id.gridView2);
        GridView gridView3 = (GridView) findViewById(R.id.gridView3);
        GridView gridView4 = (GridView) findViewById(R.id.gridView4);
        GridView gridView5 = (GridView) findViewById(R.id.gridView5);
        GridView gridView6 = (GridView) findViewById(R.id.gridView6);
        RelativeLayout rightButton = (RelativeLayout) findViewById(R.id.rightButton);

        FunctionAdapter adapter1 = new FunctionAdapter(this, DataUtil.getFunctionList("2"), true);
        gridView1.setAdapter(adapter1);
        FunctionAdapter adapter2 = new FunctionAdapter(this, DataUtil.getFunctionList("3"), true);
        gridView2.setAdapter(adapter2);
        FunctionAdapter adapter3 = new FunctionAdapter(this, DataUtil.getFunctionList("4"), true);
        gridView3.setAdapter(adapter3);
        FunctionAdapter adapter4 = new FunctionAdapter(this, DataUtil.getFunctionList("5"), true);
        gridView4.setAdapter(adapter4);
        FunctionAdapter adapter5 = new FunctionAdapter(this, DataUtil.getFunctionList("6"), true);
        gridView5.setAdapter(adapter5);
        FunctionAdapter adapter6 = new FunctionAdapter(this, DataUtil.getFunctionList("7"), true);
        gridView6.setAdapter(adapter6);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JuzhangActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
