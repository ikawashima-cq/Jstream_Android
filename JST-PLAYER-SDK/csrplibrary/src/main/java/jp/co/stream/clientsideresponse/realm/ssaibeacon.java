package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ssaibeacon  extends RealmObject {
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

    private double ads_durationInSeconds;
    public double getads_durationInSeconds() {return ads_durationInSeconds;}
    public void setads_durationInSeconds(double ads_durationInSeconds) {this.ads_durationInSeconds = ads_durationInSeconds;}

    private double ads_endTimeInSeconds;
    public double getads_endTimeInSeconds() {return ads_endTimeInSeconds;}
    public void setads_endTimeInSeconds(double ads_endTimeInSeconds) {this.ads_endTimeInSeconds = ads_endTimeInSeconds;}

    private double trackingEvents_startTimeInSeconds;
    public double gettrackingEvents_startTimeInSeconds() {return trackingEvents_startTimeInSeconds;}
    public void settrackingEvents_startTimeInSeconds(double trackingEvents_startTimeInSeconds) {this.trackingEvents_startTimeInSeconds = trackingEvents_startTimeInSeconds;}

/*
    private double trackingEvents_durationInSeconds;
    public double gettrackingEvents_durationInSeconds() {return trackingEvents_durationInSeconds;}
    public void settrackingEvents_durationInSeconds(double trackingEvents_durationInSeconds) {this.trackingEvents_durationInSeconds = trackingEvents_durationInSeconds;}
*/

    private String eventId;
    public String geteventId() {return eventId;}
    public void seteventId(String eventId) {this.eventId = eventId;}

    private String eventType;
    public String geteventType() {return eventType;}
    public void seteventType(String eventType) {this.eventType = eventType;}

    private String beaconUrls;
    public String getbeaconUrls() {return beaconUrls;}
    public void setbeaconUrls(String beaconUrls) {this.beaconUrls = beaconUrls;}

    private int eventTypeSequences;
    public int geteventTypeSequences() {return eventTypeSequences;}
    public void seteventTypeSequences(int eventTypeSequences) {this.eventTypeSequences = eventTypeSequences;}

/*
    private double adStartTimeLimit;
    public double getadStartTimeLimit() {return adStartTimeLimit;}
    public void setadStartTimeLimit(double adStartTimeLimit) {this.adStartTimeLimit = adStartTimeLimit;}

    private double adEndTimeLimit;
    public double getadEndTimeLimit() {return adEndTimeLimit;}
    public void setadEndTimeLimit(double adEndTimeLimit) {this.adEndTimeLimit = adEndTimeLimit;}
*/

    private boolean sendFlg;
    public boolean getsendFlg() {return sendFlg;}
    public void setsendFlg(boolean sendFlg) {this.sendFlg = sendFlg;}
}
