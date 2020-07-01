package jp.co.stream.jstresponse;

import android.app.Activity;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsManifest;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.jstresponse.config.BeaconUlrConfig;
import jp.co.stream.jstresponse.config.PlayerConfig;

/**
 * JStreamビーコン送信用クラス
 * @author J-Stream Inc
 * @version 0.0.1
 */

public class JSTRP {
    private static final String TAG = "JSTRP";

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

    private LinkedHashMap<String, String> mJstVideoBeaconParams;
    private LinkedHashMap <String, String> mJstAdBeaconParams;
    private LinkedHashMap <String, String> mJstTraceBeaconParams;

    private Timer mProgressCheckTimer;
    private long mVideoProgramDateTime = 0;

    private long mVideoDuration = 0;
    private boolean mIsPrepared = false;
    private long mVideoCurrentTime;
    //private int mVideoWidth = 0;
    //private int mVideoHeight = 0;
    private int mVideoPlayTime;
    private int mVideoStartTime= 0 ;
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


    private boolean mDebugSendBeaconFlg = true;
    private boolean mTestSendBeaconFlg = true;

    private BeaconUlrConfig mBeaconUlrConfig;
    /**
     * BeaconUlrConfig取得
     */
    public BeaconUlrConfig getBeaconUlrConfig() {
        return mBeaconUlrConfig;
    }
    /**
     * BeaconUlrConfig設定
     */
    public void setBeaconUlrConfig(BeaconUlrConfig b) {
        mBeaconUlrConfig = b;
    }

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


    private JSTRPEventCallBack mJSTRPEventCallBack;
    /**
     * JSTRPイベントコールバックインタフェース
     */
    public interface JSTRPEventCallBack {
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
     * JSTRPイベントコールバック設定
     */
    public void setJSTRPEventCallBack(JSTRPEventCallBack callbacks){
        mJSTRPEventCallBack = callbacks;
    }

    private JSTRPBeaconSendCallBack mJSTRPBeaconSendCallBack;
    /**
     * JSTRPイベントコールバックインタフェース
     */
    public interface JSTRPBeaconSendCallBack {
        /**
         * 送信
         */
        public void onSend(String url, int response);
    }
    /**
     * JSTRPビーコン送信イベントコールバック設定
     */
    public void setJSTRPBeaconSendCallbacks(JSTRPBeaconSendCallBack callbacks){
        mJSTRPBeaconSendCallBack = callbacks;
    }

    public JSTRP(Activity activity, ExoPlayer exoplayer)
    {
        this(activity);
        try
        {
            setUseExoPlayer(exoplayer);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

    public JSTRP(Activity activity) {

        try {
            mActivity = activity;

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
    public  void Start(ExoPlayer exoplayer)
    {
        setUseExoPlayer(exoplayer);
        Start();
    }

    public  void Start()
    {
        if(mBeaconUlrConfig == null)
        {
            throw new IllegalArgumentException("BeaconUlrConfig needs to be initialized");
        }

        if(mPlayerConfig == null)
        {
            throw new IllegalArgumentException("PlayerConfig needs to be initialized");
        }


        try {

            mDebugSendBeaconFlg = mPlayerConfig.debugSendBeaconFlg;
            mTestSendBeaconFlg = mPlayerConfig.testSendBeaconFlg;

            mVideoPlayTime = 0;

            initVideoBeacon();

            setProgressCheckTimer();

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
        if (this.mExoPlayer.get() != null) {

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

/*
                                        tmphlsManifestmediaPlayliststartTimeUs = hlsManifest.mediaPlaylist.startTimeUs;
                                        //SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        //DebugLog.d(TAG, "VideoPlayer.ProgramDateTime: " + format.format(new Date(hlsManifest.mediaPlaylist.startTimeUs /1000 + mExoPlayer.getContentPosition())));
                                        inExoProgramDateTime = (long)(tmphlsManifestmediaPlayliststartTimeUs /1000 + inExoCurrentPosition);
*/


                                        mVideoStartTime = (int)hlsManifest.mediaPlaylist.startTimeUs / 1000;
                                        inExoProgramDateTime = (long)(mVideoStartTime  + inExoCurrentPosition);
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
                                        //getLogTime("tmphlsManifestmediaPlayliststartTimeUs",tmphlsManifestmediaPlayliststartTimeUs/1000) +
                                        getLogTime("mStartTime",mVideoStartTime) +
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
                    if(!mIsPrepared)
                    {
                        mIsPrepared = true;
                        mSSAIProgramDateTimeOffset = inExoProgramDateTime;

                        if (mJSTRPEventCallBack != null) {
                            mJSTRPEventCallBack.onPrepared();
                        }
                    }

                    if (inExoPlayWhenReady) {
                        if (!mIsPrerollPlayed) {
                            mIsPrerollPlayed = true;

                        } else if (!mIsTrickPlayPlayed) {
                            mIsTrickPlayPlayed = true;
                        }

                        if (!mIsInitProcessing) {
                            mIsInitProcessing = true;

                            if (mJSTRPEventCallBack != null) {
                                mJSTRPEventCallBack.onInit();
                            }

                            //視聴初回ビーコン
                            sendJstVideoBeacon(mJstVideoBeaconParams);
                            if(mPlayerConfig.liveFlg)
                            {
                            }
                            else
                            {
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
                                        if (mJSTRPEventCallBack != null) {
                                            mJSTRPEventCallBack.onLiveTime5();
                                        }
                                    }

                                    if (mSimalVideoCurrentTime % 60 == 0) {
                                        if (mJSTRPEventCallBack != null) {
                                            mJSTRPEventCallBack.onLiveTime60();
                                        }

                                    }
                                }
                                mSimalCheckCurrentTime = mSimalVideoCurrentTime;
                            } else {
                                mDVRVideoCurrentTime = (int)inExoCurrentPosition/1000;

                                if (mDVRVideoCurrentTime > mDVRCheckCurrentTime) {

                                    //５秒おき
                                    if (mDVRVideoCurrentTime % 5 == 0) {
                                        if (mJSTRPEventCallBack != null) {
                                            mJSTRPEventCallBack.onTime5();
                                        }

                                    }

                                    if (mDVRVideoCurrentTime % 60 == 0) {
                                        if (mJSTRPEventCallBack != null) {
                                            mJSTRPEventCallBack.onTime60();
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

    /**
     * ビーコン送信（指定URL)
     */
    public void sendBeaconFromUrl(String beaconUrl,Map<String, String> params)
    {
        try
        {
            if(!mDebugSendBeaconFlg)
            {
                BeaconManager beaconManager = new BeaconManager();
                beaconManager.setBeaconSendCallbacks(new BeaconManager.BeaconSendCallBack() {
                    @Override
                    public void onSend(String url, int response) {
                        if (mJSTRPBeaconSendCallBack != null) {
                            mJSTRPBeaconSendCallBack.onSend(url, response);
                        }
                    }
                });

                beaconManager.sendBeacon(beaconUrl, params, mPlayerConfig.beaconTimeout, mPlayerConfig.beaconRetry);

            }
            else
            {
                DebugLog.d(TAG, "BeaconDebug JSTRP sendBeaconFromResources beaconManager.sendBeacon( url:["+ beaconUrl +"] param:[" + getBeaconParamString(params) + "])");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
        }
    }

    /**
     * JStream用視聴ビーコンテスト送信
     */
    public void sendJstVideoBeaconTest(Map<String, String> params)
    {
        try
        {
            sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_video_test,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * JStream用広告ビーコンテスト送信
     */
    public void sendJstAdBeaconTest(Map<String, String> params)
    {
        try
        {
            sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_ad_test,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * JStream用確認ビーコンテスト送信
     */
    public void sendJstTraceBeaconTest(Map<String, String> params)
    {
        try
        {
            sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_trace_test,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * JStream用視聴ビーコン送信
     */
    public void sendJstVideoBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_video,params);
            }
            else
            {
                sendJstVideoBeaconTest(params);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * JStream用広告ビーコン送信
     */
    public void sendJstAdBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_ad,params);
            }
            else
            {
                sendJstAdBeaconTest(params);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * JStream用確認ビーコン送信
     */
    public void sendJstTraceBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                sendBeaconFromUrl(mBeaconUlrConfig.beacon_jst_trace,params);
            }
            else
            {
                sendJstTraceBeaconTest(params);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

    private void initVideoBeacon()
    {
        try {
            mJstVideoBeaconParams = new LinkedHashMap <String, String>();

            mJstAdBeaconParams = new LinkedHashMap <String, String>();
            mJstAdBeaconParams.put("cmid", "");

            mJstTraceBeaconParams = new LinkedHashMap <String, String>();
            mJstTraceBeaconParams.put("adId", "");
            mJstTraceBeaconParams.put("vastAdId", "");
            mJstTraceBeaconParams.put("eventType", "");


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

}
