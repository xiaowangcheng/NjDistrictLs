package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;

public class TyphoonWidget extends Widget {
    private final Context context;
    private WebView webView;

    public TyphoonWidget(Context context) {
        super(context, "typhoon");
        this.context = context;

        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.layout_widget_typhoon, null);
        webView = (WebView) view.findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);

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


//        webView.setWebViewClient(new BlockAdWebviewClient());

        //         在WebView中打开链接（默认行为是使用浏览器，设置此项后都用WebView打开）
        webView.setWebViewClient(new WebViewClient());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) (250 * MyApplication.density));
        webView.setLayoutParams(params);

        contentView = view;

        request();


    }

    private void request() {
        webView.loadUrl(DataUtil.getURL("typhoon"));
    }

    @Override
    protected void refresh() {
        request();
    }

}
