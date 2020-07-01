package jp.co.stream.jstplayerdemo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

class BeaconManager {

    private static final String TAG = "BeaconManager";

    public int mTimeout = 0;
    public int mRetry = 0;
    public int mRetryCount = -1;

    private BeaconSendCallBack mBeaconSendCallBack;
    public interface BeaconSendCallBack {
        public void onSend(String url, int response);
    }
    public void setBeaconSendCallbacks(BeaconSendCallBack callbacks){
        mBeaconSendCallBack = callbacks;
    }

    public BeaconManager() {
    }

    public void sendBeacon(String url, Map<String, String> params, int timeout, int retry)
    {
        try{
            sendBeacon(url,params,timeout,retry,true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sendBeacon(String url, Map<String, String> params, int timeout, int retry,boolean httpssend)
    {
        mTimeout = timeout;
        mRetry = retry;

        try{
        StringBuilder sb = new StringBuilder(url);
        if (params != null) {
            int count = 0;
            for (String key : params.keySet()) {
                String spliter = "&";
                count++;
                if (count == 1) {
                    if (url.indexOf("?") < 0) {
                        spliter = "?";
                    }
                }
                sb.append(spliter).append(key).append("=").append(Util.urlEncode(params.get(key)));
            }
        }
            requestUrl(sb.toString(),httpssend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestUrl(final String beaconUrlString)
    {
        try{
            requestUrl(beaconUrlString,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void requestUrl(final String beaconUrlString, final boolean httpssend)
    {

        mRetryCount++;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String targetUrlString = beaconUrlString;
                    if(httpssend)
                    {
                        targetUrlString = beaconUrlString.replace("http://","https://");
                    }
                    URL beaconUrl = new URL(targetUrlString);
                    HttpURLConnection connection = (HttpURLConnection)beaconUrl.openConnection();
                    connection.setConnectTimeout(mTimeout * 1000);
                    int tmpresponse = connection.getResponseCode();
                    DebugLog.d(TAG, "beaconResponse"+tmpresponse+"(" + mRetryCount + "/" + mRetry + "): " + targetUrlString);

                    if (mBeaconSendCallBack != null) {
                        mBeaconSendCallBack.onSend(beaconUrlString, tmpresponse);
                    }

                    if (connection.getResponseCode() >= 400) {
                        if (mRetryCount < mRetry) {
                            requestUrl(beaconUrlString,httpssend);
                        }
                    }
                } catch(SocketTimeoutException e) {
                    DebugLog.d(TAG, "beaconTimeout(" + mRetryCount + "/" + mRetry + "):" + e.toString());
                    if (mBeaconSendCallBack != null) {
                        mBeaconSendCallBack.onSend(beaconUrlString, -1);
                    }
                    if (mRetryCount < mRetry) {
                        requestUrl(beaconUrlString,httpssend);
                    }
                } catch(Exception e) {
                    DebugLog.d(TAG, "beaconFailed(" + mRetryCount + "/" + mRetry + "):" + e.toString());
                    if (mBeaconSendCallBack != null) {
                        mBeaconSendCallBack.onSend(beaconUrlString, -2);
                    }
                    if (mRetryCount < mRetry) {
                        requestUrl(beaconUrlString,httpssend);
                    }
                }
            }
        }).start();
    }

    public void sendErrorBeacon(String url, Exception e)
    {
        try{
            DebugLog.d(TAG, "sendErrorBeacon(" + url + "):" + e.toString());
            if(url.isEmpty())
            {
                return;
            }

            String tmperr = "";

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            String starcktrace = sw.toString();

            tmperr += starcktrace;
            tmperr = tmperr
                    .replace(" ","_")
                    .replace("?","_")
                    .replace("&","_")
                    .replace("\n","\\n")
                    .replace("\t","\\t")
            ;
            int tmpsubstringlen = 500;
            if(tmperr.length() < tmpsubstringlen)
            {
                tmpsubstringlen = tmperr.length();
            }
            tmperr = tmperr.substring(0,tmpsubstringlen);

            sendBeacon(url + "?err=" + tmperr, null, 60, 0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
