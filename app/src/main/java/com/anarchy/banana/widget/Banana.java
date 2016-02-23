package com.anarchy.banana.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;


public class Banana extends ImageView {
    private Point mPoint = new Point();
    private int[] mBananas;
    private int count;
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
        mPoint.set(left, top);
    }

    /**
     * 设置香蕉图片
     * @param bananas
     */
    public void setBananas(int[] bananas) {
        mBananas = bananas;
    }

    /**
     * 设置显示数量为几的香蕉
     * @param count
     */
    public void putBanana(int count){
        if(mBananas==null) return;
        if(count<=0) throw new IllegalArgumentException("count should > 0");
        this.count = (count-1)%mBananas.length;
        this.setImageResource(mBananas[this.count]);
    }

    public void next(){
        if(this.count<mBananas.length-1){
            this.count ++;
        }
        this.setImageResource(mBananas[this.count]);
    }

    public void previous(){
        if(this.count>0){
            this.count --;
        }
        this.setImageResource(mBananas[this.count]);
    }
    /**
     * 获取当前显示为几的香蕉
     * @return
     */
    public int getBananaCount(){
        return this.count+1;
    }
    public Point getPoint() {
        return mPoint;
    }
}
