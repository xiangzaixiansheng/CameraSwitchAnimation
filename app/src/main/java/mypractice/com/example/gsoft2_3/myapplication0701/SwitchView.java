package mypractice.com.example.gsoft2_3.myapplication0701;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;

/**
 * Created by gsoft2-3 on 19-7-4.
 *
 *滑动切换屏幕动画
 */

public class SwitchView extends View {

    private final int START_DRAG=0;
    private final int END_DRAG=1;
    private final int START_DST_OUT_WAVE=2;
    private final int START_DISMISS_COVER=3;
    private final int START_CAMERA_SWITCH=4;

    public interface Callback{
        void OnCameraStartSwitch();
        void onAnimaStart();
        void onAnimaFinish();
    }

    private Callback mCallback;
    private View mCoverView;
    private int mCircleCenterX;
    private int mCircleCenterY;
    private float mDraggingEndRadius;
    private float mDraggingStartRadius;
    private float mRadius=0;
    private float mHoleRadius = 0.0f;
    private Paint mPaint = new Paint();
    private Paint mPaintBlack = new Paint();
    private Paint mHolePaint = new Paint();
    private float mInitialTouchX, mInitialTouchY;
    private boolean isUp = false;
    private boolean isDraggingMode = false;
    private boolean isZoomMode = false;
    private boolean isVideoCaptureSwitchMode = false;
    private PowerManager mPowerManager;
    private AnimatorSet set0, set1;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_DRAG:
                    setRadius(msg.arg1);
                    float alpha = msg.arg1 / (mDraggingEndRadius - mDraggingStartRadius);
                    //背景黑色加深
                    mCoverView.setAlpha(alpha);
                    break;
                case END_DRAG:
                    set0.start();
                    mHandler.sendEmptyMessageDelayed(START_DST_OUT_WAVE, 50);
                    break;
                case START_DST_OUT_WAVE:
                    set1.start();
                    break;
                case START_DISMISS_COVER:
                    mCoverView.setAlpha(0);
                    setRadius(0);
                    setHoleRadius(0);
                    SwitchView.this.setAlpha(1.0f);
                    break;
                case START_CAMERA_SWITCH:
                    if(mCallback != null) {
                      //调用实现类里面的切换摄像头方法
                      //mCallback.onCameraStartSwitch();
                    }
                    break;
            }

        }
    };

    public SwitchView(Context context) {
        super(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundCircle(canvas);
    }

    /**
     *
     * @param context
     * 初始化自定义ｖｉｅｗ
     */
    public void initView(Context context) {
        mPaintBlack.setColor(Color.parseColor("#ff000000"));
        mPaintBlack.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setAntiAlias(true);
        mHolePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mHolePaint.setAntiAlias(true);
        mDraggingStartRadius = (float)context.getResources().getDimensionPixelSize(R.dimen.dragging_start_radius);
        mDraggingEndRadius = (float)context.getResources().getDimensionPixelSize(R.dimen.dragging_end_radius);
        mCircleCenterX = 360;
        mCircleCenterY = 0;
        setRadius(0);
        setHoleRadius(0);
        set0 = getSwipeSwitchAnimation(this);
        set1 = getAfterSwitchAnimation(this);
        set1.addListener(new Animator.AnimatorListener() {
            /**
            * 当AnimatorSet开始时调用
            */
            @Override
            public void onAnimationStart(Animator animation) {

            }

            /**
             * 当AnimatorSet结束时调用
            * */
            @Override
            public void onAnimationEnd(Animator animation) {
                mCoverView.setAlpha(0.0f);
                setRadius(0);
                setHoleRadius(0);
                Message msg = mHandler.obtainMessage();
                msg.what = START_CAMERA_SWITCH;
                mHandler.sendMessage(msg);
            }
            /**
            * 当AnimatorSet被取消时调用
            * */
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mPowerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    }

    public void cameraOpenDone(){
        mHandler.sendEmptyMessage(START_DISMISS_COVER);
    }

    public void setPreviewCover(View view){
        mCoverView = view;
    }

    //设置callback对象
    public void setCallback(Callback callback){
        mCallback = callback;
    }

    private void drawBackgroundCircle(Canvas canvas){
        canvas.drawCircle((float)mCircleCenterX, (float)mCircleCenterY, mRadius, mPaint);
        canvas.drawCircle((float)mCircleCenterX, (float)mCircleCenterY, mHoleRadius, mPaintBlack);
    }

    public void setRadius(float radius) {
        mRadius = radius;
        postInvalidate();
    }

    public void setHoleRadius(float radius) {
        mHoleRadius = radius;
        postInvalidate();
    }

    public void setIsZoomMode(boolean iszoommode){
        isZoomMode=iszoommode;
    }

    public boolean isZoomMode(){
        return isZoomMode;
    }

    public AnimatorSet getSwipeSwitchAnimation(View v) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList list = new ArrayList();
        list.add(getSwipeSwitchAnimator(v, 200));
        animatorSet.playTogether(list);
        return animatorSet;
    }

    private ObjectAnimator getSwipeSwitchAnimator(View p1, int p2) {
        float maxRadius = 1500;
        float currentRadius = mRadius;
        PathInterpolator cubicBezierInterpolator = new PathInterpolator(0.39f, 0.57f, 0.56f, 1.0f);
        PropertyValuesHolder radius = PropertyValuesHolder.ofFloat("radius", new float[] {currentRadius, maxRadius});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(p1, new PropertyValuesHolder[] {radius});
        animator.setDuration((long) p2);
        animator.setInterpolator(cubicBezierInterpolator);
        return animator;
    }

    public AnimatorSet getAfterSwitchAnimation(View v) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList list = new ArrayList();
        list.add(getAfterSwitchAnimator(v, 200));
        animatorSet.playTogether(list);
        return animatorSet;
    }

    private ObjectAnimator getAfterSwitchAnimator(View p1, int p2) {
        float maxRadius = 1500;
        PathInterpolator cubicBezierInterpolator = new PathInterpolator(0.55f, 0.05f, 0.68f, 0.19f);
        p1.setAlpha(0.0f);
        PropertyValuesHolder holeRadius = PropertyValuesHolder.ofFloat("holeRadius", new float[] {(0.0f), maxRadius});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(p1, new PropertyValuesHolder[] {holeRadius});
        animator.setDuration((long) p2);
        animator.setInterpolator(cubicBezierInterpolator);
        return animator;
    }

    public AnimatorSet getDraggingCancelAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList list = new ArrayList();
        list.add(getDraggingCancelAnimator(this, 200));
        float alpha = mCoverView.getAlpha();
        list.add(getEaseOutAnimator(mCoverView, 0xc8, alpha));
        animatorSet.playTogether(list);
        return animatorSet;
    }

    private ObjectAnimator getDraggingCancelAnimator(SwitchView p1, int p2) {
        float currentRadius = mRadius;
        float draggingStartRadius = mDraggingStartRadius;
        PathInterpolator cubicBezierInterpolator = new PathInterpolator(0.39f, 0.57f, 0.56f, 1.0f);
        p1.setAlpha(1.0f);
        PropertyValuesHolder radius = PropertyValuesHolder.ofFloat("radius", new float[] {currentRadius, draggingStartRadius});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(p1, new PropertyValuesHolder[] {radius});
        animator.setDuration((long)p2);
        animator.setInterpolator(cubicBezierInterpolator);
        return animator;
    }

    private ObjectAnimator getEaseOutAnimator(View p1, int p2, float p3) {
        PathInterpolator cubicBezierInterpolator = new PathInterpolator(0.39f, 0.57f, 0.56f, 0.0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", new float[] {p3, 0x0});
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(p1, new PropertyValuesHolder[] {alpha});
        animator.setDuration((long)p2);
        animator.setInterpolator(cubicBezierInterpolator);
        return animator;
    }

    public void onTouch(MotionEvent event) {
        int action = event.getActionMasked();
        final float y = event.getY();
        final float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouchY = y;
                mInitialTouchX = x;
                isUp = false;
                isDraggingMode = false;
                isVideoCaptureSwitchMode = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float yDist = y - mInitialTouchY;
                float xDist = x - mInitialTouchX;
                if(!isVideoCaptureSwitchMode && Math.abs(yDist) < Math.abs(xDist) && Math.abs(xDist) >= 25) {
                    isVideoCaptureSwitchMode = true;
                }
                if(!isVideoCaptureSwitchMode && yDist > mDraggingStartRadius && !isDraggingMode && yDist > Math.abs(xDist) && Math.abs(xDist) < 25) {
                    SwitchView.this.setAlpha(1.0f);
                    setHoleRadius(0);
                    setRadius(0);
                    isDraggingMode = true;
                    if(mCallback != null){
                        mCallback.onAnimaStart();
                    }
                }
                if (yDist > mDraggingEndRadius-mDraggingStartRadius && isDraggingMode && !isZoomMode) {
                    if (!isUp) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = END_DRAG;
                        mHandler.sendMessage(msg);
                        isUp = true;
                    }
                    break;
                }
                if(!isUp && isDraggingMode && !isZoomMode) {
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = (int) yDist;
                    msg.what = START_DRAG;
                    mHandler.sendMessage(msg);
                }
                if(isZoomMode && mCoverView.getAlpha()!=0) {
                    mCoverView.setAlpha(0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isUp == false && isDraggingMode && !isZoomMode) {
                    AnimatorSet set = getDraggingCancelAnimation();
                    set.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setRadius(0);
                            if(mCallback != null){
                                mCallback.onAnimaFinish();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    if(!mPowerManager.isPowerSaveMode()){
                        set.start();
                    }else{
                        mHandler.removeMessages(START_DRAG);
                        mCoverView.setAlpha(0);
                        setRadius(0);
                        setHoleRadius(0);
                        SwitchView.this.setAlpha(1.0f);
                        if(mCallback != null){
                            mCallback.onAnimaFinish();
                        }
                    }
                }else if(isZoomMode){
                    setRadius(0);
                    setAlpha(0);
                }
                break;
        }
    }


}
