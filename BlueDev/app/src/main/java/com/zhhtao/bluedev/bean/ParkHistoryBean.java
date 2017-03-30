package com.zhhtao.bluedev.bean;

import java.io.Serializable;

/**
 * Created by ZhangHaiTao on 2017/1/10.
 */

public class ParkHistoryBean implements Serializable {
    String parkName; //停车场
    String CarPort; //停车位
    int real_price; //实际花费
    long startTime;//开始时间
    long endTime;//结束时间
    String durationTime;//实际停车时间
    String userId;//用户ID

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public int getReal_price() {
        return real_price;
    }

    public void setReal_price(int real_price) {
        this.real_price = real_price;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }
}
