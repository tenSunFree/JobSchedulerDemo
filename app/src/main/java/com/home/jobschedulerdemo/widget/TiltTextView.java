package com.home.jobschedulerdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class TiltTextView extends android.support.v7.widget.AppCompatTextView {

    public TiltTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /** 倾斜度8, 上下左右居中 */
        canvas.rotate(-8, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        super.onDraw(canvas);
    }
}
