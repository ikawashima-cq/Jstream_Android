package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import jp.co.stream.jstplayersdk.VideoPlayer;
import jp.co.stream.jstplayersdk.util.TrackSelectionDialog;

public class Webvtt_231 extends AppCompatActivity  implements View.OnClickListener {

    private VideoPlayer mVideoPlayer;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");

    private Activity inActivity;
    private TextView txtTrace;

    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean isShowingTrackSelectionDialog;
    private Button selectTracksButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webvtt_231);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Toolbar toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            DefaultTrackSelector.ParametersBuilder builder =
                    new DefaultTrackSelector.ParametersBuilder(/* context= */ this);
            trackSelectorParameters = builder.build();

            TrackSelection.Factory trackSelectionFactory;
            trackSelectionFactory = new AdaptiveTrackSelection.Factory();

            trackSelector = new DefaultTrackSelector(/* context= */ this, trackSelectionFactory);
            //trackSelector.setParameters(trackSelectorParameters);
            trackSelector.setParameters(
                    trackSelector
                            .buildUponParameters()
                            .setPreferredTextLanguage("jpn"));

            //trackSelector.getCurrentMappedTrackInfo().getTrackGroups(0);



            inActivity = this;
            txtTrace = ((TextView) findViewById(R.id.txt231));

            selectTracksButton = findViewById(R.id.select_tracks_button);
            selectTracksButton.setOnClickListener(this);
            selectTracksButton.setEnabled(true);

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

            String strurl = inActivity.getString(R.string.url_webvtt_231);;

            mVideoPlayer = new VideoPlayer(this,trackSelector);
            mVideoPlayer.initPlayer(strurl);
            mVideoPlayer.setCallbacks(videoPlayerCallBacks);

            PlayerView playerView = findViewById(R.id.player_view);
            playerView.setPlayer(mVideoPlayer.getExoPlayer());
            mVideoPlayer.play();




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == selectTracksButton
                && !isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {

            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
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
