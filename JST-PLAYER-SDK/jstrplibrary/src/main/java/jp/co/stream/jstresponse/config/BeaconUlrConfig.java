package jp.co.stream.jstresponse.config;

public class BeaconUlrConfig {
    /**
     * JStream用視聴ビーコンURL
     */
    public String beacon_jst_video;
    /**
     * JStream用視聴ビーコンテストURL
     */
    public String beacon_jst_video_test;
    /**
     * JStream用広告ビーコンURL
     */
    public String beacon_jst_ad;
    /**
     * JStream用広告ビーコンテストURL
     */
    public String beacon_jst_ad_test;
    /**
     * JStream用確認ビーコンURL
     */
    public String beacon_jst_trace;
    /**
     * JStream用確認ビーコンテストURL
     */
    public String beacon_jst_trace_test;

    public BeaconUlrConfig(
            String beacon_jst_video,
            String beacon_jst_video_test,
            String beacon_jst_ad,
            String beacon_jst_ad_test,
            String beacon_jst_trace,
            String beacon_jst_trace_test
    )
    {
        this.beacon_jst_video = beacon_jst_video;
        this.beacon_jst_video_test = beacon_jst_video_test;
        this.beacon_jst_ad = beacon_jst_ad;
        this.beacon_jst_ad_test = beacon_jst_ad_test;
        this.beacon_jst_trace = beacon_jst_trace;
        this.beacon_jst_trace_test = beacon_jst_trace_test;
    }
}
