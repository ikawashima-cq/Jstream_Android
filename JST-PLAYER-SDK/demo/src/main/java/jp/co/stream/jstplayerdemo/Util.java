package jp.co.stream.jstplayerdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
    public static void WriteTrace(Activity act, TextView txt, String msg)
    {
        try
        {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    try
                    {
                        String tmp = sdf.format(new Date()) + " " + msg + "\n" + txt.getText();
                        txt.setText(tmp);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String urlEncode(String value) {
        String encodedResult = "";
        try {
            if(value != null)
            {
                encodedResult = URLEncoder.encode(value, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return encodedResult;
    }

    public static String urlDecode(String value) {
        String decodedResult = "";
        try {
            if(value != null)
            {
                decodedResult = URLDecoder.decode(value, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
        }
        return decodedResult;
    }

    public enum RUN_URL_TYPE {

        Server_Start ("server_start"),
        Live_Start ("live_start"),
        Dvr_Start ("dvr_start"),
        Vod_Start ("vod_start"),

        Server_Stop ("server_stop"),
        Live_Stop ("live_stop"),
        Dvr_Stop ("dvr_stop"),
        Vod_Stop ("vod_stop");

        private String value;

        private RUN_URL_TYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        public static RUN_URL_TYPE getType(final String value) {
            RUN_URL_TYPE[] types = RUN_URL_TYPE.values();
            for (RUN_URL_TYPE type : types) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum SERVER_STATUS {

        starting (1),
        running (2),
        stopping (-1),
        stop (-2),
        error (-99);

        private int value;

        private SERVER_STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public static SERVER_STATUS getType(final int value) {
            SERVER_STATUS[] types = SERVER_STATUS.values();
            for (SERVER_STATUS type : types) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }
    }

    public static void SetServerStatus(Activity inActivity,SERVER_STATUS status)
    {
        try
        {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName() + "SERVER_STATUS[" + status.name() + "]");
            SharedPreferences sp = inActivity.getSharedPreferences("pref",inActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(inActivity.getString(R.string.preferences_server_status), status.getValue());	//	キー、値
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static SERVER_STATUS GetServerStatus(Activity inActivity)
    {
        SERVER_STATUS ret = null;
        try
        {
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName());

            SharedPreferences sp = inActivity.getSharedPreferences("pref",inActivity.MODE_PRIVATE);
            int tmpret = sp.getInt(inActivity.getString(R.string.preferences_server_status), SERVER_STATUS.error.getValue());
            ret = SERVER_STATUS.getType(tmpret);
            DebugLog.d(new Object(){}.getClass().getEnclosingClass().getName() ,new Object(){}.getClass().getEnclosingMethod().getName() + "SERVER_STATUS[" + ret.name() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
