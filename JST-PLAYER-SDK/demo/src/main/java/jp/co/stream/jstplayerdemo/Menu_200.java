package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

public class Menu_200 extends AppCompatActivity {

    private Activity inActivity;
    private boolean inServer;
    private int inServerID;
    private Util.SERVER_STATUS inServerStatus;

    private ServerRunCallBack mServerRunCallBack;
    public interface ServerRunCallBack {
        public void onSucceed(String run_url_type,String message);
        public void onFailed(String run_url_type,String message);
    }
    public void setServerRunCallBack(ServerRunCallBack callbacks){
        mServerRunCallBack = callbacks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_200);

        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            inActivity = this;

            Toolbar toolbar = findViewById(R.id.tool_bar);

            /*
            機能選択は戻るをさせない
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            */

            findViewById(R.id.btn210).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Player_210.class);
                }
            });

            findViewById(R.id.btn220).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Ad_220.class);
                }
            });

            findViewById(R.id.btn230).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Subtitle_230.class);
                }
            });

            findViewById(R.id.btn240).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Multiplexing_240.class);
                }
            });

            findViewById(R.id.btn250).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickMove(view,Other_250.class);
                }
            });

            findViewById(R.id.btnServerStart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{

                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.starting);

                        findViewById(R.id.btnServerStart).setEnabled(false);
                        findViewById(R.id.btnServerStop).setEnabled(false);
                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.starting.name());
                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.starting);

                        String tmpstartserverurl = inActivity.getString(R.string.url_server_start).replace(inActivity.getString(R.string.url_server_replace_string),String.valueOf(inServerID));
                        ServerRun(Util.RUN_URL_TYPE.Server_Start.getValue(), tmpstartserverurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            findViewById(R.id.btnServerStop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.stopping);

                        findViewById(R.id.btnServerStart).setEnabled(false);
                        findViewById(R.id.btnServerStop).setEnabled(false);
                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.stopping.name());
                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.stopping);

                        String tmpstopserverurl = inActivity.getString(R.string.url_server_dvr_stop).replace(inActivity.getString(R.string.url_server_replace_string),String.valueOf(inServerID));
                        ServerRun(Util.RUN_URL_TYPE.Dvr_Stop.getValue(), tmpstopserverurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
            inServer = true;
            findViewById(R.id.btnServerStart).setEnabled(inServer);
            findViewById(R.id.btnServerStop).setEnabled(!inServer);

            inServerID = sp.getInt(inActivity.getString(R.string.preferences_server_id), -1);	//	キー、デフォールト値
            if(inServerID < 0)
            {
                //SharedPreferences sp = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                inServerID =  Math.abs(UUID.randomUUID().hashCode());
                editor.putInt(inActivity.getString(R.string.preferences_server_id), inServerID);	//	キー、値
                editor.commit();
            }
            ((TextView)findViewById(R.id.txtServerID)).setText(String.valueOf(inServerID));

            setServerRunCallBack(new ServerRunCallBack() {
                @Override
                public void onSucceed(String run_url_type,String message) {

                    inActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            try{
                                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " " + message);

                                switch(Util.RUN_URL_TYPE.getType(run_url_type))
                                {
                                    case Server_Start:
                                        JSONObject urls = new JSONObject(message);

                                        String url = urls.get("url").toString();
                                        String url_ssai_session = urls.get("ssai_csr_url").toString();
                                        String url_ssai_master = urls.get("ssai_url").toString();

                                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " url:" + url);
                                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " url_ssai_session:" + url_ssai_session);
                                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " url_ssai_master:" + url_ssai_master);

                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(inActivity.getString(R.string.preferences_url_dvr_212), url);	//	キー、値
                                        editor.putString(inActivity.getString(R.string.preferences_url_ssai_221_session), url_ssai_session);	//	キー、値
                                        editor.putString(inActivity.getString(R.string.preferences_url_ssai_221_master), url_ssai_master);	//	キー、値
                                        editor.commit();

                                        String tmpstartdvrurl = inActivity.getString(R.string.url_server_dvr_start).replace(inActivity.getString(R.string.url_server_replace_string),String.valueOf(inServerID));
                                        ServerRun(Util.RUN_URL_TYPE.Dvr_Start.getValue(), tmpstartdvrurl);
                                        break;
                                    case Dvr_Start:
                                        inServer = !inServer;
                                        findViewById(R.id.btnServerStart).setEnabled(inServer);
                                        findViewById(R.id.btnServerStop).setEnabled(!inServer);
                                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.running.name());
                                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.running);
                                        break;
                                    case Dvr_Stop:
                                        String tmpstopserverurl = inActivity.getString(R.string.url_server_stop).replace(inActivity.getString(R.string.url_server_replace_string),String.valueOf(inServerID));
                                        ServerRun(Util.RUN_URL_TYPE.Server_Stop.getValue(), tmpstopserverurl);
                                        break;
                                    case Server_Stop:
                                        inServer = !inServer;
                                        findViewById(R.id.btnServerStart).setEnabled(inServer);
                                        findViewById(R.id.btnServerStop).setEnabled(!inServer);
                                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.stop.name());
                                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.stop);
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailed(String run_url_type,String message) {
                    inActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            try{
                                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " " + message);

                                switch(Util.RUN_URL_TYPE.getType(run_url_type))
                                {
                                    case Dvr_Stop:
                                        String tmpstopserverurl = inActivity.getString(R.string.url_server_stop).replace(inActivity.getString(R.string.url_server_replace_string),String.valueOf(inServerID));
                                        ServerRun(Util.RUN_URL_TYPE.Server_Stop.getValue(), tmpstopserverurl);
                                        break;
                                    case Server_Stop:
                                        inServer = !inServer;
                                        findViewById(R.id.btnServerStart).setEnabled(inServer);
                                        findViewById(R.id.btnServerStop).setEnabled(!inServer);
                                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.stop.name());
                                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.stop);
                                        break;
                                    default:
                                        findViewById(R.id.btnServerStart).setEnabled(inServer);
                                        findViewById(R.id.btnServerStop).setEnabled(!inServer);
                                        ((TextView)findViewById(R.id.txtServerStatus)).setText(Util.SERVER_STATUS.error.name());
                                        Util.SetServerStatus(inActivity,Util.SERVER_STATUS.error);
                                        break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            inServerStatus = Util.GetServerStatus(inActivity);
            if(inServerStatus.getValue() <= -99)
            {
                ((Button)findViewById(R.id.btnServerStart)).performClick();
            }
            else
            {
                inServer = inServerStatus.getValue() < 0;
                findViewById(R.id.btnServerStart).setEnabled(inServer);
                findViewById(R.id.btnServerStop).setEnabled(!inServer);
                ((TextView)findViewById(R.id.txtServerStatus)).setText(inServerStatus.name());
            }


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
            //finish();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void ServerRun(String run_url_type,String requestUrlString)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    DataOutputStream outputStream = null;
                    BufferedReader bufferedReader = null;

                    try {
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
                        DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,run_url_type + " " + requestUrlString);

                        URL requestUrl = new URL(requestUrlString);

                        connection = (HttpURLConnection) requestUrl.openConnection();

                        //connection.setDoInput(true);
                        //connection.setDoOutput(true);
                        //connection.setUseCaches(false);
                        //connection.setChunkedStreamingMode(0);

                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.addRequestProperty("Accept-Language", Locale.getDefault().toString());
                        //connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        //connection.setRequestProperty("Content-Length", String.valueOf(iContentsLength));

                        int iResponseCode = connection.getResponseCode();

                        if (iResponseCode == HttpURLConnection.HTTP_OK) {

                            try {
                                StringBuilder resultBuilder = new StringBuilder();
                                String line = "";
                                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while ((line = bufferedReader.readLine()) != null) {
                                    resultBuilder.append(String.format("%s%s", line, "\r\n"));
                                }
                                String result = resultBuilder.toString();

                                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,result);

                                if (mServerRunCallBack != null) {
                                    mServerRunCallBack.onSucceed(run_url_type,result);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (mServerRunCallBack != null) {
                                    mServerRunCallBack.onFailed(run_url_type,"http_ok after error " + e.toString());
                                }
                                new BeaconManager().sendErrorBeacon(inActivity.getString(R.string.url_debug_log),e);
                            }
                        } else {

                            try {
                                StringBuilder resultBuilder = new StringBuilder();
                                String line = "";
                                bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                                while ((line = bufferedReader.readLine()) != null) {
                                    resultBuilder.append(String.format("%s%s", line, "\r\n"));
                                }
                                String result = resultBuilder.toString();
                                DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,result);

                                if (mServerRunCallBack != null) {
                                    mServerRunCallBack.onFailed(run_url_type,"Status: " + iResponseCode + " Message:" + result);
                                }
                                new BeaconManager().sendBeacon(inActivity.getString(R.string.url_debug_log) + "?" + iResponseCode,null,0,0,true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (mServerRunCallBack != null) {
                                    mServerRunCallBack.onFailed(run_url_type,"http_ng after error " + "Status: " + iResponseCode + " e:" + e.toString());
                                }
                                new BeaconManager().sendErrorBeacon(inActivity.getString(R.string.url_debug_log),e);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mServerRunCallBack != null) {
                            mServerRunCallBack.onFailed(run_url_type,e.toString());
                        }
                        new BeaconManager().sendErrorBeacon(inActivity.getString(R.string.url_debug_log),e);

                    } finally {
                        try {
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            if (outputStream != null) {
                                outputStream.flush();
                                outputStream.close();
                            }
                            if (connection != null) {
                                connection.disconnect();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        try {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
