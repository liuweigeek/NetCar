package com.imagine.scott.netcar.operation;

/**
 * Created by Scott on 2016/5/17.
 */
public class Transform {
    public static String meterToKilo(int meter) {
        String temp = Integer.toString(meter);
        if (temp.length() < 4) {
            return temp + "米";
        }
        return meter / 1000 + "." + Integer.toString(meter % 1000).substring(0, 1) + "公里";
    }
    public static String secToMin(int time) {
        if (time > 3600) {
            int t = time / 3600;
            int m = (time % 3600) / 60;
            int s = time - 3600 * t - 60 * m;
            return t + "小时" + m + "分" + s + "秒";
        } else if (time > 60) {
            int m = time / 60;
            int s = time - 60 * m;
            return  m + "分" + s + "秒";
        } else {
            return time + "秒";
        }
    }
}
