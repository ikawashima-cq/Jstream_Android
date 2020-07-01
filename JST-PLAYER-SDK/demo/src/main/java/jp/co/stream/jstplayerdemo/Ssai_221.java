package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;

import java.util.LinkedHashMap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import jp.co.stream.clientsideresponse.CSRP;
import jp.co.stream.clientsideresponse.config.BeaconParamConfig;
import jp.co.stream.clientsideresponse.config.PlayerConfig;
import jp.co.stream.jstplayersdk.VideoPlayer;
import jp.co.stream.jstresponse.JSTRP;


public class Ssai_221 extends AppCompatActivity {

    private CSRP mCSRP;
    private JSTRP mJSTRP;
    private Activity inActivity;
    private  Uri mManifestUrl;
    private String prefixurl = "";

    private BeaconParamConfig bcfg;
    private PlayerConfig pcfg;


    private jp.co.stream.jstresponse.config.BeaconUlrConfig jstbucfg;
    private jp.co.stream.jstresponse.config.PlayerConfig jstpcfg;

    private VideoPlayer mVideoPlayer;
    private TextView txtTrace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssai_221);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Toolbar toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            inActivity = this;
            txtTrace = ((TextView) findViewById(R.id.txt221));


            PlayOnVideoPlayer();


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onSupportNavigateUp();
    }


    private void onClickMove(View view,Class target){
        try {
            Intent intent = new Intent(this,target);  //インテントの作成
            startActivity(intent);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void PlayOnVideoPlayer()
    {
        try{
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            inActivity = this;

            SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
            String strurl = sp.getString(inActivity.getString(R.string.preferences_url_ssai_221_session), "");	//	キー、デフォールト値
            if(strurl.isEmpty())
            {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_ssai_221 default");
                strurl = inActivity.getString(R.string.url_ssai_221);
            }

            String testbeaconurl = inActivity.getString(R.string.url_ssai_221_testbeacon);

            mCSRP = new CSRP(this);



            pcfg = new PlayerConfig(
                    false,
                    true,
                    true,
                    40000,
                    180,
                    180,
                    "8133810001",
                    "",
                    60,
                    5
            );
            mCSRP.setPlayerConfig(pcfg);


            bcfg = new BeaconParamConfig(
                    "1050014",
                    "m",
                    "35"
            );
            mCSRP.setBeaconParamConfig(bcfg);

            mCSRP.setCSRPTrackingCallBack(CSRPTrackingCallBack);
            mCSRP.setCSRPTrackingEventCallBack(CSRPTrackingEventCallBack);
            mCSRP.setCSRPBeaconSendCallbacks(CSRPBeaconSendCallBack);
            mCSRP.setCSRPEventCallBack(CSRPEventCallBack);

            mCSRP.setCSRPPrefixInfoCallbacks(new CSRP.CSRPPrefixInfoCallBack() {
                final String tmpClassName = "CSRPPrefixInfoCallBack";

                @Override
                public void onSucceed(Uri manifestUrl) {
                    DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                    
                    Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                    
                    mManifestUrl = manifestUrl;
                    int type;

                    Uri hlsVideoUri = mManifestUrl;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try
                            {
                                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

                                mVideoPlayer = new VideoPlayer(inActivity);
                                mVideoPlayer.initPlayer(mManifestUrl.toString());

                                PlayerView playerView = findViewById(R.id.player_view);
                                playerView.setPlayer(mVideoPlayer.getExoPlayer());

                                mCSRP.setUseExoPlayer(mVideoPlayer.getExoPlayer());


                                mJSTRP.setUseExoPlayer(mVideoPlayer.getExoPlayer());
                                mJSTRP.Start();

                                mVideoPlayer.play();


                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailed(String message) {
                    DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                    Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                }
            });



            mJSTRP = new JSTRP(this);

            jstbucfg = new jp.co.stream.jstresponse.config.BeaconUlrConfig(
                    testbeaconurl +"?beacontype=jst_video",
                    testbeaconurl +"?beacontype=jst_video_test",
                    testbeaconurl +"?beacontype=jst_ad",
                    testbeaconurl +"?beacontype=jst_ad_test",
                    testbeaconurl +"?beacontype=jst_trace",
                    testbeaconurl +"?beacontype=jst_trace_test"
            );
            mJSTRP.setBeaconUlrConfig(jstbucfg);

            jstpcfg = new jp.co.stream.jstresponse.config.PlayerConfig(
                    false,
                    true,
                    false,
                    60,
                    5
            );
            mJSTRP.setPlayerConfig(jstpcfg);

            mJSTRP.setJSTRPEventCallBack(JSTRPEventCallBack);
            mJSTRP.setJSTRPBeaconSendCallbacks(JSTRPBeaconSendCallBack);


            mCSRP.Start(strurl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  CSRP.CSRPEventCallBack CSRPEventCallBack = new CSRP.CSRPEventCallBack() {
        final String tmpClassName = "CSRPEventCallBack";
        @Override
        public void onPrepared() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInit() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onLiveTime5() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLiveTime60() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        @Override
        public void onTime5() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTime60() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    private CSRP.CSRPTrackingEventsCallBack CSRPTrackingEventCallBack = new CSRP.CSRPTrackingEventsCallBack() {
        final String tmpClassName = "CSRPTrackingEventsCallBack";
        @Override
        public void onTrackingImpression(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                LinkedHashMap<String, String> mJstTraceBeaconParams = new LinkedHashMap<String, String>();
                mJstTraceBeaconParams.put("adId", adId);
                mJstTraceBeaconParams.put("vastAdId", vastAdId);
                mJstTraceBeaconParams.put("eventType", "impression");
                mJSTRP.sendJstTraceBeacon(mJstTraceBeaconParams);

                LinkedHashMap<String, String> mJstAdBeaconParams = new LinkedHashMap<String, String>();
                mJstAdBeaconParams.put("cmid", vastAdId);
                mJSTRP.sendJstAdBeacon(mJstAdBeaconParams);

                LinkedHashMap<String, String> mVRLiveAdBeaconParams = new LinkedHashMap<String, String>();
                mVRLiveAdBeaconParams.put("vr_opt3", vastAdId);
                mVRLiveAdBeaconParams.put("vr_opt4", "mid");
                mVRLiveAdBeaconParams.put("vr_opt5", "0");


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onTrackingStart(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        public void onTrackingFirstQuartile(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onTrackingMidpoint(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onTrackingThirdQuartile(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onTrackingComplete(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(String adId, String vastAdId) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

                LinkedHashMap<String, String> mJstTraceBeaconParams = new LinkedHashMap<String, String>();
                mJstTraceBeaconParams.put("adId", adId);
                mJstTraceBeaconParams.put("vastAdId", vastAdId);
                mJstTraceBeaconParams.put("eventType", "complete");
                mJSTRP.sendJstTraceBeacon(mJstTraceBeaconParams);



            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private CSRP.CSRPTrackingCallBack CSRPTrackingCallBack = new CSRP.CSRPTrackingCallBack() {
        final String tmpClassName = "CSRPTrackingCallBack";
        @Override
        public void onSucceed(String vastConfig) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailed(String message) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private CSRP.CSRPBeaconSendCallBack CSRPBeaconSendCallBack = new CSRP.CSRPBeaconSendCallBack() {
        final String tmpClassName = "CSRPBeaconSendCallBack";
        @Override
        public void onSend(String url,int response) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName() + " " + url + " " + response);

                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName() + " " + url + " " + response);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private JSTRP.JSTRPEventCallBack JSTRPEventCallBack = new JSTRP.JSTRPEventCallBack() {
        final String tmpClassName = "JSTRPEventCallBack";
        @Override
        public void onPrepared() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInit() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLiveTime5() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onLiveTime60() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTime5() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTime60() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private JSTRP.JSTRPBeaconSendCallBack JSTRPBeaconSendCallBack = new JSTRP.JSTRPBeaconSendCallBack() {
        final String tmpClassName = "JSTRPBeaconSendCallBack";
        @Override
        public void onSend(String url,int response) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName() + " " + url + " " + response);

                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName() + " " + url + " " + response);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            if (mVideoPlayer != null) {
                mVideoPlayer.close();
            }
            if (mCSRP != null) {
                mCSRP.dispose();
            }
            if (mJSTRP != null) {
                mJSTRP.dispose();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
