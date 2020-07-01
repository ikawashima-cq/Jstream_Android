/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.stream.jstplayerdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SubtitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import jp.co.stream.jstplayersdk.VideoPlayer;
import jp.co.stream.jstplayersdk.util.MyUtils;
import jp.co.stream.jstplayersdk.util.TrackSelectionDialog;
import jp.co.stream.jstplayersdk.view.IndicatorView;
import jp.co.stream.jstplayersdk.view.PreviewCoverView;
import jp.co.stream.jstplayersdk.view.VideoControllerView;
import jp.co.stream.jstplayersdk.view.VideoSelectorView;


public class SamplePlayerFragment extends Fragment {

    private VideoPlayer mVideoPlayer;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
    private TextView txtTrace;

    private static final String ARGS_ITEM = "args_item";
    public static final String RESULT_CODE_CLOSE = "resultCodeClose";

    private DefaultTrackSelector trackSelector;

    private SamplePlayerModel currentSamplePlayerModel;

    private Activity inActivity;
    private Fragment inFragment;
    private View inView;

    private int defaultSeekTime = 10;
    private int mSeekStartTime = -1;
    private int mSeekEndTime = -1;


    private RelativeLayout mBackGroundLayout;
    private RelativeLayout mVideoLayout;
    private VideoControllerView mVideoControllerView;
    private IndicatorView mIndicatorView;
    private PreviewCoverView mPreviewCoverView;
    private VideoSelectorView mVideoSelectorView;
    private SubtitleView mSubtitleView;

    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean isShowingTrackSelectionDialog;


    List<Rect> exclusionRects;

    public static SamplePlayerFragment newInstance(SamplePlayerModel item) {
        SamplePlayerFragment fragment = new SamplePlayerFragment();
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARGS_ITEM, item);
            fragment.setArguments(bundle);
        }catch (Exception e){
            e.printStackTrace();
        }
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            inFragment = this;
            inActivity = inFragment.getActivity();
            inView = view;
            txtTrace = new TextView(inActivity);

            currentSamplePlayerModel = getArguments().getParcelable(ARGS_ITEM);
            if (currentSamplePlayerModel == null) {
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initializePlayer() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            DefaultTrackSelector.ParametersBuilder builder =
                    new DefaultTrackSelector.ParametersBuilder(/* context= */ inActivity);
            trackSelectorParameters = builder.build();

            TrackSelection.Factory trackSelectionFactory;
            trackSelectionFactory = new AdaptiveTrackSelection.Factory();

            trackSelector = new DefaultTrackSelector(/* context= */ inActivity, trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);
            /*
            trackSelector.setParameters(
                    trackSelector
                            .buildUponParameters()
                            .setPreferredAudioLanguage("en"));
             */
            setViews();

            mVideoPlayer = new VideoPlayer(inActivity,trackSelector);
            mVideoPlayer.setCallbacks(videoPlayerCallBacks);

            setIndicator(true);
            mVideoControllerView.setVisibility(View.VISIBLE);
            mVideoControllerView.setVisible(false);
            mVideoControllerView.setPlaying(true);
            mVideoControllerView.setDvr(currentSamplePlayerModel.getDvr());
            mVideoControllerView.setLive(currentSamplePlayerModel.getLive());


            mVideoSelectorView = new VideoSelectorView(inActivity);
            mVideoSelectorView.setVisible(false);
            mVideoSelectorView.setCallbacks(new VideoSelectorView.VideoSelectorViewCallBacks() {
                @Override
                public void onMenuSelect(int value, final String url) {
                    inActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mVideoSelectorView.setVisible(false);
                            if (mVideoPlayer != null) {
                                mVideoPlayer.pause();
                                mVideoPlayer.stopPlayer();
                                removeViews();
                            }
                            initializePlayer();                            }
                    });
                }
                @Override
                public void onMenuCancel() {
                    mVideoSelectorView.setVisible(false);
                    mVideoPlayer.play();
                }
            });
            inActivity.addContentView(mVideoSelectorView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mVideoControllerView.setSettingButton(false);

            View videoView = mVideoPlayer.getView();
            if (videoView != null && videoView.getParent() != null) {
                mVideoPlayer.initPlayer(currentSamplePlayerModel.getUri());
                mVideoControllerView.updateProgress(mVideoPlayer.getExoPlayer());
            } else {

                mVideoPlayer.setOnViewCreated(new VideoPlayer.ViewCreatedCallBack() {
                    @Override
                    public void onViewCreated() {
                        mVideoPlayer.setOnViewCreated(null);
                        mVideoPlayer.initPlayer(currentSamplePlayerModel.getUri());
                        mVideoControllerView.updateProgress(mVideoPlayer.getExoPlayer());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            List<Rect> exclusionRects = new ArrayList<Rect>();
                            Rect tmprect = new Rect();
                            inActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(tmprect);
                            exclusionRects.add(tmprect);
                            inActivity.getWindow().setSystemGestureExclusionRects(exclusionRects);
                        }

                        ((SimpleExoPlayer)mVideoPlayer.getExoPlayer()).addTextOutput(new TextOutput() {
                            @Override
                            public void onCues(List<Cue> cues) {
                                if (mVideoControllerView.getSubtitleView() != null) {
                                    mVideoControllerView.getSubtitleView().onCues(cues);
                                }
                            }
                        });

                    }
                });

                mVideoLayout.addView(mVideoPlayer.getView());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resize(int videoWidth, int videoHeight) {

        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            if (Build.VERSION.SDK_INT >= 19) {
                inActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"resize videoSize: "+videoWidth+":"+videoHeight);
            if (videoWidth <= 0 && videoHeight <= 0) {
                return;
            }

            Point screenSize = MyUtils.getScreenSize(inActivity);
            int displayVideoWidth = screenSize.x;
            int displayVideoHeight = screenSize.y;
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"resize screenSize: "+screenSize.x+":"+screenSize.y);

            float ratio = (float)videoWidth / (float)videoHeight;
            if (screenSize.y * ratio > screenSize.x) {
                displayVideoHeight = (int)(screenSize.x / ratio);
            } else {
                displayVideoWidth = (int)(screenSize.y * ratio);
            }
            if (mVideoSelectorView != null) {
                mVideoSelectorView.setVideoSize(screenSize.x, screenSize.y);
            }
            mVideoControllerView.setVideoSize(displayVideoWidth, displayVideoHeight);
            mVideoLayout.setLayoutParams(new FrameLayout.LayoutParams(displayVideoWidth, displayVideoHeight, Gravity.CENTER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            removeViews();
            if (mVideoPlayer != null) {
                mVideoPlayer.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setViews() {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Point screenSize = MyUtils.getScreenSize(inActivity);

            mVideoLayout = new RelativeLayout(inActivity);
            inActivity.addContentView(mVideoLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            mVideoControllerView = new VideoControllerView(inActivity);
            mVideoControllerView.initController(screenSize.x, screenSize.y);

            inActivity.addContentView(mVideoControllerView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mVideoControllerView.setCallbacks(videoControllerCallbacks);
            mVideoControllerView.setSeekable(false);
            mVideoControllerView.setVisibility(RelativeLayout.INVISIBLE);
            mVideoControllerView.setCloseButton(true);
            mVideoControllerView.setSettingButton(true);

            mIndicatorView = new IndicatorView(inActivity);
            mIndicatorView.initView();
            mIndicatorView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            inActivity.addContentView(mIndicatorView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            setIndicator(false);

            mPreviewCoverView = new PreviewCoverView(inActivity);
            inActivity.addContentView(mPreviewCoverView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mPreviewCoverView.setVisible(false);

            /*
            mSubtitleView = (SubtitleView)inActivity.findViewById(com.google.android.exoplayer2.R.id.exo_subtitles);
            if (mSubtitleView != null) {
                int mBaseSize = MyUtils.convertDpToPx(inActivity, 100);
                RelativeLayout.LayoutParams layoutParams;
                layoutParams = new RelativeLayout.LayoutParams((int)(mBaseSize * 1.04), (int)(mBaseSize * 1.04));
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                inActivity.addContentView(mSubtitleView, layoutParams);
            }
             */



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeViews() {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            if (mVideoPlayer != null && mVideoPlayer.getView() != null && mVideoPlayer.getView().getParent() != null) {
                inActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mVideoControllerView.setOnTouchListener(null);
                        mIndicatorView.setOnTouchListener(null);
                        mPreviewCoverView.setOnTouchListener(null);
                        mVideoSelectorView.setOnTouchListener(null);
                        mVideoLayout.removeView(mVideoPlayer.getView());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIndicator(final boolean visible) {

        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
        inActivity.runOnUiThread(new Runnable() {
            public void run() {
                try{

                    mVideoControllerView.setVisible(!visible);
                    mIndicatorView.setVisible(visible);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private VideoControllerView.VideoControllerCallbacks videoControllerCallbacks = new VideoControllerView.VideoControllerCallbacks() {
        @Override
        public void onPlay() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayer != null) {
                    mVideoPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPause() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayer != null) {
                    mVideoPlayer.pause();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPlayAndPause() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayer != null) {
                    if (mVideoPlayer.isPlaying()) {
                        mVideoControllerView.setPlaying(false);
                        mVideoPlayer.pause();
                    } else {
                        mVideoControllerView.setPlaying(true);
                        mVideoPlayer.play();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mSeekStartTime < 0) {
                    mSeekStartTime = (int)mVideoPlayer.getCurrentTime();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekEnd(double position) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

                if (mVideoPlayer != null) {
                    int mVideoDuration = (int)mVideoPlayer.getDuration();
                    boolean mIsPausedSeek = !mVideoPlayer.isPlaying();

                    int seekTime = (int)(mVideoDuration * position);
                    if (seekTime > mVideoDuration - 10000) {
                        seekTime = mVideoDuration - 10000;
                    }
                    mSeekEndTime = seekTime;
                    if (mSeekStartTime != mSeekEndTime) {
                        if (mIsPausedSeek) {
                            mVideoPlayer.seekAndPause(seekTime);
                        } else {
                            mVideoPlayer.seekAndPlay(seekTime);
                        }
                    } else {
                        if (mIsPausedSeek) {
                            mVideoPlayer.pause();
                        } else {
                            mVideoPlayer.play();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onChangePosition(double position) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClose() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                close(RESULT_CODE_CLOSE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickSponsorButton(String url) {
            try {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch(Exception e) {}
        }
        @Override
        public void onClickBackButtonMain(String url) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickBackButtonSub(String url) {
            try {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickSkipAdButton() {
            try {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickFastForwordButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                fastForword(defaultSeekTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickRewindButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                fastForword(-1 * defaultSeekTime);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onClickOrientationButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (inActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                    inActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    inActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickSettingButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

                if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(trackSelector))
                {
                    isShowingTrackSelectionDialog = true;
                    TrackSelectionDialog trackSelectionDialog =
                            TrackSelectionDialog.createForTrackSelector(
                                    trackSelector,
                                    /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                    trackSelectionDialog.show(((FragmentActivity)inActivity).getSupportFragmentManager(), /* tag= */ null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickNavigationNextButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickNavigationPreviousButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onClickDVRLiveButton() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void close(String statusCode) {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            inActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            inActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            inActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            Intent data = new Intent();
            data.putExtra("status", statusCode);
            inActivity.setResult(inActivity.RESULT_OK, data);
            inActivity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fastForword(int forwordTime) {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            if (mVideoPlayer != null && !mVideoPlayer.isSeeking()) {

                int mVideoCurrentTime = (int)mVideoPlayer.getCurrentTime();
                int mVideoDuration = (int)mVideoPlayer.getDuration();
                int mSeekStartTime = -1;
                int mSeekEndTime = -1;
                boolean mIsPausedSeek = false;

                int seekTime = mVideoCurrentTime + forwordTime *1000;

                if (seekTime < 0)
                {
                    return;
                }

                if (seekTime >= mVideoDuration)
                {
                    return;
                }

                if (mSeekStartTime < 0) {
                    mSeekStartTime = mVideoCurrentTime;
                }
                if (seekTime > mVideoDuration -10 *1000) {
                    seekTime = mVideoDuration -10 *1000;
                }
                mSeekEndTime = seekTime;
                if (mSeekStartTime != mSeekEndTime) {
                    mIsPausedSeek = !mVideoPlayer.isPlaying();
                    mVideoPlayer.pause();
                    if (mIsPausedSeek) {
                        mVideoPlayer.seekAndPause(seekTime);
                    } else {
                        mVideoPlayer.seekAndPlay(seekTime);
                    }
                } else {
                    if (mIsPausedSeek) {
                        mVideoPlayer.pause();
                    } else {
                        mVideoPlayer.play();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            inActivity.runOnUiThread(new Runnable() {
                public void run() {
                    initializePlayer();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            removeViews();
            if (mVideoPlayer != null) {
                mVideoPlayer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            removeViews();
            if (mVideoPlayer != null) {
                mVideoPlayer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void imHiddenNow() {
        releasePlayer();
    }

    public void imVisibleNow() {
        initializePlayer();
    }

    private VideoPlayer.VideoPlayerCallBacks videoPlayerCallBacks = new VideoPlayer.VideoPlayerCallBacks() {
        final String tmpClassName = "VideoPlayerCallBacks";
        @Override
        public void onVideoInitialized() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoPrepared() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

                if(!currentSamplePlayerModel.getLive())
                {
                    boolean blnseekable =
                            mVideoPlayer != null &&
                                    mVideoPlayer.getExoPlayer() != null &&
                                    Math.abs(mVideoPlayer.getExoPlayer().getDuration() - mVideoPlayer.getExoPlayer().getCurrentPosition()) >= defaultSeekTime * 1000;

                    mVideoControllerView.setSeekable(blnseekable);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoRenderingStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoCompleted() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPlaying() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                SimpleExoPlayer tmpexo = (SimpleExoPlayer) mVideoPlayer.getExoPlayer();
                tmpexo.getVolume();
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"Volume:[" + tmpexo.getVolume() + "]");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPaused() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoSizeChanged(int width, int height) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"width:" + width + " height:" + height);

                resize(width, height);
                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoError(String message) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDRMError(String message) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekCompleted() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onBufferingStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onBufferingCompleted() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                setIndicator(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPositionDiscontinuity(int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSeekProcessed() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
