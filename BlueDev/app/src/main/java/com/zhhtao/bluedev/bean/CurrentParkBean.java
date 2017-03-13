package com.zhhtao.bluedev.bean;

import java.io.Serializable;

/**
 * Created by ZhangHaiTao on 2017/1/10.
 */

public class CurrentParkBean implements Serializable{
    String parkName; //停车场
    String CarPort; //停车位
    int price = 15; //价格
    String cur_price; //当前花费
    long startTime;

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getCarPort() {
        return CarPort;
    }

    public void setCarPort(String carPort) {
        CarPort = carPort;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCur_price() {
        return cur_price;
    }

    public void setCur_price(String cur_price) {
        this.cur_price = cur_price;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
