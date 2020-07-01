package jp.co.stream.jstplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.r0adkll.slidr.Slidr;

public class Player_210 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_210);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            Toolbar toolbar = findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            Slidr.attach(this);


            findViewById(R.id.btn211).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view, SamplePlayerActivity.class,1);
                }
            });

            findViewById(R.id.btn212).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view, SamplePlayerActivity.class,2);
                }
            });

            findViewById(R.id.btn213).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view, SamplePlayerActivity.class,3);
                }
            });
            findViewById(R.id.btn214).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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


    private void onClickMove(View view, Class target, int index){
        try {
            Intent intent = new Intent(this,target);  //インテントの作成
            intent.putExtra("index", index);
            startActivity(intent);
            //finish();
        }catch (Exception e){
            System.out.println(e);
        }
    }


}
