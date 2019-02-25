package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 用于scrollview嵌套gridview，防止此时的gridview只显示一行
 * 
 * @author Administrator
 *
 */
public class MyGridView extends GridView {

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
