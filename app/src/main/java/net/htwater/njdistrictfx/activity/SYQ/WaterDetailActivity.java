package net.htwater.njdistrictfx.activity.SYQ;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.BlockAdWebviewClient;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

/**
 * created by lzy on 2017/4/1.水情站点页面使用
 */
public class WaterDetailActivity extends BaseActivity {
    private WebView webView;
    private String stcd;
    private String sttp;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_detail);

        webView = (WebView) findViewById(R.id.webView);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        TextView title = (TextView) findViewById(R.id.title);
        TextView rightButton = (TextView) findViewById(R.id.rightButton);

        stcd = getIntent().getStringExtra("stcd");
        sttp = getIntent().getStringExtra("sttp");
        String stnm = getIntent().getStringExtra("stnm");

        title.setText(stnm);

        initWebView();

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript: toggleMenu()");
            }
        });

        String url;
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            url = "http://218.2.110.162/njfxphone_page/htmls/zdylxq_v.html";
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOW) {
            url = "http://218.2.110.162/njfxphone_page/htmls/llls_v.html";
        } else {
            url = "http://218.2.110.162/njfxphone_page/htmls/waterline.html";
        }
        webView.loadUrl(url);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
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

    }

    private final BlockAdWebviewClient client = new BlockAdWebviewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN
                    || MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOW) {
                webView.loadUrl("javascript: getJSON('" + stcd + "')");
            } else {
                webView.loadUrl("javascript: getJSON('" + stcd + "','" + sttp + "')");
            }
        }

    };
}
