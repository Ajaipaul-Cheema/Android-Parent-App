package ca.cmpt276.parentapp.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.HashSet;
import java.util.Set;

import ca.cmpt276.as3.parentapp.R;

/**
 * The TossImageView class
 * Can design the parameters of the animation,
 * including the number of rotations, rotation direction, animation time, delay time,
 * Can set the coin to draw a parabola,
 * Can set the movement trajectory of the coin parabola X axis, Y axis and Z axis,
 * and return the result of tossing the coin to FlipCoinActivity through the callback function.
 */

public class TossImageView extends android.support.v7.widget.AppCompatImageView {

    public static final int RESULT_FRONT = 1; // front side
    public static final int RESULT_REVERSE = -1; // back side

    /**
     * Number of turns
     */
    private int mCircleCount;
    /**
     * x-axis rotation direction
     */
    private int mXAxisDirection;
    /**
     * y-axis rotation direction
     */
    private int mYAxisDirection;
    /**
     * Z axis rotation direction
     */
    private int mZAxisDirection;
    /**
     * The result of a coin toss
     */
    private int mResult;
    /**
     * Animation time
     */
    private int mDuration;
    /**
     * delay time
     */
    private int mStartOffset;
    /**
     * Interpolator
     */
    private Interpolator mInterpolator = new DecelerateInterpolator();

    /**
     * Obverse of coin
     */
    private Drawable mFrontDrawable;

    /**
     * Reverse side of the coin
     */
    private Drawable mReversetDrawable;

    /**
     * Callback function for coin toss
     */
    private TossAnimation.TossAnimationListener mTossAnimationListener;

    private final Set<Animation> mOtherAnimation = new HashSet<>();

    public TossImageView(Context context) {
        super(context);
        setCoinDrawableIfNecessage();
    }

    public TossImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TossAnimation);

        mCircleCount = a.getInteger(R.styleable.TossAnimation_circleCount, context.getResources().getInteger(R.integer.toss_default_circleCount));
        mXAxisDirection = a.getInteger(R.styleable.TossAnimation_xAxisDirection, context.getResources().getInteger(R.integer.toss_default_xAxisDirection));
        mYAxisDirection = a.getInteger(R.styleable.TossAnimation_yAxisDirection, context.getResources().getInteger(R.integer.toss_default_yAxisDirection));
        mZAxisDirection = a.getInteger(R.styleable.TossAnimation_zAxisDirection, context.getResources().getInteger(R.integer.toss_default_zAxisDirection));
        mResult = a.getInteger(R.styleable.TossAnimation_result, context.getResources().getInteger(R.integer.toss_default_result));

        mFrontDrawable = a.getDrawable(R.styleable.TossAnimation_frontDrawable);
        mReversetDrawable = a.getDrawable(R.styleable.TossAnimation_reverseDrawable);

        mDuration = a.getInteger(R.styleable.TossAnimation_duration, context.getResources().getInteger(R.integer.toss_default_duration));
        mStartOffset = a.getInteger(R.styleable.TossAnimation_startOffset, context.getResources().getInteger(R.integer.toss_default_startOffset));

        a.recycle();

        setCoinDrawableIfNecessage();
    }

    public TossImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TossAnimation, defStyleAttr, 0);

        mCircleCount = a.getInteger(R.styleable.TossAnimation_circleCount, context.getResources().getInteger(R.integer.toss_default_circleCount));
        mXAxisDirection = a.getInteger(R.styleable.TossAnimation_xAxisDirection, context.getResources().getInteger(R.integer.toss_default_xAxisDirection));
        mYAxisDirection = a.getInteger(R.styleable.TossAnimation_yAxisDirection, context.getResources().getInteger(R.integer.toss_default_yAxisDirection));
        mZAxisDirection = a.getInteger(R.styleable.TossAnimation_zAxisDirection, context.getResources().getInteger(R.integer.toss_default_zAxisDirection));
        mResult = a.getInteger(R.styleable.TossAnimation_result, context.getResources().getInteger(R.integer.toss_default_result));

        mFrontDrawable = a.getDrawable(R.styleable.TossAnimation_frontDrawable);
        mReversetDrawable = a.getDrawable(R.styleable.TossAnimation_reverseDrawable);

        mDuration = a.getInteger(R.styleable.TossAnimation_duration, context.getResources().getInteger(R.integer.toss_default_duration));
        mStartOffset = a.getInteger(R.styleable.TossAnimation_startOffset, context.getResources().getInteger(R.integer.toss_default_startOffset));

        a.recycle();

        setCoinDrawableIfNecessage();
    }

    /**
     * Set mFrontDrawable and mReversetDrawable 's value
     */
    private void setCoinDrawableIfNecessage() {
        if (mFrontDrawable == null) {
            mFrontDrawable = getDrawable();
        }
        if (mReversetDrawable == null) {
            mReversetDrawable = getDrawable();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setCoinDrawableIfNecessage();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setCoinDrawableIfNecessage();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setCoinDrawableIfNecessage();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setCoinDrawableIfNecessage();
    }

    /**
     * Set the number of revolutions of the coin
     *
     * @param circleCount
     * @return
     */
    public TossImageView setCircleCount(int circleCount) {
        this.mCircleCount = circleCount;
        return this;
    }

    /**
     * Set the x-axis rotation direction
     *
     * @param xAxisDirection TossAnimation.DIRECTION_NONE  or  TossAnimation.DIRECTION_CLOCKWISE  or  TossAnimation.DIRECTION_ABTUCCLOCKWISE
     * @return
     */
    public TossImageView setXAxisDirection(int xAxisDirection) {
        if (Math.abs(xAxisDirection) > 1) {
            throw new RuntimeException("Math.abs(Direction) must be less than 1");
        }
        this.mXAxisDirection = xAxisDirection;
        return this;
    }

    /**
     * Set the y-axis rotation direction
     *
     * @param yAxisDirection TossAnimation.DIRECTION_NONE  or  TossAnimation.DIRECTION_CLOCKWISE  or  TossAnimation.DIRECTION_ABTUCCLOCKWISE
     * @return
     */
    public TossImageView setYAxisDirection(int yAxisDirection) {
        if (Math.abs(yAxisDirection) > 1) {
            throw new RuntimeException("Math.abs(Direction) must be less than 1");
        }
        this.mYAxisDirection = yAxisDirection;
        return this;
    }

    /**
     * Set z-axis optional direction
     *
     * @param zAxisDirection TossAnimation.DIRECTION_NONE  or  TossAnimation.DIRECTION_CLOCKWISE  or  TossAnimation.DIRECTION_ABTUCCLOCKWISE
     * @return
     */
    public TossImageView setZAxisDirection(int zAxisDirection) {
        if (Math.abs(zAxisDirection) > 1) {
            throw new RuntimeException("Math.abs(Direction) must be less than 1");
        }
        this.mZAxisDirection = zAxisDirection;
        return this;
    }

    /**
     * Set the result of a coin toss
     *
     * @param result TossAnimation.RESULT_FRONT
     *               TossAnimation.RESULT_REVERSE
     */
    public void setResult(int result) {
        if (Math.abs(result) != 1) {
            throw new RuntimeException("Math.abs(Direction) must be 1");
        }
        this.mResult = result;
    }

    /**
     * Set animation duration
     *
     * @param duration
     * @return
     */
    public TossImageView setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    /**
     * set Interpolator
     *
     * @param interpolator
     * @return
     */
    public TossImageView setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    /**
     * Add an Animation
     *
     * @param animation
     */
    public void addOtherAnimation(Animation animation) {
        mOtherAnimation.add(animation);
    }

    /**
     * Empty Animation
     */
    public void cleareOtherAnimation() {
        mOtherAnimation.clear();
    }

    /**
     * Set the animation callback interface
     *
     * @param tossAnimationListener
     */
    public void setTossAnimationListener(TossAnimation.TossAnimationListener tossAnimationListener) {
        this.mTossAnimationListener = tossAnimationListener;
    }

    /**
     * Start coin toss animation
     */
    public void startToss() {

        clearAnimation();

        TossAnimation tossAnimation = new TossAnimation(mCircleCount, mXAxisDirection, mYAxisDirection, mZAxisDirection, mResult);
        tossAnimation.setDuration(mDuration);
        tossAnimation.setStartOffset(mStartOffset);
        tossAnimation.setInterpolator(mInterpolator);
        tossAnimation.setTossAnimationListener(new QTTossAnimationListener(mTossAnimationListener));

        AnimationSet as = new AnimationSet(false);
        as.addAnimation(tossAnimation);

        for (Animation animation : mOtherAnimation) {
            as.addAnimation(animation);
        }

        startAnimation(as);

    }

    /**
     * Can be change the ImageView
     */
    public class QTTossAnimationListener implements TossAnimation.TossAnimationListener {

        private final TossAnimation.TossAnimationListener mTossAnimationListener;

        public QTTossAnimationListener(TossAnimation.TossAnimationListener tossAnimationListener) {
            mTossAnimationListener = tossAnimationListener;
        }

        @Override
        public void onDrawableChange(int result, TossAnimation animation) {
            switch (result) {
                case TossAnimation.RESULT_FRONT:
                    setImageDrawable(mFrontDrawable);
                    break;
                case TossAnimation.RESULT_REVERSE:
                    setImageDrawable(mReversetDrawable);
                    break;
            }
            if (mTossAnimationListener != null) {
                mTossAnimationListener.onDrawableChange(result, animation);
            }
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (mTossAnimationListener != null) {
                mTossAnimationListener.onAnimationStart(animation);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mTossAnimationListener != null) {
                mTossAnimationListener.onAnimationEnd(animation);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            if (mTossAnimationListener != null) {
                mTossAnimationListener.onAnimationRepeat(animation);
            }
        }
    }
}
