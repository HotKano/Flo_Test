package kr.anymobi.floproject.service;

public class Const {

    // ================ URL ================ //
    public static final String BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/";
    public static final String TEST_BASE_URL = "http://dpsw23.dothome.co.kr/";

    // ================ EventBus Flag ================ //
    public static final String MUSIC_SERVICE_PREPARED = "kr.anymobi.floproject.service.onPrepared";
    public static final String MUSIC_SERVICE_COMPLETION = "kr.anymobi.floproject.service.onCompletion";
    public static final String MUSIC_SERVICE_ERROR = "kr.anymobi.floproject.service.onError";

    // ================ Handler MSG ================ //
    public static final int MUSIC_SERVICE_PREPARED_MSG = 1000;

    // ================ String pattern ================ //
    public static final String PLAYER_TIME_PATTERN = "mm:ss";
    public static final String LYRICS_TIME_PATTERN = "mm:ss:SSS";

}
