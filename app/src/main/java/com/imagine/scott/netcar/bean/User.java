package com.imagine.scott.netcar.bean;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 2016/5/5.
 */
public class User {

    private Integer id;
    private String phone;
    private String username;	//用户名
    private String password;	//密码
    private String sex;		//性别
    private String drivingYears;	//驾龄
    private String region;	//地区
    private String headimage;	//头像存储路径
    private String background;	//背景图片存储路径

    private List<UserCar> userCars = new ArrayList<>();	//拥有的车辆列表
    private List<Order> orders = new ArrayList<>();	//订单列表

    private Date registerDate;	//帐号注册时间
    private Date modifyDate;	//帐号信息修改时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDrivingYears() {
        return drivingYears;
    }

    public void setDrivingYears(String drivingYears) {
        this.drivingYears = drivingYears;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<UserCar> getUserCars() {
        return userCars;
    }

    public void setUserCars(List<UserCar> userCars) {
        this.userCars = userCars;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
