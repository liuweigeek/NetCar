package com.imagine.scott.netcar.bean;

import java.io.Serializable;

/**
 * Created by Scott on 2016/5/5.
 *
 * 汽车
 */
public class Car implements Serializable {

    private Integer id;

    private String vehicleBrand;   //汽车品牌
    private String vehicleBrandZh;   //汽车中文品牌
    private String vehicleModel;   //汽车型号
    private Integer doorNum;    //汽车车门数量
    private Integer seatNum;    //汽车座位数

    /********Getters and Setters********/

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getVehicleBrand() {
        return vehicleBrand;
    }
    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }
    public String getVehicleBrandZh() {
        return vehicleBrandZh;
    }
    public void setVehicleBrandZh(String vehicleBrandZh) {
        this.vehicleBrandZh = vehicleBrandZh;
    }
    public String getVehicleModel() {
        return vehicleModel;
    }
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    public Integer getDoorNum() {
        return doorNum;
    }
    public void setDoorNum(Integer doorNum) {
        this.doorNum = doorNum;
    }
    public Integer getSeatNum() {
        return seatNum;
    }
    public void setSeatNum(Integer seatNum) {
        this.seatNum = seatNum;
    }
}
