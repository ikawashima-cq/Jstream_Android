package jp.co.stream.vrresponse.config;

public class BeaconParamConfig {
    public String vr_tagid1;
    public String vr_tagid2;
    public String id1;
    public String url;
    public String vr_opt1;
    public String vr_opt2;
    public String vr_opt3;
    public String vr_opt4;
    public String vr_opt5;
    public String vr_opt6;
    public String vr_opt8;
    public String vr_opt10;
    public String vr_opt15;
    public String vr_opt16;
    public String vr_tid;

    public  BeaconParamConfig(
            String vr_tagid1,
            String vr_tagid2,
            String id1,
            String url,
            String vr_opt1,
            String vr_opt2,
            String vr_opt3,
            String vr_opt4,
            String vr_opt5,
            String vr_opt6,
            String vr_opt8,
            String vr_opt10,
            String vr_opt15,
            String vr_opt16,
            String vr_tid
    )
    {
        this.vr_tagid1 = vr_tagid1;
        this.vr_tagid2 = vr_tagid2;
        this.id1 = id1;
        this.url = url;
        this.vr_opt1 = vr_opt1;
        this.vr_opt2 = vr_opt2;
        this.vr_opt3 = vr_opt3;
        this.vr_opt4 = vr_opt4;
        this.vr_opt5 = vr_opt5;
        this.vr_opt6 = vr_opt6;
        this.vr_opt8 = vr_opt8;
        this.vr_opt10 = vr_opt10;
        this.vr_opt15 = vr_opt15;
        this.vr_opt16 = vr_opt16;
        this.vr_tid = vr_tid;
    }

    public  BeaconParamConfig(
            String vr_tagid1,
            String vr_tagid2,
            String vr_opt1,
            String vr_opt6,
            String vr_opt15
    )
    {
        this.vr_tagid1 = vr_tagid1;
        this.vr_tagid2 = vr_tagid2;
        this.vr_opt1 = vr_opt1;
        this.vr_opt6 = vr_opt6;
        this.vr_opt15 = vr_opt15;

    }
}
