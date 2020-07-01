package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import jp.co.stream.jstplayersdk.VideoPlayer;

public class Ima_223 extends AppCompatActivity {

    private VideoPlayer mVideoPlayer;
    PlayerView playerView;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
    private ImaAdsLoader adsLoader;

    private Activity inActivity;
    private TextView txtTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ima_223);

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


            String strurl = inActivity.getString(R.string.url_ima_223);

            adsLoader = new ImaAdsLoader(inActivity, Uri.parse(inActivity.getString(R.string.url_ima_223_ads)));

            mVideoPlayer = new VideoPlayer(inActivity);
            mVideoPlayer.initPlayer(strurl);

            playerView = findViewById(R.id.player_view);
            playerView.setPlayer(mVideoPlayer.getExoPlayer());
            adsLoader.setPlayer(mVideoPlayer.getExoPlayer());

            // Create the AdsMediaSource using the AdsLoader and the MediaSource.
            AdsMediaSource adsMediaSource =
                    new AdsMediaSource(mVideoPlayer.getMediaSource(), mVideoPlayer.getDataSourceFactory(), adsLoader, playerView);

            mVideoPlayer.getExoPlayer().prepare(adsMediaSource);
            mVideoPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
