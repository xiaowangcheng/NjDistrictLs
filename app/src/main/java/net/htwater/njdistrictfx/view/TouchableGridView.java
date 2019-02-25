package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by LZY on 2017/6/7.
 */

public class TouchableGridView extends GridView {
    public TouchableGridView(Context context) {
        super(context);
    }

    public TouchableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 禁止父view拦截触摸事件,可以解决scroll嵌套，导致无法滑动的问题
        getParent().requestDisallowInterceptTouchEvent(true);

        return super.dispatchTouchEvent(ev);
    }

}
