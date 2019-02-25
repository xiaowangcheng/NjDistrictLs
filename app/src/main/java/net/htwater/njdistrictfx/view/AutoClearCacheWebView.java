package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by LZY on 2016/7/7.自动清除webview缓存
 */
public class AutoClearCacheWebView extends WebView {

    public AutoClearCacheWebView(Context context) {
        super(context);
        clearCache();
    }

    public AutoClearCacheWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        clearCache();
    }

    public AutoClearCacheWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        clearCache();
    }

    private void clearCache() {
        // if (SharedPreferencesUtil.isFirstBootAfterUpdate()) {
        clearCache(true);
        // }
    }
}
