package net.htwater.njdistrictfx.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.graphics.drawable.DrawableCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 检查是否有SD卡
     */
    public static boolean hasStorage() {
        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        } else {
//            return false;
//        }
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 保留1位小数
     */
    public static double get1Double(double a) {
        DecimalFormat df = new DecimalFormat("0.0");
        return Double.valueOf(df.format(a));
    }

    /**
     * 保留2位小数
     */
    public static double get2Double(double a) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(a));
    }

    /**
     * 图片变色
     *
     * @param drawable
     * @param color
     * @return
     */
    public static Drawable tintDrawable(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color));
        return wrappedDrawable;
    }

    /**
     * 真实坐标转换为高德坐标
     *
     * @param lat
     * @param lon
     * @return
     */
    public static double[] delta(double lat, double lon) {
        // Krasovsky 1940
        //
        // a = 6378245.0, 1/f = 298.3
        // b = a * (1 - f)
        // ee = (a^2 - b^2) / a^2;
        double PI = 3.14159265358979324;
        double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
        double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        return new double[]{lat + dLat, lon + dLon};
    }

    private static double transformLat(double x, double y) {
        double PI = 3.14159265358979324;
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double PI = 3.14159265358979324;
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 将json数组转化为Double型
     *
     * @param str
     * @return
     */
    public static double[] getJsonToDoubleArray(String str) throws JSONException {
        JSONArray jsonArray = new JSONArray(str);// JSONArray.fromObject(str);
        double[] array = new double[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getDouble(i);
        }
        return array;
    }

    /**
     * 从字符串里提取汉字
     *
     * @param x
     * @return
     */
    public static String getHanziFromString(String x) {
        String reg = "[^\u4e00-\u9fa5]";
        return x.replaceAll(reg, "");
    }

    /**
     * 获取X天前的日期，格式为2016-06-30
     *
     * @param x
     * @return
     */
    public static String getDateBefore(int x) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (x == 0) {
            return df.format(date);
        } else {
            return df.format(new Date(date.getTime() - (long) x * 24 * 60 * 60 * 1000));
        }
    }

    /**
     * 获取day日x天前的日期
     *
     * @param x
     * @return
     */
    public static String getDateBefore(String day, int x) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df.format(new Date(date.getTime() - (long) x * 24 * 60 * 60 * 1000));
    }

    /**
     * 将日期转日期字符串
     *
     * @param date           日期
     * @param formatepattern 日期格式
     * @return String 日期字符串
     */
    public static String parseDate2String(Date date, String formatepattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatepattern);
        return sdf.format(date);
    }

}
