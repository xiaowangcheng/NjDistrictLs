package net.htwater.njdistrictfx.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TouchableWebView extends AutoClearCacheWebView {
    private int scrollFlag = 0;

    public TouchableWebView(Context context) {
        super(context);
    }

    public TouchableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 禁止父view拦截触摸事件,可以解决scroll嵌套webview，导致webview无法滑动的问题
//        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (scrollFlag > 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (scrollFlag > 0) {
                    scrollFlag = 0;
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            case MotionEvent.ACTION_CANCEL:
                scrollFlag = 0;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float webcontent = getContentHeight() * getScale();// webview的高度
        float webnow = getHeight() + getScrollY();// 当前webview的高度
        if (Math.abs(webcontent - webnow) < 1) {
            // 已经处于底端
            scrollFlag = 2;
        } else if (getScrollY() == 0) {
            // 已经处于顶端
            scrollFlag = 1;
        } else {
            scrollFlag = 0;
        }
    }

}
