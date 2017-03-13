package com.zhhtao.bluedev.bean;

import java.io.Serializable;

/**
 * @author ZhangHaiTao
 * @ClassName: ParkInfoBean
 * Description: TODO
 * @date 2016/8/5 20:49
 */
public class ParkInfoBean implements Serializable{
    String name;
    int total;
    int canUse;
    String address;
    String contact;
    String price;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCanUse() {
        return canUse;
    }

    public void setCanUse(int canUse) {
        this.canUse = canUse;
    }
}
