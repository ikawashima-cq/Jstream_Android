package jp.co.stream.jstplayersdk.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SubtitleView;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.jstplayersdk.util.DebugLog;
import jp.co.stream.jstplayersdk.util.MyUtils;

public class VideoControllerView extends RelativeLayout {

    private static final String TAG = "VideoControllerView";

    private Activity mActivity;
    private Activity mContext;
    private VideoControllerCallbacks mVideoControllerCallbacks;

    private static final int ID_STATUS_BAR_AREA = 1;
    private static final int ID_TOP_PANEL = 10;
    private static final int ID_CLOSE_BUTTON = 11;
    private static final int ID_VOLUME_BAR = 12;
    private static final int ID_VOLUME_ON_BUTTON = 13;
    private static final int ID_VOLUME_OFF_BUTTON = 14;
    private static final int ID_ORIENTATION_BUTTON = 15;
    private static final int ID_SETTING_BUTTON = 16;
    private static final int ID_MIDDLE_PANEL = 20;
    private static final int ID_SEEK_BAR_AREA_PANEL = 21;
    private static final int ID_BACK_BUTTON_MAIN = 23;
    private static final int ID_BACK_BUTTON_SUB = 24;
    private static final int ID_SKIP_AD_BUTTON = 25;
    private static final int ID_BOTTOM_PANEL = 30;
    private static final int ID_TIME_LABEL = 33;
    private static final int ID_SEEK_BAR = 34;
    private static final int ID_DVR_LIVE_BUTTON = 35;
    private static final int ID_NOT_HIDE_MIDDLE_PANEL = 40;
    private static final int ID_CLICK_THROUGH_BUTTON = 41;
    private static final int ID_CENTER_CONTROL_PANEL = 50;
    private static final int ID_PLAY_BUTTON = 51;
    private static final int ID_PAUSE_BUTTON = 52;
    private static final int ID_FAST_FORWORD_BUTTON = 53;
    private static final int ID_REWIND_BUTTON = 54;
    private static final int ID_NAVIGATION_BUTTON_AREA_PANEL = 60;
    private static final int ID_NAVIGATION_PREVIOUS_BUTTON = 61;
    private static final int ID_NAVIGATION_NEXT_BUTTON = 62;
    private static final int ID_NAVIGATION_PREVIOUS_LABEL = 63;
    private static final int ID_NAVIGATION_NEXT_LABEL = 64;

    private RelativeLayout mStatusBarAreaPanel;
    private RelativeLayout mTopPanel;
    private RelativeLayout mMiddlePanel;
    private RelativeLayout mNotHideVideoSizePanel;
    private RelativeLayout mCenterControlPanel;
    private RelativeLayout mBottomPanel;
    private RelativeLayout mSeekBarAreaPanel;
    private RelativeLayout mNavigationButtonAreaPanel;
    private ImageButton mPlayPauseButton;
    private ImageButton mVolumeOnButton;
    private ImageButton mVolumeOffButton;
    private ImageButton mCloseButton;
    private ImageButton mOrientationButton;
    private ImageButton mNavigationNextButton;
    private ImageButton mNavigationPreviousButton;
    private ImageButton mSettingButton;
    private Button mClickThrough;
    private Button mSkipAdButton;
    private ImageButton mFastForwordButton;
    private ImageButton mRewindButton;
    private Button mBackButtonMain;
    private Button mBackButtonSub;
    private TextView mTimeLabel;
    private ImageButton mDVRLiveButton;
    private SeekBarView mSeekBarView;
    private SeekBarView mVolumeBarView;
    private boolean mIsVisible;
    private int mCurrentVolume;
    private String mSponsorUrl;
    private String mBackUrlMain;
    private String mBackUrlSub;
    private boolean mIsLive;
    private int mBaseSize;
    private Timer mConsoleHideTimer;
    private TextView mNavigationPreviousLabel;
    private TextView mNavigationNextLabel;
    private boolean mIsNavigationNextSelected;
    private boolean mIsNavigationPreviousSelected;
    private SubtitleView mSubtitleView;

    public void setSubtitleView(SubtitleView subtitleView) {
        mSubtitleView = subtitleView;
    }
    public SubtitleView getSubtitleView() {
        return mSubtitleView;
    }

    private ExoPlayer mExoPlayer;
    public void setExoPlayer(ExoPlayer exoPlayer) {
        mExoPlayer = exoPlayer;
    }
    public ExoPlayer getExoPlayer() {
        return mExoPlayer;
    }
    private final Runnable updateProgressAction;
    private boolean isAttachedToWindow;
    private boolean showMultiWindowTimeBar;
    private boolean multiWindowTimeBar;
    /** The maximum interval between time bar position updates. */
    private static final int MAX_UPDATE_INTERVAL_MS = 1000;


    public interface VideoControllerCallbacks {
        public void onPlay();
        public void onPause();
        public void onPlayAndPause();
        public void onSeekStart();
        public void onSeekEnd(double position);
        public void onChangePosition(double position);
        public void onClickSponsorButton(String url);
        public void onClickBackButtonMain(String url);
        public void onClickBackButtonSub(String url);
        public void onClickSkipAdButton();
        public void onClickFastForwordButton();
        public void onClickRewindButton();
        public void onClickOrientationButton();
        public void onClickSettingButton();
        public void onClickNavigationNextButton();
        public void onClickNavigationPreviousButton();
        public void onClickDVRLiveButton();
        public void onClose();
    }

    public void setCallbacks(VideoControllerCallbacks callbacks){
        mVideoControllerCallbacks = callbacks;
    }


    public VideoControllerView(Activity activity) {
        super(activity);
        mContext = activity;
        mActivity = activity;
        mBaseSize = MyUtils.convertDpToPx(mActivity, 100);
        updateProgressAction = this::updateProgress;
    }

    public void initController(int screenWidth, int screenHeight) {

        int videoWidth = screenWidth;
        int videoHeight = screenHeight;
        float ratio = 16.0f / 9.0f;
        if (videoHeight * ratio > videoWidth) {
            videoHeight = (int)(videoWidth / ratio);
        } else {
            videoWidth = (int)(videoHeight * ratio);
        }

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_DOWN"
                        );

                        setVisible(!mIsVisible);
                        setConsoleHideTimer(mIsVisible);
                        break;
                    case MotionEvent.ACTION_UP:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_UP"
                        );
                        setVisible(!mIsVisible);
                        setConsoleHideTimer(mIsVisible);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_MOVE"
                        );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_CANCEL"
                        );
                        break;
                }
                return false;
            }
        });

        LayoutParams layoutParams;

        int btnFontSize = mBaseSize /8;

        mStatusBarAreaPanel = new RelativeLayout(mActivity);
        mStatusBarAreaPanel.setId(ID_STATUS_BAR_AREA);
        mStatusBarAreaPanel.setBackgroundColor(0xAA000000);
        addView(mStatusBarAreaPanel);

        mSeekBarAreaPanel = new RelativeLayout(mContext);
        mSeekBarAreaPanel = new RelativeLayout(mContext);
        mSeekBarAreaPanel.setId(ID_SEEK_BAR_AREA_PANEL);
        mSeekBarAreaPanel.setBackgroundColor(0xAA000000);
        mSeekBarAreaPanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_DOWN"
                        );
                        unsetConsoleHideTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_UP"
                        );
                        //setVisible(!mIsVisible);
                        setConsoleHideTimer();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_MOVE"
                        );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_CANCEL"
                        );
                        break;
                }
                return false;
            }
        });
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBaseSize /3);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        addView(mSeekBarAreaPanel, layoutParams);

        mBottomPanel = new RelativeLayout(mActivity);
        mBottomPanel.setId(ID_BOTTOM_PANEL);
        mBottomPanel.setBackgroundColor(0x00000000);
        //layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBaseSize /3);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBottomPanel, layoutParams);

        mDVRLiveButton = new ImageButton(mActivity);
        mDVRLiveButton.setId(ID_DVR_LIVE_BUTTON);
        mDVRLiveButton.setPadding(10, 0, 40, 0);
        mDVRLiveButton.setImageResource(getResources().getIdentifier("player_dvr_live_on", "drawable", mActivity.getPackageName()));
        mDVRLiveButton.setBackgroundColor(0x00000000);
        mDVRLiveButton.setScaleType(ScaleType.FIT_CENTER);
        mDVRLiveButton.setAdjustViewBounds(true);
        mDVRLiveButton.setVisibility(INVISIBLE);
        mDVRLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLive) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickDVRLiveButton();
                    }
                }
            }
        });
        //layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mBaseSize /3);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //mBottomPanel.addView(mDVRLiveButton, layoutParams);
        mBottomPanel.addView(mDVRLiveButton);
        setDVRLiveButton(false);

        mTimeLabel = new TextView(mActivity);
        mTimeLabel.setId(ID_TIME_LABEL);
        mTimeLabel.setSingleLine(true);
        mTimeLabel.setBackgroundColor(0x00000000);
        mTimeLabel.setTextColor(0xFF999999);
        mTimeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBaseSize /6);
        mTimeLabel.setText("00:00:00/00:00:00 ");
        mTimeLabel.setGravity(Gravity.CENTER_VERTICAL);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mBaseSize /3);
        //layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_DVR_LIVE_BUTTON);
        mBottomPanel.addView(mTimeLabel, layoutParams);

        mSeekBarView = new SeekBarView(mActivity);
        mSeekBarView.setId(ID_SEEK_BAR);
        mSeekBarView.setPadding(0, 0, 0, 0);
        mSeekBarView.setBackgroundColor(0x00000000);
        mSeekBarView.setScreenSize(screenWidth, screenHeight);
        mSeekBarView.setCallbacks(new SeekBarView.SeekBarCallbacks() {
            @Override
            public void onChangePosition(float position) {
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onChangePosition(position);
                }
            }
            @Override
            public void onSeekStart() {
                unsetConsoleHideTimer();
                mCenterControlPanel.setVisibility(INVISIBLE);
                mMiddlePanel.setVisibility(INVISIBLE);
                mNavigationButtonAreaPanel.setVisibility(INVISIBLE);
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onSeekStart();
                }
            }
            @Override
            public void onSeekEnd(float position) {
                setConsoleHideTimer();
                mCenterControlPanel.setVisibility(VISIBLE);
                mMiddlePanel.setVisibility(VISIBLE);
                if (mIsNavigateButtonVisibility) {
                    mNavigationButtonAreaPanel.setVisibility(VISIBLE);
                } else {
                    mNavigationButtonAreaPanel.setVisibility(INVISIBLE);
                }
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onSeekEnd(position);
                }
            }
        });
        //layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBaseSize /4);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_TIME_LABEL);
        mBottomPanel.addView(mSeekBarView, layoutParams);
        mSeekBarView.setSeekBarAreaSize(mSeekBarView.getWidth(), mBaseSize /3);

        mBottomPanel.setClipChildren(false);
        mBottomPanel.setClipToPadding(false);
        mSeekBarView.setClipChildren(false);
        mSeekBarView.setClipToPadding(false);

        mTopPanel = new RelativeLayout(mActivity);
        mTopPanel.setId(ID_TOP_PANEL);
        mTopPanel.setPadding(mBaseSize /20, 0, mBaseSize /20, 0);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int)(mBaseSize /3));
        layoutParams.addRule(RelativeLayout.BELOW, ID_STATUS_BAR_AREA);
        mTopPanel.setLayoutParams(layoutParams);
        mTopPanel.setBackgroundColor(0xFF999999);
        addView(mTopPanel);

        mCloseButton = new ImageButton(mActivity);
        mCloseButton.setId(ID_CLOSE_BUTTON);
        mCloseButton.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mCloseButton.setImageResource(getResources().getIdentifier("close_button", "drawable", mActivity.getPackageName()));
        mCloseButton.setBackgroundColor(0x00000000);
        mCloseButton.setScaleType(ScaleType.FIT_CENTER);
        mCloseButton.setAdjustViewBounds(true);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onClose();
                }
            }
        });
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mTopPanel.addView(mCloseButton, layoutParams);

        mSettingButton = new ImageButton(mActivity);
        mSettingButton.setId(ID_SETTING_BUTTON);
        mSettingButton.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mSettingButton.setImageResource(getResources().getIdentifier("setting_button", "drawable", mActivity.getPackageName()));
        mSettingButton.setBackgroundColor(0x00000000);
        mSettingButton.setScaleType(ScaleType.FIT_CENTER);
        mSettingButton.setAdjustViewBounds(true);
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onClickSettingButton();
                }
            }
        });
        //layoutParams = new LayoutParams(mBaseSize /3, mBaseSize /3);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mTopPanel.addView(mSettingButton, layoutParams);

        mOrientationButton = new ImageButton(mActivity);
        mOrientationButton.setId(ID_ORIENTATION_BUTTON);
        mOrientationButton.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mOrientationButton.setImageResource(getResources().getIdentifier("orientation_button", "drawable", mActivity.getPackageName()));
        mOrientationButton.setBackgroundColor(0x00000000);
        mOrientationButton.setScaleType(ScaleType.FIT_CENTER);
        mOrientationButton.setAdjustViewBounds(true);
        mOrientationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onClickOrientationButton();
                }
            }
        });
        //layoutParams = new LayoutParams(mBaseSize /3, mBaseSize /3);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_SETTING_BUTTON);
        mTopPanel.addView(mOrientationButton, layoutParams);

        mVolumeBarView = new SeekBarView(mActivity);
        mVolumeBarView.setId(ID_VOLUME_BAR);
        mVolumeBarView.setBackgroundColor(0x00000000);
        mVolumeBarView.setScreenSize(screenWidth, screenHeight);
        mVolumeBarView.setCallbacks(new SeekBarView.SeekBarCallbacks() {
            @Override
            public void onChangePosition(float position) {
                AudioManager manager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
                float setVolume = position * manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                setVolume((int)setVolume);
            }
            @Override
            public void onSeekStart() {
                unsetConsoleHideTimer();
            }
            @Override
            public void onSeekEnd(float position) {
                setConsoleHideTimer();
                AudioManager manager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
                float setVolume = position * manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                setVolume((int)setVolume);
            }
        });
        mVolumeBarView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_DOWN"
                        );
                        break;
                    case MotionEvent.ACTION_UP:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_UP"
                        );
                        break;
                    case MotionEvent.ACTION_MOVE:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_MOVE"
                        );
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName()
                                + " " + "ACTION_CANCEL"
                        );
                        break;
                }
                return true;
            }
        });


        layoutParams = new LayoutParams(mBaseSize * 3/2, mBaseSize /2);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTopPanel.addView(mVolumeBarView, layoutParams);

        mVolumeOnButton = new ImageButton(mActivity);
        mVolumeOnButton.setId(ID_VOLUME_ON_BUTTON);
        mVolumeOnButton.setPadding(0, 0, 0, 0);
        mVolumeOnButton.setImageResource(getResources().getIdentifier("speaker_on", "drawable", mActivity.getPackageName()));
        mVolumeOnButton.setBackgroundColor(0x00000000);
        mVolumeOnButton.setScaleType(ScaleType.FIT_CENTER);
        mVolumeOnButton.setAdjustViewBounds(true);
        mVolumeOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                setMute(true);
            }
        });
        layoutParams = new LayoutParams(mBaseSize /4, mBaseSize /4);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_VOLUME_BAR);
        mTopPanel.addView(mVolumeOnButton, layoutParams);

        mVolumeOffButton = new ImageButton(mActivity);
        mVolumeOffButton.setId(ID_VOLUME_OFF_BUTTON);
        mVolumeOffButton.setPadding(0, 0, 0, 0);
        mVolumeOffButton.setImageResource(getResources().getIdentifier("speaker_off", "drawable", mActivity.getPackageName()));
        mVolumeOffButton.setBackgroundColor(0x00000000);
        mVolumeOffButton.setScaleType(ScaleType.FIT_CENTER);
        mVolumeOffButton.setAdjustViewBounds(true);
        mVolumeOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                setMute(false);
            }
        });
        layoutParams = new LayoutParams(mBaseSize /4, mBaseSize /4);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_VOLUME_BAR);
        mTopPanel.addView(mVolumeOffButton, layoutParams);

        mMiddlePanel = new RelativeLayout(mActivity);
        mMiddlePanel.setId(ID_MIDDLE_PANEL);
        mMiddlePanel.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mMiddlePanel.setBackgroundColor(0x00000000);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, ID_TOP_PANEL);
        layoutParams.addRule(RelativeLayout.ABOVE, ID_SEEK_BAR_AREA_PANEL);
        addView(mMiddlePanel, layoutParams);

        mNavigationButtonAreaPanel = new RelativeLayout(mActivity);
        mNavigationButtonAreaPanel.setId(ID_NAVIGATION_BUTTON_AREA_PANEL);
        mNavigationButtonAreaPanel.setBackgroundColor(0x00000000);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mNavigationButtonAreaPanel, layoutParams);

        mNavigationNextButton = new ImageButton(mActivity);
        mNavigationNextButton.setId(ID_NAVIGATION_NEXT_BUTTON);
        mNavigationNextButton.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button", "drawable", mActivity.getPackageName()));
        mNavigationNextButton.setBackgroundColor(0x00000000);
        mNavigationNextButton.setScaleType(ScaleType.FIT_CENTER);
        mNavigationNextButton.setAdjustViewBounds(true);
        mNavigationNextButton.setVisibility(INVISIBLE);
        mNavigationNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                if (mIsNavigationNextSelected) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickNavigationNextButton();
                    }
                } else {
                    mIsNavigationNextSelected = true;
                    mIsNavigationPreviousSelected = false;
                    mNavigationNextLabel.setTextColor(0xFFFF0000);
                    mNavigationPreviousLabel.setTextColor(0xFFFFFFFF);
                    mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button_on", "drawable", mActivity.getPackageName()));
                    mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button", "drawable", mActivity.getPackageName()));
                }
            }
        });
        layoutParams = new LayoutParams((int)(mBaseSize /3), (int)(mBaseSize /3));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mNavigationButtonAreaPanel.addView(mNavigationNextButton, layoutParams);

        mNavigationNextLabel = new TextView(mActivity);
        mNavigationNextLabel.setId(ID_NAVIGATION_NEXT_LABEL);
        mNavigationNextLabel.setSingleLine(true);
        mNavigationNextLabel.setBackgroundColor(0x00000000);
        mNavigationNextLabel.setTextColor(0xFFFFFFFF);
        mNavigationNextLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBaseSize /10);
        mNavigationNextLabel.setText("次の動画");
        mNavigationNextLabel.setGravity(Gravity.CENTER_VERTICAL);
        mNavigationNextLabel.setVisibility(INVISIBLE);
        mNavigationNextLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                if (mIsNavigationNextSelected) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickNavigationNextButton();
                    }
                } else {
                    mIsNavigationNextSelected = true;
                    mIsNavigationPreviousSelected = false;
                    mNavigationNextLabel.setTextColor(0xFFFF0000);
                    mNavigationPreviousLabel.setTextColor(0xFFFFFFFF);
                    mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button_on", "drawable", mActivity.getPackageName()));
                    mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button", "drawable", mActivity.getPackageName()));
                }
            }
        });
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.LEFT_OF, ID_NAVIGATION_NEXT_BUTTON);
        mNavigationButtonAreaPanel.addView(mNavigationNextLabel, layoutParams);
        mIsNavigationNextSelected = false;

        mNavigationPreviousButton = new ImageButton(mActivity);
        mNavigationPreviousButton.setId(ID_NAVIGATION_PREVIOUS_BUTTON);
        mNavigationPreviousButton.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button", "drawable", mActivity.getPackageName()));
        mNavigationPreviousButton.setBackgroundColor(0x00000000);
        mNavigationPreviousButton.setScaleType(ScaleType.FIT_CENTER);
        mNavigationPreviousButton.setAdjustViewBounds(true);
        mNavigationPreviousButton.setVisibility(INVISIBLE);
        mNavigationPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                if (mIsNavigationPreviousSelected) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickNavigationPreviousButton();
                    }
                } else {
                    mIsNavigationPreviousSelected = true;
                    mIsNavigationNextSelected = false;
                    mNavigationPreviousLabel.setTextColor(0xFFFF0000);
                    mNavigationNextLabel.setTextColor(0xFFFFFFFF);
                    mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button", "drawable", mActivity.getPackageName()));
                    mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button_on", "drawable", mActivity.getPackageName()));
                }
            }
        });
        layoutParams = new LayoutParams((int)(mBaseSize /3), (int)(mBaseSize /3));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mNavigationButtonAreaPanel.addView(mNavigationPreviousButton, layoutParams);

        mNavigationPreviousLabel = new TextView(mActivity);
        mNavigationPreviousLabel.setId(ID_NAVIGATION_PREVIOUS_LABEL);
        mNavigationPreviousLabel.setSingleLine(true);
        mNavigationPreviousLabel.setBackgroundColor(0x00000000);
        mNavigationPreviousLabel.setTextColor(0xFFFFFFFF);
        mNavigationPreviousLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBaseSize /10);
        mNavigationPreviousLabel.setText("前の動画");
        mNavigationPreviousLabel.setGravity(Gravity.CENTER_VERTICAL);
        mNavigationPreviousLabel.setVisibility(INVISIBLE);
        mNavigationPreviousLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsoleHideTimer();
                if (mIsNavigationPreviousSelected) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickNavigationPreviousButton();
                    }
                } else {
                    mIsNavigationPreviousSelected = true;
                    mIsNavigationNextSelected = false;
                    mNavigationPreviousLabel.setTextColor(0xFFFF0000);
                    mNavigationNextLabel.setTextColor(0xFFFFFFFF);
                    mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button", "drawable", mActivity.getPackageName()));
                    mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button_on", "drawable", mActivity.getPackageName()));
                }
            }
        });
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, ID_NAVIGATION_PREVIOUS_BUTTON);
        mNavigationButtonAreaPanel.addView(mNavigationPreviousLabel, layoutParams);
        mIsNavigationPreviousSelected = false;

        mCenterControlPanel = new RelativeLayout(mActivity);
        mCenterControlPanel.setId(ID_CENTER_CONTROL_PANEL);
        mCenterControlPanel.setPadding(0, 0, 0, 0);
        mCenterControlPanel.setBackgroundColor(0x00000000);
        layoutParams = new LayoutParams((int)(mBaseSize * 3.36), LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mCenterControlPanel, layoutParams);

        mPlayPauseButton = new ImageButton(mActivity);
        mPlayPauseButton.setId(ID_PLAY_BUTTON);
        mPlayPauseButton.setImageResource(getResources().getIdentifier("play_button", "drawable", mActivity.getPackageName()));
        mPlayPauseButton.setBackgroundColor(0x00000000);
        mPlayPauseButton.setScaleType(ScaleType.FIT_CENTER);
        mPlayPauseButton.setAdjustViewBounds(true);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    setConsoleHideTimer();
                    mVideoControllerCallbacks.onPlayAndPause();
                }
            }
        });
        layoutParams = new LayoutParams((int)(mBaseSize * 1.04), (int)(mBaseSize * 1.04));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mCenterControlPanel.addView(mPlayPauseButton, layoutParams);

        mFastForwordButton = new ImageButton(mActivity);
        mFastForwordButton.setId(ID_FAST_FORWORD_BUTTON);
        mFastForwordButton.setImageResource(getResources().getIdentifier("fastforword10_button", "drawable", mActivity.getPackageName()));
        mFastForwordButton.setBackgroundColor(0x00000000);
        mFastForwordButton.setScaleType(ScaleType.FIT_CENTER);
        mFastForwordButton.setAdjustViewBounds(true);
        mFastForwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    setConsoleHideTimer();
                    mVideoControllerCallbacks.onClickFastForwordButton();
                }
            }
        });
        layoutParams = new LayoutParams((int)(mBaseSize * 0.9), (int)(mBaseSize * 0.9));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mCenterControlPanel.addView(mFastForwordButton, layoutParams);

        mRewindButton = new ImageButton(mActivity);
        mRewindButton.setId(ID_REWIND_BUTTON);
        mRewindButton.setImageResource(getResources().getIdentifier("rewind10_button", "drawable", mActivity.getPackageName()));
        mRewindButton.setBackgroundColor(0x00000000);
        mRewindButton.setScaleType(ScaleType.FIT_CENTER);
        mRewindButton.setAdjustViewBounds(true);
        mRewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    setConsoleHideTimer();
                    mVideoControllerCallbacks.onClickRewindButton();
                }
            }
        });
        layoutParams = new LayoutParams((int)(mBaseSize * 0.9), (int)(mBaseSize * 0.9));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mCenterControlPanel.addView(mRewindButton, layoutParams);

        mSubtitleView = new SubtitleView(mActivity);
        mSubtitleView.setUserDefaultStyle();
        mSubtitleView.setUserDefaultTextSize();
        mSubtitleView.setFractionalTextSize((float)0.3);
        layoutParams = new RelativeLayout.LayoutParams((int)(mBaseSize * 5.04), (int)(mBaseSize * 3.04));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mSubtitleView.setPadding(0, (int)(mBaseSize * 2 ), 0, 0);
        mCenterControlPanel.addView(mSubtitleView, layoutParams);


        mSkipAdButton = new Button(mActivity);
        mSkipAdButton.setId(ID_SKIP_AD_BUTTON);
        mSkipAdButton.setVisibility(INVISIBLE);
        mSkipAdButton.setAllCaps(false);
        mSkipAdButton.setPadding(0, 0, 0, 0);
        mSkipAdButton.setBackgroundColor(0xCC888888);
        mSkipAdButton.setTextColor(0xFFFFFFFF);
        mSkipAdButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);
        mSkipAdButton.setText("CMをスキップ");
        mSkipAdButton.setGravity(Gravity.CENTER);
        mSkipAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoControllerCallbacks != null) {
                    mVideoControllerCallbacks.onClickSkipAdButton();
                }
            }
        });
        layoutParams = new LayoutParams(screenWidth /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mMiddlePanel.addView(mSkipAdButton, layoutParams);

        mBackButtonMain = new Button(mActivity);
        mBackButtonMain.setId(ID_BACK_BUTTON_MAIN);
        mBackButtonMain.setVisibility(INVISIBLE);
        mBackButtonMain.setAllCaps(false);
        mBackButtonMain.setPadding(0, 0, 0, 0);
        mBackButtonMain.setBackgroundColor(0xCCFF0000);
        mBackButtonMain.setTextColor(0xFFFFFFFF);
        mBackButtonMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);
        mBackButtonMain.setText("");
        mBackButtonMain.setGravity(Gravity.CENTER);
        mBackButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackUrlMain != null) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickBackButtonMain(mBackUrlMain);
                    }
                }
            }
        });
        layoutParams = new LayoutParams(screenWidth /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mMiddlePanel.addView(mBackButtonMain, layoutParams);

        mBackButtonSub = new Button(mActivity);
        mBackButtonSub.setId(ID_BACK_BUTTON_SUB);
        mBackButtonSub.setVisibility(INVISIBLE);
        mBackButtonSub.setAllCaps(false);
        mBackButtonSub.setPadding(0, 0, 0, 0);
        mBackButtonSub.setBackgroundColor(0xCC1AAAFF);
        mBackButtonSub.setTextColor(0xFFFFFFFF);
        mBackButtonSub.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);
        mBackButtonSub.setText("");
        mBackButtonSub.setGravity(Gravity.CENTER);
        mBackButtonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackUrlSub != null) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickBackButtonSub(mBackUrlSub);
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBackUrlSub));
                        mActivity.startActivity(intent);
                    } catch(Exception e) {}
                }
            }
        });
        layoutParams = new LayoutParams(screenWidth /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mMiddlePanel.addView(mBackButtonSub, layoutParams);

        mNotHideVideoSizePanel = new RelativeLayout(mActivity);
        mNotHideVideoSizePanel.setId(ID_NOT_HIDE_MIDDLE_PANEL);
        mNotHideVideoSizePanel.setPadding(mBaseSize /20, mBaseSize /20, mBaseSize /20, mBaseSize /20);
        mNotHideVideoSizePanel.setBackgroundColor(0x00000000);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, ID_TOP_PANEL);
        layoutParams.addRule(RelativeLayout.ABOVE, ID_SEEK_BAR_AREA_PANEL);
        addView(mNotHideVideoSizePanel, layoutParams);

        mClickThrough = new Button(mActivity);
        mClickThrough.setId(ID_CLICK_THROUGH_BUTTON);
        mClickThrough.setVisibility(INVISIBLE);
        mClickThrough.setAllCaps(false);
        mClickThrough.setPadding(0, 0, 0, 0);
        mClickThrough.setBackgroundColor(0xAAFF0066);
        mClickThrough.setTextColor(0xFFFFFFFF);
        mClickThrough.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);
        mClickThrough.setText("m o r e    i n f o");
        mClickThrough.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        mClickThrough.setGravity(Gravity.CENTER);
        mClickThrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSponsorUrl != null) {
                    if (mVideoControllerCallbacks != null) {
                        mVideoControllerCallbacks.onClickSponsorButton(mSponsorUrl);
                    }
                }
            }
        });
        layoutParams = new LayoutParams(screenWidth /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mNotHideVideoSizePanel.addView(mClickThrough, layoutParams);

        setPlaying(false);
        setVolumeBar();
    }

    public void setVideoSize(int width, int height) {

        Point screenSize = MyUtils.getScreenSize(mActivity);
        int areaLeft = screenSize.x /2 - width /2;
        int areaTop = screenSize.y /2 - height /2;
        int areaWidht = width;
        int areaHeigth= height;

        if (mTopPanel.getY() + mTopPanel.getHeight() > areaTop) {
            areaTop = (int)mTopPanel.getY() + mTopPanel.getHeight();
        }
        if (mBottomPanel.getY() < areaTop + areaHeigth) {
            areaHeigth = (int)mBottomPanel.getY() - areaTop;
        }
        LayoutParams layoutParams;
        layoutParams = new LayoutParams(areaWidht, areaHeigth);
        layoutParams.setMargins(areaLeft, areaTop, 0, 0);
        mNotHideVideoSizePanel.setLayoutParams(layoutParams);

        int btnFontSize = mBaseSize /8;

        layoutParams = new LayoutParams(width /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mSkipAdButton.setLayoutParams(layoutParams);
        mSkipAdButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);

        layoutParams = new LayoutParams(width /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mBackButtonMain.setLayoutParams(layoutParams);
        mBackButtonMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);

        layoutParams = new LayoutParams(width /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mBackButtonSub.setLayoutParams(layoutParams);
        mBackButtonSub.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);

        layoutParams = new LayoutParams(width /3, mBaseSize /3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mClickThrough.setLayoutParams(layoutParams);
        mClickThrough.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnFontSize);

        mSeekBarView.setSeekBarAreaSize(mSeekBarView.getWidth(), mBaseSize /3);
    }

    public void loadThumbnailImage(String mediaId, int duration, int interval) {
        mSeekBarView.loadThumbnailImage(mediaId, duration / 1000, interval);
    }

    public void setPlaying(boolean playing) {
        if (playing) {
            mPlayPauseButton.setImageResource(getResources().getIdentifier("pause_button", "drawable", mActivity.getPackageName()));
        } else {
            mPlayPauseButton.setImageResource(getResources().getIdentifier("play_button", "drawable", mActivity.getPackageName()));
        }
    }


    public void setTimeLabel(int currentTime, int duration, int bufferTime) {
        setDVRLiveButton(false);
        if (!mIsLive) {
            mTimeLabel.setText(moldTimeFormat(currentTime / 1000) + "/" + moldTimeFormat(duration / 1000) + " ");
        } else {
            mTimeLabel.setText(moldTimeFormat(currentTime / 1000) + " ");
        }
        if (duration >= 0) {
            mSeekBarView.resize(mSeekBarView.getWidth(), mBaseSize /3, ((float)currentTime / (float)duration) * 100, ((float)bufferTime / (float)duration) * 100);
        } else {
            mSeekBarView.resize(mSeekBarView.getWidth(), mBaseSize /3, 0, 0);
        }
        setVolumeBar();
    }

    public void setAdLabel(int currentTime, int duration, int currentVASTId, int totalVASTId) {
        setDVRLiveButton(false);
        mTimeLabel.setText("CM:"+moldTimeFormatAd((duration - currentTime) /1000)+"("+currentVASTId+"/"+totalVASTId+") ");
        if (duration >= 0) {
            mSeekBarView.setProgress(((float)currentTime / (float)duration) * 100, 0);
        } else {
            mSeekBarView.setProgress(0, 0);
        }
        setVolumeBar();
    }


    public boolean mIsDVR;
    public void setDVRLabel(int currentTime, int duration, int bufferTime, boolean live) {
        mIsDVR = true;
        mIsLive = live;
        setDVRLiveButton(true);

        if (currentTime < 0) {
            currentTime = 0;
        }
        if (currentTime > duration) {
            currentTime = duration;
        }
        if (!mIsLive) {
            mTimeLabel.setText(moldTimeFormat(currentTime / 1000) + " ");
            mDVRLiveButton.setImageResource(getResources().getIdentifier("player_dvr_live_off", "drawable", mActivity.getPackageName()));
        } else {
            mTimeLabel.setText(" ");
            mDVRLiveButton.setImageResource(getResources().getIdentifier("player_dvr_live_on", "drawable", mActivity.getPackageName()));
        }
        if (duration >= 0) {
            if (!mIsLive) {
                mSeekBarView.resize(mSeekBarView.getWidth(), mBaseSize / 3, ((float) currentTime / (float) duration) * 100, ((float) bufferTime / (float) duration) * 100);
            } else {
                mSeekBarView.resize(mSeekBarView.getWidth(), mBaseSize / 3, 100, 0);
            }
        } else {
            mSeekBarView.resize(mSeekBarView.getWidth(), mBaseSize /3, 0, 0);
        }

        if (mIsLive) {
            mFastForwordButton.setVisibility(INVISIBLE);
            mRewindButton.setVisibility(VISIBLE);
        } else {
            if (mIsSeekable) {
                mFastForwordButton.setVisibility(VISIBLE);
                mRewindButton.setVisibility(VISIBLE);
            } else {
                mFastForwordButton.setVisibility(INVISIBLE);
                mRewindButton.setVisibility(INVISIBLE);
            }
        }

        setVolumeBar();
    }

    public void setDVRLiveButton(boolean visible) {
        LayoutParams layoutParams;
        if (visible) {
            mDVRLiveButton.setVisibility(VISIBLE);
            //layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mBaseSize /3);
            layoutParams = new LayoutParams( mBaseSize * 4/5 ,  mBaseSize * 1/3);
        } else {
            mDVRLiveButton.setVisibility(INVISIBLE);
            layoutParams = new LayoutParams(0, mBaseSize * 1/3);
        }
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mDVRLiveButton.setLayoutParams(layoutParams);
    }

    public void setLive(boolean isLive) {
        mIsLive = isLive;
        setSeekable(!isLive);
        setDVRLiveButton(!isLive);

        if (mIsLive && !mIsDVR) {
            mSeekBarView.setVisibility(INVISIBLE);
        } else {
            mSeekBarView.setVisibility(VISIBLE);
        }
    }

    public void setDvr(boolean isDvr) {
        mIsDVR = isDvr;
        if (!mIsDVR) {
            setLive(false);
        }
    }


    public void setThumbnailImage(int currentTime) {
        mSeekBarView.setThumbnailImage(currentTime / 1000);
    }

    public void setAdSkipButton(String title) {
        mSkipAdButton.setVisibility(VISIBLE);
        mSkipAdButton.setText(title);
    }

    public void unSetAdSkipButton() {
        mSkipAdButton.setVisibility(INVISIBLE);
    }

    public void setBackButton(String mainName, String mainUrl, String subName, String subUrl) {
        DebugLog.d(TAG, "setBackButton="+mainName+": "+mainUrl+"; "+subName+": "+subUrl);
        mBackButtonMain.setVisibility(VISIBLE);
        mBackButtonSub.setVisibility(VISIBLE);
        mBackButtonMain.setText(mainName);
        mBackButtonSub.setText(subName);
        mBackUrlMain = mainUrl;
        mBackUrlSub = subUrl;
    }

    public void unSetBackButton() {
        mBackButtonMain.setVisibility(INVISIBLE);
        mBackButtonSub.setVisibility(INVISIBLE);
        mBackButtonMain.setText("");
        mBackButtonSub.setText("");
        mBackUrlMain = null;
        mBackUrlSub = null;
    }

    public void setCloseButton(boolean visible) {
        if (visible) {
            mCloseButton.setVisibility(VISIBLE);
        } else {
            mCloseButton.setVisibility(INVISIBLE);
        }
    }

    public void setOrientationButton(boolean visible) {
        if (visible) {
            mOrientationButton.setVisibility(VISIBLE);
        } else {
            mOrientationButton.setVisibility(INVISIBLE);
        }
    }

    public void setSettingButton(boolean visible) {
        LayoutParams layoutParams;
        if (visible) {
            mSettingButton.setVisibility(VISIBLE);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        } else {
            mSettingButton.setVisibility(INVISIBLE);
            layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        }
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mSettingButton.setLayoutParams(layoutParams);
    }

    public void setNavigationPreviousButton(boolean visible) {
        if (visible) {
            mNavigationPreviousButton.setVisibility(VISIBLE);
            mNavigationPreviousLabel.setVisibility(VISIBLE);
        } else {
            mNavigationPreviousButton.setVisibility(INVISIBLE);
            mNavigationPreviousLabel.setVisibility(INVISIBLE);
        }
    }

    public void setNavigationNextButton(boolean visible) {
        if (visible) {
            mNavigationNextButton.setVisibility(VISIBLE);
            mNavigationNextLabel.setVisibility(VISIBLE);
        } else {
            mNavigationNextButton.setVisibility(INVISIBLE);
            mNavigationNextLabel.setVisibility(INVISIBLE);
        }
    }

    public void setSponsorButton(String sponsorUrl) {
        mClickThrough.setVisibility(VISIBLE);
        mSponsorUrl = sponsorUrl;
    }

    public void unSetSponsorButton() {
        mClickThrough.setVisibility(INVISIBLE);
        mSponsorUrl = null;
    }

    public void setMute(boolean isMute) {
        if (isMute) {
            mCurrentVolume = getVolume();
            setVolume(0);
        } else {
            setVolume(mCurrentVolume);
        }
    }

    public void volumeUp() {
        setVolume(getVolume() +1);
    }

    public void volumeDown() {
        setVolume(getVolume() -1);
    }

    public void setVolume(int volume) {
        AudioManager manager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
        mCurrentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    public int getVolume() {
        AudioManager manager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
        return manager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolumeBar() {
        AudioManager manager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
        float getVolume = (float)manager.getStreamVolume(AudioManager.STREAM_MUSIC) / (float)manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeBarView.setProgress(getVolume * 100, 0);

        if (getVolume > 0) {
            mVolumeOnButton.setVisibility(VISIBLE);
            mVolumeOffButton.setVisibility(INVISIBLE);
        } else {
            mVolumeOnButton.setVisibility(INVISIBLE);
            mVolumeOffButton.setVisibility(VISIBLE);
        }
    }

    private boolean mIsNavigateButtonVisibility;
    public void setNavigateButtonVisibility(boolean visible) {
        mIsNavigateButtonVisibility = visible;
        if (!visible) {
            mNavigationButtonAreaPanel.setVisibility(INVISIBLE);
            mIsNavigationPreviousSelected = false;
            mIsNavigationNextSelected = false;
            mNavigationPreviousLabel.setTextColor(0xFFFFFFFF);
            mNavigationNextLabel.setTextColor(0xFFFFFFFF);
            mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button", "drawable", mActivity.getPackageName()));
            mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button", "drawable", mActivity.getPackageName()));
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    public void setVisible(boolean visibility) {
        DebugLog.d(TAG, "controllerVisibility: "+visibility);
        mIsVisible = visibility;
        if (visibility) {
            mTopPanel.setVisibility(VISIBLE);
            mMiddlePanel.setVisibility(VISIBLE);
            mBottomPanel.setVisibility(VISIBLE);
            mCenterControlPanel.setVisibility(VISIBLE);
            mStatusBarAreaPanel.setVisibility(VISIBLE);
            mSeekBarAreaPanel.setVisibility(VISIBLE);
            if (mIsNavigateButtonVisibility) {
                mNavigationButtonAreaPanel.setVisibility(VISIBLE);
            } else {
                mNavigationButtonAreaPanel.setVisibility(INVISIBLE);
            }
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            updateProgress();
        } else {
            mTopPanel.setVisibility(INVISIBLE);
            mMiddlePanel.setVisibility(INVISIBLE);
            mBottomPanel.setVisibility(INVISIBLE);
            mCenterControlPanel.setVisibility(INVISIBLE);
            mStatusBarAreaPanel.setVisibility(INVISIBLE);
            mSeekBarAreaPanel.setVisibility(INVISIBLE);
            mNavigationButtonAreaPanel.setVisibility(INVISIBLE);
            mIsNavigationPreviousSelected = false;
            mIsNavigationNextSelected = false;
            mNavigationPreviousLabel.setTextColor(0xFFFFFFFF);
            mNavigationNextLabel.setTextColor(0xFFFFFFFF);
            mNavigationNextButton.setImageResource(getResources().getIdentifier("navigation_next_button", "drawable", mActivity.getPackageName()));
            mNavigationPreviousButton.setImageResource(getResources().getIdentifier("navigation_previous_button", "drawable", mActivity.getPackageName()));
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        int satusBarHeight = MyUtils.getStatusBarHeight(mActivity);
        if (satusBarHeight > 0) {
            setStatusBarHeight(satusBarHeight);
        }
    }

    public boolean getVisible() {
        return mIsVisible;
    }

    public boolean isSeeking() {
        return mSeekBarView.isSeeking();
    }

    private Boolean mIsSeekable;
    public void setSeekable(boolean isSeekable) {
        mIsSeekable = isSeekable;
        mSeekBarView.setSeekable(mIsSeekable);

        if (!mIsDVR) {
            if (mIsSeekable) {
                mFastForwordButton.setVisibility(VISIBLE);
                mRewindButton.setVisibility(VISIBLE);
            } else {
                mFastForwordButton.setVisibility(INVISIBLE);
                mRewindButton.setVisibility(INVISIBLE);
            }
        } else {
            if (mIsLive) {
                if (mIsSeekable) {
                    mFastForwordButton.setVisibility(INVISIBLE);
                    mRewindButton.setVisibility(VISIBLE);
                } else {
                    mFastForwordButton.setVisibility(INVISIBLE);
                    mRewindButton.setVisibility(INVISIBLE);
                }
            } else {
                if (mIsSeekable) {
                    mFastForwordButton.setVisibility(VISIBLE);
                    mRewindButton.setVisibility(VISIBLE);
                } else {
                    mFastForwordButton.setVisibility(INVISIBLE);
                    mRewindButton.setVisibility(INVISIBLE);
                }
            }
        }
    }

    public void setAdPoint(Float... adPointArray) {
        mSeekBarView.setAdPoint(adPointArray);
    }

    public void setAdPointVisible(boolean visible) {
        mSeekBarView.setAdPointVisible(visible);
    }

    public void setStatusBarHeight(int height) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mStatusBarAreaPanel.setLayoutParams(layoutParams);
    }

    private void setConsoleHideTimer(boolean bln) {
        if(bln)
        {
            setConsoleHideTimer();
        }
        else
        {
            unsetConsoleHideTimer();
        }
    }

    private void setConsoleHideTimer() {
        unsetConsoleHideTimer();
        mConsoleHideTimer = new Timer(true);
        mConsoleHideTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        setVisible(false);
                    }
                });
            }
        }, 5000);
    }

    private void unsetConsoleHideTimer() {
        if (mConsoleHideTimer != null) {
            mConsoleHideTimer.cancel();
            mConsoleHideTimer = null;
        }
    }

    public void cancelLoadThumbnailImage() {
        if (mSeekBarView != null) {
            mSeekBarView.cancelLoadThumbnailImage();
        }
    }

	/*
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mIsVisible) {
					unsetConsoleHideTimer();
				}
				break;
			case MotionEvent.ACTION_UP:
				setVisible(!mIsVisible);
				if (mIsVisible) {
					setConsoleHideTimer();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
		}
		return true;
	}
	*/

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //setVisible(false);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        DebugLog.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            isAttachedToWindow = false;
            removeCallbacks(updateProgressAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            isAttachedToWindow = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String moldTimeFormat(int sec) {
        String HH = String.format("%02d", (sec / 3600));
        String mm = String.format("%02d", (sec % 3600 / 60));
        String ss = String.format("%02d", (sec % 60));
        return new StringBuilder().append(HH).append(":").append(mm).append(":").append(ss).toString();
    }

    private String moldTimeFormatAd(int sec) {
        int mm = sec / 60;
        String ss = String.format("%02d", (sec % 60));
        return new StringBuilder().append(mm).append(":").append(ss).toString();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            int width = r - l;
            int height = b - t;
            if (mSeekBarView != null) {
                mSeekBarView.setScreenSize(width, height);
                mSeekBarView.setSeekBarAreaSize(mSeekBarView.getWidth(), mBaseSize /3);
            }
            if (mVolumeBarView != null) {
                mVolumeBarView.setScreenSize(width, height);
                mVolumeBarView.setSeekBarAreaSize(mVolumeBarView.getWidth(), mBaseSize /3);
            }
            if (mNavigationButtonAreaPanel != null) {
                if (width > height) {
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mNavigationButtonAreaPanel.setY(height / 2 - mNavigationButtonAreaPanel.getHeight() / 2);
                } else {
                    mNavigationButtonAreaPanel.setY(height / 2 + (width / 16 * 9) / 2);
                }
            }
        }
    }


    public void updateProgress(ExoPlayer exoPlayer) {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            setExoPlayer(exoPlayer);
            updateProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgress() {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            if (!getVisible()|| isSeeking() || !isAttachedToWindow) {
                return;
            }

            @Nullable Player player = mExoPlayer;

            int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
            if (player != null && player.isPlaying())
            {
                int mVideoCurrentTime = (int)player.getCurrentPosition();
                int mVideoDuration = (int)player.getDuration();
                int mBufferTime = (int)player.getBufferedPosition();

                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName(),  new Object(){}.getClass().getEnclosingMethod().getName() + " ID[" + this.getId() + "]" + " VideoCurrentTime[" + mVideoCurrentTime + "] VideoDuration[" + mVideoDuration + "] BufferTime[" + mBufferTime + "]");

                if(mIsDVR)
                {
                    setDVRLabel(mVideoCurrentTime, mVideoDuration, mBufferTime,mIsLive);
                }
                else
                {
                    setTimeLabel(mVideoCurrentTime, mVideoDuration, mBufferTime);
                }
            }
            final Handler handler = new Handler();
            handler.postDelayed(updateProgressAction, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
