package com.imagine.scott.netcar.bean;

/**
 * Created by Scott on 2016/5/25.
 */
public class VehicleBrand {
    private Integer id;
    private String vehicleBrandLogo;
    private String vehicleBrandName;
    private String vehicleBrandZhName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleBrandLogo() {
        return vehicleBrandLogo;
    }

    public void setVehicleBrandLogo(String vehicleBrandLogo) {
        this.vehicleBrandLogo = vehicleBrandLogo;
    }

    public String getVehicleBrandName() {
        return vehicleBrandName;
    }

    public void setVehicleBrandName(String vehicleBrandName) {
        this.vehicleBrandName = vehicleBrandName;
    }

    public String getVehicleBrandZhName() {
        return vehicleBrandZhName;
    }

    public void setVehicleBrandZhName(String vehicleBrandZhName) {
        this.vehicleBrandZhName = vehicleBrandZhName;
    }
}
