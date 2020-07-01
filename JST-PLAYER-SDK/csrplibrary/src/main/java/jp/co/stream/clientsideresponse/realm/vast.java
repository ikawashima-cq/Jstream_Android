package jp.co.stream.clientsideresponse.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class vast extends RealmObject {
    private RealmList<avails> avails;
    public RealmList<avails> getavails() {return avails;}
    public void setavails(RealmList<avails> ads) {this.avails = avails;}
}
