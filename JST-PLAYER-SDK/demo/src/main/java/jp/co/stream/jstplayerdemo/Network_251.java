package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import jp.co.stream.jstplayersdk.VideoPlayer;

public class Network_251 extends AppCompatActivity {

    private VideoPlayer mVideoPlayer;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");

    private Activity inActivity;
    private TextView txtTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_251);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Toolbar toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            inActivity = this;

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

            String strurl = inActivity.getString(R.string.url_network_251);


            mVideoPlayer = new VideoPlayer(this);
            mVideoPlayer.initPlayer(strurl);
            mVideoPlayer.setCallbacks(videoPlayerCallBacks);

            PlayerView playerView = findViewById(R.id.player_view);
            playerView.setPlayer(mVideoPlayer.getExoPlayer());
            mVideoPlayer.play();



        } catch (Exception e) {
            e.printStackTrace();
        }
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
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPlaying() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
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
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVideoError(String message) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDRMError(String message) {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onSeekCompleted() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onBufferingStart() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onBufferingCompleted() {
            try{
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                Util.WriteTrace(inActivity,txtTrace,tmpClassName + " " +  new Object(){}.getClass().getEnclosingMethod().getName());
                
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
    public void onDestroy() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
            if (mVideoPlayer != null) {
                mVideoPlayer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
