package net.htwater.njdistrictfx.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.BlockAdWebviewClient;
import net.htwater.njdistrictfx.util.DataUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

public class TxlActivity extends BaseActivity {
    private WebView webView;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txl);

        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        webView = (WebView) findViewById(R.id.webView);

        webView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setWebViewClient(new BlockAdWebviewClient());
        webView.addJavascriptInterface(this, "my");

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);

        title.setText("通讯录");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        webView.loadUrl(DataUtil.getURL("通讯录") + "?name=" + SharedPreferencesUtil.getAccount());
    }

    @Override
    public void onBackPressed() {
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
