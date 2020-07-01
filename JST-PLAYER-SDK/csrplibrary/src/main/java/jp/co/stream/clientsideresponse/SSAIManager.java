package jp.co.stream.clientsideresponse;


import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.co.stream.clientsideresponse.config.BeaconParamConfig;
import jp.co.stream.clientsideresponse.config.PlayerConfig;
import jp.co.stream.clientsideresponse.realm.CsrpRealmModule;
import jp.co.stream.clientsideresponse.realm.ads;
import jp.co.stream.clientsideresponse.realm.avails;
import jp.co.stream.clientsideresponse.realm.ssaibeacon;
import jp.co.stream.clientsideresponse.realm.trackingEvents;
import jp.co.stream.clientsideresponse.realm.vast;
import jp.co.stream.clientsideresponse.realm.vastbeacon;


class SSAIManager {

    private static final String TAG = "SSAIManager";

    private Activity mActivity;

    private String mManifestUrl;
    private String mTrackingUrl;
    //private JSONObject mVASTData;
    private SSAIManagerPrefixInfoCallBack mSSAIManagerPrefixInfoCallBack;
    private SSAIManagerTrackingCallBack mSSAIManagerTrackingCallBack;
    private SSAIManagerTrackingEventCallBack mSSAIManagerTrackingEventCallBack;
    //private int mCheckTrackingParcentage;


    private PlayerConfig mPlayerConfig;
    public PlayerConfig getPlayerConfig() {
        return mPlayerConfig;
    }
    public void setPlayerConfig(PlayerConfig b) {
        mPlayerConfig = b;
    }

    private BeaconParamConfig mBeaconParamConfig;
    public BeaconParamConfig getBeaconParamConfig() {
        return mBeaconParamConfig;
    }
    public void setBeaconParamConfig(BeaconParamConfig b) {
        mBeaconParamConfig = b;
    }

    private  String mTestSendBeaconUrlCo3 = "";
    public String getTestSendBeaconUrlCo3() {
        return mTestSendBeaconUrlCo3;
    }
    public void setTestSendBeaconUrlCo3(String b) {
        mTestSendBeaconUrlCo3 = b;
    }


    private final RealmConfiguration realmConfig;
    //private Realm realm;
    private Realm inmemrealm;


    public interface SSAIManagerPrefixInfoCallBack {
        public void onSucceed(String manifestUrl, String trackingUrl);
        public void onFailed(String message);
    }

    public interface SSAIManagerTrackingCallBack {
        public void onSucceed(String vastConfig);
        public void onFailed(String message);
    }

    public interface SSAIManagerTrackingEventCallBack {
        public void onTrackingImpression(String adId,String vastAdId);
        public void onTrackingStart(String adId,String vastAdId);
        public void onTrackingFirstQuartile(String adId,String vastAdId);
        public void onTrackingMidpoint(String adId,String vastAdId);
        public void onTrackingThirdQuartile(String adId,String vastAdId);
        public void onTrackingComplete(String adId,String vastAdId);
        public void onError(String adId,String vastAdId);
    }


    public void setPrefixInfoCallbacks(SSAIManagerPrefixInfoCallBack callbacks){
        mSSAIManagerPrefixInfoCallBack = callbacks;
    }

    public void setTrackingCallbacks(SSAIManagerTrackingCallBack callbacks){
        mSSAIManagerTrackingCallBack = callbacks;
    }

    public void setTrackingEventCallbacks(SSAIManagerTrackingEventCallBack callbacks){
        mSSAIManagerTrackingEventCallBack = callbacks;
    }

    private SSAIManagerBeaconSendCallBack mSSAIManagerBeaconSendCallBack;
    public interface SSAIManagerBeaconSendCallBack {
        public void onSend(String url,int response);
    }
    public void setBeaconSendCallbacks(SSAIManagerBeaconSendCallBack callbacks){
        mSSAIManagerBeaconSendCallBack = callbacks;
    }


    public SSAIManager(Activity activity) {
        mActivity = activity;

        Realm.init(activity);
        realmConfig = new RealmConfiguration.Builder()     // The app is responsible for calling `Realm.init(Context)`
                .name("library.csrp.ssaimanager.realm")                 // So always use a unique name
                .modules(new CsrpRealmModule())           // Always use explicit modules in library projects
                //.inMemory()
                .build();
        try {
            Realm.getInstance(realmConfig).close();
            Realm.deleteRealm(realmConfig);
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
            
            Realm.deleteRealm(realmConfig);
        }

        //inMemory用
        inmemrealm = Realm.getInstance(realmConfig);

    }

    public String getManifestUrl() {
        return mManifestUrl;
    }

    public String getTrackingUrl() {
        return mTrackingUrl;
    }


    public void getSSAIPrefixInfo(final String requestUrlString,final String mediaId,final String pageUrl,final String advertisingId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                /*

                int age = 0;
                if (enqueteAnswer.birth.length() >= 6) {
                    int birthYear = Integer.parseInt(enqueteAnswer.birth.substring(0, 4));
                    int birthMonth = Integer.parseInt(enqueteAnswer.birth.substring(4, 6));
                    Calendar calendar = Calendar.getInstance();
                    int currentYear = calendar.get(Calendar.YEAR);
                    int currentMonth = calendar.get(Calendar.MONTH) + 1;

                    age = currentYear - birthYear;
                    if (currentMonth < birthMonth) {
                        age = age - 1;
                    }
                }
                */

                String adId = "";
                if (!Strings.isNullOrEmpty(advertisingId) && !advertisingId.equals("optout")) {
                    adId = advertisingId;
                }

                String[] splitedUrl = pageUrl.split("/");
                String siteId = "stream.co.jp";

                String tmpname = "NoName";
                //PackageManagerのオブジェクトを取得
                PackageManager pm = mActivity.getPackageManager();
                //インストール済パッケージ情報を取得する
                final List<ApplicationInfo> appInfoList = pm.getInstalledApplications(0);

                for(ApplicationInfo ai : appInfoList){
                    String tmpappname = ai.loadLabel(pm).toString();
                    String tmppackagename = ai.packageName;

                    //DebugLog.d(TAG, "getSSAIPrefixInfo appname: " + tmpappname);
                    //DebugLog.d(TAG, "getSSAIPrefixInfo packageName: " + tmppackagename);
                    //DebugLog.d(TAG, "getSSAIPrefixInfo getPackageName: " + mActivity.getPackageName() );

                    if(tmpappname!=null && mActivity.getPackageName().equals(tmppackagename)){
                        //アプリ名取得
                        tmpname = tmpappname;
                        break;
                    }
                }

                String short_postal = mBeaconParamConfig.postal.length() > 2 ? mBeaconParamConfig.postal.substring(0, 2):mBeaconParamConfig.postal;

                StringBuilder contentsBuilder = new StringBuilder();
                int iContentsLength = 0;
                contentsBuilder.append("{\"adsParams\":{");
                contentsBuilder.append("\"device\":\"0003\",");                                                   //0001:PC, 0002:iOS, 0003:Android
                contentsBuilder.append("\"program\":\"" + mediaId + "\",");                                       //EPIDで我慢できないか確認
                contentsBuilder.append("\"postal\":\"" + mBeaconParamConfig.postal + "\",");                       //郵便番号 数値7桁
                contentsBuilder.append("\"short_postal\":\"" + short_postal+ "\","); //郵便番号 上2桁
                contentsBuilder.append("\"gender\":\"" + mBeaconParamConfig.gender + "\",");                           //男性:m, 女性:f
                contentsBuilder.append("\"age\":\"" + mBeaconParamConfig.age + "\",");                        //年齢（誕生時換算）
                contentsBuilder.append("\"site_domain\":\""+siteId+"\",");
                contentsBuilder.append("\"ifa\": \""+ adId +"\",");                                               //AdvertisinID
                contentsBuilder.append("\"bundle\":\"" + mActivity.getPackageName() + "\",");                     //アプリのバンドル名
                contentsBuilder.append("\"domain\":\""+ siteId +"\",");
                contentsBuilder.append("\"name\":\"" + tmpname + "\"");                                                 //アプリ名
                contentsBuilder.append("}}");

                DebugLog.d(TAG, "getSSAIPrefixInfo.Request: " + contentsBuilder.toString());

                try {
                    iContentsLength = contentsBuilder.toString().getBytes("UTF-8").length;
                } catch (Exception e1) {
                    e1.printStackTrace();

                }

                HttpURLConnection connection = null;
                DataOutputStream outputStream = null;
                BufferedReader bufferedReader = null;

                try {
                    URL requestUrl = new URL(requestUrlString);

                    connection = (HttpURLConnection) requestUrl.openConnection();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setChunkedStreamingMode(0);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    connection.setRequestProperty("Content-Length", String.valueOf(iContentsLength));

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(contentsBuilder.toString());

                    int iResponseCode = connection.getResponseCode();
                    DebugLog.d(TAG, "getSSAIPrefixInfo.Status: "+iResponseCode);

                    if (iResponseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder resultBuilder = new StringBuilder();
                        String line = "";
                        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null) {
                            resultBuilder.append(String.format("%s%s", line, "\r\n"));
                        }
                        String result = resultBuilder.toString();
                        //DebugLog.d(TAG, "getSSAIPrefixInfo.Result: " + result);

                        try {
                            JSONObject adJSONObject = new JSONObject(result);

                            mManifestUrl = requestUrl.toURI().resolve(adJSONObject.get("manifestUrl").toString()).toString();
                            mTrackingUrl = requestUrl.toURI().resolve(adJSONObject.get("trackingUrl").toString()).toString();

                            DebugLog.d(TAG, "mManifestUrl: " + mManifestUrl);
                            DebugLog.d(TAG, "mTrackingUrl: " + mTrackingUrl);

                            if (!Strings.isNullOrEmpty(mManifestUrl) && !Strings.isNullOrEmpty(mTrackingUrl)) {
                                if (mSSAIManagerPrefixInfoCallBack != null) {
                                    mSSAIManagerPrefixInfoCallBack.onSucceed(mManifestUrl, mTrackingUrl);
                                }
                            } else {
                                if (mSSAIManagerPrefixInfoCallBack != null) {
                                    mSSAIManagerPrefixInfoCallBack.onFailed("manifestUrl or trackingUrl is null or empty.");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mSSAIManagerPrefixInfoCallBack != null) {
                                mSSAIManagerPrefixInfoCallBack.onFailed(e.toString());
                            }
                            
                        }
                    } else {
                        if (mSSAIManagerPrefixInfoCallBack != null) {
                            mSSAIManagerPrefixInfoCallBack.onFailed("Status: " + iResponseCode);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSSAIManagerPrefixInfoCallBack != null) {
                        mSSAIManagerPrefixInfoCallBack.onFailed(e.toString());
                    }
                    
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
        }).start();
    }

    public void getSSAITracking(int checkTime) {
        DebugLog.d(TAG, "getSSAITracking: "+mTrackingUrl);
        if (!Strings.isNullOrEmpty(mTrackingUrl)) {
            try {
                URL adParamsUrl = new URL(mTrackingUrl);
                HttpURLConnection connection = (HttpURLConnection) adParamsUrl.openConnection();

                InputStream is = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[];
                byte buff[] = new byte[ 1024 ];
                int read;
                while( ( read = is.read( buff ) ) > 0 ) {
                    baos.write( buff, 0, read );
                }
                buffer = baos.toByteArray();

/*
                mVASTData = new JSONObject(vastString);
*/
                String vastString = MyUtils.InputStreamToString(new ByteArrayInputStream(buffer));

                JsonElement json = new JsonParser().parse(new InputStreamReader(new ByteArrayInputStream(buffer)));
                Gson gson = new GsonBuilder().create();
                vast vast =  gson.fromJson(json, vast.class);

                is.close();
                baos.close();


                int jsonadssize = 0;
                RealmList<ads> jsonads = new RealmList<ads>();

                for (int i=0;vast.getavails().size() >i;i++)
                {
                    jsonadssize += vast.getavails().get(i).getads().size();
                    for(int j=0;vast.getavails().get(i).getads().size()>j;j++)
                    {
                        jsonads.add(vast.getavails().get(i).getads().get(j));
                    }
                }

                DebugLog.d(TAG, "jsonadssize:" + jsonadssize);

                Realm realm;
                realm = Realm.getInstance(realmConfig);
                try
                {
                    realm.beginTransaction();
                    RealmResults<ads> dbresultads = realm.where(ads.class).sort("adId",Sort.ASCENDING).findAll();
                    int dbadssize = dbresultads.size();

                    RealmList <ads> dbads = new RealmList<ads>();
                    dbads.addAll(dbresultads.subList(0, dbresultads.size()));

                    DebugLog.d(TAG, "dbadssize:" + dbadssize);

                    boolean jsondbcompare = true;
                    if(jsonadssize != dbadssize)
                    {
                        jsondbcompare = false;
                    }
                    else
                    {
                        for(int i=0;jsonadssize > i;i++)
                        {
                            String tmpjsonadId = jsonads.get(i).getadId();
                            String tmpdbadId = dbads.get(i).getadId();

                            jsondbcompare = tmpjsonadId.equals(tmpdbadId);
                            DebugLog.d(TAG, "jsondbcompare:" + jsondbcompare + " jsonadId:" + tmpjsonadId + " dbadId:" + tmpdbadId);
                            if(!jsondbcompare)
                            {
                                break;
                            }
                        }
                    }

                    DebugLog.d(TAG, "jsondbcompare:" + jsondbcompare);

                    if(!jsondbcompare)
                    {
                        DebugLog.d(TAG, "realm db delete_insert start");

                        realm.where(vast.class).findAll().deleteAllFromRealm();
                        realm.where(ads.class).findAll().deleteAllFromRealm();
                        realm.where(avails.class).findAll().deleteAllFromRealm();
                        realm.where(trackingEvents.class).findAll().deleteAllFromRealm();

                        realm.insertOrUpdate(vast);

                        RealmResults<trackingEvents> tmpte = realm.where(trackingEvents.class).findAll();

                        RealmList<ssaibeacon> ssaibeaconlist = new RealmList<>();
                        RealmList<vastbeacon> vastbeaconlist = new RealmList<>();
                        List<String> vastbeaconkeylist = new ArrayList<String>();
                        for(int i=0; tmpte.size() > i;i++)
                        {
                            DebugLog.d(TAG, "adId " + tmpte.get(i).getowners().get(0).getadId()  + " adStartTimeLimit" + tmpte.get(i).getowners().get(0).getadStartTimeLimit(mPlayerConfig.SSAI_Tracking_Offset_Second_Start) + "adEndTimeLimit" + tmpte.get(i).getowners().get(0).getadEndTimeLimit(mPlayerConfig.SSAI_Tracking_Offset_Second_End));

                            if(tmpte.get(i).getbeaconUrls().size() > 0 &&
                                    tmpte.get(i).geteventTypeSequences() < trackingEvents.eventTypeSequencesLimit &&
                                    tmpte.get(i).getowners().get(0).getadStartTimeLimit(mPlayerConfig.SSAI_Tracking_Offset_Second_Start) < (double)checkTime &&
                                    tmpte.get(i).getowners().get(0).getadEndTimeLimit(mPlayerConfig.SSAI_Tracking_Offset_Second_End) > (double)checkTime &&
                                    1 == 1
                            )
                            {
                                for(int j=0; tmpte.get(i).getbeaconUrls().size() > j;j++)
                                {
                                    String tmpprimarykey =
                                            tmpte.get(i).getowners().get(0).getadId() + "_" +
                                                    tmpte.get(i).getowners().get(0).getvastAdId() + "_" +
                                                    tmpte.get(i).geteventType() + "_" +
                                                    tmpte.get(i).geteventId() + "_" +
                                                    tmpte.get(i).getbeaconUrls().get(j).toString()
                                            ;

                                    //切腹レベルの構文(nullに=するのも気に食わん）
                                    ssaibeacon existssaibeacon = realm.where(ssaibeacon.class)
                                            .equalTo("primarykey",tmpprimarykey)
                                            .findFirst()
                                            ;

                                    if(existssaibeacon == null)
                                    {
                                        ssaibeacon tmpssai = new ssaibeacon();
                                        tmpssai.setprimarykey(tmpprimarykey);

                                        tmpssai.setadId(tmpte.get(i).getowners().get(0).getadId());
                                        tmpssai.setvastAdId(tmpte.get(i).getowners().get(0).getvastAdId());
                                        tmpssai.setbeaconUrls(tmpte.get(i).getbeaconUrls().get(j).toString());
                                        //tmpssai.settrackingEvents_durationInSeconds(Double.parseDouble(tmpte.get(i).getdurationInSeconds()));
                                        tmpssai.settrackingEvents_startTimeInSeconds(Double.parseDouble(tmpte.get(i).getstartTimeInSeconds()));

                                        tmpssai.setads_durationInSeconds(Double.parseDouble(tmpte.get(i).getowners().get(0).getdurationInSeconds()));
                                        tmpssai.setads_startTimeInSeconds(Double.parseDouble(tmpte.get(i).getowners().get(0).getstartTimeInSeconds()));
                                        tmpssai.setads_endTimeInSeconds(Double.parseDouble(tmpte.get(i).getowners().get(0).getstartTimeInSeconds()) + Double.parseDouble(tmpte.get(i).getowners().get(0).getdurationInSeconds()));

                                        tmpssai.seteventId(tmpte.get(i).geteventId());
                                        tmpssai.seteventType(tmpte.get(i).geteventType());
                                        tmpssai.seteventTypeSequences(tmpte.get(i).geteventTypeSequences());
                                        //tmpssai.setadStartTimeLimit(tmpte.get(i).getowners().get(0).getadStartTimeLimit());
                                        //tmpssai.setadEndTimeLimit(tmpte.get(i).getowners().get(0).getadEndTimeLimit());
                                        tmpssai.setsendFlg(false);
                                        ssaibeaconlist.add(tmpssai);

                                    }
                                }

                                String tmpvastprimarykey =
                                        tmpte.get(i).getowners().get(0).getadId() + "_" +
                                                tmpte.get(i).getowners().get(0).getvastAdId();
                                        ;

                                //切腹レベルの構文(nullに=するのも気に食わん）
                                vastbeacon existvastbeacon = realm.where(vastbeacon.class)
                                        .equalTo("primarykey",tmpvastprimarykey)
                                        .findFirst()
                                        ;

                                boolean listexistvastbeacon = vastbeaconkeylist.contains(tmpvastprimarykey);


                                if(existvastbeacon == null && !listexistvastbeacon)
                                {
                                    vastbeacon tmpvast = new vastbeacon();
                                    tmpvast.setprimarykey(tmpvastprimarykey);

                                    tmpvast.setadId(tmpte.get(i).getowners().get(0).getadId());
                                    tmpvast.setvastAdId(tmpte.get(i).getowners().get(0).getvastAdId());

                                    tmpvast.setads_startTimeInSeconds(Double.parseDouble(tmpte.get(i).getowners().get(0).getstartTimeInSeconds()));
                                    tmpvast.setads_endTimeInSeconds(Double.parseDouble(tmpte.get(i).getowners().get(0).getstartTimeInSeconds()) + Double.parseDouble(tmpte.get(i).getowners().get(0).getdurationInSeconds()));

                                    tmpvast.setimpressionSendFlg(false);
                                    tmpvast.setcompleteSendFlg(false);
                                    vastbeaconlist.add(tmpvast);
                                    vastbeaconkeylist.add(tmpvastprimarykey);
                                }

                            }
                        }

                        DebugLog.d(TAG, "realm db ssaibeaconlist insert count:[" + ssaibeaconlist.size() + "]");

                        realm.insert(ssaibeaconlist);
                        realm.insert(vastbeaconlist);

                        realm.where(vast.class).findAll().deleteAllFromRealm();
                        //realm.where(ads.class).findAll().deleteAllFromRealm();
                        realm.where(avails.class).findAll().deleteAllFromRealm();
                        realm.where(trackingEvents.class).findAll().deleteAllFromRealm();
                    }

                    realm.commitTransaction();

                } catch (Exception e) {
                    DebugLog.d(TAG, "realm error:" + e.toString());
                    
                    realm.cancelTransaction();
                }
                finally {
                    realm.close();
                }
                if (mSSAIManagerTrackingCallBack != null) {
                    mSSAIManagerTrackingCallBack.onSucceed(vastString);
                }
            } catch (Exception e) {
                DebugLog.d(TAG, "error:" + e.toString());
                if (mSSAIManagerTrackingCallBack != null) {
                    mSSAIManagerTrackingCallBack.onFailed(e.toString());
                }
                
            }
        } else {
            if (mSSAIManagerTrackingCallBack != null) {
                mSSAIManagerTrackingCallBack.onFailed("");
            }
        }
    }

    private String mplayingVastAdId = "";
    public String checkSSAITracking(int checkTime) {
        String tmpkey = "";
        String adTimeList = "";
        Boolean ssaiAdPlaying = false;
        int adCurrentTime = -1;
        int adDuration = -1;
        String playingAdId = "";
        String playingVastAdId = "";
        try {
            DebugLog.d(TAG, "SSAIManager_checkSSAITracking_checkTime: " + checkTime + "s(" + (checkTime/60) + "m)" );

            Realm realm;
            realm = Realm.getInstance(realmConfig);
            try
            {
                realm.beginTransaction();

                //impression判定
                RealmResults<vastbeacon> tmpvastbeaconimpression = realm.where(vastbeacon.class)
                        .equalTo("impressionSendFlg", false)
                        .and()
                        .lessThanOrEqualTo("ads_startTimeInSeconds",(double)checkTime)
                        .greaterThan("ads_endTimeInSeconds",(double)checkTime - 10)
                        .findAll();

                int tmpsendvastbeaconimpressionsize = tmpvastbeaconimpression.size();

                if(tmpsendvastbeaconimpressionsize > 0) {
                    DebugLog.i(TAG, "impression send target:" + tmpsendvastbeaconimpressionsize);

                    while(tmpvastbeaconimpression.size() > 0)
                    {
                        int i = 0;
                        //DebugLog.i(TAG, "send target i:" + i);
                        DebugLog.i(TAG, "impression send target realtmpvastbeaconimpression:" + tmpvastbeaconimpression.size());

                        playingAdId = tmpvastbeaconimpression.get(i).getadId();
                        playingVastAdId = tmpvastbeaconimpression.get(i).getvastAdId();
                        tmpkey = playingAdId + "_" + playingVastAdId;

                        //if (!mplayingVastAdId.equals(playingVastAdId)) {
                        DebugLog.d(TAG, "SSAIManager.Impression(" + tmpkey + ")");
                        mplayingVastAdId = playingVastAdId;
                        if (mSSAIManagerTrackingEventCallBack != null) {
                            mSSAIManagerTrackingEventCallBack.onTrackingImpression(playingAdId,playingVastAdId);
                        }
                        tmpvastbeaconimpression.get(i).setimpressionSendFlg(true);
                    }
                }

                //ssai判定
                String[] names = {"adId","eventTypeSequences" };
                Sort[] sorts = { Sort.ASCENDING,Sort.ASCENDING };

                RealmResults<ssaibeacon> tmpsendbeacon = realm.where(ssaibeacon.class)
                        .equalTo("sendFlg", false)
                        .and()
                        .lessThanOrEqualTo("trackingEvents_startTimeInSeconds",(double)checkTime)
                        .greaterThan("ads_endTimeInSeconds",(double)checkTime - 10)
                        .sort(names, sorts)
                        .findAll();

                int tmpsendbeaconsize = tmpsendbeacon.size();

                if(tmpsendbeaconsize > 0) {
                    DebugLog.i(TAG, "send target:" + tmpsendbeaconsize);

                    //for(int j=0; tmpsendbeaconsize> 0;j++)
                    while(tmpsendbeacon.size() > 0)
                    {
                        int i = 0;
                        //DebugLog.i(TAG, "send target i:" + i);
                        DebugLog.i(TAG, "send target realtmpsendbeaconsize:" + tmpsendbeacon.size());

                        tmpsendbeacon = tmpsendbeacon.sort(names, sorts);

                        double tmpads_startTimeInSeconds = tmpsendbeacon.get(i).getads_startTimeInSeconds();
                        double tmptrackingEvents_startTimeInSeconds = tmpsendbeacon.get(i).gettrackingEvents_startTimeInSeconds();
                        playingAdId = tmpsendbeacon.get(i).getadId();
                        playingVastAdId = tmpsendbeacon.get(i).getvastAdId();
                        tmpkey = playingAdId + "_" + playingVastAdId;
                        adCurrentTime = checkTime - (int)tmpsendbeacon.get(i).getads_startTimeInSeconds();
                        adDuration = (int)tmpsendbeacon.get(i).getads_durationInSeconds();
                        String trackingUrl = tmpsendbeacon.get(i).getbeaconUrls();
                        String eventType = tmpsendbeacon.get(i).geteventType();
                        int tmpeventTypeSequences = tmpsendbeacon.get(i).geteventTypeSequences();

                        DebugLog.d(TAG, "SSAIManager_checkSSAITracking_Realm_time (adId_vastAdId:[" + tmpkey + "] playingVastAdId:["+ playingVastAdId +"] eventType:[" + eventType + "] tmptrackingEvents_startTimeInSeconds:["+ tmptrackingEvents_startTimeInSeconds +"] tmpeventTypeSequences:[" + tmpeventTypeSequences + "] checkTime:["+ checkTime +"]  adCurrentTime:[" + adCurrentTime + "] adDuration:[" + adDuration + "] trackingUrl:[" + trackingUrl + "])");
                        //DebugLog.d(TAG, "SSAIManager checkSSAITracking Realm time (adId_vastAdId:[" + tmpkey + "] tmpeventtype:[" + tmpeventtype + "] )");

                        if(mPlayerConfig.testSendBeaconFlg)
                        {
                            //trackingUrl = mTestSendBeaconUrl + "?adId_vastAdId=" + tmpkey + "&eventType=" + eventType + "&tmptrackingEvents_startTimeInSeconds=" + tmptrackingEvents_startTimeInSeconds + "&checkTime=" + checkTime + "&adCurrentTime=" + adCurrentTime + "&trackingUrl=" + trackingUrl;
                        }


                        if(!mPlayerConfig.debugSendBeaconFlg)
                        {
                            BeaconManager beaconManager = new BeaconManager();
                            beaconManager.setBeaconSendCallbacks(new BeaconManager.BeaconSendCallBack() {
                                @Override
                                public void onSend(String url, int response) {
                                    if (mSSAIManagerBeaconSendCallBack != null) {
                                        mSSAIManagerBeaconSendCallBack.onSend(url, response);
                                    }
                                }
                            });
                            beaconManager.sendBeacon(trackingUrl, null, mPlayerConfig.beaconTimeout, mPlayerConfig.beaconRetry);

                            if(mPlayerConfig.testSendBeaconFlg)
                            {
                                String tmpbeaconurl = mTestSendBeaconUrlCo3 + "?realurl=" + trackingUrl.replace("?","&");
                                beaconManager.sendBeacon(tmpbeaconurl, null, mPlayerConfig.beaconTimeout, mPlayerConfig.beaconRetry);
                            }

                        }
                        else
                        {
                            DebugLog.d(TAG, "BeaconDebug SSAIManager checkSSAITracking Realm beaconManager.sendBeacon(key:[" + tmpkey +  "] url:["+ trackingUrl +"])");
                        }
                        tmpsendbeacon.get(i).setsendFlg(true);
                    }
                }

                //complete判定
                RealmResults<vastbeacon> tmpvastbeaconcomplete = realm.where(vastbeacon.class)
                        .equalTo("completeSendFlg", false)
                        .and()
                        .lessThanOrEqualTo("ads_endTimeInSeconds",(double)checkTime)
                        .greaterThan("ads_endTimeInSeconds",(double)checkTime - 10)
                        .findAll();

                int tmpsendvastbeaconcompletesize = tmpvastbeaconcomplete.size();

                if(tmpsendvastbeaconcompletesize > 0) {
                    DebugLog.i(TAG, "complete send target:" + tmpsendvastbeaconcompletesize);

                    while(tmpvastbeaconcomplete.size() > 0)
                    {
                        int i = 0;
                        //DebugLog.i(TAG, "send target i:" + i);
                        DebugLog.i(TAG, "complete send target realtmpvastbeaconcomplete:" + tmpvastbeaconcomplete.size());

                        playingAdId = tmpvastbeaconcomplete.get(i).getadId();
                        playingVastAdId = tmpvastbeaconcomplete.get(i).getvastAdId();
                        tmpkey = playingAdId + "_" + playingVastAdId;

                        DebugLog.d(TAG, "SSAIManager.complete(" + tmpkey + ")");
                        if (mSSAIManagerTrackingEventCallBack != null) {
                            mSSAIManagerTrackingEventCallBack.onTrackingComplete(playingAdId,playingVastAdId);
                        }
                        tmpvastbeaconcomplete.get(i).setcompleteSendFlg(true);
                    }
                }

                realm.commitTransaction();
            } catch (Exception e) {
                realm.cancelTransaction();
                DebugLog.d(TAG, "realm error:" + e.toString());
                
            }
            finally {
                realm.close();
            }
        } catch (Exception e) {
            DebugLog.d(TAG, "checkSSAITracking: Exception="+e.toString());
            
        }
        if (!Strings.isNullOrEmpty(playingVastAdId)) {
            //this._videoController.setSeekable(false);
        } else {
            mplayingVastAdId = "";
            //mCheckTrackingParcentage = 0;
        }
/*
        //DebugLog.d(TAG, "SSAIManager.checkTime/adTimeList: " + checkTime +"/"+ adTimeList);
        if (!Strings.isNullOrEmpty(playingVastAdId)) {
            DebugLog.d(TAG, "SSAIManager.adPlaying(" + playingVastAdId + "): " + adCurrentTime + "/" + adDuration);
        }
*/

        return playingVastAdId;
    }

    public String getCUrrentAdId() {
        return mplayingVastAdId;
    }

    public void release() {
        try
        {
            inmemrealm.close();
            Realm.getInstance(realmConfig).close();
        } catch (Exception e) {
            DebugLog.d(TAG, "release: Exception="+e.toString());
            
        }
    }
}


