package jp.co.stream.vrresponse;

import android.app.Activity;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsManifest;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.stream.vrresponse.config.BeaconParamConfig;
import jp.co.stream.vrresponse.config.BeaconUlrConfig;
import jp.co.stream.vrresponse.config.PlayerConfig;

public class VRRP {
    private static final String TAG = "VRRP";

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

    private LinkedHashMap <String, String> mVRLiveVideoBeaconParams;
    private LinkedHashMap <String, String> mVRLiveAdBeaconParams;
    private LinkedHashMap <String, String> mVRVideoBeaconParams;
    private LinkedHashMap <String, String> mVRAdBeaconParams;

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

    public long VideoProgramDateTime() {return mVideoProgramDateTime;}
    public long VideoDuration() {return mVideoDuration;}
    public boolean IsPrepared() {return mIsPrepared;}
    public long VideoCurrentTime() {return  mVideoCurrentTime;}
    public int VideoPlayTime() {return  mVideoPlayTime;}
    public int ViewStartRealTime() {return  mViewStartRealTime;}
    public int ViewCheckRealTime() {return  mViewCheckRealTime;}
    public int SeekStartTime() {return  mSeekStartTime;}
    public int SeekEndTime() {return  mSeekEndTime;}
    public int PreviewViewedTime() {return  mPreviewViewedTime;}
    public long CheckCurrentTime() {return  mCheckCurrentTime;}

    private boolean mDebugSendBeaconFlg = true;
    private boolean mTestSendBeaconFlg = true;

    private BeaconUlrConfig mBeaconUlrConfig;
    public BeaconUlrConfig getBeaconUlrConfig() {
        return mBeaconUlrConfig;
    }
    public void setBeaconUlrConfig(BeaconUlrConfig b) {
        mBeaconUlrConfig = b;
    }

    private PlayerConfig mPlayerConfig;
    public PlayerConfig getPlayerConfig() {
        return mPlayerConfig;
    }
    public void setPlayerConfig(PlayerConfig b) {
        mPlayerConfig = b;
    }

    private BeaconParamConfig mBeaconParamConfig;
    public BeaconParamConfig getBeaconParamConfig() {
        return mBeaconParamConfig;
    }
    public void setBeaconParamConfig(BeaconParamConfig b) {
        mBeaconParamConfig = b;
    }

    private VRRPEventCallBack mVRRPEventCallBack;
    public interface VRRPEventCallBack {
        public void onPrepared();
        public void onInit();
        public void onSendVRLiveVideoInit();
        public void onSendVRVideoInit();
        public void onLiveTime5();
        public void onLiveTime60();
        public void onSendVRLiveVideo();
        public void onTime5();
        public void onTime60();
        public void onSendVRVideo();
    }
    public void setVRRPEventCallBack(VRRPEventCallBack callbacks){
        mVRRPEventCallBack = callbacks;
    }
    
    private VRRPBeaconSendCallBack mVRRPBeaconSendCallBack;
    public interface VRRPBeaconSendCallBack {
        public void onSend(String url,int response);
    }
    public void setVRRPBeaconSendCallbacks(VRRPBeaconSendCallBack callbacks){
        mVRRPBeaconSendCallBack = callbacks;
    }

    private ReplaceVRLiveVideoUrlParam mReplaceVRLiveVideoUrlParam;
    public interface ReplaceVRLiveVideoUrlParam{
        public String ReplaceParam(String Url);
    }
    public void setReplaceVRLiveVideoUrlParam(ReplaceVRLiveVideoUrlParam replaceVRLiveVideoUrlParam){
        mReplaceVRLiveVideoUrlParam = replaceVRLiveVideoUrlParam;
    }
    private  String DefaultReplaceVRLiveVideoUrlParam(String Url)
    {
        String tmpret = "";
        String[] tmpParam = Url.split("/");

        String rand = "00000000" + (int)(Math.random()*100000000);
        rand = rand.substring(rand.length() -8);

        StringBuffer tmpsb = new StringBuffer();
        for(int i = 0;i < tmpParam.length;i++)
        {
            if(i < tmpParam.length - 1)
            {
                tmpsb.append(tmpParam[i]);
                tmpsb.append("/");
            }
            else
            {
                tmpsb.append(String.format(tmpParam[i],rand));
            }
        }
        tmpret = String.format(tmpsb.toString());
        return tmpret;
    }

    private ReplaceVRLiveAdUrlParam mReplaceVRLiveAdUrlParam;
    public interface ReplaceVRLiveAdUrlParam{
        public String ReplaceParam(String Url);
    }
    public void setReplaceVRLiveAdUrlParam(ReplaceVRLiveAdUrlParam replaceVRLiveAdUrlParam){
        mReplaceVRLiveAdUrlParam = replaceVRLiveAdUrlParam;
    }
    private  String DefaultReplaceVRLiveAdUrlParam(String Url)
    {
        String tmpret = "";
        String[] tmpParam = Url.split("/");

        String rand = "00000000" + (int)(Math.random()*100000000);
        rand = rand.substring(rand.length() -8);

        StringBuffer tmpsb = new StringBuffer();
        for(int i = 0;i < tmpParam.length;i++)
        {
            if(i < tmpParam.length - 1)
            {
                tmpsb.append(tmpParam[i]);
                tmpsb.append("/");
            }
            else
            {
                tmpsb.append(String.format(tmpParam[i],rand));
            }
        }
        tmpret = String.format(tmpsb.toString());
        return tmpret;
    }

    private ReplaceVRVideoUrlParam mReplaceVRVideoUrlParam;
    public interface ReplaceVRVideoUrlParam{
        public String ReplaceParam(String Url);
    }
    public void setReplaceVRVideoUrlParam(ReplaceVRVideoUrlParam replaceVRVideoUrlParam){
        mReplaceVRVideoUrlParam = replaceVRVideoUrlParam;
    }
    private  String DefaultReplaceVRVideoUrlParam(String Url)
    {
        String tmpret = "";
        String[] tmpParam = Url.split("/");

        String rand = "00000000" + (int)(Math.random()*100000000);
        rand = rand.substring(rand.length() -8);

        StringBuffer tmpsb = new StringBuffer();
        for(int i = 0;i < tmpParam.length;i++)
        {
            if(i < tmpParam.length - 1)
            {
                tmpsb.append(tmpParam[i]);
                tmpsb.append("/");
            }
            else
            {
                tmpsb.append(String.format(tmpParam[i],rand));
            }
        }
        tmpret = String.format(tmpsb.toString());
        return tmpret;
    }

    private ReplaceVRAdUrlParam mReplaceVRAdUrlParam;
    public interface ReplaceVRAdUrlParam{
        public String ReplaceParam(String Url);
    }
    public void setReplaceVRAdUrlParam(ReplaceVRAdUrlParam replaceVRAdUrlParam){
        mReplaceVRAdUrlParam = replaceVRAdUrlParam;
    }
    private  String DefaultReplaceVRAdUrlParam(String Url)
    {
        String tmpret = "";
        String[] tmpParam = Url.split("/");

        String rand = "00000000" + (int)(Math.random()*100000000);
        rand = rand.substring(rand.length() -8);

        StringBuffer tmpsb = new StringBuffer();
        for(int i = 0;i < tmpParam.length;i++)
        {
            if(i < tmpParam.length - 1)
            {
                tmpsb.append(tmpParam[i]);
                tmpsb.append("/");
            }
            else
            {
                tmpsb.append(String.format(tmpParam[i],rand));
            }
        }
        tmpret = String.format(tmpsb.toString());
        return tmpret;
    }

    public VRRP(Activity activity, ExoPlayer exoplayer)
    {
        this(activity);
        try
        {
            setUseExoPlayer(exoplayer);
        } catch (Exception e) {
            e.printStackTrace();
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
        }
    }

    public VRRP(Activity activity) {

        try {
            mActivity = activity;

        } catch (Exception e) {
            e.printStackTrace();
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
        }
    }
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

        if(mBeaconParamConfig == null)
        {
            throw new IllegalArgumentException("BeaconParamConfig needs to be initialized");
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
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
        }
    }



    public void dispose()
    {
        closeProgressCheckTimer();
        if (this.mExoPlayer.get() != null) {
            ExoPlayer var1 = (ExoPlayer)this.mExoPlayer.get();
        }
    }
    public void release() {
        closeProgressCheckTimer();
        if (this.mExoPlayer.get() != null) {
            ExoPlayer var1 = (ExoPlayer)this.mExoPlayer.get();
        }
    }

    public  void setUseExoPlayer(ExoPlayer exoplayer)
    {
        try
        {
            mExoPlayer = new WeakReference(exoplayer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
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
                            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
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

                        if (mVRRPEventCallBack != null) {
                            mVRRPEventCallBack.onPrepared();
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

                            if (mVRRPEventCallBack != null) {
                                mVRRPEventCallBack.onInit();
                            }

                            //視聴初回ビーコン
                            if(mPlayerConfig.liveFlg)
                            {
                                if(mVRLiveVideoBeaconParams != null)
                                {
                                    mVRLiveVideoBeaconParams.put("vr_opt7", "start");
                                    mVRLiveVideoBeaconParams.put("vr_opt16", String.valueOf(mVideoProgramDateTime /1000));
                                    sendVRLiveVideoBeacon(mVRLiveVideoBeaconParams);
                                    if (mVRRPEventCallBack != null) {
                                        mVRRPEventCallBack.onSendVRLiveVideoInit();
                                    }
                                }
                            }
                            else
                            {
                                mVRVideoBeaconParams.put("vr_opt3", String.valueOf(mVideoStartTime));
                                mVRVideoBeaconParams.put("vr_opt4", String.valueOf((int) (mVideoDuration / 1000)));
                                mVRVideoBeaconParams.put("vr_opt7", "start");
                                sendVRVideoBeacon(mVRVideoBeaconParams);
                                if (mVRRPEventCallBack != null) {
                                    mVRRPEventCallBack.onSendVRVideoInit();
                                }
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
                                        if (mVRRPEventCallBack != null) {
                                            mVRRPEventCallBack.onLiveTime5();
                                        }
                                    }

                                    if (mSimalVideoCurrentTime % 60 == 0) {
                                        if (mVRRPEventCallBack != null) {
                                            mVRRPEventCallBack.onLiveTime60();
                                        }

                                        if(mVRLiveVideoBeaconParams != null)
                                        {
                                            //mVRLiveVideoBeaconParams.put("vr_opt3", String.valueOf((int)(System.currentTimeMillis() /1000)));
                                            mVRLiveVideoBeaconParams.put("vr_opt7", "loop");
                                            mVRLiveVideoBeaconParams.put("vr_opt16", String.valueOf(mVideoProgramDateTime /1000));
                                            sendVRLiveVideoBeacon(mVRLiveVideoBeaconParams);
                                            if (mVRRPEventCallBack != null) {
                                                mVRRPEventCallBack.onSendVRLiveVideo();
                                            }
                                        }
                                    }
                                }
                                mSimalCheckCurrentTime = mSimalVideoCurrentTime;
                            } else {
                                mDVRVideoCurrentTime = (int)inExoCurrentPosition/1000;

                                if (mDVRVideoCurrentTime > mDVRCheckCurrentTime) {

                                    //５秒おき
                                    if (mDVRVideoCurrentTime % 5 == 0) {
                                        if (mVRRPEventCallBack != null) {
                                            mVRRPEventCallBack.onTime5();
                                        }

                                    }

                                    if (mDVRVideoCurrentTime % 60 == 0) {
                                        if (mVRRPEventCallBack != null) {
                                            mVRRPEventCallBack.onTime60();
                                        }

                                        //レジュームは昨日削除
                                        //saveResumeTime(mConfig.mediaId, videoCurrentTimeSec, videoDurationSec, mConfig.rentalFlag, false);
                                        mVRVideoBeaconParams.put("vr_opt3", String.valueOf(videoCurrentTimeSec));
                                        mVRVideoBeaconParams.put("vr_opt7", "loop");
                                        sendVRVideoBeacon(mVRVideoBeaconParams);
                                        if (mVRRPEventCallBack != null) {
                                            mVRRPEventCallBack.onSendVRVideo();
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
                new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
            }
        }
    }

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
                        if (mVRRPBeaconSendCallBack != null) {
                            mVRRPBeaconSendCallBack.onSend(url, response);
                        }
                    }
                });

                beaconManager.sendBeacon(beaconUrl, params, mPlayerConfig.beaconTimeout, mPlayerConfig.beaconRetry);

            }
            else
            {
                DebugLog.d(TAG, "BeaconDebug VRRP sendBeaconFromResources beaconManager.sendBeacon( url:["+ beaconUrl +"] param:[" + getBeaconParamString(params) + "])");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
        }
    }


    public void sendVRLiveAdBeaconTest(Map<String, String> params)
    {
        try
        {
            String tmpurl = mBeaconUlrConfig.beacon_vr_live_ad_test;

            if(mReplaceVRLiveAdUrlParam != null)
            {
                tmpurl = mReplaceVRLiveAdUrlParam.ReplaceParam(tmpurl);
            }
            else
            {
                tmpurl = DefaultReplaceVRLiveAdUrlParam(tmpurl);
            }

            sendBeaconFromUrl(tmpurl,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRLiveVideoBeaconTest(Map<String, String> params)
    {
        try
        {
            String tmpurl = mBeaconUlrConfig.beacon_vr_live_video_test;

            if(mReplaceVRLiveVideoUrlParam != null)
            {
                tmpurl = mReplaceVRLiveVideoUrlParam.ReplaceParam(tmpurl);
            }
            else
            {
                tmpurl = DefaultReplaceVRLiveVideoUrlParam(tmpurl);
            }

            sendBeaconFromUrl(tmpurl,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRVideoBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                String tmpurl = mBeaconUlrConfig.beacon_vr_video;

                if(mReplaceVRVideoUrlParam != null)
                {
                    tmpurl = mReplaceVRVideoUrlParam.ReplaceParam(tmpurl);
                }
                else
                {
                    tmpurl = DefaultReplaceVRLiveVideoUrlParam(tmpurl);
                }

                sendBeaconFromUrl(tmpurl,params);
            }
            else
            {
                sendVRVideoBeaconTest(params);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRVideoBeaconTest(Map<String, String> params)
    {
        try
        {
            String tmpurl = mBeaconUlrConfig.beacon_vr_video_test;

            if(mReplaceVRVideoUrlParam != null)
            {
                tmpurl = mReplaceVRVideoUrlParam.ReplaceParam(tmpurl);
            }
            else
            {
                tmpurl = DefaultReplaceVRLiveVideoUrlParam(tmpurl);
            }

            sendBeaconFromUrl(tmpurl,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRAdBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                String tmpurl = mBeaconUlrConfig.beacon_vr_ad;

                if(mReplaceVRAdUrlParam != null)
                {
                    tmpurl = mReplaceVRAdUrlParam.ReplaceParam(tmpurl);
                }
                else
                {
                    tmpurl = DefaultReplaceVRLiveAdUrlParam(tmpurl);
                }

                sendBeaconFromUrl(tmpurl,params);
            }
            else
            {
                sendVRAdBeaconTest(params);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRAdBeaconTest(Map<String, String> params)
    {
        try
        {
            String tmpurl = mBeaconUlrConfig.beacon_vr_ad_test;

            if(mReplaceVRAdUrlParam != null)
            {
                tmpurl = mReplaceVRAdUrlParam.ReplaceParam(tmpurl);
            }
            else
            {
                tmpurl = DefaultReplaceVRLiveAdUrlParam(tmpurl);
            }

            sendBeaconFromUrl(tmpurl,params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void sendVRLiveAdBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                String tmpurl = mBeaconUlrConfig.beacon_vr_live_ad;

                if(mReplaceVRLiveAdUrlParam != null)
                {
                    tmpurl = mReplaceVRLiveAdUrlParam.ReplaceParam(tmpurl);
                }
                else
                {
                    tmpurl = DefaultReplaceVRLiveAdUrlParam(tmpurl);
                }

                sendBeaconFromUrl(tmpurl,params);
            }
            else
            {
                sendVRLiveAdBeaconTest(params);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendVRLiveVideoBeacon(Map<String, String> params)
    {
        try
        {
            if(!mTestSendBeaconFlg)
            {
                String tmpurl = mBeaconUlrConfig.beacon_vr_live_video;

                if(mReplaceVRLiveVideoUrlParam != null)
                {
                    tmpurl = mReplaceVRLiveVideoUrlParam.ReplaceParam(tmpurl);
                }
                else
                {
                    tmpurl = DefaultReplaceVRLiveVideoUrlParam(tmpurl);
                }

                sendBeaconFromUrl(tmpurl,params);
            }
            else
            {
                sendVRLiveVideoBeaconTest(params);
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

            mVRLiveVideoBeaconParams = new LinkedHashMap <String, String>();
            mVRLiveVideoBeaconParams.put("vr_tagid1", mBeaconParamConfig.vr_tagid1);
            mVRLiveVideoBeaconParams.put("vr_tagid2", mBeaconParamConfig.vr_tagid2);
            mVRLiveVideoBeaconParams.put("id1", mBeaconParamConfig.id1);
            mVRLiveVideoBeaconParams.put("url", mBeaconParamConfig.url);
            mVRLiveVideoBeaconParams.put("vr_opt1", "live-movie");
            mVRLiveVideoBeaconParams.put("vr_opt2", mBeaconParamConfig.vr_opt2);
            //mVRLiveVideoBeaconParams.put("vr_opt3", "");
            mVRLiveVideoBeaconParams.put("vr_opt5", mBeaconParamConfig.vr_opt15);
            mVRLiveVideoBeaconParams.put("vr_opt6", mBeaconParamConfig.vr_opt6);
            mVRLiveVideoBeaconParams.put("vr_opt7", "");
            mVRLiveVideoBeaconParams.put("vr_opt8", mBeaconParamConfig.vr_opt8);
            mVRLiveVideoBeaconParams.put("vr_opt10", mBeaconParamConfig.vr_opt10);
            mVRLiveVideoBeaconParams.put("vr_opt16", "");

            mVRLiveAdBeaconParams = new LinkedHashMap <String, String>();
            mVRLiveAdBeaconParams.put("vr_tagid1", mBeaconParamConfig.vr_tagid1);
            mVRLiveAdBeaconParams.put("vr_tagid2", mBeaconParamConfig.vr_tagid2);
            mVRLiveAdBeaconParams.put("id1", mBeaconParamConfig.id1);
            mVRLiveAdBeaconParams.put("url", mBeaconParamConfig.url);
            mVRLiveAdBeaconParams.put("vr_opt1", mBeaconParamConfig.vr_opt1);
            mVRLiveAdBeaconParams.put("vr_opt2", mBeaconParamConfig.vr_opt2);
            mVRLiveAdBeaconParams.put("vr_opt3", "");
            mVRLiveAdBeaconParams.put("vr_opt4", mBeaconParamConfig.vr_opt4);
            mVRLiveAdBeaconParams.put("vr_opt5", mBeaconParamConfig.vr_opt5);
            mVRLiveAdBeaconParams.put("vr_opt6", mBeaconParamConfig.vr_opt6);
            mVRLiveAdBeaconParams.put("vr_opt8", mBeaconParamConfig.vr_opt8);
            mVRLiveAdBeaconParams.put("vr_opt10", mBeaconParamConfig.vr_opt10);
            mVRLiveAdBeaconParams.put("vr_opt15", mBeaconParamConfig.vr_opt15);
            mVRLiveAdBeaconParams.put("vr_opt16", mBeaconParamConfig.vr_opt16);

            mVRVideoBeaconParams = new LinkedHashMap <String, String>();
            mVRVideoBeaconParams.put("vr_tagid1", mBeaconParamConfig.vr_tagid1);
            mVRVideoBeaconParams.put("vr_tagid2", mBeaconParamConfig.vr_tagid2);
            mVRVideoBeaconParams.put("id1", mBeaconParamConfig.id1);
            mVRVideoBeaconParams.put("url", mBeaconParamConfig.url);
            mVRVideoBeaconParams.put("vr_opt1", "movie");
            mVRVideoBeaconParams.put("vr_opt2", mBeaconParamConfig.vr_opt2);
            mVRVideoBeaconParams.put("vr_opt8", mBeaconParamConfig.vr_opt8);
            mVRVideoBeaconParams.put("vr_opt10", mBeaconParamConfig.vr_opt10);
            mVRVideoBeaconParams.put("vr_tid", mBeaconParamConfig.vr_tid);

            mVRAdBeaconParams = new LinkedHashMap <String, String>();
            mVRAdBeaconParams.put("vr_tagid1", mBeaconParamConfig.vr_tagid1);
            mVRAdBeaconParams.put("vr_tagid2", mBeaconParamConfig.vr_tagid2);
            mVRAdBeaconParams.put("id1", mBeaconParamConfig.id1);
            mVRAdBeaconParams.put("url", mBeaconParamConfig.url);
            mVRAdBeaconParams.put("vr_opt1", "ad");
            mVRAdBeaconParams.put("vr_opt2", mBeaconParamConfig.vr_opt2);
            mVRAdBeaconParams.put("vr_opt8", mBeaconParamConfig.vr_opt8);
            mVRVideoBeaconParams.put("vr_tid", mBeaconParamConfig.vr_tid);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            new BeaconManager(mTestSendBeaconFlg).sendErrorBeacon(e);
        }

    }

    private String getLogTime(String name, long time)
    {
        String ret = "";
        ret =  " " + name + ":[" + time + "ms(" + (time/1000) + "s)(" + time/1000/60 + "m)]";
        return ret;

    }

}
