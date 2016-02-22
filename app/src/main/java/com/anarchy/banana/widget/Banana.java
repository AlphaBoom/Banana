package com.anarchy.banana.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;


public class Banana extends ImageView {
    private Point mPoint = new Point();
    public Banana(Context context) {
        super(context);
    }

    public Banana(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Banana(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPoint.set(left,top);
    }

    public Point getPoint() {
        return mPoint;
    }
}
