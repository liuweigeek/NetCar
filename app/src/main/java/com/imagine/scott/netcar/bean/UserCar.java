package com.imagine.scott.netcar.bean;

import java.io.Serializable;

public class UserCar implements Serializable {

	private Integer id;

	private int version;

	private Car car;	//车型
	private String licensePlateNumber;  //汽车车牌号
	private String engineNum;  //汽车发动机号
	private String vin;  //汽车车架号
	private Integer mileage; //汽车里程数
	private Integer lastMaintainMile;
	private Integer avgEcon;    //平均油耗
	private Boolean lampWell;   //汽车车灯状况
	private Boolean engineWell;   //汽车发动机状况
	private Boolean transmissionWell;   //汽车变速箱状况
	private Integer oilMass;  //汽车油量
	private Boolean tirePressure;   //胎压
	private Boolean airSacSafe;	//气囊安全状况

	/********Getters and Setters********/

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public String getLicensePlateNumber() {
		return licensePlateNumber;
	}
	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}
	public String getEngineNum() {
		return engineNum;
	}
	public void setEngineNum(String engineNum) {
		this.engineNum = engineNum;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public Integer getMileage() {
		return mileage;
	}
	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}
	public Integer getLastMaintainMile() {
		return lastMaintainMile;
	}
	public void setLastMaintainMile(Integer lastMaintainMile) {
		this.lastMaintainMile = lastMaintainMile;
	}
	public Integer getAvgEcon() {
		return avgEcon;
	}
	public void setAvgEcon(Integer avgEcon) {
		this.avgEcon = avgEcon;
	}
	public Boolean getLampWell() {
		return lampWell;
	}
	public void setLampWell(Boolean lampWell) {
		this.lampWell = lampWell;
	}
	public Boolean getEngineWell() {
		return engineWell;
	}
	public void setEngineWell(Boolean engineWell) {
		this.engineWell = engineWell;
	}
	public Boolean getTransmissionWell() {
		return transmissionWell;
	}
	public void setTransmissionWell(Boolean transmissionWell) {
		this.transmissionWell = transmissionWell;
	}
	public Integer getOilMass() {
		return oilMass;
	}
	public void setOilMass(Integer oilMass) {
		this.oilMass = oilMass;
	}
	public Boolean getTirePressure() {
		return tirePressure;
	}
	public void setTirePressure(Boolean tirePressure) {
		this.tirePressure = tirePressure;
	}
	public Boolean getAirSacSafe() {
		return airSacSafe;
	}
	public void setAirSacSafe(Boolean airSacSafe) {
		this.airSacSafe = airSacSafe;
	}
}
