package com.anarchy.banana.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.anarchy.banana.R;


public class BananaLayout extends RelativeLayout {
    private ViewDragHelper mViewDragHelper;
    private Banana mBanana;
    private CircleImageView mUpFace;
    private CircleImageView mUpHalo;
    private CircleImageView mCircleImageView;
    private AnimatorSet mSet;
    private boolean isCaptured = false;
    public BananaLayout(Context context) {
        super(context);
        init();
    }

    public BananaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BananaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child instanceof Banana;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                isCaptured = false;
                if(releasedChild instanceof Banana){
                    releaseBanana();
                }
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                isCaptured = true;
                if(capturedChild instanceof Banana){
                    captureBanana();
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                double distance;
                left += changedView.getWidth()/2;//left 即 centerX
                top += changedView.getHeight()/2;//top 即 centerY


            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBanana = (Banana) findViewById(R.id.banana);
        mUpFace = (CircleImageView) findViewById(R.id.up);
        mUpHalo = (CircleImageView) findViewById(R.id.up_halo);
        //缩小mUpHalo 至invisible
        mUpHalo.setScaleX(0.0f);
        mUpHalo.setScaleY(0.0f);
    }

    public void releaseBanana(){
        if(mBanana!=null){
            mBanana.bringToFront();
            mBanana.animate().scaleY(1.0f).scaleX(1.0f).start();
            mViewDragHelper.smoothSlideViewTo(mBanana, mBanana.getPoint().x, mBanana.getPoint().y);
            invalidate();
        }
    }

    public void captureBanana(){
        if(mBanana!=null){
            //放大 香蕉
            mBanana.bringToFront();
            mBanana.animate().scaleX(1.2f).scaleY(1.2f).start();

            //up 向 香蕉靠近
            this.mSet = new AnimatorSet();
        }
    }
    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }


    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
