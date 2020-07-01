package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class mediaFiles  extends RealmObject {
    private RealmList<String> mediaFilesList;
    public RealmList<String>  getmediaFilesList() {return mediaFilesList;}
    public void setmediaFilesList(RealmList<String>   mediaFilesList) {this.mediaFilesList = mediaFilesList;}

    private String mezzanine;
    public String getmezzanine() {return mezzanine;}
    public void setmezzanine(String mezzanine) {this.mezzanine = mezzanine;}
}
