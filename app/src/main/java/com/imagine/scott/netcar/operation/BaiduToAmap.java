package com.imagine.scott.netcar.operation;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.imagine.scott.netcar.activity.MainActivity;

/**
 * Created by Scott on 2016/5/16.
 */
public class BaiduToAmap {
    public static LatLng convert(double lat, double lng) {
        LatLng sourceLatLng = new LatLng(lat, lng);
        CoordinateConverter converter = new CoordinateConverter(MainActivity.mainActivity);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.BAIDU);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
}
