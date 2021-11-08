package ca.cmpt276.parentapp.UI;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * The TossAnimation class set an animation effect,
 * Can adjust the animation time, adjust the number of turns of the coin,
 * Can adjust the direction of the coin from the outside.
 * also can set the front and back of the coin toss.
 */

public class TossAnimation extends Animation {

    public static final int DIRECTION_NONE = 0; // Does not change in that direction
    public static final int DIRECTION_CLOCKWISE = 1; // Clockwise
    public static final int DIRECTION_ABTUCCLOCKWISE = -1; // Counterclockwise

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

    // The total number of degrees to be turned
    private int mTotalAngle;
    // The serial number of the drawable displayed by the current ImageView
    private int mCurrentResult = -1;

    private Camera mCamera;

    private int mWidth;
    private int mHeight;

    public TossAnimation(int circleCount, int xAxisDirection, int yAxisDirection, int zAxisDirection, int result) {
        this.mCircleCount = circleCount;
        this.mXAxisDirection = xAxisDirection;
        this.mYAxisDirection = yAxisDirection;
        this.mZAxisDirection = zAxisDirection;
        this.mResult = result;

        mTotalAngle = 360 * mCircleCount;
        mCamera = new Camera();
    }

    private TossAnimationListener mTossAnimationListener;

    public void setTossAnimationListener(TossAnimationListener mTossAnimationListener) {
        this.mTossAnimationListener = mTossAnimationListener;
        setAnimationListener(mTossAnimationListener);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        // The number of angles in the current cycle
        int degreeInCircle = ((int) (interpolatedTime * mTotalAngle)) % 360;

        // Change the Drawable in ImageView
        if (degreeInCircle > 90 && degreeInCircle < 270) {
            if (mCurrentResult != -mResult) {
                mCurrentResult = -mResult;
                // Call the interface to change the Drawable of the ImageView
                if (mTossAnimationListener != null) {
                    mTossAnimationListener.onDrawableChange(mCurrentResult, this);
                }
            }
        } else {
            if (mCurrentResult != mResult) {
                mCurrentResult = mResult;
                // Call the interface to change the Drawable of the ImageView
                if (mTossAnimationListener != null) {
                    mTossAnimationListener.onDrawableChange(mCurrentResult, this);
                }
            }
        }

        Matrix matrix = t.getMatrix();

        // Set the angle of deflection
        mCamera.save();
        mCamera.rotate(mXAxisDirection * degreeInCircle, mYAxisDirection * degreeInCircle, mZAxisDirection * degreeInCircle);
        mCamera.getMatrix(matrix);
        mCamera.restore();

        // Rotate at the center of the View
        matrix.preTranslate(-(mWidth >> 1), -(mHeight >> 1));
        matrix.postTranslate(mWidth >> 1, mHeight >> 1);

    }

    public interface TossAnimationListener extends AnimationListener {

        /**
         * Need to show the obverse/reverse side of the coin
         *
         * @param result    Need to show front or back   TossImageView.RESULT_FRONT or TossImageView.RESULT_REVERSE
         * @param animation The started animation.
         */
        void onDrawableChange(int result, TossAnimation animation);
    }
}