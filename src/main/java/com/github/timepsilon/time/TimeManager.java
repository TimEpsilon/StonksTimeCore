package com.github.timepsilon.time;

public class TimeManager {

    public static final int BASE_TIME = 4 * 60 * 60; // 4h TODO : Make this a config

    public static final int TIME_TO_MONEY = 2; // Todo : Make this a config

    public static String secondsToTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

}
