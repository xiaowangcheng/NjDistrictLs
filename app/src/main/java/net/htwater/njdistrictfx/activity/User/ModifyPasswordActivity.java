package net.htwater.njdistrictfx.activity.User;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzy on 2017/03/02
 */
public class ModifyPasswordActivity extends BaseActivity {
    private EditText password;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        Button commit = (Button) findViewById(R.id.commit);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);

        commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String value1 = password.getText().toString().trim();
                String value2 = password2.getText().toString().trim();

                if (value1.isEmpty() || value2.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!value1.equals(value2)) {
                    Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                send(value1);
            }
        });
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void send(final String x) {
        String url = Constants.ServerIP + "/portals/updatePW!user?userName="
                + SharedPreferencesUtil.getAccount() + "&password=" + x;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if ("Success".equals(result)) {
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        SharedPreferencesUtil.setPassword(x);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.mQueue.add(request);
    }
}
