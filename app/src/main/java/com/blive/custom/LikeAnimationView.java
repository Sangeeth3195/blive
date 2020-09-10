package com.blive.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by sans on 18-08-2018.
 **/

public class LikeAnimationView extends View implements Runnable {
    public static final int mDefaultHeight = 100;
    public static final float mDuration = 0.01f;
    protected static final long mQueenDuration = 200;
    protected ArrayList<AnimationInfo> mAnimationInfos;
    protected ArrayList<AnimationInfo> mDeadPool;
    private boolean mIsDrawing;
    private long mLastAddTime;
    private Paint mPaint;
    private Provider mProvider;
    protected PointF mPtEnd;
    protected PointF mPtStart;
    protected List<Object> mQueen;
    protected final Random mRandom;
    private boolean mRunning;
    private Thread mThread;

    public class AnimationInfo {
        public PointF mBreakPoint;
        public float mDuration = 0.0f;
        public PointF mEndPoint;
        public float mStartX;
        public float mStartY;
        public Object mType;
        public float mX;
        public float mY;

        AnimationInfo(float x, float y, PointF breakPoint, PointF endPoint, Object type) {
            this.mEndPoint = endPoint;
            this.mX = x;
            this.mY = y;
            this.mStartX = x;
            this.mStartY = y;
            this.mBreakPoint = breakPoint;
            this.mType = type;
        }

        public void reset() {
            this.mDuration = 0.0f;
            this.mX = this.mStartX;
            this.mY = this.mStartY;
        }
    }

    public interface Provider {
        Bitmap getBitmap(Object obj);
    }

    public LikeAnimationView(Context context) {
        this(context, null);
    }

    public LikeAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mRandom = new Random();
        this.mDeadPool = new ArrayList();
        this.mLastAddTime = 0;
        this.mRunning = true;
        this.mIsDrawing = false;
        init();
    }

    public void run() {
        while (this.mRunning) {
            if (!(this.mProvider == null || this.mQueen == null || this.mIsDrawing || this.mAnimationInfos == null)) {
                dealQueen();
                if (this.mAnimationInfos.size() != 0) {
                    prepareForAnimation();
                    this.mIsDrawing = true;
                    postInvalidate();
                }
            }
        }
        release();
    }

    private void prepareForAnimation() {
        int i = 0;
        while (i < this.mAnimationInfos.size()) {
            AnimationInfo animationInfo = (AnimationInfo) this.mAnimationInfos.get(i);
            float timeLeft = 1.0f - animationInfo.mDuration;
            animationInfo.mDuration += mDuration;
            float time1 = timeLeft * timeLeft;
            float time2 = (2.0f * timeLeft) * animationInfo.mDuration;
            float time3 = animationInfo.mDuration * animationInfo.mDuration;
            animationInfo.mX = ((this.mPtStart.x * time1) + (animationInfo.mBreakPoint.x * time2)) + (animationInfo.mEndPoint.x * time3);
            animationInfo.mY = ((this.mPtStart.y * time1) + (animationInfo.mBreakPoint.y * time2)) + (animationInfo.mEndPoint.y * time3);
            if (animationInfo.mY <= animationInfo.mEndPoint.y) {
                this.mAnimationInfos.remove(i);
                this.mDeadPool.add(animationInfo);
                i--;
            }
            i++;
        }
    }

    private void dealQueen() {
        long now = System.currentTimeMillis();
        if (this.mQueen.size() > 0 && now - this.mLastAddTime > mQueenDuration) {
            this.mLastAddTime = System.currentTimeMillis();
            AnimationInfo animationInfo = null;
            if (this.mDeadPool.size() > 0) {
                animationInfo = (AnimationInfo) this.mDeadPool.get(0);
                this.mDeadPool.remove(0);
            }
            if (animationInfo == null) {
                animationInfo = createAnimationNode(this.mQueen.get(0));
            }
            animationInfo.reset();
            animationInfo.mType = this.mQueen.get(0);
            this.mAnimationInfos.add(animationInfo);
            this.mQueen.remove(0);
        }
    }

    private void init() {
        this.mPaint = new Paint(1);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setLikeAnimationViewProvider(Provider provider) {
        this.mProvider = provider;
    }

    public PointF getStartPoint() {
        return this.mPtStart;
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    public void startAnimation(Object obj) {
        if (this.mAnimationInfos == null) {
            this.mAnimationInfos = new ArrayList(30);
        }
        if (this.mQueen == null) {
            this.mQueen = Collections.synchronizedList(new ArrayList(30));
        }
        this.mQueen.add(obj);
        if (this.mThread == null) {
            this.mThread = new Thread(this);
            this.mThread.start();
        }
    }

    public void stop() {
        if (this.mAnimationInfos != null) {
            this.mAnimationInfos.clear();
        }
        if (this.mQueen != null) {
            this.mQueen.clear();
        }
        if (this.mDeadPool != null) {
            this.mDeadPool.clear();
        }
    }

    public void release() {
        stop();
        this.mPtEnd = null;
        this.mPtStart = null;
        this.mAnimationInfos = null;
        this.mQueen = null;
        this.mDeadPool = null;
    }

    public void setStartPoint(PointF point) {
        this.mPtStart = point;
    }

    public void setEndPoint(PointF point) {
        this.mPtEnd = point;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mRunning = false;
    }

    protected void onDraw(Canvas canvas) {
        if (!(!this.mRunning || this.mProvider == null || this.mAnimationInfos == null)) {
            Iterator it = this.mAnimationInfos.iterator();
            while (it.hasNext()) {
                AnimationInfo animationInfo = (AnimationInfo) it.next();
                this.mPaint.setAlpha((int) ((255.0f * animationInfo.mY) / this.mPtStart.y));
                canvas.drawBitmap(this.mProvider.getBitmap(animationInfo.mType), animationInfo.mX, animationInfo.mY, this.mPaint);
            }
        }
        this.mIsDrawing = false;
    }

    private PointF getBreakPointF(int scale1, int scale2) {
        PointF pointF = new PointF();
        pointF.x = (float) (this.mRandom.nextInt(((getMeasuredWidth() - getPaddingRight()) + getPaddingLeft()) / scale1) + (getMeasuredWidth() / scale2));
        pointF.y = (float) (this.mRandom.nextInt(((getMeasuredHeight() - getPaddingBottom()) + getPaddingTop()) / scale1) + (getMeasuredHeight() / scale2));
        return pointF;
    }

    protected AnimationInfo createAnimationNode(Object type) {
        PointF endPoint = this.mPtEnd;
        if (endPoint == null) {
            endPoint = new PointF((float) this.mRandom.nextInt(getMeasuredWidth()), 0.0f);
        }
        if (this.mPtStart == null) {
            this.mPtStart = new PointF((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() - 100));
        }
        return new AnimationInfo(this.mPtStart.x, this.mPtStart.y, getBreakPointF(2, 3), endPoint, type);
    }
}
