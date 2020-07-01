package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class avails  extends RealmObject {
    private RealmList<ads> ads;
    public RealmList<ads> getads() {return ads;}
    public void setads(RealmList<ads> ads) {this.ads = ads;}

    private String availId;
    public String getavailId() {return availId;}
    public void setavailId(String availId) {this.availId = availId;}

/*
    private String duration;
    public String getduration() {return duration;}
    public void setduration(String duration) {this.duration = duration;}

    private String durationInSeconds;
    public String getdurationInSeconds() {return durationInSeconds;}
    public void setdurationInSeconds(String durationInSeconds) {this.durationInSeconds = durationInSeconds;}

    private String meta;
    public String getmeta() {return meta;}
    public void setmeta(String meta) {this.meta = meta;}

    private String startTime;
    public String getstartTime() {return startTime;}
    public void setstartTime(String startTime) {this.startTime = startTime;}

    private String startTimeInSeconds;
    public String getstartTimeInSeconds() {return startTimeInSeconds;}
    public void setstartTimeInSeconds(String startTimeInSeconds) {this.startTimeInSeconds = startTimeInSeconds;}
*/
}

