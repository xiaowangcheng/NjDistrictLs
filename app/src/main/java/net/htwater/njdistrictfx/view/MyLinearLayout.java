package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 定制的LinearLayout，搭配MyScrollView使用。作用是拦截touch事件。
 * 
 * @author Administrator
 *
 */
public class MyLinearLayout extends LinearLayout {

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}
