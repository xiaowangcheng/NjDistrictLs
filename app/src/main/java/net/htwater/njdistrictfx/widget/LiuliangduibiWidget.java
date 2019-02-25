package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.BlockAdWebviewClient;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;

public class LiuliangduibiWidget extends Widget {
    private final Context context;
    private WebView webView;

    public LiuliangduibiWidget(Context context) {
        super(context, "liuliangduibi");
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
        webView.loadUrl(DataUtil.getURL("流量对比"));
    }

    @Override
    protected void refresh() {
        request();
    }

}
