package net.htwater.njdistrictfx.activity.User;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.activity.DownloadActivity;
import net.htwater.njdistrictfx.activity.MainActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.util.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private EditText account;
    private EditText password;
    private ProgressDialog progressDialog;

    private static final int PERMISSON_REQUESTCODE = 100;

    //需要进行检测的权限数组
    private String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        TextView login = (TextView) findViewById(R.id.login);

        // TextView forgetPassword = (TextView) findViewById(R.id.forgetPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录");
        progressDialog.setCanceledOnTouchOutside(false);

        // String userName = SharedPreferencesUtil.getUserName();
        String savedAccount = SharedPreferencesUtil.getAccount();
        String savedPassword = SharedPreferencesUtil.getPassword();
        if (!savedAccount.equals("") && !savedPassword.equals("")) {
            account.setText(savedAccount);
            password.setText(savedPassword);
        }

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content1 = account.getText().toString().trim();
                String content2 = password.getText().toString().trim();
                if (content1.equals("") || content2.equals("")) {
                    Toast.makeText(getApplicationContext(), "帐号或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                simulationData(content1, content2);
                //login2(content1, content2);
            }
        });


//        forgetPassword.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
//                startActivity(intent);
//            }
//        });

        checkPermission(needPermissions);

        checkNewVersion();
    }


    private boolean checkperiod(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String currentDate =sf.format(c.getTime());
        System.out.println( "当前日期："+currentDate);
        if(isDate2Bigger(currentDate,"2019-1-30")){
            return true;
        } else {
            return  false;
        }
    }

    public static boolean isDate2Bigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = false;
        } else if (dt1.getTime() <= dt2.getTime()) {
            isBigger = true;
        }
        return isBigger;
    }

    private void checkNewVersion() {
        if (true){
            autoLogin();
            return;
        }
        String url = MyApplication.UpdateServerIP + "/htmarket/getAppVersion!public";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("appid", Constants.APPID);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    int version = resultJsonObject.getInt("version");
                    final String apkfile = resultJsonObject.getString("apkfile");
                    if (version > SharedPreferencesUtil.getVersionCode()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("检查更新");
                        builder.setMessage("检测到新版本，是否升级？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = MyApplication.UpdateServerIP + "/htmarket_files/apk/" + apkfile;
                                Intent intent = new Intent(LoginActivity.this, DownloadActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                autoLogin();
                            }
                        });

                        builder.show();
                    } else {
//                        if (hasApp()) {
//                            checkVideoNewVersion();
//                        }
//                        else{
                            autoLogin();
//                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                autoLogin();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                autoLogin();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void autoLogin() {
        String account = SharedPreferencesUtil.getAccount();
        String password = SharedPreferencesUtil.getPassword();
        if ("".equals(account) || "".equals(password)) {
            return;
        }
//        checkUser();
        simulationData(account, password);
        // login2(account, password);
    }

    private void checkPermission(String[] needPermissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needRequestPermissionList = new ArrayList<>();
            for (String perm : needPermissions) {
                if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED || shouldShowRequestPermissionRationale(perm)) {
                    needRequestPermissionList.add(perm);
                }
            }
            if (null != needRequestPermissionList && needRequestPermissionList.size() > 0) {
                requestPermissions(needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]), PERMISSON_REQUESTCODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.finishAll();
    }

    private void login(final String account, final String password) {
        progressDialog.show();

//        String url = Constants.ServerIP + "/portals/login!public";
        String url ="http://59.83.223.39:8091/northriver/jiangbeiapi/apiLogin";

        final String umdeviceid = SharedPreferencesUtil.getdeviceToken();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (null == response || response.length() == 0) {
                    Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (response.equals("error")) {
                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String login = jsonObject.getString("login");
                    if (login.equals("fail")) {
                        String failInfo = jsonObject.getString("failInfo");
                        Toast.makeText(getApplicationContext(), "登录失败 " + failInfo, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();

                    SharedPreferencesUtil.setAccount(account);
                    SharedPreferencesUtil.setPassword(password);
                    SharedPreferencesUtil.setUserName(jsonObject.getString("realname"));
                    SharedPreferencesUtil.setAutoLogin(true);
                    String x = jsonObject.getString("ugrouptypes");
                    if (null != x && !x.isEmpty() && !x.equals("null")) {
                        SharedPreferencesUtil.setType(Integer.valueOf(x));
                    }

                    String uleader = jsonObject.getString("uleader");
                    if (null != uleader && uleader.equals("1")) {
                        SharedPreferencesUtil.setLeader(true);
                    } else {
                        SharedPreferencesUtil.setLeader(false);
                    }

                    String txl_qx = jsonObject.getString("txl_qx");
                    if (null != txl_qx && "1".equals(txl_qx)) {
                        SharedPreferencesUtil.settxl_qx(true);
                    } else {
                        SharedPreferencesUtil.settxl_qx(false);
                    }

//                    String menu = jsonObject.getJSONObject("menu").toString();

                    String menu = new JSONObject(readMenu()).getJSONObject("menu").toString();

                    if (null != menu && !menu.isEmpty()) {
                        SharedPreferencesUtil.setMenu(menu);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
//                map.put("userName", account);
                map.put("username", account);
                map.put("password", Util.SHA1(password));
//                map.put("mac", SharedPreferencesUtil.getImei());
//                if (!umdeviceid.isEmpty()) {
//                    map.put("umdeviceid", umdeviceid);
//                }
                return map;
            }
        };
        MyApplication.mQueue.add(stringRequest);
    }


    private void simulationData(final String account, final String password){
        String result = "{\"uid\":\"7\",\"uname\": \"zkk\",\"realname\":\"张康康\",\"weatherforecast\":\"http://59.83.223.39:8091/northOfYangtzeRiver/views/weatherForecast/WeatherForecast.html\",\"productsforecast\":\"http://59.83.223.39:8091/northOfYangtzeRiver/views/weatherForecast/professionalProductsForecast.html\",\"radarenlarge\":\"http://59.83.223.39:8091/northOfYangtzeRiver/views/jicengfangxun/radarEnlarge.html\",\"waterdiagram\":\"http://59.83.223.39:8091/northOfYangtzeRiver/views/baseinfo/img/shuixi.jpg\",\"login\": \"success\"}";

        if( !checkperiod()){
            /*AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("温馨提示");
            builder.setMessage("试用期已经结束");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();*/
            return;
        }

        if (null == result || result.length() == 0) {
            Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (result.equals("error")) {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject jsonObject = new JSONObject(result);
            String login = jsonObject.optString("login");
            if (login.equals("fail")) {
                String failInfo = jsonObject.optString("failInfo");
                Toast.makeText(getApplicationContext(), "登录失败 " + failInfo, Toast.LENGTH_SHORT).show();
                return;
            }
            String success=jsonObject.optString("success");
            if (success.equals("false")) {
//                        String failInfo = jsonObject.getString("failInfo");
                Toast.makeText(getApplicationContext(), "登录失败 " + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();

            SharedPreferencesUtil.setAccount(account);
            SharedPreferencesUtil.setPassword(password);
            SharedPreferencesUtil.setUserName(jsonObject.getString("realname"));
            SharedPreferencesUtil.setAutoLogin(true);

            SharedPreferencesUtil.setUrlValue(DataUtil.weatherforecast,jsonObject.getString("weatherforecast"));
            SharedPreferencesUtil.setUrlValue(DataUtil.productsforecast,jsonObject.getString("productsforecast"));
            SharedPreferencesUtil.setUrlValue(DataUtil.radarenlarge,jsonObject.getString("radarenlarge"));
            SharedPreferencesUtil.setUrlValue(DataUtil.waterdiagram,jsonObject.getString("waterdiagram"));
//                    String x = jsonObject.getString("ugrouptypes");
//                    if (null != x && !x.isEmpty() && !x.equals("null")) {
//                        SharedPreferencesUtil.setType(Integer.valueOf(x));
//                    }
//
//                    String uleader = jsonObject.getString("uleader");
//                    if (null != uleader && uleader.equals("1")) {
//                        SharedPreferencesUtil.setLeader(true);
//                    } else {
//                        SharedPreferencesUtil.setLeader(false);
//                    }

//                    String txl_qx = jsonObject.getString("txl_qx");
//                    if (null != txl_qx && "1".equals(txl_qx)) {
//                        SharedPreferencesUtil.settxl_qx(true);
//                    } else {
//                        SharedPreferencesUtil.settxl_qx(false);
//                    }

//                    String menu = jsonObject.getJSONObject("menu").toString();

            String menu = new JSONObject(readMenu()).getJSONObject("menu").toString();

            if (null != menu && !menu.isEmpty()) {
                SharedPreferencesUtil.setMenu(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    private void login2(final String account, final String password) {
        progressDialog.show();

        //String url = Constants.ServerIP + "/portals/login!public";
        String url =Constants.ServerURL +"apiLogin";
        RequestParams params=new RequestParams(url);
        params.addBodyParameter("username",account);
        params.addBodyParameter("password",password);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                if (null == result || result.length() == 0) {
                    Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (result.equals("error")) {
                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    String login = jsonObject.optString("login");
                    if (login.equals("fail")) {
                        String failInfo = jsonObject.optString("failInfo");
                        Toast.makeText(getApplicationContext(), "登录失败 " + failInfo, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String success=jsonObject.optString("success");
                    if (success.equals("false")) {
//                        String failInfo = jsonObject.getString("failInfo");
                        Toast.makeText(getApplicationContext(), "登录失败 " + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();

                    SharedPreferencesUtil.setAccount(account);
                    SharedPreferencesUtil.setPassword(password);
                    SharedPreferencesUtil.setUserName(jsonObject.getString("realname"));
                    SharedPreferencesUtil.setAutoLogin(true);

                    SharedPreferencesUtil.setUrlValue(DataUtil.weatherforecast,jsonObject.getString("weatherforecast"));
                    SharedPreferencesUtil.setUrlValue(DataUtil.productsforecast,jsonObject.getString("productsforecast"));
                    SharedPreferencesUtil.setUrlValue(DataUtil.radarenlarge,jsonObject.getString("radarenlarge"));
                    SharedPreferencesUtil.setUrlValue(DataUtil.waterdiagram,jsonObject.getString("waterdiagram"));
//                    String x = jsonObject.getString("ugrouptypes");
//                    if (null != x && !x.isEmpty() && !x.equals("null")) {
//                        SharedPreferencesUtil.setType(Integer.valueOf(x));
//                    }
//
//                    String uleader = jsonObject.getString("uleader");
//                    if (null != uleader && uleader.equals("1")) {
//                        SharedPreferencesUtil.setLeader(true);
//                    } else {
//                        SharedPreferencesUtil.setLeader(false);
//                    }

//                    String txl_qx = jsonObject.getString("txl_qx");
//                    if (null != txl_qx && "1".equals(txl_qx)) {
//                        SharedPreferencesUtil.settxl_qx(true);
//                    } else {
//                        SharedPreferencesUtil.settxl_qx(false);
//                    }

//                    String menu = jsonObject.getJSONObject("menu").toString();

                    String menu = new JSONObject(readMenu()).getJSONObject("menu").toString();

                    if (null != menu && !menu.isEmpty()) {
                        SharedPreferencesUtil.setMenu(menu);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }




    private String readMenu() {
        String menu = null;
        String fileName = "menu2.json";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String str;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
            menu = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }

}
