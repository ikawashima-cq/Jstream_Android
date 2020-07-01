package jp.co.stream.clientsideresponse.config;

/**
 * プレイヤー設定用クラス
 * @author J-Stream Inc
 * @version 0.0.1
 */
public class PlayerConfig {
    /**
     * 実際にBeaconを送信するかどうかの判定に用いる
     * True：コンソールに表示
     * False：実際に送信
     */
    public boolean debugSendBeaconFlg;
    /**
     * テストURLを用いるかどうかの判定に用いる
     * True：テストURL
     * False：本番URL
     */
    public boolean testSendBeaconFlg;
    /**
     * 番組がDVRかLIVEかの判定に用いる
     * True：LIVE
     * False：DVR
     */
    public boolean liveFlg;
    /**
     * LIVE動作時、MediaTailorからの広告開始～終了時間に対しての
     * オフセット時間設定に用いる
     * ※DVR動作時はプレイヤーのカレント時間を使用するため、
     * この値は用いない
     */
    public long live_SSAI_LiveCurrent_Offset_MilliSecond;
    /**
     * MediaTailorからの広告情報に対して、キューとして貯める情報の
     * 範囲設定に用いる広告開始時間
     */
    public int SSAI_Tracking_Offset_Second_Start;
    /**
     * MediaTailorからの広告情報に対して、キューとして貯める情報の
     * 範囲設定に用いる広告終了時間
     */
    public int SSAI_Tracking_Offset_Second_End;
    /**
     * 番組ID
     */
    public String mediaId;
    /**
     * ページURL
     * siteIDの判定に用いる
     */
    public String pageUrl;
    /**
     * advertisingId
     */
    public String advertisingId;
    /**
     * ビーコンタイムアウト時間
     */
    public int beaconTimeout;
    /**
     * ビーコンのResponseが200以外の場合に行われるRetryの実施回数
     */
    public int beaconRetry;


    public PlayerConfig(
            boolean debugSendBeaconFlg,
            boolean testSendBeaconFlg,
            boolean liveFlg,
            long live_SSAI_LiveCurrent_Offset_MilliSecond,
            int SSAI_Tracking_Offset_Second_Start,
            int SSAI_Tracking_Offset_Second_End,
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
        this.live_SSAI_LiveCurrent_Offset_MilliSecond = live_SSAI_LiveCurrent_Offset_MilliSecond;
        this.SSAI_Tracking_Offset_Second_Start = SSAI_Tracking_Offset_Second_Start;
        this.SSAI_Tracking_Offset_Second_End = SSAI_Tracking_Offset_Second_End;
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
            long live_SSAI_LiveCurrent_Offset_MilliSecond,
            int SSAI_Tracking_Offset_Second_Start,
            int SSAI_Tracking_Offset_Second_End,
            String mediaId,
            String pageUrl,
            int beaconTimeout,
            int beaconRetry
    )
    {
        this.debugSendBeaconFlg = debugSendBeaconFlg;
        this.testSendBeaconFlg = testSendBeaconFlg;
        this.liveFlg = liveFlg;
        this.live_SSAI_LiveCurrent_Offset_MilliSecond = live_SSAI_LiveCurrent_Offset_MilliSecond;
        this.SSAI_Tracking_Offset_Second_Start = SSAI_Tracking_Offset_Second_Start;
        this.SSAI_Tracking_Offset_Second_End = SSAI_Tracking_Offset_Second_End;
        this.mediaId = mediaId;
        this.pageUrl = pageUrl;
        this.beaconTimeout = beaconTimeout;
        this.beaconRetry = beaconRetry;
    }

}
