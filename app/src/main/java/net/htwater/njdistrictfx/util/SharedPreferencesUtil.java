package net.htwater.njdistrictfx.util;

import android.content.SharedPreferences;

import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.htwater.njdistrictfx.core.MyApplication.sp;
import static net.htwater.njdistrictfx.core.MyApplication.versionName;

/**
 * Created by LZY on 2016/11/22.
 */
public class SharedPreferencesUtil {
    /**
     * 判断是否为首次启动
     *
     * @return
     */
    public static boolean isFirstBoot() {
        boolean isFirstBoot = sp.getBoolean("isFirstBoot", true);
        if (isFirstBoot) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstBoot", false);
            editor.apply();
        }
        return isFirstBoot;
    }

    public static void setUserName(String name) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName", name);
        editor.apply();
    }

    /**
     * 返回值为空字符串表示没有登录
     */
    public static String getUserName() {
        return sp.getString("userName", "");
    }

    /**
     * 登录手机号
     *
     * @return
     */
    public static String getAccount() {
        return sp.getString("phone", "");
    }

    public static void setAccount(String phone) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phone", phone);
        editor.apply();
    }


    public static String getUrlVlaue(String urlkey){
        return sp.getString(urlkey, "");
    }
    public static void setUrlValue(String urlkey,String urlValue){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(urlkey, urlValue);
        editor.apply();
    }

    /**
     * 密码
     *
     * @return
     */
    public static String getPassword() {
        return sp.getString("password", "");
    }

    public static void setPassword(String password) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", password);
        editor.apply();
    }

    /**
     * 判断是否开启自动登录
     *
     * @return
     */
    public static boolean isAutoLogin() {
        return sp.getBoolean("isAutoLogin", true);
    }

    public static void setAutoLogin(boolean param) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isAutoLogin", param);
        editor.apply();
    }

    public static boolean isFloodSeason() {
        return sp.getBoolean("isFloodSeason", false);
    }

    public static void setFloodSeason(boolean param) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFloodSeason", param);
        editor.apply();
    }

    public static boolean isLeader() {
        return sp.getBoolean("isLeader", false);
    }

    public static void setLeader(boolean param) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLeader", param);
        editor.apply();
    }

    public static int getType() {
        return sp.getInt("type", 3);
    }

    public static void setType(int type) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("type", type);
        editor.apply();
    }

    /**
     * 获取首页插件列表
     *
     * @return
     */
    public static List<String> getWidgetList() {
        String[] array = sp.getString("widgetList", "weather,flood").split(",");
        List<String> list = new ArrayList<>();
        for (String anArray : array) {
            if (!anArray.equals("")) {
                list.add(anArray);
            }
        }
        return list;
    }

    public static void setWidgetList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            builder.append(",");
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("widgetList", builder.toString());
        editor.apply();
    }

    /**
     * 判断是否为更新后首次启动
     *
     * @return
     */
    public static boolean isFirstBootAfterUpdate() {
        String x = sp.getString("versionname", "0");
        if (x.equals(versionName)) {
            return false;
        } else {
            sp.edit().putString("versionname", versionName).apply();
            return true;
        }
    }

    public static List<String> getFavoritesList() {
        String key = null;
        switch (MyApplication.CONSTANS_WATER_TYPE) {
            case Constants.CONSTANS_RAIN:
                key = Constants.CONSTANS_RAIN_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_PUMP_WATER:
                key = Constants.CONSTANS_PUMP_WATER_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_RIVER_WATER:
                key = Constants.CONSTANS_RIVER_WATER_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_RES_WATER:
                key = Constants.CONSTANS_RES_WATER_FAVORITES_KEY;
                break;
        }
        if (null == key) {
            return new ArrayList<>();
        }
        String x = sp.getString(key, "");
        if ("".equals(x)) {
            return new ArrayList<>();
        }
        String[] array = x.split(",");
        List<String> cacheList = Arrays.asList(array);
        List<String> resultList = new ArrayList<>();
        resultList.addAll(cacheList);
        return resultList;
    }

    public static void setFavorites(List<String> list) {
        String key = null;
        switch (MyApplication.CONSTANS_WATER_TYPE) {
            case Constants.CONSTANS_RAIN:
                key = Constants.CONSTANS_RAIN_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_PUMP_WATER:
                key = Constants.CONSTANS_PUMP_WATER_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_RIVER_WATER:
                key = Constants.CONSTANS_RIVER_WATER_FAVORITES_KEY;
                break;
            case Constants.CONSTANS_RES_WATER:
                key = Constants.CONSTANS_RES_WATER_FAVORITES_KEY;
                break;
        }
        if (null == key) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String x : list) {
            stringBuilder.append(x).append(",");
        }
        String y = stringBuilder.toString();
        if (y.endsWith(",")) {
            y = y.substring(0, y.length());
        }
        sp.edit().putString(key, y).apply();
    }

    public static void setdeviceToken(String deviceToken) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("deviceToken", deviceToken);
        editor.apply();
    }

    public static String getdeviceToken() {
        return sp.getString("deviceToken", "");
    }

    public static void settxl_qx(boolean param) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("txlqx", param);
        editor.apply();
    }

    public static boolean gettxl_qx() {
        return sp.getBoolean("txlqx", false);
    }

    public static void setImei(String imei) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("imei", imei);
        editor.apply();
    }

    public static String getImei() {
        return sp.getString("imei", "");
    }

    public static void setMenu(String menu) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("menu", menu);
        editor.apply();
    }

    public static String getMenu() {
        return sp.getString("menu", "");
    }

    public static void setFunctionIdList(String functionIdList) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("functionIdList", functionIdList);
        editor.apply();
    }

    public static String getFunctionIdList() {
        return sp.getString("functionIdList", "2,3,4,5,6");
    }

    public static void setFunctionNameList(String functionNameList) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("functionNameList", functionNameList);
        editor.apply();
    }

    public static String getFunctionNameList() {
//        return sp.getString("functionNameList", "气象,汛情,工程,办公,视频,会商");
        return sp.getString("functionNameList", "气象,汛情,工程,办公,视频");

    }

    public static void setFunctionSubitemList(String x) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("functionSubitem", x);
        editor.apply();
    }

    public static String getFunctionSubitemList() {
        return sp.getString("functionSubitem", "");
    }

    /**
     * 当前版本号
     */
    public static void setVersionCode(int versionCode) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("versionCode", versionCode);
        editor.apply();
    }

    public static int getVersionCode(){
        return sp.getInt("versionCode",0);
    }

    /**
     * 当前版本名
     */
    public static void setVersionName(String versionName) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("versionName", versionName);
        editor.apply();
    }

    public static String getVersionName() {
        return sp.getString("versionName", "");
    }
}
