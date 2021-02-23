package com.imagine.scott.netcar.operation;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.imagine.scott.netcar.bean.GasStation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Scott on 2016/5/11.
 */
public class GasJSONOperate {

    public static ArrayList<Map<String, Object>> getGasInformation(String jonString)
            throws Exception {

        ArrayList<Map<String, Object>> all = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jonString);
        JSONObject jsonResult = jsonObject.getJSONObject("result");
        JSONArray jsonArray = jsonResult.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, Object> map = new HashMap<>();
            JSONObject jsonInfo = jsonArray.getJSONObject(i);
            map.put("id", jsonInfo.optString("id"));
            map.put("name", jsonInfo.optString("name"));
            map.put("area", jsonInfo.optString("area"));
            map.put("areaname", jsonInfo.optString("areaname"));
            map.put("address", jsonInfo.optString("address"));
            map.put("brandname", jsonInfo.optString("brandname"));
            map.put("type", jsonInfo.optString("type"));
            map.put("discount", jsonInfo.optString("discount"));
            map.put("exhaust", jsonInfo.optString("exhaust"));
            map.put("position", jsonInfo.optString("position"));
            map.put("lon", jsonInfo.optString("lon"));
            map.put("lat", jsonInfo.optString("lat"));

            JSONObject jsonPrice = jsonInfo.optJSONObject("price");
            JSONObject jsonGasPrice = jsonInfo.optJSONObject("gastprice");
            map.put("price90", jsonPrice.optString("E90"));
            map.put("price93", jsonPrice.optString("E93"));
            map.put("price97", jsonPrice.optString("E97"));
            map.put("price0", jsonPrice.optString("E0"));

            if (jsonGasPrice != null) {
                map.put("gasprice93", TextUtils.isEmpty(jsonGasPrice.optString("93#")) ? jsonGasPrice.optString("92#") : jsonGasPrice.optString("93#"));
                map.put("gasprice97", TextUtils.isEmpty(jsonGasPrice.optString("97#")) ? jsonGasPrice.optString("95#") : jsonGasPrice.optString("97#"));
                map.put("gasprice0", jsonGasPrice.optString("0#车柴"));
            } else {
                map.put("gasprice93", "");
                map.put("gasprice97", "");
                map.put("gasprice0", "");
            }

            map.put("fwlsmc", jsonInfo.optString("fwlsmc"));
            map.put("distance", jsonInfo.optInt("distance"));
            all.add(map);
        }
        return all;
    }

    public static ArrayList<GasStation> getGasStations(ArrayList<Map<String, Object>> all) {
        TreeSet<GasStation> gasStationSet = new TreeSet<>();
        for (Map<String, Object> gasInfo : all) {
            GasStation gasStation = new GasStation();
            gasStation.setId(Integer.parseInt((String) gasInfo.get("id")));
            gasStation.setName((String) gasInfo.get("name"));
            gasStation.setArea(Integer.parseInt((String) gasInfo.get("area")));
            gasStation.setAreaname((String) gasInfo.get("areaname"));
            gasStation.setAddress((String) gasInfo.get("address"));
            gasStation.setBrandname((String) gasInfo.get("brandname"));
            gasStation.setType((String) gasInfo.get("type"));
            gasStation.setDiscount((String) gasInfo.get("discount"));
            gasStation.setExhaust((String) gasInfo.get("exhaust"));
            gasStation.setPosition((String) gasInfo.get("position"));

            double lat = Double.parseDouble((String) gasInfo.get("lat"));
            double lng = Double.parseDouble((String) gasInfo.get("lon"));
            LatLng aMapLatLng = BaiduToAmap.convert(lat, lng);
            gasStation.setLat(aMapLatLng.latitude);
            gasStation.setLon(aMapLatLng.longitude);

            String price0 = TextUtils.isEmpty((String) gasInfo.get("gasprice0")) ? (String) gasInfo.get("price0") : (String) gasInfo.get("gasprice0");
            String price93 = TextUtils.isEmpty((String) gasInfo.get("gasprice93")) ? (String) gasInfo.get("price93") : (String) gasInfo.get("gasprice93");
            String price97 = TextUtils.isEmpty((String) gasInfo.get("gasprice97")) ? (String) gasInfo.get("price97") : (String) gasInfo.get("gasprice97");
            String price90 = (String) gasInfo.get("price90");

            gasStation.setGasPrice_0(Float.parseFloat(price0));
            gasStation.setGasPrice_93(Float.parseFloat(price93));
            gasStation.setGasPrice_97(Float.parseFloat(price97));
            gasStation.setGasPrice_90(Float.parseFloat(price90));

            gasStation.setFwlsmc((String) gasInfo.get("fwlsmc"));
            gasStation.setDistance((Integer) gasInfo.get("distance"));
            gasStationSet.add(gasStation);
        }
        ArrayList<GasStation> gasStationList = new ArrayList<>();
        gasStationList.addAll(gasStationSet);
        return new ArrayList<>(gasStationList.subList(0, 10));
    }

    public static Map<String, Object> getLatLngInfo(String jonString) throws Exception {
        JSONObject jsonObject = new JSONObject(jonString);
        JSONObject jsonResult = jsonObject.getJSONObject("result");
        Map<String, Object> all = new HashMap<>();
        all.put("lng", jsonResult.getDouble("lng"));
        all.put("lat", jsonResult.getDouble("lat"));
        all.put("off_lng", jsonResult.getDouble("off_lng"));
        all.put("off_lat", jsonResult.getDouble("off_lat"));
        all.put("type", jsonResult.getInt("type"));
        return all;
    }
}
