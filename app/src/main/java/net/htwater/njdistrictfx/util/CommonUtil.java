package net.htwater.njdistrictfx.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by 96955 on 2017/6/27.
 */

public class CommonUtil {
    public static double longitudeShuiku =  118.6567640305 ;
    public static double latitudeShuiku =  32.1408433929 ;

    public static double longitude =  118.7230682373 ;
    public static double latitude =  32.1198011118 ;

    public static float zoonshuiku =  13 ;
    public static float zoon =  10 ;
    /**
     * 删除文件
     *
     * @param path 文件
     */
    public static boolean deleteFile(String path) {
        if (StringUtil.isBlank(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getPath());
            }
        }
        return file.delete();
    }

    // 复制文件
    public static boolean copyFile(File sourceFile, File targetFile) throws IOException {
        boolean bRtn = false;
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
            bRtn = true;
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
        return bRtn;
    }

    /**
     * 文件是否存在
     */
    public static boolean existFile(String path) {
        boolean bRtn = false;
        if (!StringUtil.isBlank(path)) {
            File file = new File(path);
            try {
                bRtn = file.exists();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                file = null;
            }
        }
        return bRtn;
    }

    /**
     * 是否有存储卡
     *
     * @return boolean 是否有存储卡
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 生成随机数0-9
     *
     * @param count 长度
     * @return String 生成0-9随机数
     */
    public static String getRandom(int count) {
        String rtn = "";
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            rtn = rtn + String.valueOf(random.nextInt(10));
        }
        return rtn;
    }

    /**
     * 压缩图片。先降分辨率，再压画质。
     *
     * @param oldFile 原始文件
     * @param newPath 新文件
     * @return boolean 是否压缩成功
     */
    public static boolean compressPhotoSrc(File oldFile, String newPath) {
        if (!oldFile.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(oldFile.getAbsolutePath(), options);
        options.inJustDecodeBounds = false;
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        int max = height > width ? height : width;
        while ((max / inSampleSize) > 1800) {
            inSampleSize *= 2;
        }
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(oldFile.getAbsolutePath(), options);
        return compressBitmap(bitmap, newPath);
    }

    /**
     * 压缩图片并保存。采用降低画质的方法
     *
     * @param bitmap  原始图片
     * @param newPath 压缩路径
     * @return boolean 是否成功
     */
    private static boolean compressBitmap(Bitmap bitmap, String newPath) {
        boolean bRtn = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > 700 * 1024) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            bRtn = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bRtn;
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 保留1位小数
     */
    public static String get1Double(double a) {
        return formatDouble(a, "0.0");
    }

    /**
     * 保留2位小数
     */
    public static String get2Double(double a) {
        return formatDouble(a, "0.00");
    }

    public static String formatDouble(double a, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(a);
    }

    /**
     * 格式1位小数
     */
    public static String format2One(String x) {
        if (x.equals("--")) {
            return x;
        }
        return CommonUtil.get1Double(Double.valueOf(x));
    }

    /**
     * 格式化2位小数
     */
    public static String format2Two(String x) {
        if (x.equals("--")) {
            return x;
        }
        return CommonUtil.get2Double(Double.valueOf(x));
    }
}
