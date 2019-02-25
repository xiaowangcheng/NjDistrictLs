package net.htwater.njdistrictfx.activity.User;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laizhiyi on 2016/6/20
 */
public class FeedbackActivity extends BaseActivity {
    private EditText content;
    private EditText qq;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        content = (EditText) findViewById(R.id.content);
        qq = (EditText) findViewById(R.id.qq);
        name = (EditText) findViewById(R.id.name);
        Button commit = (Button) findViewById(R.id.commit);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);

        title.setText("意见反馈");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getText().toString().equals("")
                        || name.getText().toString().equals("")
                        || qq.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请将信息填写完整",Toast.LENGTH_SHORT).show();
                    return;
                }
                send();
            }
        });
    }

    private void send() {
        String url = "http://122.227.239.196:8080/hisweb/postAdvice!main";
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    return;
                }
                Toast.makeText(getApplicationContext(), response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getApplicationContext(), "发送失败",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("advice", "[南京防汛android]" + content.getText().toString());
                params.put("submit", name.getText().toString());
                params.put("contact", qq.getText().toString());
                return params;
            }
        };
        MyApplication.mQueue.add(request);
    }
}
