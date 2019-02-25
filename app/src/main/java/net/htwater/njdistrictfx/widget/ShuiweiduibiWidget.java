package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.WebViewActivity;
import net.htwater.njdistrictfx.core.BlockAdWebviewClient;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;

public class ShuiweiduibiWidget extends Widget {
    private final Context context;
    private WebView webView;

    public ShuiweiduibiWidget(Context context) {
        super(context, "shuiweiduibi");
        this.context = context;

        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.layout_widget_duibi, null);
        webView = (WebView) view.findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new BlockAdWebviewClient());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) (300 * MyApplication.density));
        webView.setLayoutParams(params);

        contentView = view;

        request();
    }

    private void request() {
        webView.loadUrl(DataUtil.getURL("duibi"));
    }

    @Override
    protected void refresh() {
        request();
    }

    @Override
    protected void jump() {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", DataUtil.getURL("水位对比"));
        context.startActivity(intent);
    }

}
