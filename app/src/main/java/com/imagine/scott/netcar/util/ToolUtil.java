/**
 * ToolUtil.java
 * com.chad.android.library.util
 * <p>
 * <p>
 * ver     date      		author
 * ---------------------------------------
 * 2015-4-9 		chadwii
 * <p>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.imagine.scott.netcar.util;

import android.content.Context;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ClassName:ToolUtil
 *
 * @author chadwii
 * @version
 * @since Ver 1.1
 * @Date 2015-4-9  5:17:32
 *
 * @see
 */
public class ToolUtil {
    /**one hour in ms*/
    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    /**one minute in ms*/
    private static final int ONE_MIN = 1 * 60 * 1000;
    /**one second in ms*/
    private static final int ONE_SECOND = 1 * 1000;

    private static int sScreenWidth;
    private static int sScreenHeight;
    private static float sDensity;

    public static int dp2px(Context ctx, int dp) {
        if (sDensity == 0) {
            sDensity = ctx.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * sDensity + 0.5f);
    }

    public static int px2dp(Context ctx, int px) {
        if (sDensity == 0) {
            sDensity = ctx.getResources().getDisplayMetrics().density;
        }
        return (int) (px / sDensity + 0.5f);
    }

    public static int getScreenWidth(Context ctx) {
        if (sScreenWidth == 0) {
            sScreenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Context ctx) {
        if (sScreenHeight == 0) {
            sScreenHeight = ctx.getResources().getDisplayMetrics().heightPixels;
        }
        return sScreenHeight;
    }

    /**HH:mm:ss*/
    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().equals("") || str.trim().equals("null")) {
            return true;
        }
        return false;
    }

    public static int isInTime(String time) throws IllegalArgumentException {
        if (TextUtils.isEmpty(time) || !time.contains("-") || !time.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + time);
        }
        String[] args = time.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String nowStr = sdf.format(new Date(System.currentTimeMillis()));
        try {
            long now = sdf.parse(nowStr).getTime();
            long start = sdf.parse(args[0]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "23:59";
            }
            long end = sdf.parse(args[1]).getTime();
            if (now > end) {
                return -1;
            } else if (now >= start && now <= end) {
                return 0;
            } else {
                return 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + time);
        }
    }
}

