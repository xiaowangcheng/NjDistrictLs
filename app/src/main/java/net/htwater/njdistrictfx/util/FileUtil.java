package net.htwater.njdistrictfx.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

/**
 * Created by LZY on 2017/6/27.
 */

public class FileUtil {

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

    public static Uri getPhotoUri() {
        String rootPath = Environment.getExternalStorageDirectory().toString();
        String savePath = rootPath + File.separator + "htwater" + File.separator + ".photo" + File.separator;
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return Uri.fromFile(file);
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
     * 删除文件
     *
     * @param path 文件
     */
    public static boolean deleteFile(String path) {
        if (path.isEmpty()) {
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

    /**
     * 上传文件
     *
     * @param uploadUrl
     * @param filePath
     * @param params
     * @return String
     * @since v 1.0
     */
    public static void uploadFile(String uploadUrl, String filePath, Map<String, String> params, Handler handler) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = generateBoundary();
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        String responseFromServer = "";
        int index = filePath.lastIndexOf("/");
        String fileName = filePath.substring(index + 1, filePath.length());
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            // 表单字段
            StringBuffer sb = new StringBuffer();
            for (String key : params.keySet()) {
                sb = sb.append("--");
                sb = sb.append(boundary);
                sb = sb.append("\r\n");
                sb = sb.append("Content-Disposition: form-data; name=\"" + key
                        + "\"\r\n\r\n");
                sb = sb.append(URLEncoder.encode(params.get(key), "utf-8"));
                sb = sb.append("\r\n");
            }
            dos.writeBytes(sb.toString());
            // end表单字段
            // 文件
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                    + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            System.out.println("File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception ioe) {
            System.out.println("error: " + ioe.getMessage());
            // EventBus.getDefault().post("");
            handler.sendEmptyMessage(0);
        }
        // ------------------ read the SERVER RESPONSE
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while (br.read() != -1) {
                responseFromServer += br.readLine();
            }
            br.close();
            System.out.println(responseFromServer);

            // EventBus.getDefault().post(responseFromServer);
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = responseFromServer;
            handler.sendMessage(message);
        } catch (IOException exception) {
            exception.printStackTrace();
            // EventBus.getDefault().post("");
            handler.sendEmptyMessage(0);
        }
    }

    public static void uploadFile(String uploadUrl, String filePath, Handler handler) {
        HttpURLConnection conn = null;
        // DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = generateBoundary();
        // int bytesRead, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        String responseFromServer = "";
        int index = filePath.lastIndexOf("/");
        String fileName = filePath.substring(index + 1, filePath.length());
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//            // 表单字段
//            StringBuffer sb = new StringBuffer();
//            for (String key : params.keySet()) {
//                sb = sb.append("--");
//                sb = sb.append(boundary);
//                sb = sb.append("\r\n");
//                sb = sb.append("Content-Disposition: form-data; name=\"" + key
//                        + "\"\r\n\r\n");
//                sb = sb.append(URLEncoder.encode(params.get(key), "utf-8"));
//                sb = sb.append("\r\n");
//            }
//            dos.writeBytes(sb.toString());
            // end表单字段
            // 文件
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            System.out.println("File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception ioe) {
            System.out.println("error: " + ioe.getMessage());
            // EventBus.getDefault().post("");
            handler.sendEmptyMessage(0);
        }
        // ------------------ read the SERVER RESPONSE
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while (br.read() != -1) {
                responseFromServer += br.readLine();
            }
            br.close();
            System.out.println(responseFromServer);

            // EventBus.getDefault().post(responseFromServer);
            Message message = handler.obtainMessage();
            message.what = 1;
            message.obj = responseFromServer;
            handler.sendMessage(message);
        } catch (IOException exception) {
            exception.printStackTrace();
            // EventBus.getDefault().post("");
            handler.sendEmptyMessage(0);
        }
    }

    private static String generateBoundary() {
        char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    /**
     * upLoadByAsyncHttpClient:由人造post上传
     *
     * @return void
     * @throws IOException
     * @throws
     * @since CodingExample　Ver 1.1
     */
    public static String upLoadByCommonPost(String uploadUrl, String[] paths) throws IOException {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        URL url = new URL(uploadUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String result = null;
        try {
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            for (String path : paths) {
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + path.substring(path.lastIndexOf("/") + 1) + "\"" + end);
                dos.writeBytes("Content-Type:application/octet-stream" + end);
                dos.writeBytes(end);

                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192]; // 8k
                int count;
                // 读取文件
                while ((count = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, count);
                }
                fis.close();
                dos.writeBytes(end);
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            dos.close();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED
                    || httpURLConnection.getResponseCode() == httpURLConnection.HTTP_CREATED) {
                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
//                result = br.readLine();
                StringBuilder sb = new StringBuilder();
                String inputLine = "";
                while (((inputLine = br.readLine()) != null)) {
                    sb.append(inputLine);
                }
                br.close();
                is.close();
                result = sb.toString();
            } else {
                InputStream is = httpURLConnection.getErrorStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
//                result = br.readLine();
                StringBuilder sb = new StringBuilder();
                String inputLine = "";
                while (((inputLine = br.readLine()) != null)) {
                    sb.append(inputLine);
                }
                br.close();
                is.close();
                result = sb.toString();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            httpURLConnection.disconnect();
        }

        return result;
    }
}
