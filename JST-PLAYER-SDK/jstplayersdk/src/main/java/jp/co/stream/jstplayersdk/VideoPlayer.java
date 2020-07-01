package jp.co.stream.jstplayersdk;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.annotation.Nullable;
import jp.co.stream.jstplayersdk.util.DebugLog;

/**
 * 動画再生用クラス
 * @author J-Stream Inc
 * @version 0.0.1
 */
public class VideoPlayer {

    private static final String TAG = "VideoPlayer";

    private SimpleExoPlayer mExoPlayer;
    private SurfaceView mSurfaceView;
    private DataSource.Factory mDataSourceFactory;
    private DrmSessionManager<ExoMediaCrypto> mDrmSessionManager;
    private Object mMediaSOurce;

    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private boolean mIsPreparing = false;
    private boolean mIsPrepared = false;
    private boolean mIsSeeking = false;
    private boolean mIsBuffering = false;

    private Activity mActivity;
    private VideoPlayerCallBacks mVideoPlayerCallbacks;
    private ViewCreatedCallBack mViewCreatedCallBack;

    private String mVideoURL;
    private int mStartTime;
    private boolean mAutoPlay;
    private String mTicket;
    private boolean mIsLive;
    private int mCurrentTime;
    private int mDurationTime;
    private int mCurrentBitrate;

    private int mErrorCount = 0;

    private String mEpisodeId;
    private String mExperimentName;

    public interface VideoPlayerCallBacks {
        public void onVideoInitialized();
        public void onVideoPrepared();
        public void onVideoRenderingStart();
        public void onVideoCompleted();
        public void onPlaying();
        public void onPaused();
        public void onSeekStart();
        public void onSeekCompleted();
        public void onBufferingStart();
        public void onBufferingCompleted();
        public void onVideoSizeChanged(int width, int height);
        public void onVideoError(String message);
        public void onDRMError(String message);

        public void onTimelineChanged(Timeline timeline, int reason);
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason);
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections);
        public void onLoadingChanged(boolean isLoading);
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason);
        public void onIsPlayingChanged(boolean isPlaying);
        public void onRepeatModeChanged(int repeatMode);
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled);
        public void onPositionDiscontinuity(int reason);
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters);
        public void onSeekProcessed();
    }

    public interface ViewCreatedCallBack {
        public void onViewCreated();
    }


    public VideoPlayer(Activity activity,DefaultTrackSelector trackSelector)
    {
        this(activity,"",trackSelector);
    }

    public VideoPlayer(Activity activity)
    {
        this(activity,"",null);
    }
    public VideoPlayer(Activity activity,String ticket)
    {
        this(activity,ticket,null);
    }

    public VideoPlayer(Activity activity, String ticket,DefaultTrackSelector trackSelector) {
        DebugLog.d(TAG, Thread.currentThread().getStackTrace()[1].getClassName() + " " + Thread.currentThread().getStackTrace()[1].getMethodName());
        try{
            mActivity = activity;
            mTicket = ticket;
            mErrorCount = 0;

            mDrmSessionManager = null;
            FrameworkMediaDrm mediaDrm = null;

            if(mTicket != "")
            {
                DebugLog.d(TAG, "DRM");

                int licenseServerId = mActivity.getResources().getIdentifier("player_drm_license_server", "string", mActivity.getPackageName());
                final String licenseServerUrl = mActivity.getResources().getString(licenseServerId, mTicket);
                HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseServerUrl, new DefaultHttpDataSourceFactory(mActivity.getPackageName()));
                UUID uuid = C.WIDEVINE_UUID;

                try {
                    mediaDrm = FrameworkMediaDrm.newInstance(uuid);
                    mDrmSessionManager = new DefaultDrmSessionManager(uuid, mediaDrm, drmCallback, null, false);
                    //mDrmSessionManager.addListener(new Handler(), defaultDrmSessionEventListener);
                } catch (UnsupportedDrmException e) {
                    DebugLog.d(TAG, "UnsupportedDrmException: "+e.toString());
                }
            }
            else{
                mDrmSessionManager = DrmSessionManager.getDummyDrmSessionManager();

                DebugLog.d(TAG, "NonDRM");
            }

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            mDataSourceFactory = new DefaultDataSourceFactory(mActivity, Util.getUserAgent(mActivity, mActivity.getPackageName()), bandwidthMeter);

            int minDurationForQualityIncreaseMs = AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS;
            int maxDurationForQualityDecreaseMs = AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS;
            int minDurationToRetainAfterDiscardMs = AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS;
            float bandwidthFraction = AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION;
            //float bandwidthFraction = 0.80f;

/*
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(
                    bandwidthMeter,
                    minDurationForQualityIncreaseMs,
                    maxDurationForQualityDecreaseMs,
                    minDurationToRetainAfterDiscardMs,
                    bandwidthFraction
            );
*/

            DefaultTrackSelector inTrackSelector = trackSelector;
            if(trackSelector == null)
            {
                DefaultTrackSelector.Parameters inTrackSelectorParameters;
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
                inTrackSelector = new DefaultTrackSelector(mActivity, videoTrackSelectionFactory);
                DefaultTrackSelector.ParametersBuilder builder = new DefaultTrackSelector.ParametersBuilder(mActivity);
                inTrackSelectorParameters = builder.build();
                inTrackSelector.setParameters(inTrackSelectorParameters);
            }


            DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE); //Defailt

            DefaultLoadControl.Builder loadControlBuilder = new DefaultLoadControl.Builder();
            loadControlBuilder.setAllocator(allocator);
            int minBufferMs = 30000;
            int maxBufferMs = 90000;
            //int minBufferMs = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS; //15000
            //int maxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS; //30000
            int bufferForPlaybackMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS; //2500
            int bufferForPlaybackAfterRebufferMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS; //5000
            loadControlBuilder.setBufferDurationsMs(minBufferMs, maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs);

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mActivity, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControlBuilder.createDefaultLoadControl(), drmSessionManager);
            //2.9.3用//mExoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity,renderersFactory, trackSelector, loadControlBuilder.createDefaultLoadControl(), drmSessionManager);
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity,renderersFactory, inTrackSelector, loadControlBuilder.createDefaultLoadControl(), drmSessionManager);

            mExoPlayer = new SimpleExoPlayer.Builder(/* context= */ mActivity, renderersFactory)
                    .setTrackSelector(inTrackSelector)
                    .build();

            mExoPlayer.addListener(exoPlayerEventListener);
            mExoPlayer.addVideoListener(exoPlayerVideoListener);
            mExoPlayer.addAnalyticsListener(analyticsListener);

            mSurfaceView = new SurfaceView(mActivity);
            mSurfaceView.getHolder().addCallback(surfaceHolderCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCallbacks(VideoPlayerCallBacks callbacks){
        mVideoPlayerCallbacks = callbacks;
    }

    public void setOnViewCreated(ViewCreatedCallBack callbacks){
        mViewCreatedCallBack = callbacks;
    }

    private double mStartPostition;

    /**
     * ロードから再生までを行う
     */
    public void initPlayer(final Uri uri) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            initPlayer(uri.toString(), 0, 0, true, true, "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(final String urlString) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            initPlayer(urlString, 0, 0, true, true, "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(final String urlString, double startPosition, boolean autoPlay, boolean isLive, String episodeId, String experimentName) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            mStartPostition = startPosition;
            initPlayer(urlString, 0, startPosition, autoPlay, isLive, episodeId, experimentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initPlayer(final String urlString, int startTime, boolean autoPlay, boolean isLive, String episodeId, String experimentName) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            mStartPostition = 0;
            initPlayer(urlString, startTime, 0, autoPlay, isLive, episodeId, experimentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initPlayer(final String urlString, int startTime, double startPosition, boolean autoPlay, boolean isLive, String episodeId, String experimentName) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            mVideoURL = urlString;
            mAutoPlay = autoPlay;
            mStartTime = startTime;
            mIsLive = isLive;
            mIsPrepared = false;
            mEpisodeId = episodeId;
            mExperimentName = experimentName;

            loadItem(urlString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 動画ソースurlを読み込む
     */
    public void loadItem(final String urlString)
    {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try
        {
            int type = Util.inferContentType(Uri.parse(urlString));

            switch (type) {
                case C.TYPE_DASH:
                    DebugLog.d(TAG, "VideoType: MPEG-Dash("+type+")");
                    mMediaSOurce = new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mDataSourceFactory), mDataSourceFactory)
                            .setDrmSessionManager(mDrmSessionManager)
                            .createMediaSource(Uri.parse(urlString));
                    mExoPlayer.prepare((MediaSource) mMediaSOurce);
                    return;
                case C.TYPE_SS:
                    DebugLog.d(TAG, "VideoType: Smooth Streaming("+type+")");
                    close();
                    if (mVideoPlayerCallbacks != null) {
                        mVideoPlayerCallbacks.onVideoError("Unsupported.");
                    }
                    return;
                case C.TYPE_HLS:
                    DebugLog.d(TAG, "VideoType: HLS("+type+")");
                    mMediaSOurce = new HlsMediaSource.Factory(mDataSourceFactory)
                            .setDrmSessionManager(mDrmSessionManager)
                            .createMediaSource(Uri.parse(urlString));
                    mExoPlayer.prepare((MediaSource) mMediaSOurce);
                    return;
                case C.TYPE_OTHER:
                    DebugLog.d(TAG, "VideoType: Regular Media Files("+type+")");
                    mMediaSOurce = new ExtractorMediaSource.Factory(mDataSourceFactory)
                            .setDrmSessionManager(mDrmSessionManager)
                            .createMediaSource(Uri.parse(urlString));
                    mExoPlayer.prepare((MediaSource) mMediaSOurce);
                    return;
                default:
                    DebugLog.d(TAG, "Unsupported("+type+")");
                    close();
                    if (mVideoPlayerCallbacks != null) {
                        mVideoPlayerCallbacks.onVideoError("Unsupported.");
                    }
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Player.EventListener exoPlayerEventListener = new Player.EventListener() {
        final String tmpClassName = "Player.EventListener";
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onTimelineChanged(timeline,reason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onTimelineChanged(timeline,manifest,reason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onTracksChanged(trackGroups,trackSelections);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onLoadingChanged(isLoading);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        DebugLog.d(TAG, "VideoPlayer.onPlayerStateChanged: STATE_BUFFERING");
                        mIsBuffering = true;
                        if (mVideoPlayerCallbacks != null) {
                            mVideoPlayerCallbacks.onBufferingStart();
                        }
                        break;
                    case Player.STATE_ENDED:
                        DebugLog.d(TAG, "VideoPlayer.onPlayerStateChanged: STATE_ENDED");
                        mIsPrepared = false;
                        if (mVideoPlayerCallbacks != null) {
                            mVideoPlayerCallbacks.onVideoCompleted();
                        }
                        break;
                    case Player.STATE_IDLE:
                        DebugLog.d(TAG, "VideoPlayer.onPlayerStateChanged: STATE_IDLE");
                        break;
                    case Player.STATE_READY:
                        DebugLog.d(TAG, "VideoPlayer.onPlayerStateChanged: STATE_READY");
                        if (!mIsPrepared) {
                            DebugLog.d(TAG, "VideoPlayer.onPrepared");
                            mIsPrepared = true;
                            mIsPreparing = true;
                            mErrorCount = 0;
                            if (mVideoPlayerCallbacks != null) {
                                mVideoPlayerCallbacks.onVideoPrepared();
                            }
                            if (mStartTime != 0) {
                                if (mStartTime > 0) {
                                    seek(mStartTime);
                                }
                                if (mStartTime < 0 && getCurrentTime() > 0) {
                                    //seek((int) (getDuration() + mStartTime));
                                    seek((int)(getCurrentTime() + mStartTime));
                                }
                            } else if (mStartPostition > 0) {
                                seek((int)(getDuration() * mStartPostition));
                            }
                            if (mAutoPlay != playWhenReady) {
                                mExoPlayer.setPlayWhenReady(mAutoPlay);
                            }
                            mIsPreparing = false;
                        }
                        if (!mIsPreparing) {
                            if (mVideoPlayerCallbacks != null) {
                                if (mExoPlayer.getPlayWhenReady()) {
                                    mVideoPlayerCallbacks.onPlaying();
                                } else {
                                    mVideoPlayerCallbacks.onPaused();
                                }
                            }
                        }
                        if (mIsBuffering) {
                            mIsBuffering = false;
                            if (mVideoPlayerCallbacks != null) {
                                mVideoPlayerCallbacks.onBufferingCompleted();
                            }
                        }
                        if (mIsSeeking) {
                            mIsSeeking = false;
                            if (mVideoPlayerCallbacks != null) {
                                mVideoPlayerCallbacks.onSeekCompleted();
                            }
                            if (mIsSeekAndPlay) {
                                mIsSeekAndPlay = false;
                                play();
                            }
                            if (mIsSeekAndPause) {
                                mIsSeekAndPause = false;
                                pause();
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onPlaybackSuppressionReasonChanged(playbackSuppressionReason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onIsPlayingChanged(isPlaying);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onRepeatModeChanged(repeatMode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onShuffleModeEnabledChanged(shuffleModeEnabled);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
            if (mVideoPlayerCallbacks != null) {
                mVideoPlayerCallbacks.onVideoError(String.valueOf(e.type));
            }
            try{
                mIsPrepared = false;
                boolean isError = false;
                switch (e.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        DebugLog.e(TAG, "VideoPlayer.TYPE_SOURCE: " + e.getSourceException().getMessage());
                        break;
                    case ExoPlaybackException.TYPE_RENDERER:
                        DebugLog.e(TAG, "VideoPlayer.TYPE_RENDERER: " + e.getRendererException().getMessage());
                        break;
                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        DebugLog.e(TAG, "VideoPlayer.TYPE_UNEXPECTED: " + e.getUnexpectedException().getMessage());
                        break;
                }
                if (mIsLive) {
                    mErrorCount++;
                    if (mErrorCount <= 10) {
                        final Handler handler = new Handler();
                        Timer retryWaitTimer = new Timer(true);
                        retryWaitTimer.schedule( new TimerTask(){
                            @Override
                            public void run() {
                                handler.post( new Runnable() {
                                    public void run() {
                                        initPlayer(mVideoURL, mStartTime, mAutoPlay, mIsLive, mEpisodeId, mExperimentName);
                                    }
                                });
                            }
                        }, 1000);
                    } else {
                        isError = true;
                    }
                } else {
                    isError = true;
                }
                if (isError) {
                    close();
                    if (mVideoPlayerCallbacks != null) {
                        mVideoPlayerCallbacks.onVideoError(String.valueOf(e.type));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onPositionDiscontinuity(reason);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onPlaybackParametersChanged(playbackParameters);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSeekProcessed() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() + tmpClassName ,new Object(){}.getClass().getEnclosingMethod().getName());
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onSeekProcessed();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private VideoListener exoPlayerVideoListener = new VideoListener() {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
                mVideoWidth = width;
                mVideoHeight = height;
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onVideoSizeChanged(mVideoWidth, mVideoHeight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onRenderedFirstFrame() {
        }
    };

    private AnalyticsListener analyticsListener = new AnalyticsListener() {
        @Override
        public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {}
        @Override
        public void onTimelineChanged(EventTime eventTime, int reason) {}
        @Override
        public void onPositionDiscontinuity(EventTime eventTime, int reason) {}
        @Override
        public void onSeekStarted(EventTime eventTime) {}
        @Override
        public void onSeekProcessed(EventTime eventTime) {}
        @Override
        public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {}
        @Override
        public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {}
        @Override
        public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {}
        @Override
        public void onLoadingChanged(EventTime eventTime, boolean isLoading) {}
        @Override
        public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {}
        @Override
        public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}
        @Override
        public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            if (mediaLoadData != null && mediaLoadData.trackFormat != null)  {
                mCurrentBitrate = mediaLoadData.trackFormat.bitrate;
            }
        }
        @Override
        public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {}
        @Override
        public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {}
        @Override
        public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {}
        @Override
        public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {}
        @Override
        public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {}
        @Override
        public void onMediaPeriodCreated(EventTime eventTime) {}
        @Override
        public void onMediaPeriodReleased(EventTime eventTime) {}
        @Override
        public void onReadingStarted(EventTime eventTime) {}
        @Override
        public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {}
        ///2.9.3用コメントアウト
//        @Override
//        public void onViewportSizeChange(EventTime eventTime, int width, int height) {}
//        @Override
//        public void onNetworkTypeChanged(EventTime eventTime, @Nullable NetworkInfo networkInfo) {}
        ///2.9.3用コメントアウト
        @Override
        public void onMetadata(EventTime eventTime, Metadata metadata) {}
        @Override
        public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {}
        @Override
        public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {}
        @Override
        public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {}
        @Override
        public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {}
        @Override
        public void onAudioSessionId(EventTime eventTime, int audioSessionId) {}
        @Override
        public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {}
        @Override
        public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {}
        @Override
        public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {}
        @Override
        public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {}
        @Override
        public void onDrmKeysLoaded(EventTime eventTime) {}
        @Override
        public void onDrmSessionManagerError(EventTime eventTime, Exception error) {}
        @Override
        public void onDrmKeysRestored(EventTime eventTime) {}
        @Override
        public void onDrmKeysRemoved(EventTime eventTime) {}
    };

    private DefaultDrmSessionEventListener defaultDrmSessionEventListener = new DefaultDrmSessionEventListener() {
        @Override
        public void onDrmKeysLoaded() {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDrmSessionManagerError(Exception e) {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
                close();
                if (mVideoPlayerCallbacks != null) {
                    mVideoPlayerCallbacks.onDRMError(e.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        @Override
        public void onDrmKeysRestored() {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDrmKeysRemoved() {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 使用するExoPlayerオブジェクトを返却する
     */
    public ExoPlayer getExoPlayer()
    {
        return mExoPlayer;
    }

    public View getView() {
        return mSurfaceView;
    }

    /**
     * 使用するDataSourceFactoryオブジェクトを返却する
     */
    public DataSource.Factory getDataSourceFactory() {return mDataSourceFactory;}

    public MediaSource getMediaSource() {return (MediaSource) mMediaSOurce;}

    public int getCurrentTime() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                return (int)mExoPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getProgramDateTime() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                int type = Util.inferContentType(Uri.parse(mVideoURL));
                if (type == C.TYPE_HLS) {
                    HlsManifest hlsManifest = (HlsManifest)mExoPlayer.getCurrentManifest();
                    if (hlsManifest.mediaPlaylist.hasProgramDateTime) {
                        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        //DebugLog.d(TAG, "VideoPlayer.ProgramDateTime: " + format.format(new Date(hlsManifest.mediaPlaylist.startTimeUs /1000 + mExoPlayer.getContentPosition())));
                        return (long)(hlsManifest.mediaPlaylist.startTimeUs /1000 + mExoPlayer.getContentPosition());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * BufferTimeを返却する
     */
    public int getBufferTime() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                return (int)mExoPlayer.getBufferedPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 動画尺を返却する
     */
    public int getDuration() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                return (int)mExoPlayer.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * プレイヤーViewの幅を返却する
     */
    public int getVideoWidth() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                if (mExoPlayer != null && mExoPlayer.getVideoFormat() != null) {
                    return mExoPlayer.getVideoFormat().width;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * プレイヤーViewの高さを返却する
     */
    public int getVideoHeight() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                if (mExoPlayer != null && mExoPlayer.getVideoFormat() != null) {
                    return mExoPlayer.getVideoFormat().height;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 現在のBitrateを返却する
     */
    public int getCurrentBitrate() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                return mCurrentBitrate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 再生準備完了状態であるかどうかを返却する
     */
    public boolean isPrepared() {
        return mIsPrepared;
    }

    public boolean isPlaying() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                return mExoPlayer.getPlayWhenReady();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 再生を開始する
     */
    public void play() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                mExoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 再生を一時停止する（再生再開ができる状態）
     */
    public void pause() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                mExoPlayer.setPlayWhenReady(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 再生位置をmsecに設定する
     */
    public boolean seek(int msec) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (mIsPrepared) {
                if (!mIsSeeking) {
                    mIsSeeking = true;
                    mExoPlayer.seekTo((long)msec);
                    if (mIsSeeking) {
                        if (mVideoPlayerCallbacks != null) {
                            mVideoPlayerCallbacks.onSeekStart();
                        }
                    }
                    return mIsSeeking;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean mIsSeekAndPlay;
    /**
     * 再生位置をmsecに設定する
     * シーク後に再生再開する
     */
    public void seekAndPlay(int msec) {
        if (seek(msec)) {
            mIsSeekAndPlay = true;
        }
    }

    private boolean mIsSeekAndPause;
    /**
     * 再生位置をmsecに設定する
     * シーク後に一時停止する
     */
    public void seekAndPause(int msec) {
        if (seek(msec)) {
            mIsSeekAndPause = true;
        }
    }

    /**
     * シーク中であるかどうかを返却する
     */
    public boolean isSeeking() {
        return mIsSeeking;
    }

    /**
     * Buffer中であるかどうかを返却する
     */
    public boolean isBuffering() {
        return mIsBuffering;
    }

    /**
     * プレイヤーの表示をvisibilityに設定する
     */
    public void setVisible(boolean visibility) {
        if (visibility) {
            getView().setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            getView().setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        }
    }

    /**
     * 再生を停止し動画をアンロードする
     */
    public void stopPlayer() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            mCurrentTime = getCurrentTime();
            mIsPrepared = false;
            if (mExoPlayer != null) {
                mExoPlayer.stop(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restorePlayer(boolean autoPlay) {
        restorePlayer(-1, autoPlay);
    }
    public void restorePlayer(int starttTime, boolean autoPlay) {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            if (starttTime < 0) {
                starttTime = mCurrentTime;
            }
            initPlayer(mVideoURL, starttTime, autoPlay, mIsLive, mEpisodeId, mExperimentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 動画の画角の幅を返却する
     */
    public int getWidth() {
        return mVideoWidth;
    }

    /**
     * 動画の画角の高さを返却する
     */
    public int getHeight() {
        return mVideoHeight;
    }

    /**
     * プレイヤーを破棄する
     */
    public void close() {
        DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
        try{
            mIsPrepared = false;
            if (mExoPlayer != null) {
                mExoPlayer.removeListener(exoPlayerEventListener);
                mExoPlayer.removeVideoListener(exoPlayerVideoListener);
                mExoPlayer.setVideoSurface(null);
                mExoPlayer.release();
            }
            if (mSurfaceView != null) {
                mSurfaceView.getHolder().removeCallback(surfaceHolderCallback);
            }
            mSurfaceView = null;
            mExoPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
                mExoPlayer.setVideoSurfaceHolder(holder);
                if (mViewCreatedCallBack != null) {
                    mViewCreatedCallBack.onViewCreated();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            DebugLog.d(TAG, new Object(){}.getClass().getEnclosingClass().getName() + " " + new Object(){}.getClass().getEnclosingMethod().getName());
            try{
                mExoPlayer.setVideoSurfaceHolder(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

}
