package net.htwater.njdistrictfx.core;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhutianhong on 2016/1/25.
 */
public class FileDownloadThread extends Thread {
    private static String TAG = "FileDownloadThread";
    private boolean stop = false;
    private Context m_context;
    private Handler m_handler;
    private String m_downloadUrl;
    private Uri m_newfileUrl;

    public FileDownloadThread(Context context, String downloadUrl, Uri newfileUrl, Handler handler) {
        m_context = context;
        m_handler = handler;
        m_downloadUrl = downloadUrl;
        m_newfileUrl = newfileUrl;
    }

    public FileDownloadThread(Context context, String downloadUrl, Uri newfileUrl) {
        m_context = context;
        m_downloadUrl = downloadUrl;
        m_newfileUrl = newfileUrl;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        boolean bRtn = false;
        int fileSize = 0;
        int readSize = 0;
        try {
            URL url = new URL(m_downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (null != conn) {
                // 读取超时时间 毫秒级
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    fileSize = conn.getContentLength();
                    if (fileSize > 0) {
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(m_newfileUrl.getPath());
                        try {
                            byte[] buffer = new byte[1024];
                            int i = 0;
                            int tempProgress = -1;
                            while ((!stop) && ((i = is.read(buffer)) != -1)) {
                                readSize += i;
                                int progress = (int) (readSize * 100.0 / fileSize);
                                if (tempProgress != progress) {
                                    tempProgress = progress;
                                    sendMessage(progress, "0", "0");
                                }
                                fos.write(buffer, 0, i);
                            }
                            fos.flush();
                            sendMessage(100, "1", "0");
                        } catch (Exception e1) {
                            Log.e(TAG, e1.toString());
                            sendMessage(0, "0", "1");
                        } finally {
                            fos.close();
                            is.close();
                        }
                    }
                } else {
                    sendMessage(0, "0", "3");
                }
            } else {
                sendMessage(0, "0", "2");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            sendMessage(0, "0", "2");
        }
    }

    private void sendMessage(int progress, String okflag, String err) {
        Map<String, Object> map = new HashMap<>();
        map.put("stop", stop);
        map.put("progress", progress);
        map.put("okflag", okflag);
        map.put("err", err);
        map.put("apkfile", m_newfileUrl.getPath());

        Message msg = m_handler.obtainMessage();
        msg.obj = map;
        m_handler.sendMessage(msg);
//        if (null == m_handler) {
        // EventBus.getDefault().post(new DownloadEvent(map));
//        } else {
//            Message msg = new Message();
//            msg.obj = map;
//            msg.what = 301;
//            m_handler.sendMessage(msg);
//        }
    }

}
