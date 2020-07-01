package jp.co.stream.jstplayersdk.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.co.stream.jstplayersdk.util.DebugLog;

public class SeekBarView extends RelativeLayout {

    private static final String TAG = "SeekBarView";

    private static final int ID_SEEK_BAR_PANEL = 0;
    private static final int ID_SEEK_BAR_AREA = 1;
    private static final int ID_SEEK_BAR = 2;
    private static final int ID_PROGRESS_BAR = 3;
    private static final int ID_AD_POINT_BAR = 4;
    private static final int ID_THUMB = 5;
    private static final int ID_THUMB_LING = 6;
    private static final int ID_BUFFER_BAR = 7;
    private static final int ID_SEEK_BAR_THUMBNAIL_AREA = 8;
    private static final int ID_SEEK_BAR_THUMBNAIL_VIEW = 9;
    private static final int ID_SEEK_BAR_THUMBNAIL_TIME_LABEL = 10;
    private static final int ID_SEEK_BAR_THUMBNAIL_TAIL =11;

    private Context mContext;
    private float mProgress = 0;
    private float mBufferSize = 0;
    private RelativeLayout mSeekBarArea;
    private RelativeLayout mSeekBar;
    private ImageView mSeekBarBackGround;
    private ImageView mProgressBar;
    private ImageView mBufferBar;
    private ImageView mThumb;
    private RelativeLayout mAdPointBar;
    private boolean mIsSeeking;
    private boolean mIsSeekable;
    private SeekBarCallbacks mSeekBarCallbacks;
    private RelativeLayout mSeekBarBalloonArea;
    private SeekBarThumbnailView mSeekBarThumbnailView;
    private int mSeekBarAreaWidth;
    private int mSeekBarAreaHeight;
    private int mScreenWidth;
    private int mScreenHeight;
    private TextView mTimeLabel;
    private ImageView mSeekBarBalloonTail;

    public interface SeekBarCallbacks {
        public void onChangePosition(float position);
        public void onSeekStart();
        public void onSeekEnd(float position);
    }

    public void setCallbacks(SeekBarCallbacks callbacks){
        mSeekBarCallbacks = callbacks;
    }

    public SeekBarView(Context context) {
        super(context);
        mContext = context;

        try{
            LayoutParams layoutParams;

            mProgress = 0;
            mBufferSize = 0;
            mIsSeekable = true;

            mSeekBarArea = new RelativeLayout(mContext);
            mSeekBarArea.setId(ID_SEEK_BAR_PANEL);
            mSeekBarArea.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DebugLog.d(TAG, "mSeekBarArea.setOnTouchListener onTouch");

                    switch ( event.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mIsSeeking) {
                                int newXpos = (int)(event.getX());
                                if (newXpos <= mSeekBar.getX()) {
                                    newXpos = (int)mSeekBar.getX();
                                }
                                if (newXpos >= mSeekBar.getWidth() + mSeekBar.getX()) {
                                    newXpos = mSeekBar.getWidth() + (int)mSeekBar.getX();
                                }
                                int tmpthumbsetx = newXpos - mThumb.getWidth() /2;
                                DebugLog.d(TAG, "mSeekBarArea.setOnTouchListener thumbsetx:" + tmpthumbsetx);
                                mThumb.setX(tmpthumbsetx);
                                float position = (mThumb.getX() + mThumb.getWidth() /2 - mSeekBar.getX()) / mSeekBar.getWidth();
                                setSeekPosition(position);
                                if (mSeekBarCallbacks != null) {
                                    mSeekBarCallbacks.onChangePosition(position);
                                }
                            }
                            setThumbnailVisible(mIsSeeking);
                            break;
                        case MotionEvent.ACTION_UP:
                            setDragStart(false);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            setDragStart(false);
                            break;
                    }
                    return true;
                }
            });
            addView(mSeekBarArea);

            mSeekBar = new RelativeLayout(mContext);
            mSeekBar.setId(ID_SEEK_BAR_AREA);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mSeekBar.setLayoutParams(layoutParams);
            mSeekBarArea.addView(mSeekBar);

            mSeekBarBackGround = new ImageView(mContext);
            mSeekBarBackGround.setId(ID_SEEK_BAR);
            int seekBarBackgroundId = getResources().getIdentifier("seek_bar", "drawable", mContext.getPackageName());
            mSeekBarBackGround.setImageResource(seekBarBackgroundId);
            mSeekBarBackGround.setBackgroundColor(0xFFFF0000);
            mSeekBarBackGround.setScaleType(ImageView.ScaleType.FIT_XY);

            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(CENTER_IN_PARENT);
            mSeekBar.addView(mSeekBarBackGround);

            mBufferBar = new ImageView(mContext);
            mBufferBar.setId(ID_BUFFER_BAR);
            mBufferBar.setImageResource(getResources().getIdentifier("buffer_bar", "drawable", mContext.getPackageName()));
            mBufferBar.setBackgroundColor(0x00000000);
            mBufferBar.setScaleType(ImageView.ScaleType.FIT_XY);
            mSeekBar.addView(mBufferBar);

            mProgressBar = new ImageView(mContext);
            mProgressBar.setId(ID_PROGRESS_BAR);
            mProgressBar.setImageResource(getResources().getIdentifier("progress_bar", "drawable", mContext.getPackageName()));
            mProgressBar.setBackgroundColor(0x00000000);
            mProgressBar.setScaleType(ImageView.ScaleType.FIT_XY);
            mSeekBar.addView(mProgressBar);

            mAdPointBar = new RelativeLayout(mContext);
            mAdPointBar.setId(ID_AD_POINT_BAR);
            mAdPointBar.setBackgroundColor(0x00000000);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mAdPointBar.setLayoutParams(layoutParams);
            mSeekBar.addView(mAdPointBar);

            mThumb = new ImageView(mContext);
            mThumb.setId(ID_THUMB);
            mThumb.setImageResource(getResources().getIdentifier("seek_thumb", "drawable", mContext.getPackageName()));

            mThumb.setBackgroundColor(0x00000000);
            mThumb.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mThumb.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DebugLog.d(TAG, "mThumb.setOnTouchListener onTouch");

                    switch ( event.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                            setDragStart(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            setDragStart(false);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            setDragStart(false);
                            break;
                    }
                    return false;
                }
            });
            mSeekBarArea.addView(mThumb);
            setSeekBarBalloon();

            setProgress(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScreenSize(int width, int height) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " width[" + width + "] height[" + height + "]");

        try{
            mScreenWidth = width;
            mScreenHeight = height;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProgress(float progress, float bufferSize) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " progress[" + progress + "] bufferSize[" + bufferSize + "]");

        try{
            resize(mSeekBarAreaWidth, mSeekBarAreaHeight, progress, bufferSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeekBarAreaSize(int width, int height) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " width[" + width + "] height[" + height + "]");

        try{
            resize(width, height, mProgress, mBufferSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resize(int width, int height, float progress, float bufferSize) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " width[" + width + "] height[" + height + "] progress[" + progress + "] bufferSize[" + bufferSize + "]");

        try{
            mSeekBarAreaWidth = width;
            mSeekBarAreaHeight = height;
            mProgress = progress;
            mBufferSize = bufferSize;

            int thumbWidth = (int)(mSeekBarAreaHeight * 0.8);
            int thumbHeight = (int)(mSeekBarAreaHeight * 0.8);
            int seekBarWidth = (int)(mSeekBarAreaWidth * 0.85);
            int seekBarHeight = (int)(mSeekBarAreaHeight * 0.08);
            int thumbnailWidth = (int)(mSeekBarAreaHeight * 16/3);
            int thumbnailHeight = (int)(mSeekBarAreaHeight * 9/3);
            int timeLavelHeight = (int)(mSeekBarAreaHeight * 0.6);


            mSeekBarArea.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams(mSeekBarAreaWidth, mSeekBarAreaHeight);
                    layoutParams.addRule(ALIGN_PARENT_BOTTOM);
                    mSeekBarArea.setLayoutParams(layoutParams);
                    mSeekBarArea.setBackgroundColor(0x00000000);
                }
            });


            mSeekBar.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams(seekBarWidth, seekBarHeight);
                    layoutParams.addRule(CENTER_IN_PARENT);
                    mSeekBar.setLayoutParams(layoutParams);
                }
            });

            mSeekBarBackGround.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(CENTER_IN_PARENT);
                    mSeekBarBackGround.setLayoutParams(layoutParams);
                }
            });

            mThumb.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams(thumbWidth, thumbHeight);
                    layoutParams.addRule(CENTER_VERTICAL);
                    mThumb.setLayoutParams(layoutParams);
                }
            });

            if (mSeekBarBalloonArea != null) {

                if (mSeekBarThumbnailView != null) {

                    mSeekBarThumbnailView.post(new Runnable() {
                        @Override public void run() {
                            LayoutParams layoutParams = new LayoutParams(thumbnailWidth, thumbnailHeight);
                            mSeekBarThumbnailView.setLayoutParams(layoutParams);
                        }
                    });

                    mSeekBarBalloonArea.post(new Runnable() {
                        @Override public void run() {
                            LayoutParams layoutParams = new LayoutParams(thumbnailWidth, thumbnailHeight + timeLavelHeight);
                            mSeekBarBalloonArea.setLayoutParams(layoutParams);
                        }
                    });
                } else {
                    mSeekBarBalloonArea.post(new Runnable() {
                        @Override public void run() {
                            LayoutParams layoutParams = new LayoutParams(thumbnailWidth /3, timeLavelHeight);
                            mSeekBarBalloonArea.setLayoutParams(layoutParams);
                        }
                    });

                }

                mTimeLabel.post(new Runnable() {
                    @Override public void run() {
                        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, timeLavelHeight);
                        layoutParams.addRule(RelativeLayout.BELOW, ID_SEEK_BAR_THUMBNAIL_VIEW);
                        layoutParams.addRule(CENTER_HORIZONTAL);
                        mTimeLabel.setLayoutParams(layoutParams);
                        mTimeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeLavelHeight /2);
                    }
                });

                mSeekBarBalloonTail.post(new Runnable() {
                    @Override public void run() {
                        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, timeLavelHeight /2);
                        mSeekBarBalloonTail.setLayoutParams(layoutParams);
                    }
                });

            }

            mProgressBar.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams((int)Math.ceil(mSeekBar.getWidth() * (mProgress / 100)), LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(ALIGN_PARENT_LEFT);
                    mProgressBar.setLayoutParams(layoutParams);
                }
            });

            mBufferBar.post(new Runnable() {
                @Override public void run() {
                    LayoutParams layoutParams = new LayoutParams((int)Math.ceil(mSeekBar.getWidth() * (mBufferSize / 100)), LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(ALIGN_PARENT_LEFT);
                    mBufferBar.setLayoutParams(layoutParams);
                }
            });


            if (!mIsSeeking) {
                setSeekPosition(progress / 100);
            }

            if (mAdPointArray != null) {
                setAdPoint(mAdPointArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeekBarBalloon() {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + "");

        try{
            if (mSeekBarBalloonArea == null) {
                LayoutParams layoutParams;
                mSeekBarBalloonArea = new RelativeLayout(mContext);
                mSeekBarBalloonArea.setId(ID_SEEK_BAR_THUMBNAIL_AREA);
                mSeekBarBalloonArea.setBackgroundColor(0x00000000);
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                mSeekBarBalloonArea.setLayoutParams(layoutParams);
                addView(mSeekBarBalloonArea);

                mTimeLabel = new TextView(mContext);
                mTimeLabel.setId(ID_SEEK_BAR_THUMBNAIL_TIME_LABEL);
                mTimeLabel.setSingleLine(true);
                mTimeLabel.setBackgroundColor(0xAA000000);
                mTimeLabel.setTextColor(0xFF999999);
                mTimeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                mTimeLabel.setText("00:00:00");
                mTimeLabel.setGravity(Gravity.CENTER);
                layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
                layoutParams.addRule(RelativeLayout.BELOW, ID_SEEK_BAR_THUMBNAIL_VIEW);
                layoutParams.addRule(CENTER_HORIZONTAL);
                mSeekBarBalloonArea.addView(mTimeLabel, layoutParams);

                mSeekBarBalloonTail = new ImageView(mContext);
                mSeekBarBalloonTail.setId(ID_SEEK_BAR_THUMBNAIL_TAIL);
                mSeekBarBalloonTail.setImageResource(getResources().getIdentifier("balloon_tail", "drawable", mContext.getPackageName()));
                mSeekBarBalloonTail.setBackgroundColor(0x00000000);
                mSeekBarBalloonTail.setAlpha(0.7f);
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, ID_SEEK_BAR_THUMBNAIL_AREA);
                layoutParams.addRule(CENTER_HORIZONTAL);
                addView(mSeekBarBalloonTail, layoutParams);

                setThumbnailVisible(mIsSeeking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Float[] mAdPointArray;
    public void setAdPoint(Float... adPointArray) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + "");

        try{
            mAdPointArray = adPointArray;
            mAdPointBar.removeAllViews();

            int seekBarWidth = (int)(mSeekBarAreaWidth * 0.85);
            int seekBarHeight = (int)(mSeekBarAreaHeight * 0.08);

            int pointSize = seekBarHeight;
            LayoutParams layoutParams = new LayoutParams(pointSize, pointSize);
            List<ImageView> pointImageList = new ArrayList<ImageView>();
            for (float point : mAdPointArray) {
                pointImageList.add(new ImageView(mContext));
                ImageView pointImage = pointImageList.get(pointImageList.size() -1);
                int adPointId = getResources().getIdentifier("ad_point", "drawable", mContext.getPackageName());
                pointImage.setImageResource(adPointId);
                pointImage.setX(seekBarWidth * point - pointSize /2);
                pointImage.setLayoutParams(layoutParams);
                mAdPointBar.addView(pointImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAdPointVisible(boolean visible) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " visible[" + visible + "]");

        try{
            if (visible) {
                mAdPointBar.setVisibility(VISIBLE);
                int seekThumbId = getResources().getIdentifier("seek_thumb_ring", "drawable", mContext.getPackageName());
                mThumb.setImageResource(seekThumbId);
            } else {
                mAdPointBar.setVisibility(INVISIBLE);
                int seekThumbId = getResources().getIdentifier("seek_thumb", "drawable", mContext.getPackageName());
                mThumb.setImageResource(seekThumbId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeekable(boolean isSeekable) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " isSeekable[" + isSeekable + "]");

        mIsSeekable = isSeekable;
        if (mIsSeekable) {
            mThumb.setVisibility(VISIBLE);
        } else {
            mThumb.setVisibility(INVISIBLE);
        }
    }

    private void setDragStart(boolean isDrag) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " isDrag[" + isDrag + "]");

        try{
            if (mIsSeekable) {
                if (isDrag) {
                    mIsSeeking = true;
                    if (mSeekBarCallbacks != null) {
                        mSeekBarCallbacks.onSeekStart();
                    }
                } else {
                    if (mIsSeeking && !isDrag) {
                        mIsSeeking = false;
                        float position = (mThumb.getX() + mThumb.getWidth() /2 - mSeekBar.getX()) / mSeekBar.getWidth();
                        if (mSeekBarCallbacks != null) {
                            mSeekBarCallbacks.onSeekEnd(position);
                        }
                    }
                }
                setThumbnailVisible(mIsSeeking);
            } else {
                mIsSeeking = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeekStart(boolean isSeek) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " isSeek[" + isSeek + "]");

        try{
            mIsSeeking = false;
            if (mIsSeekable) {
                mIsSeeking = isSeek;
            } else {
                mIsSeeking = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeekPosition(float seekPosition) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " seekPosition[" + seekPosition + "]");

        try{

            int tmpthumbsetx = (int)mSeekBar.getX() + (int)Math.ceil(mSeekBar.getWidth() * seekPosition) - mThumb.getWidth() /2;
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " thumbsetx[" + tmpthumbsetx + "]");
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " mThumb.getVisibility[" + mThumb.getVisibility() + "]");


            mThumb.setX(tmpthumbsetx);
            if (mSeekBarBalloonArea != null) {
                mSeekBarBalloonTail.post(new Runnable() {
                    @Override public void run() {
                        float thumbnailX = mThumb.getX() + mThumb.getWidth() / 2 - mSeekBarBalloonArea.getWidth() / 2;
                        float thumbnailY = mSeekBarArea.getY() - mSeekBarBalloonArea.getHeight() - mSeekBarBalloonTail.getHeight();
                        if (thumbnailX < 0) {
                            thumbnailX = 0;
                        }
                        if (thumbnailX + mSeekBarBalloonArea.getWidth() > mScreenWidth) {
                            thumbnailX = mScreenWidth - mSeekBarBalloonArea.getWidth();
                        }
                        mSeekBarBalloonArea.setX(thumbnailX);
                        mSeekBarBalloonArea.setY(thumbnailY);
                        mSeekBarBalloonTail.setX(mThumb.getX() + mThumb.getWidth() / 2 - mSeekBarBalloonTail.getWidth() / 2);
                        mSeekBarBalloonTail.setY(thumbnailY + mSeekBarBalloonArea.getHeight());
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadThumbnailImage(String mediaId, int duration, int interval) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " mediaId[" + mediaId + "] duration[" + duration + "] interval[" + interval + "]");

        try{
            if (mSeekBarThumbnailView == null) {
                LayoutParams layoutParams;
                mSeekBarThumbnailView = new SeekBarThumbnailView(mContext);
                mSeekBarThumbnailView.setId(ID_SEEK_BAR_THUMBNAIL_VIEW);
                layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                mSeekBarThumbnailView.setLayoutParams(layoutParams);
                mSeekBarBalloonArea.addView(mSeekBarThumbnailView);
                setSeekBarAreaSize(mSeekBarAreaWidth, mSeekBarAreaHeight);
            }
            mSeekBarThumbnailView.loadThumbnailImage(mediaId, duration, interval);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setThumbnailImage(int currentTime) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " currentTime[" + currentTime + "]");

        try{
            if (mSeekBarThumbnailView != null) {
                mSeekBarThumbnailView.setThumbnailImage(currentTime);
            }
            if (mTimeLabel != null) {
                mTimeLabel.setText(moldTimeFormat(currentTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setThumbnailVisible(boolean visible) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " visible[" + visible + "]");

        try{
            if (mSeekBarBalloonArea != null) {
                if (mSeekBarThumbnailView != null) {
                    if (mIsSeekable) {
                        if (visible) {
                            mSeekBarBalloonArea.setVisibility(VISIBLE);
                            mSeekBarBalloonTail.setVisibility(VISIBLE);
                        } else {
                            mSeekBarBalloonArea.setVisibility(INVISIBLE);
                            mSeekBarBalloonTail.setVisibility(INVISIBLE);
                        }
                    } else {
                        mSeekBarBalloonArea.setVisibility(INVISIBLE);
                        mSeekBarBalloonTail.setVisibility(INVISIBLE);
                    }
                } else {
                    mSeekBarBalloonArea.setVisibility(INVISIBLE);
                    mSeekBarBalloonTail.setVisibility(INVISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getThumbnailInterval() {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + "");

        try{
            if (mSeekBarThumbnailView != null) {
                return mSeekBarThumbnailView.getInterval();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String moldTimeFormat(int sec) {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " sec[" + sec + "]");

        try{
            String HH = String.format("%02d", (sec / 3600));
            String mm = String.format("%02d", (sec % 3600 / 60));
            String ss = String.format("%02d", (sec % 60));
            return new StringBuilder().append(HH).append(":").append(mm).append(":").append(ss).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isSeeking() {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " sec[" + mIsSeeking + "]");

        return mIsSeeking;
    }

    public void cancelLoadThumbnailImage() {
        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + "");

        try{
            if (mSeekBarThumbnailView != null) {
                mSeekBarThumbnailView.cancelLoadThumbnailImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + "");

        try{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
