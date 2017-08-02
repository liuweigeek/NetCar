package com.imagine.scott.netcar.bean;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {

	private Integer id;

	private Date date;	//订单时间
	private String gasStation;	//加油站名称
    private String brandname;
	private Double gasLat;	//加油站经度
	private Double gasLng;	//加油站纬度
	private String oilType;	//加油类型
	private Double litre;	//加油升数
	private Double money;	//总金额

	/********Getters and Setters********/

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getGasStation() {
		return gasStation;
	}
	public void setGasStation(String gasStation) {
		this.gasStation = gasStation;
	}
	public String getBrandname() {
		return brandname;
	}
	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}
	public Double getGasLat() {
		return gasLat;
	}
	public void setGasLat(Double gasLat) {
		this.gasLat = gasLat;
	}
	public Double getGasLng() {
		return gasLng;
	}
	public void setGasLng(Double gasLng) {
		this.gasLng = gasLng;
	}
	public String getOilType() {
		return oilType;
	}
	public void setOilType(String oilType) {
		this.oilType = oilType;
	}
	public Double getLitre() {
		return litre;
	}
	public void setLitre(Double litre) {
		this.litre = litre;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	/********Getters and Setters********/
}
