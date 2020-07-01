package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ads  extends RealmObject {

/*
    private static int adLimitOffset = 30; //30は俺のさじ加減
*/

    @PrimaryKey
    private String adId;
    public String getadId() {return adId;}
    public void setadId(String adId) {this.adId = adId;}

/*
    private String adParameters;
    public String getadParameters() {return adParameters;}
    public void setadParameters(String adParameters) {this.adParameters = adParameters;}

    private String adSystem;
    public String getadSystem() {return adSystem;}
    public void setadSystem(String adSystem) {this.adSystem = adSystem;}

    private String adTitle;
    public String getadTitle() {return adTitle;}
    public void setadTitle(String adTitle) {this.adTitle = adTitle;}
*/

    //private String companionAds;
    //public String getcompanionAds() {return companionAds;}
    //public void setcompanionAds(String companionAds) {this.companionAds = companionAds;}

/*
    private String creativeId;
    public String getcreativeId() {return creativeId;}
    public void setcreativeId(String creativeId) {this.creativeId = creativeId;}

    private String creativeSequence;
    public String getcreativeSequence() {return creativeSequence;}
    public void setcreativeSequence(String creativeSequence) {this.creativeSequence = creativeSequence;}

    private String duration;
    public String getduration() {return duration;}
    public void setduration(String duration) {this.duration = duration;}
*/

    private String durationInSeconds;
    public String getdurationInSeconds() {return durationInSeconds;}
    public void setdurationInSeconds(String durationInSeconds) {this.durationInSeconds = durationInSeconds;}

    //private mediaFiles mediaFiles;
    //public mediaFiles getmediaFiles() {return mediaFiles;}
    //public void setmediaFiles(mediaFiles mediaFiles) {this.mediaFiles = mediaFiles;}

/*
    private String startTime;
    public String getstartTime() {return startTime;}
    public void setstartTime(String startTime) {this.startTime = startTime;}
*/

    private String startTimeInSeconds;
    public String getstartTimeInSeconds() {return startTimeInSeconds;}
    public void setstartTimeInSeconds(String startTimeInSeconds) {this.startTimeInSeconds = startTimeInSeconds;}

    private RealmList<trackingEvents> trackingEvents;
    public RealmList<trackingEvents> gettrackingEvents() {return trackingEvents;}
    public void settrackingEvents(RealmList<trackingEvents> trackingEvents) {this.trackingEvents = trackingEvents;}

    private String vastAdId;
    public String getvastAdId() {return vastAdId;}
    public void setvastAdId(String vastAdId) {this.vastAdId = vastAdId;}


/*
    public  double getadStartTimeLimit()
    {
        double ret = -1;
        ret = Double.parseDouble(startTimeInSeconds) + (-1 * adLimitOffset);
        return ret;
    }

    public  double getadEndTimeLimit()
    {
        double ret = -1;
        ret = Double.parseDouble(startTimeInSeconds) + Double.parseDouble(durationInSeconds) + (adLimitOffset);
        return ret;
    }
*/

    public  double getadStartTimeLimit(int offset_second)
    {
        double ret = -1;
        ret = Double.parseDouble(startTimeInSeconds) + (-1 * offset_second);
        return ret;
    }

    public  double getadEndTimeLimit(int offset_second)
    {
        double ret = -1;
        ret = Double.parseDouble(startTimeInSeconds) + Double.parseDouble(durationInSeconds) + (offset_second);
        return ret;
    }


}
