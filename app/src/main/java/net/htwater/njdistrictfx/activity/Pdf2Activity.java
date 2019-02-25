package net.htwater.njdistrictfx.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class Pdf2Activity extends BaseActivity{

    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/"+Constants.FILE_TEMP_URL+"/pdf/";
    private ProgressDialog progressDialog;
    private PDFView pdfView;

    private String fileName;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf2);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("防汛预案");
        pdfView = findViewById(R.id.pdfView);

        String url = "http://59.83.223.39:8091/northOfYangtzeRiver/views/baseinfo/views/pdf/prevention.pdf";

        String[] paths=url.split("/");
        fileName = paths[paths.length-1];

        String path = BASE_PATH + fileName;

        file=new File(path);

      /*  if (file.exists()){
            pdfView.fromFile(file)
//                  .pages(0, 0, 0, 0, 0, 0) // 默认全部显示，pages属性可以过滤性显示
                    .defaultPage(1)//默认展示第一页
                    //.onPageChange(this)//监听页面切换
                    .load();
        }else {
            downloadFile(url, path);
        }*/

        pdfView.fromAsset("prevention.pdf").defaultPage(0).onPageChange(new OnPageChangeListener() {

            @Override
            public void onPageChanged(int page, int pageCount) {
                // 当用户在翻页时候将回调。
                //Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
            }
        }).load();

        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void downloadFile(final String url, final String path) {
        progressDialog = new ProgressDialog(this);
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("亲，努力下载中。。。");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);
            }

            @Override
            public void onSuccess(File result) {
//                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                pdfView.fromFile(file)
//                                        .pages(0, 0, 0, 0, 0, 0) // 默认全部显示，pages属性可以过滤性显示
                        .defaultPage(1)//默认展示第一页
                        //.onPageChange(this)//监听页面切换
                        .load();
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
//                Toast.makeText(MainActivity.this, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

}
