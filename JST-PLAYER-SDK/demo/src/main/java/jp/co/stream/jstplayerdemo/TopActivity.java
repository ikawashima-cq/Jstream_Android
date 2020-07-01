package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class TopActivity extends AppCompatActivity {

    private Activity inActivity;
    private ProgressBar progressBar;

    private int percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            progressBar = findViewById(R.id.progressbar);
            percent = 0; //初期状態を81%にします。

            progressBar.setMax(100);
            progressBar.setProgress(percent);
            //progressBar.setProgress(percent,true);    //24以上
            //progressBar.setMin(0);    //26以上

            inActivity = this;

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            new Thread(new Runnable() {
                public void run() {
                    while(percent < 100)
                    {
                        try {
                            percent++;
                            progressBar.setProgress(percent);
                            //Thread.sleep(50); //3000ミリ秒Sleepする
                            Thread.sleep(0); //3000ミリ秒Sleepする
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //メニューに遷移
                    Intent intent = new Intent(inActivity, Menu_200.class);  //インテントの作成
                    startActivity(intent);
                }
            }).start();


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
