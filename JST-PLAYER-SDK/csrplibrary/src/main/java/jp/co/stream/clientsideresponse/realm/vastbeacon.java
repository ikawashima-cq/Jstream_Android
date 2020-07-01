package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class vastbeacon  extends RealmObject {
    @PrimaryKey
    private  String primarykey;
    public String getprimarykey() {return primarykey;}
    public void setprimarykey(String primarykey) {this.primarykey = primarykey;}

    private String adId;
    public String getadId() {return adId;}
    public void setadId(String adId) {this.adId = adId;}

    private String vastAdId;
    public String getvastAdId() {return vastAdId;}
    public void setvastAdId(String vastAdId) {this.vastAdId = vastAdId;}

    private double ads_startTimeInSeconds;
    public double getads_startTimeInSeconds() {return ads_startTimeInSeconds;}
    public void setads_startTimeInSeconds(double ads_startTimeInSeconds) {this.ads_startTimeInSeconds = ads_startTimeInSeconds;}

    private double ads_endTimeInSeconds;
    public double getads_endTimeInSeconds() {return ads_endTimeInSeconds;}
    public void setads_endTimeInSeconds(double ads_endTimeInSeconds) {this.ads_endTimeInSeconds = ads_endTimeInSeconds;}

    private boolean impressionSendFlg;
    public boolean getimpressionSendFlg() {return impressionSendFlg;}
    public void setimpressionSendFlg(boolean impressionSendFlg) {this.impressionSendFlg = impressionSendFlg;}

    private boolean completeSendFlg;
    public boolean getcompleteSendFlg() {return completeSendFlg;}
    public void setcompleteSendFlg(boolean completeSendFlg) {this.completeSendFlg = completeSendFlg;}

}
