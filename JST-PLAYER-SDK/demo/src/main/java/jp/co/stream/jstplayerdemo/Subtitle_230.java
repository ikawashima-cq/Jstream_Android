package jp.co.stream.jstplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Subtitle_230 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitle_230);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Toolbar toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            findViewById(R.id.btn231).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Webvtt_231.class);
                }
            });

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


    private void onClickMove(View view, Class target){
        try {
            Intent intent = new Intent(this,target);  //インテントの作成
            startActivity(intent);
            //finish();
        }catch (Exception e){
            System.out.println(e);
        }
    }


}
