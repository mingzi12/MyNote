/**
 * @author LHT
 */
package com.mingzi.onenote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

public class NoteEditText extends EditText {
	
	private Paint paint;
	
	public NoteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
	}
	
	/**
	 * 重画界面
	 * @see android.widget.TextView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		int lineHeight = this.getLineHeight();
		
		int topPadding = this.getPaddingTop();
		int leftPadding = this.getPaddingLeft();
		
		float textSize = getTextSize();
		setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
		
		int y = (int)(topPadding + textSize + 3);
		
		for(int i = 0; i < getLineCount(); i++) {
			canvas.drawLine(leftPadding, y + 5, getRight() - leftPadding, y + 5, paint);
			y += lineHeight;
		}
		canvas.translate(0, 0);
		
		super.onDraw(canvas);
	}
}
