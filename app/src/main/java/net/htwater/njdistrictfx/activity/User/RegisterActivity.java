package net.htwater.njdistrictfx.activity.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener, OnClickListener {
    private EditText userName;
    private EditText password;
    private EditText password2;
    private EditText phone;
    private EditText name;
    private EditText department;
    private EditText post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        phone = (EditText) findViewById(R.id.phone);
        name = (EditText) findViewById(R.id.name);
        department = (EditText) findViewById(R.id.department);
        post = (EditText) findViewById(R.id.post);
        Button ok = (Button) findViewById(R.id.ok);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);

        userName.setOnFocusChangeListener(this);
        phone.setOnFocusChangeListener(this);
        ok.setOnClickListener(this);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Register(final String uname, final String password, final String phone, final String realname
            , final String department, final String post) {
        String url = Constants.ServerIP + "/portals/addUser!user";//?uname="
        //+ uname + "&password=" + password + "&realname=" + realname + "&utype=0&department=" + department + "&phone=" + phone;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (null == response || response.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject object = new JSONObject(response);
                    String result = object.getString("result");
                    if (result.equals("Success")) {
                        Toast.makeText(getApplicationContext(), "注册成功，等待审核", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String failInfo = object.getString("failInfo");
                        if (null == failInfo || failInfo.isEmpty()) {
                            failInfo = "注册失败";
                        }
                        Toast.makeText(getApplicationContext(), failInfo, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("uname", uname);
                map.put("password", password);
                map.put("realname", realname);
                // map.put("utype", "0");
                map.put("department", department);
                map.put("phone", phone);
                map.put("job", post);
                return map;
            }
        };
        MyApplication.mQueue.add(stringRequest);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    phone.setFocusable(true);
                    phone.setFocusableInTouchMode(true);
                    phone.requestFocus();
                    phone.requestFocusFromTouch();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.phone:
                if (!b) {
                    final String phoneValue = phone.getText().toString().trim();
                    if (phoneValue.length() == 0) {
                        return;
                    }
                    if (phoneValue.length() != 11) {
                        mHandler.sendEmptyMessage(100);
                        Toast.makeText(RegisterActivity.this, "手机号长度不对,请填写正确的手机号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String url = Constants.ServerIP + "/portals/isPhoneExist!user?phone=" + phoneValue;
                    StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (!"Success".equals(jsonObject.getString("result"))) {
                                    Toast.makeText(RegisterActivity.this, "手机号未申报,请更换手机号", Toast.LENGTH_SHORT).show();
                                    phone.setFocusable(true);
                                    phone.setFocusableInTouchMode(true);
                                    phone.requestFocus();
                                    phone.findFocus();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
                    MyApplication.mQueue.add(stringRequest);
                }
                break;
            case R.id.userName:
                if (!b) {
                    final String unameValue = userName.getText().toString().trim();
                    if (userName.length() == 0) {
                        return;
                    }
                    String url = Constants.ServerIP + "/portals/isUserExist!user?uname=" + unameValue;
                    StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (!"Success".equals(jsonObject.getString("result"))) {
                                    Toast.makeText(RegisterActivity.this, "用户名已存在,请更换用户名", Toast.LENGTH_SHORT).show();
                                    userName.setFocusable(true);
                                    userName.setFocusableInTouchMode(true);
                                    userName.requestFocus();
                                    userName.findFocus();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
                    MyApplication.mQueue.add(stringRequest);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                String unameValue = userName.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                String passwordValue2 = password2.getText().toString().trim();
                String phoneValue = phone.getText().toString().trim();
                String realnameValue = name.getText().toString().trim();
                String departmentValue = department.getText().toString().trim();
                String postValue = post.getText().toString().trim();
                if (unameValue.equals("") || passwordValue.equals("") || passwordValue2.equals("") || phoneValue.equals("")
                        || realnameValue.equals("") || departmentValue.equals("") || postValue.equals("")) {
                    Toast.makeText(getApplicationContext(), "请填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordValue.equals(passwordValue2)) {
                    Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                Register(unameValue, passwordValue, phoneValue, realnameValue, departmentValue, postValue);
                break;
        }
    }
}
