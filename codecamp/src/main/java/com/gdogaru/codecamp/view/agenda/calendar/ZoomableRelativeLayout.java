package com.gdogaru.codecamp.view.agenda.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class ZoomableRelativeLayout extends RelativeLayout {
    float mScaleFactor = 1;
    float mPivotX;
    float mPivotY;

    public ZoomableRelativeLayout(Context context) {
        super(context);
    }

    public ZoomableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void dispatchDraw(Canvas canvas) {
//todo        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, mPivotX, mPivotY);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void scale(float scaleFactor, float pivotX, float pivotY) {
        mScaleFactor = scaleFactor;
        mPivotX = pivotX;
        mPivotY = pivotY;
        this.invalidate();
    }

    public void restore() {
        mScaleFactor = 1;
        this.invalidate();
    }

}