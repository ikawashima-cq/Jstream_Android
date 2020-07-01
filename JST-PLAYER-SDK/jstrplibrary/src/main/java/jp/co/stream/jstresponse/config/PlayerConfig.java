package jp.co.stream.jstresponse.config;

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
            boolean liveflg,
            int beaconTimeout,
            int beaconRetry
    )
    {
        this.debugSendBeaconFlg = debugSendBeaconFlg;
        this.testSendBeaconFlg = testSendBeaconFlg;
        this.liveFlg = liveflg;
        this.beaconTimeout = beaconTimeout;
        this.beaconRetry = beaconRetry;
    }

}
