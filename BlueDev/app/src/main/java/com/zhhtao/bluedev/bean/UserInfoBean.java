package com.zhhtao.bluedev.bean;

import java.io.Serializable;

/**
 * @author ZhangHaiTao
 * @ClassName: UserInfoBean
 * Description: TODO
 * @date 2016/5/30 12:30
 */
public class UserInfoBean implements Serializable {
    private static final long serialVersionUID = 7247714666080613254L;

    String phone;
    String password;

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
