package net.htwater.njdistrictfx.core;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by LZY on 2017/7/10.屏蔽运营商广告，防止运营商流量劫持。
 */

public class BlockAdWebviewClient extends WebViewClient {

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return super.shouldInterceptRequest(view, request);
        }
        String url = request.getUrl().toString();
        if (null == url) {
            return super.shouldInterceptRequest(view, request);
        }
        if (url.contains("10086") || url.contains("222.186.61.97") || url.contains("cnzz") || url.contains("baidu")) {
            try {
                InputStream in = new ByteArrayInputStream("".getBytes("UTF-8"));
                return new WebResourceResponse("text/plain", "utf-8", in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

}
