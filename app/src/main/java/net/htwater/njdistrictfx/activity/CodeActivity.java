package net.htwater.njdistrictfx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;

public class CodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);

        title.setText("二维码分享");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
