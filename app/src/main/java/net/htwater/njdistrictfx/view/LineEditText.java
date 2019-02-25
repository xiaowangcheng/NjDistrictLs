package net.htwater.njdistrictfx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;

public class LineEditText extends EditText {
	private final Paint paint;

	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(context.getResources().getColor(R.color.text_black));
		paint.setAntiAlias(true);
		paint.setStrokeWidth(MyApplication.density);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// int lineCount = getLineCount();
		// int lineHeight = (int) (getLineHeight() * 1.15);
		// for (int i = 0; i < lineCount; i++) {
		// int lineY = (i + 1) * lineHeight;
		// canvas.drawLine(0, lineY, this.getWidth(), lineY, paint);
		// }
		canvas.drawLine(0, this.getHeight(), this.getWidth(), this.getHeight(),
				paint);
	}
}
