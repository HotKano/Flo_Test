package kr.anymobi.floproject.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static kr.anymobi.floproject.service.Const.LYRICS_TIME_PATTERN;

public class CommFunc {

    @SuppressLint("SimpleDateFormat")
    // 현재 시간을 요청 받은 pattern 으로 포맷하여 String 반환함.
    public static String getTimeData(long time, String pattern) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return formatter.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    // Lyrics List 중에서 현재 시간 이전과 이 후에 가사를 받을 수 있는 시간대인지를 확인한다.
    public static boolean compareTimeZone(String nowTime, String preTime, String postTime) {
        try {
            Date nowDate = new SimpleDateFormat(LYRICS_TIME_PATTERN).parse(nowTime);
            Date preDate = new SimpleDateFormat(LYRICS_TIME_PATTERN).parse(preTime);
            if (preDate != null && nowDate != null)
                if (!TextUtils.isEmpty(postTime)) {
                    Date postDate = new SimpleDateFormat(LYRICS_TIME_PATTERN).parse(postTime);
                    if (postDate != null)
                        return nowDate.compareTo(preDate) >= 0 && nowDate.compareTo(postDate) < 0;

                } else {
                    return nowDate.compareTo(preDate) >= 0;
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressLint("SimpleDateFormat")
    // Lyrics List 중의 가장 첫번째 곡의 시간과 현재 시간을 비교한다.
    public static boolean compareTimeZoneBeforeFirst(String nowTime, String firstTime) {
        try {
            Date nowDate = new SimpleDateFormat(LYRICS_TIME_PATTERN).parse(nowTime);
            Date preDate = new SimpleDateFormat(LYRICS_TIME_PATTERN).parse(firstTime);
            if (preDate != null)
                return preDate.compareTo(nowDate) > 0;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressLint("SimpleDateFormat")
    // 패턴으로 된 스트링으로부터 int time 반환.
    public static int getTimeFromPattern(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(LYRICS_TIME_PATTERN);
            // 협정 세계시
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = sdf.parse(data);

            if (d != null)
                return (int) d.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 화면 해상도 X 반환
    public static int getXPixel(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    // 화면 해상도 Y 반환
    public static int getYPixel(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }
}
