package jp.co.stream.jstplayerdemo;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

/**
 * Created by nileshdeokar on 05/10/2017.
 */

public class SamplePlayerModel implements Parcelable {

    private String name;
    private Uri uri;
    private Boolean dvr;
    private Boolean live;

    public SamplePlayerModel(){}


    public SamplePlayerModel(String name, String uri, Boolean dvr, Boolean live){
        this.name = name;
        this.uri = Uri.parse(uri);
        this.dvr = dvr;
        this.live = live;
    }

    public SamplePlayerModel(String name, Uri uri, Boolean dvr, Boolean live){
        this.name = name;
        this.uri = uri;
        this.dvr = dvr;
        this.live = live;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected SamplePlayerModel(Parcel in) {
        name = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        dvr = in.readBoolean();
        dvr = in.readBoolean();
    }

    public static final Creator<SamplePlayerModel> CREATOR = new Creator<SamplePlayerModel>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public SamplePlayerModel createFromParcel(Parcel in) {
            return new SamplePlayerModel(in);
        }

        @Override
        public SamplePlayerModel[] newArray(int size) {
            return new SamplePlayerModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public  Boolean getDvr(){return dvr;}
    public  Boolean getLive(){return live;}

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(uri, i);
        parcel.writeBoolean(dvr);
        parcel.writeBoolean(live);
    }
}
