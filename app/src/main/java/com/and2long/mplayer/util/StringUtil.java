package com.and2long.mplayer.util;

/**
 * Created by L on 2016/12/3.
 */

public class StringUtil {

    public static String formatMediaDuration(long duration) {
        int HOUR = 60 * 60 * 1000;
        int MINUTE = 60 * 1000;
        int SECOND = 1000;

        int hour = (int) (duration / HOUR);
        long remainTime = duration % HOUR;

        int minute = (int) (remainTime / MINUTE);
        remainTime = remainTime % MINUTE;

        int second = (int) (remainTime / SECOND);
        if (hour == 0) {
            return String.format("%02d:%02d", minute, second);
        } else {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }
}
