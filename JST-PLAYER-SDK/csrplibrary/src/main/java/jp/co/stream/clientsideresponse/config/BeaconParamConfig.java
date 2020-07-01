package jp.co.stream.clientsideresponse.config;

/**
 * CSRPビーコン設定用クラス
 * @author J-Stream Inc
 * @version 0.0.1
 */
public class BeaconParamConfig {
    /**
     * 番組ID
     */
    public String program;
    /**
     * 郵便番号(7桁)
     */
    public String postal;
    /**
     * 性別
     */
    public String gender;
    /**
     * 年齢
     */
    public String age;

    public  BeaconParamConfig(
            String program,
            String postal,
            String gender,
            String age
    )
    {
        this.program = program;
        this.postal = postal;
        this.gender = gender;
        this.age = age;
    }

    public  BeaconParamConfig(
            String postal,
            String gender,
            String age
    )
    {
        this.postal = postal;
        this.gender = gender;
        this.age = age;
    }
}
