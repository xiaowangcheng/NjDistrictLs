package net.htwater.njdistrictfx.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.FileDownloadThread;
import net.htwater.njdistrictfx.util.Util;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * Created by lzy on 2017/6/14
 */
public class DownloadActivity extends Activity {
    private TextView tv;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Map<String, Object> data = (Map<String, Object>) msg.obj;
            refreshProgress(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        tv = (TextView) findViewById(R.id.tv);

        String url = getIntent().getStringExtra("url");
        if (null != url) {
            downloadApk(url);
        }
    }

    public void downloadApk(String url) {
        String savePath = Environment.getExternalStorageDirectory().toString();
        File file = new File(savePath + "/htwater/setup/");
        if (!file.exists()) {
            file.mkdirs();
        }
        Uri apkSaveDirUri = Uri.fromFile(file);

        String tempFileName = Util.parseDate2String(new Date(), "yyyyMMddHHmmssSSS");
        Uri apkSaveUri = Uri.withAppendedPath(apkSaveDirUri, tempFileName + ".apk");

        FileDownloadThread downloadThread = new FileDownloadThread(this, url, apkSaveUri, handler);
        downloadThread.start();
    }

    private void refreshProgress(Map<String, Object> data) {
        if ("0".equals(data.get("err"))) {
            if (!(boolean) data.get("stop")) {
                //更新进度
                if ("1".equals(data.get("okflag"))) {
                    //完成
                    installApk(data.get("apkfile") + "");
                    finish();
                } else {
                    //未完成
                    int progress = (int) data.get("progress");
                    tv.setText(progress + "%");
                }
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "发生异常，不能更新", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 安装apk
     */
    private void installApk(String apkTempfile) {
        File apkfile = new File(apkTempfile);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
