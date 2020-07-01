package jp.co.stream.vrresponse.config;

public class PlayerConfig {
    public boolean debugSendBeaconFlg;
    public boolean testSendBeaconFlg;
    public boolean liveFlg;
    public String mediaId;
    public String pageUrl;
    public String advertisingId;
    public int beaconTimeout;
    public int beaconRetry;


    public PlayerConfig(
            boolean debugSendBeaconFlg,
            boolean testSendBeaconFlg,
            boolean liveFlg,
            String mediaId,
            String pageUrl,
            String advertisingId,
            int beaconTimeout,
            int beaconRetry
    )
    {
        this.debugSendBeaconFlg = debugSendBeaconFlg;
        this.testSendBeaconFlg = testSendBeaconFlg;
        this.liveFlg = liveFlg;
        this.mediaId = mediaId;
        this.pageUrl = pageUrl;
        this.advertisingId = advertisingId;
        this.beaconTimeout = beaconTimeout;
        this.beaconRetry = beaconRetry;
    }

    public PlayerConfig(
            boolean debugSendBeaconFlg,
            boolean testSendBeaconFlg,
            boolean liveFlg,
            String mediaId,
            String pageUrl,
            int beaconTimeout,
            int beaconRetry
    )
    {
        this.debugSendBeaconFlg = debugSendBeaconFlg;
        this.testSendBeaconFlg = testSendBeaconFlg;
        this.liveFlg = liveFlg;
        this.mediaId = mediaId;
        this.pageUrl = pageUrl;
        this.beaconTimeout = beaconTimeout;
        this.beaconRetry = beaconRetry;
    }

}
