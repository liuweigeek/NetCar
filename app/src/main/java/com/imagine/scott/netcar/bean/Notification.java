package com.imagine.scott.netcar.bean;

import java.util.Date;

/**
 * Created by Scott on 2016/5/8.
 */
public class Notification {

    private Integer id;
    private String title;
    private String text;
    private Boolean haspush;
    private Date date;
    private UserCar userCar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getHaspush() {
        return haspush;
    }

    public void setHaspush(Boolean haspush) {
        this.haspush = haspush;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserCar getUserCar() {
        return userCar;
    }

    public void setUserCar(UserCar userCar) {
        this.userCar = userCar;
    }
}
