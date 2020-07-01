package jp.co.stream.jstplayerdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SamplePlayerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private static final int REQUEST_CODE_EXTERNAL = 100;

    public ViewPager mViewPager;

    protected int mPreviousPos = 0;

    private Activity inActivity;
    private TextView txtTrace;

    private Toolbar toolbar;

    private List<SamplePlayerModel> mData = new ArrayList<>();

    protected SamplePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_plater);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            /*
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            */
            Intent i = getIntent();
            mPreviousPos = i.getIntExtra("index",0);

            inActivity = this;

            mViewPager = findViewById(R.id.SamplePlayerPager);


            ActionBar mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setHomeButtonEnabled(true);
            }

            buildDemoDataSet();


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

    private void buildDemoDataSet() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);

            String url_dvr = sp.getString(inActivity.getString(R.string.preferences_url_dvr_212), "");	//	キー、デフォールト値
            if(url_dvr.isEmpty())
            {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_dvr default");
                url_dvr = inActivity.getString(R.string.url_dvr_212);
            }

            String url_live = sp.getString(inActivity.getString(R.string.preferences_url_dvr_212), "");	//	キー、デフォールト値
            if(url_live.isEmpty())
            {
                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_live default");
                url_live = inActivity.getString(R.string.url_live_211);
            }

            String url_vod = inActivity.getString(R.string.url_vod_213);

            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_live:" + url_live);
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_dvr:" + url_dvr);
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,"url_vod:" + url_vod);

            mData = new ArrayList<>();
            mData.add(new SamplePlayerModel("LIVE",url_live,true,true));
            mData.add(new SamplePlayerModel("DVR",url_dvr,true,false));
            mData.add(new SamplePlayerModel("VOD",url_vod,false,false));

            mAdapter = new SamplePagerAdapter(getSupportFragmentManager(),mData);

            mViewPager.addOnPageChangeListener(this);
            mViewPager.setAdapter(mAdapter);

            int tmpStartPos = mPreviousPos + (SamplePagerAdapter.LOOPS_COUNT/2);

            mViewPager.setCurrentItem(tmpStartPos,false);
            updateTheToolbar(tmpStartPos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateTheToolbar(int position)
    {
        if(toolbar != null && position <= mData.size() ){
            SamplePlayerModel model = mData.get(position);
            toolbar.setTitle(model.getName());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        try {
            //((SamplePlayerFragment)mAdapter.getItem(mPreviousPos)).imHiddenNow();
            //((SamplePlayerFragment)mAdapter.getItem(position)).imVisibleNow();

        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreviousPos = position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
