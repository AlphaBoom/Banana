package com.anarchy.banana.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
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
    private AnimatorSet mSet;
    private Point mUpPoint = new Point();
    private float mUpScope;
    private boolean isCaptured = false;
    private boolean isHaveBanana = false;
    private BananaActionListener mBananaActionListener;
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
               if(state == ViewDragHelper.STATE_IDLE){
                   onBananaReset();
               }

            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if(isCaptured){
                    moveBanana(changedView,left,top,dx,dy);
                }
            }

        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBanana = (Banana) findViewById(R.id.banana);
        mUpFace = (CircleImageView) findViewById(R.id.up);

        mUpFace.setBorderWidth(60);
        mUpFace.setBorderColor(Color.TRANSPARENT);
    }

    private void showHalo(){
        mUpFace.setBorderColor(0x33FEFEFE);
        mUpFace.invalidate();
    }

    private void hideHalo(){
        mUpFace.setBorderColor(Color.TRANSPARENT);
        mUpFace.invalidate();
    }


    public void releaseBanana(){
        if(mBanana!=null){
            if(isHaveBanana){
                mSet = new AnimatorSet();
                mSet.play(ObjectAnimator.ofFloat(mBanana,"scaleX",new float[]{1.2f,0.0f}))
                        .with(ObjectAnimator.ofFloat(mBanana,"scaleY",new float[]{1.2f,0.0f}))
                        .with(ObjectAnimator.ofFloat(mUpFace, "scaleX", new float[]{1.0f, 1.2f}))
                        .with(ObjectAnimator.ofFloat(mUpFace, "scaleY", new float[]{1.0f, 1.2f}));
                mSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mBananaActionListener != null) {
                            mBananaActionListener.onBananaIsAte(mBanana);
                        }

                    }
                });
                mSet.setDuration(300);
                mSet.start();
                return;
            }
            mViewDragHelper.smoothSlideViewTo(mBanana, mBanana.getPoint().x, mBanana.getPoint().y);
            mSet = new AnimatorSet();
            mSet.play(ObjectAnimator.ofFloat(mUpFace,"X",new float[]{mUpPoint.x}))
                    .with(ObjectAnimator.ofFloat(mUpFace, "Y", new float[]{mUpPoint.y}));
            mSet.setDuration(300);
            mSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    invalidate();
                }
            });
            mSet.start();
        }
    }

    public void captureBanana(){
        if(mBanana!=null){
            //放大 香蕉
            mBanana.animate().scaleX(1.2f).scaleY(1.2f).start();


            if(mUpPoint.x==0&&mUpPoint.y==0){
                mUpPoint.x = mUpFace.getLeft();
                mUpPoint.y = mUpFace.getTop();
                mUpScope = 0.3F*mUpFace.getHeight();
            }
            //up 向 香蕉靠近
            mSet = new AnimatorSet();
            mSet.play(ObjectAnimator.ofFloat(mUpFace, "Y", new float[]{mUpPoint.y+mUpScope}))
                    .with(ObjectAnimator.ofFloat(mUpFace, "X", new float[]{mUpPoint.x}));
            mSet.setDuration(300);
            mSet.start();
        }
    }


    public void moveBanana(View changedView,int left,int top,int dx,int dy){
        int selectedCenterX = left + changedView.getWidth()/2;
        int selectedCenterY = top + changedView.getHeight()/2;

        int targetCenterX = mUpPoint.x + mUpFace.getWidth()/2;
        int targetCenterY = mUpPoint.y + mUpFace.getHeight()/2;


        boolean b = isHaveBanana;

        double distance = calculateDistance(selectedCenterX,selectedCenterY,targetCenterX,targetCenterY);
        if(distance<4.0D*mUpScope){
            isHaveBanana = true;
            float reduceDx = 0.2F*dx;
            float reduceDy = 0.2F*dy;
            mUpFace.offsetTopAndBottom((int) checkTopAndBottom(reduceDy));
            mUpFace.offsetLeftAndRight((int) checkLeftAndRight(reduceDx));
        }else {
            isHaveBanana = false;
            double degree = calculateRadian(selectedCenterX, targetCenterX, distance);
            float identifier = Math.signum(selectedCenterY - targetCenterY);
            int newX = (int) (mUpPoint.x+mUpScope*Math.cos(degree));
            int newY = (int) (mUpPoint.y+identifier*mUpScope*Math.sin(degree));
            if(b^isHaveBanana){
                mUpFace.animate().x(newX).y(newY);
            }else {
                mUpFace.setX(newX);
                mUpFace.setY(newY);
            }
        }

        if(b^isHaveBanana){
            if(!isHaveBanana){
                closeMouth();
                return;
            }
            openMouth();
        }
    }


    private void onBananaReset(){
        if(!isHaveBanana)
        mBanana.animate().scaleX(1.0f).scaleY(1.0f);
    }

    private float checkLeftAndRight(float offset){
        if(offset>0){
            if(mUpFace.getRight()<mUpPoint.x+mUpFace.getWidth()+1.2F*mUpScope){
                return offset;
            }
            return 0;
        }else {
            if(mUpFace.getLeft()>mUpPoint.x-1.2F*mUpScope){
                return offset;
            }
            return 0;
        }

    }

    private float checkTopAndBottom(float offset){
        if(offset>0){
            if(mUpFace.getBottom()<mUpPoint.y+mUpFace.getHeight()+1.2F*mUpScope){
                return offset;
            }
            return 0;
        }else {
            if(mUpFace.getTop()>mUpPoint.y-1.2F*mUpScope){
                return offset;
            }
            return 0;
        }
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    public double calculateDistance(int firstX,int firstY,int secondX,int secondY){
        return Math.sqrt(Math.pow(firstX - secondX, 2.0D) + Math.pow(firstY - secondY, 2.0D));
    }


    public double calculateRadian(int firstX, int secondX,double distance){
        double dx = firstX-secondX;
        double radian = Math.acos(dx / distance);
        return radian;
    }
    private void openMouth(){
        mUpFace.animate().scaleY(1.2f).scaleX(1.2f);
        showHalo();
    }
    private void closeMouth(){
        mUpFace.animate().scaleX(1.0f).scaleY(1.0f);
        hideHalo();
    }
    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void setBananaActionListener(BananaActionListener bananaActionListener) {
        mBananaActionListener = bananaActionListener;
    }

    public interface BananaActionListener {
        void onBananaIsAte(Banana banana);
    }

}
