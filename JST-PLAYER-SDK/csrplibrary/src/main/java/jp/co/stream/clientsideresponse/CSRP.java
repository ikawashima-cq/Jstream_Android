package jp.co.stream.clientsideresponse;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.clientsideresponse.config.BeaconParamConfig;
import jp.co.stream.clientsideresponse.config.PlayerConfig;

/**
 * Client Side Report出力用クラス
 * @author J-Stream Inc
 * @version 0.0.1
 */
public class CSRP {
    private static final String TAG = "CSRP";

    private SSAIManager mSSAIManager;
    private long mSSAIProgramDateTimeOffset;
    private long mSSAICurrentTimeOffset;
    private int mSimalVideoCurrentTime;
    private int mSimalVideoCurrentTimeNoOffset;
    private int mSimalCheckCurrentTime;

    private int mDVRVideoCurrentTime;
    private int mDVRCheckCurrentTime;

    private Activity mActivity;

    private WeakReference<ExoPlayer> mExoPlayer;
    private Uri mPrefixUrl;
    private Uri mTrackingUrl;
    private Uri mManifestUrl;

    private Timer mProgressCheckTimer;
    private long mVideoProgramDateTime = 0;

    private long mVideoDuration = 0;
    private boolean mIsPrepared = false;
    private long mVideoCurrentTime;
    //private int mVideoWidth = 0;
    //private int mVideoHeight = 0;
    private int mVideoPlayTime;
    private int mViewStartRealTime = -1;
    private int mViewCheckRealTime = 0;

    private boolean mIsInitProcessing = false;
    private boolean mIsEndProcessing = false;
    private boolean mIsPrerollPlayed = false;
    private boolean mIsPostrollPlayed = false;
    private boolean mIsTrickPlayPlayed = true;
    private boolean mIsEnqueteAnswered = false;

    private int mSeekStartTime = -1;
    private int mSeekEndTime = -1;
    private int mPreviewViewedTime;
    private long mCheckCurrentTime = 0;

    private boolean mDebugSendBeaconFlg = true;
    private boolean mTestSendBeaconFlg = true;


    private PlayerConfig mPlayerConfig;
    /**
     * PlayerConfig取得
     */
    public PlayerConfig getPlayerConfig() {
        return mPlayerConfig;
    }
    /**
     * PlayerConfig設定
     */
    public void setPlayerConfig(PlayerConfig b) {
        mPlayerConfig = b;
    }

    private BeaconParamConfig mBeaconParamConfig;
    /**
     * BeaconParamConfig取得
     */
    public BeaconParamConfig getBeaconParamConfig() {
        return mBeaconParamConfig;
    }
    /**
     * BeaconParamConfig設定
     */
    public void setBeaconParamConfig(BeaconParamConfig b) {
        mBeaconParamConfig = b;
    }

    /**
     * PrefixUrl取得
     */
    public Uri PrefixUrl() {
        return mPrefixUrl;
    }

    /**
     * ビーコンUrl取得
     */
    public Uri TrackingUrl() {
        return mTrackingUrl;
    }

    /**
     * 視聴Url取得
     */
    public Uri ManifestUrl() {
        return mManifestUrl;
    }

    /**
     * 動画ソースの起点日時（秒）を返却する
     */
    public long VideoProgramDateTime() {return mVideoProgramDateTime;}
    /**
     * 動画尺を返却する
     */
    public long VideoDuration() {return mVideoDuration;}

    /**
     * 再生準備完了状態であるかどうかを返却する
     */
    public boolean IsPrepared() {return mIsPrepared;}

    /**
     * 現在の再生位置を返却する
     */
    public long VideoCurrentTime() {return  mVideoCurrentTime;}

    //public int VideoPlayTime() {return  mVideoPlayTime;}

    //public int ViewStartRealTime() {return  mViewStartRealTime;}

    /**
     * 再生開始後のUnixTimeを返却する
     */
    public int ViewCheckRealTime() {return  mViewCheckRealTime;}

    //public int SeekStartTime() {return  mSeekStartTime;}

    //public int SeekEndTime() {return  mSeekEndTime;}

    //public int PreviewViewedTime() {return  mPreviewViewedTime;}

    //public long CheckCurrentTime() {return  mCheckCurrentTime;}



    private CSRPTrackingEventsCallBack mCSRPTrackingEventsCallBack;
    /**
     * CSRPトラッキングイベントコールバックインタフェース
     */
    public interface CSRPTrackingEventsCallBack {
        /**
         * Imporession
         */
        public void onTrackingImpression(String adId,String vastAdId);
        /**
         * Start
         */
        public void onTrackingStart(String adId,String vastAdId);
        /**
         * FirstQuartile
         */
        public void onTrackingFirstQuartile(String adId,String vastAdId);
        /**
         * Midpoint
         */
        public void onTrackingMidpoint(String adId,String vastAdId);
        /**
         * ThirdQuartile
         */
        public void onTrackingThirdQuartile(String adId,String vastAdId);
        /**
         * Complete
         */
        public void onTrackingComplete(String adId,String vastAdId);
        /**
         * Error
         */
        public void onError(String adId,String vastAdId);
    }
    /**
     * CSRPトラッキングイベントコールバック設定
     */
    public void setCSRPTrackingEventCallBack(CSRPTrackingEventsCallBack callbacks){
        mCSRPTrackingEventsCallBack = callbacks;
    }


    private CSRPPrefixInfoCallBack mCSRPPrefixInfoCallBack;
    /**
     * CSRPPrefixInfo取得イベントコールバックインタフェース
     */
    public interface CSRPPrefixInfoCallBack {
        /**
         * 成功
         */
        public void onSucceed(Uri manifestUrl);
        /**
         * 失敗
         */
        public void onFailed(String message);
    }
    /**
     * CSRPPrefixInfo取得イベントコールバック設定
     */
    public void setCSRPPrefixInfoCallbacks(CSRPPrefixInfoCallBack callbacks){
        mCSRPPrefixInfoCallBack = callbacks;
    }


    private CSRPTrackingCallBack mCSRPTrackingCallBack;
    /**
     * CSRPトラッキング処理コールバックインタフェース
     */
    public interface CSRPTrackingCallBack {
        /**
         * 成功
         */
        public void onSucceed(String vastConfig);
        /**
         * 失敗
         */
        public void onFailed(String message);
    }
    /**
     * CSRPトラッキング処理コールバック設定
     */
    public void setCSRPTrackingCallBack(CSRPTrackingCallBack callbacks){
        mCSRPTrackingCallBack = callbacks;
    }

    private CSRPBeaconSendCallBack mCSRPBeaconSendCallBack;
    /**
     * CSRPビーコン送信イベントコールバックインタフェース
     */
    public interface CSRPBeaconSendCallBack {
        /**
         * 送信
         */
        public void onSend(String url,int response);
    }
    /**
     * CSRPビーコン送信イベントコールバック設定
     */
    public void setCSRPBeaconSendCallbacks(CSRPBeaconSendCallBack callbacks){
        mCSRPBeaconSendCallBack = callbacks;
    }

    private CSRPEventCallBack mCSRPEventCallBack;
    /**
     * CSRPイベントコールバックインタフェース
     */
    public interface CSRPEventCallBack {
        /**
         * 準備完了
         */
        public void onPrepared();
        /**
         * 初期化
         */
        public void onInit();
        /**
         * 5秒経過(Live)
         */
        public void onLiveTime5();
        /**
         * 60秒経過(Live)
         */
        public void onLiveTime60();
        /**
         * 5秒経過
         */
        public void onTime5();
        /**
         * 60秒経過
         */
        public void onTime60();
    }
    /**
     * CSRPイベントコールバック設定
     */
    public void setCSRPEventCallBack(CSRPEventCallBack callbacks){
        mCSRPEventCallBack = callbacks;
    }
    

    public CSRP(Activity activity, ExoPlayer exoplayer)
    {
        this(activity);
        try
        {
            setUseExoPlayer(exoplayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CSRP(Activity activity) {

        try {
            mActivity = activity;

            mSSAIProgramDateTimeOffset = -1;
            mSSAICurrentTimeOffset = -1;

            mSSAIManager = new SSAIManager(mActivity);

            mSSAIManager.setTrackingCallbacks(new SSAIManager.SSAIManagerTrackingCallBack() {
                @Override
                public void onSucceed(String vastConfig) {
                    if (mCSRPTrackingCallBack != null) {
                        mCSRPTrackingCallBack.onSucceed(vastConfig);
                    }
                }

                @Override
                public void onFailed(String message) {
                    if (mCSRPTrackingCallBack != null) {
                        mCSRPTrackingCallBack.onFailed(message);
                    }
                }
            });

            mSSAIManager.setTrackingEventCallbacks(new SSAIManager.SSAIManagerTrackingEventCallBack() {
                @Override
                public void onTrackingImpression(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingImpression(adId,vastAdId);
                    }
                }

                @Override
                public void onTrackingStart(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingStart(adId,vastAdId);
                    }

                }

                @Override
                public void onTrackingFirstQuartile(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingFirstQuartile(adId,vastAdId);
                    }

                }

                @Override
                public void onTrackingMidpoint(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingMidpoint(adId,vastAdId);
                    }

                }

                @Override
                public void onTrackingThirdQuartile(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingThirdQuartile(adId,vastAdId);
                    }

                }

                @Override
                public void onTrackingComplete(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onTrackingComplete(adId,vastAdId);
                    }

                }

                @Override
                public void onError(String adId, String vastAdId) {
                    if (mCSRPTrackingEventsCallBack != null) {
                        mCSRPTrackingEventsCallBack.onError(adId,vastAdId);
                    }

                }
            });

            mSSAIManager.setPrefixInfoCallbacks(new SSAIManager.SSAIManagerPrefixInfoCallBack() {
                @Override
                public void onSucceed(String manifestUrl, String trackingUrl) {
                    try {
                        mManifestUrl = Uri.parse(manifestUrl);
                        mTrackingUrl = Uri.parse(trackingUrl);
                        if (mCSRPPrefixInfoCallBack != null) {
                            mCSRPPrefixInfoCallBack.onSucceed(mManifestUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                }

                @Override
                public void onFailed(String message) {
                    try {
                        if (mCSRPPrefixInfoCallBack != null) {
                            mCSRPPrefixInfoCallBack.onFailed(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                    dispose();
                }
            });

            mSSAIManager.setBeaconSendCallbacks(new SSAIManager.SSAIManagerBeaconSendCallBack() {
                @Override
                public void onSend(String url, int response) {
                    if (mCSRPBeaconSendCallBack != null) {
                        mCSRPBeaconSendCallBack.onSend(url,response);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
    /**
     * 処理開始
     *   視聴URL取得
     *   ビーコンURL取得
     *   ビーコン送信タスク起動
     * が行われる
     */
    public  void Start(String prefixurl, ExoPlayer exoplayer)
    {
        setUseExoPlayer(exoplayer);
        Start(Uri.parse(prefixurl));
    }

    public  void Start(String prefixurl)
    {
        Start(Uri.parse(prefixurl));
    }
    public  void Start(Uri prefixurl)
    {

        if(mPlayerConfig == null)
        {
            throw new IllegalArgumentException("PlayerConfig needs to be initialized");
        }

        if(mBeaconParamConfig == null)
        {
            throw new IllegalArgumentException("BeaconParamConfig needs to be initialized");
        }

        try {
            mPrefixUrl = prefixurl;


            mDebugSendBeaconFlg = mPlayerConfig.debugSendBeaconFlg;
            mTestSendBeaconFlg = mPlayerConfig.testSendBeaconFlg;


            mSSAICurrentTimeOffset = mPlayerConfig.live_SSAI_LiveCurrent_Offset_MilliSecond;

            mSSAIManager.setTrackingEventCallbacks(ssaiTrackingEventCallBack);
            mVideoPlayTime = 0;

            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String advertisingId = "";
                    try {
                        AdvertisingIdClient.Info info =
                                AdvertisingIdClient.getAdvertisingIdInfo(mActivity.getApplicationContext());
                        if (!info.isLimitAdTrackingEnabled()) {
                            advertisingId = info.getId();
                        }
                    } catch (GooglePlayServicesNotAvailableException e) {
                    } catch (GooglePlayServicesRepairableException e) {
                    } catch (IOException e) {
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        
                    }
                    return advertisingId;
                }

                @Override
                protected void onPostExecute(String advertisingId) {
                    try {

                        DebugLog.d(TAG, "advertisingId: "+advertisingId);

                        mPlayerConfig.advertisingId = advertisingId;
                        mSSAIManager.setPlayerConfig(mPlayerConfig);
                        mBeaconParamConfig.program = mPlayerConfig.mediaId;
                        mSSAIManager.setBeaconParamConfig(mBeaconParamConfig);

                        mSSAIManager.getSSAIPrefixInfo(mPrefixUrl.toString(), mPlayerConfig.mediaId, mPlayerConfig.pageUrl, mPlayerConfig.advertisingId);
                        setProgressCheckTimer();

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        
                    }
                }
            };
            task.execute();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
    }



    /**
     * オブジェクトの破棄
     */
    public void dispose()
    {
        closeProgressCheckTimer();
        mSSAIManager.release();
        if (this.mExoPlayer.get() != null) {
            ExoPlayer var1 = (ExoPlayer)this.mExoPlayer.get();
        }
    }

    /**
     * 使用するExoPlayerを設定する
     * 番組を変更する際に用いる
     */
    public  void setUseExoPlayer(ExoPlayer exoplayer)
    {
        try
        {
            mExoPlayer = new WeakReference(exoplayer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
    }

    private void closeProgressCheckTimer() {
        if (mProgressCheckTimer != null) {
            mProgressCheckTimer.cancel();
            mProgressCheckTimer = null;
        }
    }
    private void setProgressCheckTimer() {
        closeProgressCheckTimer();
        mProgressCheckTimer = new Timer(true);
        mProgressCheckTimer.schedule(new progressCheckTimerTask(), 100, 1000);
    }

    /**
     * ビーコン送信用TimerTask処理
     */
    public class progressCheckTimerTask extends TimerTask {
        long inExoDuration=-1;
        long inExoCurrentPosition;
        boolean inExoPlayWhenReady;
        HlsManifest inExoHLSCurrentManifest;
        long inExoProgramDateTime;
        @Override
        public void run() {
            try
            {
                if (mExoPlayer == null) {
                    DebugLog.d(TAG, "ExoPlayer is null" );
                    return;
                }

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        try
                        {
                            ExoPlayer inExoPlayer = (ExoPlayer)mExoPlayer.get();

                            if (inExoPlayer != null) {
                                long tmphlsManifestmediaPlayliststartTimeUs = -1;
                                int currentRealTime = -1;
                                inExoDuration = inExoPlayer.getDuration();
                                inExoCurrentPosition = inExoPlayer.getContentPosition();
                                inExoPlayWhenReady = inExoPlayer.getPlayWhenReady();
                                inExoHLSCurrentManifest = (HlsManifest)inExoPlayer.getCurrentManifest();

                                if (mVideoDuration <= 0 || mVideoDuration != inExoDuration) {
                                    mVideoDuration = inExoDuration;
                                }
                                if (mVideoDuration > 0) {
                                    mVideoCurrentTime = inExoCurrentPosition;
                                    //mVideoWidth = mExoPlayer.getVideoFormat().width;
                                    //if (mConfig.live && !mConfig.dvr) {
                                    if (inExoPlayWhenReady) {
                                        mVideoPlayTime = mVideoPlayTime +100;
                                    }
                                    mVideoCurrentTime = mVideoPlayTime;
                                    //}

                                    HlsManifest hlsManifest = inExoHLSCurrentManifest;
                                    if (hlsManifest.mediaPlaylist.hasProgramDateTime) {

                                        tmphlsManifestmediaPlayliststartTimeUs = hlsManifest.mediaPlaylist.startTimeUs;
                                        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        //DebugLog.d(TAG, "VideoPlayer.ProgramDateTime: " + format.format(new Date(hlsManifest.mediaPlaylist.startTimeUs /1000 + mExoPlayer.getContentPosition())));
                                        inExoProgramDateTime = (long)(tmphlsManifestmediaPlayliststartTimeUs /1000 + inExoCurrentPosition);
                                    }

                                    //inExoProgramDateTime = getProgramDateTime();

                                    if (mVideoProgramDateTime <= 0 || mVideoProgramDateTime != inExoProgramDateTime) {
                                        mVideoProgramDateTime = inExoProgramDateTime;
                                    }
                                }
                                if (mViewStartRealTime <= 0) {
                                    mViewStartRealTime = (int)(System.currentTimeMillis() / 1000);
                                    mViewCheckRealTime = mViewStartRealTime;
                                }

                                //if (!Strings.isNullOrEmpty(mConfig.viewBeaconUrl) && mConfig.viewBeaconInterval > 0) {
                                if (mViewStartRealTime > 0) {
                                    currentRealTime = (int) (System.currentTimeMillis() / 1000);
                                    int progressRealTime = currentRealTime - mViewStartRealTime;
                                    mViewCheckRealTime = currentRealTime;
                                }

                                DebugLog.d(TAG, "ExoInfo" +
                                        getLogTime("inExoDuration",inExoDuration) +
                                        getLogTime("inExoCurrentPosition",inExoCurrentPosition) +
                                        getLogTime("mVideoDuration",mVideoDuration) +
                                        getLogTime("mVideoCurrentTime",mVideoCurrentTime) +
                                        getLogTime("mVideoPlayTime",mVideoPlayTime) +
                                        getLogTime("tmphlsManifestmediaPlayliststartTimeUs",tmphlsManifestmediaPlayliststartTimeUs/1000) +
                                        getLogTime("inExoProgramDateTime",inExoProgramDateTime) +
                                        getLogTime("mVideoProgramDateTime",mVideoProgramDateTime) +
                                        getLogTime("mViewStartRealTime",mViewStartRealTime) +
                                        getLogTime("currentRealTime",currentRealTime) +
                                        getLogTime("mViewCheckRealTime",mViewCheckRealTime) +
                                        getLogTime("mSSAICurrentTimeOffset",mSSAICurrentTimeOffset) +
                                        getLogTime("mSSAIProgramDateTimeOffset",mSSAIProgramDateTimeOffset) +
                                        getLogTime("mSimalVideoCurrentTime",mSimalVideoCurrentTime) +
                                        getLogTime("mSimalVideoCurrentTimeNoOffset",mSimalVideoCurrentTimeNoOffset) +
                                        getLogTime("mCheckCurrentTime",mCheckCurrentTime) +
                                        getLogTime("mSimalCheckCurrentTime",mSimalCheckCurrentTime) +
                                        "");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            
                        }
                    }
                });


                if(inExoDuration > 0)
                {
                    //逐次でやるならここしかない（ExoPlayerの外プロセスアクセス問題から）
                    if(!mIsPrepared)
                    {
                        mIsPrepared = true;
                        mSSAIProgramDateTimeOffset = inExoProgramDateTime;
                        if (mCSRPEventCallBack != null) {
                            mCSRPEventCallBack.onPrepared();
                        }                        
                    }

                    if (inExoPlayWhenReady) {
                        if (!mIsPrerollPlayed) {
                            mIsPrerollPlayed = true;

                        } else if (!mIsTrickPlayPlayed) {
                            mIsTrickPlayPlayed = true;
                            int random = (int)(Math.random() * 100); //0-99
                            DebugLog.d(TAG, "Trick!!: "+random);
                        }

                        if (!mIsInitProcessing) {
                            mIsInitProcessing = true;
                            if (mCSRPEventCallBack != null) {
                                mCSRPEventCallBack.onInit();
                            }

                        }
                        if (mSeekStartTime >= 0 && mSeekEndTime >= 0) {
                                mVideoCurrentTime = mSeekEndTime;
                                mCheckCurrentTime = mSeekEndTime;
                                mSeekStartTime = -1;
                                mSeekEndTime = -1;
                        } else {
                            int videoCurrentTimeSec = (int)(mVideoCurrentTime /1000);
                            int checkCurrentTimeSec = (int)(mCheckCurrentTime /1000);
                            int videoDurationSec = (int)(mVideoDuration /1000);

                            if (mPlayerConfig.liveFlg) {
                                mSimalVideoCurrentTimeNoOffset = (int)(inExoProgramDateTime - mSSAIProgramDateTimeOffset) /1000;
                                mSimalVideoCurrentTime = ((int)mSSAICurrentTimeOffset) /1000 + mSimalVideoCurrentTimeNoOffset;

                                if (mSimalVideoCurrentTime > mSimalCheckCurrentTime) {

                                    //５秒おき
                                    if (mSimalVideoCurrentTime % 5 == 0) {
                                        if (mCSRPEventCallBack != null) {
                                            mCSRPEventCallBack.onLiveTime5();
                                        }
                                        mSSAIManager.getSSAITracking(mSimalVideoCurrentTime);
                                    }

                                    mSSAIManager.checkSSAITracking(mSimalVideoCurrentTime);
                                    if (mSimalVideoCurrentTime % 60 == 0) {
                                        if (mCSRPEventCallBack != null) {
                                            mCSRPEventCallBack.onLiveTime60();
                                        }
                                    }
                                }
                                mSimalCheckCurrentTime = mSimalVideoCurrentTime;
                            } else {
                                mDVRVideoCurrentTime = (int)inExoCurrentPosition/1000;

                                if (mDVRVideoCurrentTime > mDVRCheckCurrentTime) {

                                    //５秒おき
                                    if (mDVRVideoCurrentTime % 5 == 0) {
                                        mSSAIManager.getSSAITracking(mDVRVideoCurrentTime);
                                        if (mCSRPEventCallBack != null) {
                                            mCSRPEventCallBack.onTime5();
                                        }
                                    }

                                    mSSAIManager.checkSSAITracking(mDVRVideoCurrentTime);
                                    if (mDVRVideoCurrentTime % 60 == 0) {
                                        if (mCSRPEventCallBack != null) {
                                            mCSRPEventCallBack.onTime60();
                                        }
                                    }
                                }
                                mDVRCheckCurrentTime = mDVRVideoCurrentTime;

                                if (mVideoCurrentTime > 0 && checkCurrentTimeSec != videoCurrentTimeSec) {
                                    if (videoCurrentTimeSec % 60 == 0) {
                                    }
                                    int currentParcentage = (int) (((float) mVideoCurrentTime / (float) mVideoDuration) * 100);
                                    int checkParcentage = (int) ((((float) mCheckCurrentTime) / (float) mVideoDuration) * 100);
                                    if (currentParcentage > checkParcentage) {
                                        DebugLog.d(TAG, "parcent:" + currentParcentage + "%");
                                        if (currentParcentage == 1 || currentParcentage == 25 || currentParcentage == 50 || currentParcentage == 75 || currentParcentage == 95) {
                                        }
                                    }
                                }
                                mCheckCurrentTime = mVideoCurrentTime;
                            }
                            mSeekStartTime = -1;
                            mSeekEndTime = -1;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
    }


    private SSAIManager.SSAIManagerTrackingEventCallBack ssaiTrackingEventCallBack = new SSAIManager.SSAIManagerTrackingEventCallBack() {
        @Override
        public void onTrackingImpression(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingImpression(adId,vastAdId);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
        @Override
        public void onTrackingStart(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingStart(adId,vastAdId);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
        @Override
        public void onTrackingFirstQuartile(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingFirstQuartile(adId,vastAdId);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
        @Override
        public void onTrackingMidpoint(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingMidpoint(adId,vastAdId);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
        @Override
        public void onTrackingThirdQuartile(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingThirdQuartile(adId,vastAdId);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
        }
        @Override
        public void onTrackingComplete(String adId,String vastAdId) {
            try {
                if (mCSRPTrackingEventsCallBack != null) {
                    mCSRPTrackingEventsCallBack.onTrackingComplete(adId,vastAdId);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }

        }
        @Override
        public void onError(String adId,String vastAdId) {}
    };

    private void sendBeaconFromUrl(String beaconUrl,Map<String, String> params)
    {
        try
        {
            if(!mDebugSendBeaconFlg)
            {
                BeaconManager beaconManager = new BeaconManager();
                beaconManager.setBeaconSendCallbacks(new BeaconManager.BeaconSendCallBack() {
                    @Override
                    public void onSend(String url, int response) {
                        if (mCSRPBeaconSendCallBack != null) {
                            mCSRPBeaconSendCallBack.onSend(url, response);
                        }
                    }
                });

                beaconManager.sendBeacon(beaconUrl, params, mPlayerConfig.beaconTimeout, mPlayerConfig.beaconRetry);


            }
            else
            {
                DebugLog.d(TAG, "BeaconDebug CSRP sendBeaconFromResources beaconManager.sendBeacon( url:["+ beaconUrl +"] param:[" + getBeaconParamString(params) + "])");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
    }


    private String getLogTime(String name, long time)
    {
        String ret = "";
        ret =  " " + name + ":[" + time + "ms(" + (time/1000) + "s)(" + time/1000/60 + "m)]";
        return ret;

    }

    private String getBeaconParamString(Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            if (params != null) {
                int count = 0;
                for (String key : params.keySet()) {
                    String spliter = "&";
                    sb.append(spliter).append(key).append("=").append(MyUtils.urlEncode(params.get(key)));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
