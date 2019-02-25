package net.htwater.njdistrictfx.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.BlockAdWebviewClient;
import net.htwater.njdistrictfx.util.DataUtil;

import java.lang.reflect.Method;

public class WebViewActivity extends BaseActivity {
    private String name = "";
    private WebView webView;
    private TextView title;
    private final BlockAdWebviewClient client = new BlockAdWebviewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (name.equals("防汛简报") || name.equals("降水预报")|| name.equals("台风信息")) {
                title.setText(name);
            } else {
                title.setText(webView.getTitle());
            }
        }

    };

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webView);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        title = (TextView) findViewById(R.id.title);

        if (null != getIntent().getStringExtra("name")) {
            name = getIntent().getStringExtra("name");
        }

        if (name.equals("天气预报") ||
                name.equals("专业预报") ||
                name.equals("气象云图") ||
                name.equals("水系图") ||
                name.equals("卫星云图") || name.equals("雷达图")|| name.equals("天气信息")|| name.equals("降水预报")|| name.equals("台风信息")
                || name.equals("短临降雨")|| name.equals("防汛简报")|| name.equals("三天天气图")|| name.equals("公告栏")) {
            title.setText(name);
        } else {
            title.setText(webView.getTitle());
        }

        webView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setWebViewClient(client);
        webView.addJavascriptInterface(this, "my");

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);


        settings.setJavaScriptEnabled(true);

//        webSettings.setSupportZoom(true);
//        webSettings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);

        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.supportMultipleWindows();
        settings.setAllowFileAccess(true);
        settings.setNeedInitialFocus(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);



        //去掉缩放按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        } else {
            //低版本隐藏缩放按钮
            try {
                Class webview = Class.forName("android.webkit.WebView");
                Method method = webview.getMethod("getZoomButtonsController");
                ZoomButtonsController zoom_controll = (ZoomButtonsController) method.invoke(webView, true);
                zoom_controll.getZoomControls().setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        webView.requestFocusFromTouch();

        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String url = getIntent().getStringExtra("url");
        if (null != url) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl(DataUtil.getURL(name));
        }

        //         在WebView中打开链接（默认行为是使用浏览器，设置此项后都用WebView打开）
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onBackPressed() {
        if (name.equals("汛情简报") || name.equals("flood")) {
            super.onBackPressed();
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @JavascriptInterface
    public void DialTelephone(String number) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + number));
        startActivity(intent);
    }
}
