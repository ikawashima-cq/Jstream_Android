package jp.co.stream.jstplayersdk.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.jstplayersdk.util.DebugLog;
import jp.co.stream.jstplayersdk.util.MyUtils;

public class IndicatorView extends RelativeLayout {

    private static final String TAG = "IndicatorView";

    private Activity mActivity;
    private Context mContext;
    private ImageView mIndicatorImage;
    private int mCurrentAngle;
    private Timer mRotationTimer;
    private int mBaseSize;

    public IndicatorView(Context context) {
        super(context);
        mContext = context;
        mActivity = (Activity)this.getContext();
        mBaseSize = MyUtils.convertDpToPx(mContext, 100);
    }

    public void initView() {

        setBackgroundColor(0x88000000);

        mIndicatorImage = new ImageView(mContext);
        mIndicatorImage.setScaleType(ScaleType.MATRIX);
        int resourceId = getResources().getIdentifier("spinner", "drawable", mContext.getPackageName());
        mIndicatorImage.setImageResource(resourceId);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mIndicatorImage.setLayoutParams(layoutParams);

        Matrix matrix = new Matrix();
        int imageSize = mBaseSize * 4/5;

        matrix.postScale((float)imageSize / (float)mIndicatorImage.getWidth(), (float)imageSize / (float)mIndicatorImage.getHeight());
        matrix.postTranslate((float)imageSize /2, (float)imageSize /2);
        mIndicatorImage.setImageMatrix(matrix);

        addView(mIndicatorImage);
    }

    public void setVisible(final boolean visibility) {
        DebugLog.d(TAG, "IndicatorView: visibility("+visibility+")");
        if (visibility) {
            setVisibility(VISIBLE);
            if (mIndicatorImage.getWidth() > 0 && mIndicatorImage.getHeight() > 0) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mRotationTimer != null) {
                            mRotationTimer.cancel();
                            mRotationTimer = null;
                        }
                        mRotationTimer = new Timer(true);
                        mRotationTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Matrix matrix = new Matrix();
                                        int imageSize = mBaseSize * 4/5;

                                        matrix.postScale((float)imageSize / (float)mIndicatorImage.getWidth(), (float)imageSize / (float)mIndicatorImage.getHeight());
                                        mCurrentAngle += 2;
                                        if (mCurrentAngle >= 360) {
                                            mCurrentAngle = 0;
                                        }
                                        matrix.postRotate(mCurrentAngle, imageSize /2, imageSize /2);
                                        matrix.postTranslate((float)mIndicatorImage.getWidth() /2 - (float)imageSize /2, (float)mIndicatorImage.getHeight() /2 - (float)imageSize /2);
                                        mIndicatorImage.setImageMatrix(matrix);
                                    }
                                });
                            }
                        }, 0, 100);
                    }
                });
            }
        } else {
            setVisibility(GONE);
            mCurrentAngle = 0;
            if (mRotationTimer != null) {
                mRotationTimer.cancel();
                mRotationTimer = null;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugLog.d(TAG, "onDetachedFromWindow");
        mCurrentAngle = 0;
        if (mRotationTimer != null) {
            mRotationTimer.cancel();
            mRotationTimer = null;
        }
    }
}
