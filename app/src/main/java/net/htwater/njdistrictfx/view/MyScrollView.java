package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 定制的HorizontalScrollView,用在listview顶部。滚动时让list中的其他HorizontalScrollView也一起滚动。
 * 
 * @author Administrator
 *
 */
public class MyScrollView extends HorizontalScrollView {
	private final List<HorizontalScrollView> list;

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		list = new ArrayList<>();
	}

	public void addToList(HorizontalScrollView view) {
		if (!list.contains(view)) {
			list.add(view);
			// Log.i("MyScrollView", "addToList");
		}
	}

	// @Override
	// protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
	// boolean clampedY) {
	// super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	// for (int i = 0; i < list.size(); i++) {
	// list.get(i).smoothScrollTo(scrollX, scrollY);
	// }
	// }

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).smoothScrollTo(l, t);
		}
	}
}
