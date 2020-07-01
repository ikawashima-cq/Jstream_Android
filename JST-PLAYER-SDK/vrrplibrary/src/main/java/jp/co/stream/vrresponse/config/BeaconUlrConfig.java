package jp.co.stream.vrresponse.config;

public class BeaconUlrConfig {
    public String beacon_vr_live_video;
    public String beacon_vr_live_video_test;
    public String beacon_vr_live_ad;
    public String beacon_vr_live_ad_test;
    public String beacon_vr_video;
    public String beacon_vr_video_test;
    public String beacon_vr_ad;
    public String beacon_vr_ad_test;

    public BeaconUlrConfig(
            String beacon_vr_live_video,
            String beacon_vr_live_video_test,
            String beacon_vr_live_ad,
            String beacon_vr_live_ad_test,
            String beacon_vr_video,
            String beacon_vr_video_test,
            String beacon_vr_ad,
            String beacon_vr_ad_test
    )
    {
        this.beacon_vr_live_video = beacon_vr_live_video;
        this.beacon_vr_live_video_test = beacon_vr_live_video_test;
        this.beacon_vr_live_ad = beacon_vr_live_ad;
        this.beacon_vr_live_ad_test = beacon_vr_live_ad_test;
        this.beacon_vr_video = beacon_vr_live_video;
        this.beacon_vr_video_test = beacon_vr_live_video_test;
        this.beacon_vr_ad = beacon_vr_live_ad;
        this.beacon_vr_ad_test = beacon_vr_live_ad_test;
    }
}
