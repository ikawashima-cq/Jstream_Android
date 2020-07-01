package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;

public class trackingEvents  extends RealmObject {

    private RealmList<String> beaconUrls;
    public RealmList<String> getbeaconUrls() {return beaconUrls;}
    public void setbeaconUrls(RealmList<String> beaconUrls) {this.beaconUrls = beaconUrls;}

/*
    private String duration;
    public String getduration() {return duration;}
    public void setduration(String duration) {this.duration = duration;}
*/

    private String durationInSeconds;
    public String getdurationInSeconds() {return durationInSeconds;}
    public void setdurationInSeconds(String durationInSeconds) {this.durationInSeconds = durationInSeconds;}

    private String eventId;
    public String geteventId() {return eventId;}
    public void seteventId(String eventId) {this.eventId = eventId;}

    private String eventType;
    public String geteventType() {return eventType;}
    public void seteventType(String eventType) {this.eventType = eventType;}

/*
    private String startTime;
    public String getstartTime() {return startTime;}
    public void setstartTime(String startTime) {this.startTime = startTime;}
*/

    private String startTimeInSeconds;
    public String getstartTimeInSeconds() {return startTimeInSeconds;}
    public void setstartTimeInSeconds(String startTimeInSeconds) {this.startTimeInSeconds = startTimeInSeconds;}

    @LinkingObjects("trackingEvents")
    private final RealmResults<ads> owners = null;
    public RealmResults<ads> getowners() {return owners;}

    public static int eventTypeSequencesLimit = 10000;

    public int geteventTypeSequences() {
        int ret = -1;
        switch (eventType.toLowerCase())
        {
            case "impression":
                ret = 100;
                break;
            case "start":
                ret = 101;
                break;
            case "firstquartile":
                ret = 102;
                break;
            case "midpoint":
                ret = 103;
                break;
            case "thirdquartile":
                ret = 104;
                break;
            case "complete":
                ret = 105;
                break;
            default:
                ret = eventTypeSequencesLimit;
                break;
        }
        return ret;
    }

}
