package com.imagine.scott.netcar.bean;

import java.io.Serializable;

/**
 * Created by Scott on 2016/5/12.
 */
public class GasStation implements Serializable, Comparable<GasStation> {
    private int id; //加油站id
    private String name;    //加油站名称
    private int area;   //加油站所在地邮编
    private String areaname;    //加油站所在市区
    private String address; //加油站地址
    private String brandname;   //加油站品牌
    private String type;    //加油站类型
    private String discount;    //加油站折扣信息
    private String exhaust; //排放标准
    private String position;    //加油站Google坐标
    private double lon;   //加油站百度纬度
    private double lat;    //加油站百度经度
    private float gasPrice_90;
    private float gasPrice_93;  //#93汽油价格
    private float gasPrice_97;  //#97汽油价格
    private float gasPrice_0;   //柴油价格
    private String fwlsmc;  //周边服务
    private int distance;   //加油站距离

    public int compareTo(GasStation gasStation) {
        if (this.distance > gasStation.getDistance()) {
            return 1;
        } else if (this.distance < gasStation.getDistance()) {
            return -1;
        } else {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getExhaust() {
        return exhaust;
    }

    public void setExhaust(String exhaust) {
        this.exhaust = exhaust;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public float getGasPrice_90() {
        return gasPrice_90;
    }

    public void setGasPrice_90(float gasPrice_90) {
        this.gasPrice_90 = gasPrice_90;
    }

    public float getGasPrice_93() {
        return gasPrice_93;
    }

    public void setGasPrice_93(float gasPrice_93) {
        this.gasPrice_93 = gasPrice_93;
    }

    public float getGasPrice_97() {
        return gasPrice_97;
    }

    public void setGasPrice_97(float gasPrice_97) {
        this.gasPrice_97 = gasPrice_97;
    }

    public float getGasPrice_0() {
        return gasPrice_0;
    }

    public void setGasPrice_0(float gasPrice_0) {
        this.gasPrice_0 = gasPrice_0;
    }

    public String getFwlsmc() {
        return fwlsmc;
    }

    public void setFwlsmc(String fwlsmc) {
        this.fwlsmc = fwlsmc;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
