package com.zhhtao.bluedev.base;

/**
 * @author ZhangHaiTao
 * @ClassName: MyConstant
 * Description: TODO
 * @date 2016/5/29 15:27
 */
public class MyConstant {
    public static final boolean DEBUG = true;

    public static final String APP_NAME = "自助终端";

    public static String USER_ID = "";
    public static String LEANCLOUD_SAVE_ID = "";

    public static long CUR_MONEY = 1528; //用户当前余额
    public static long PARK_TIMES = 13; //用户当前停车次数
    public static String isUseNow = "isUseNow"; //是否正在使用停车位


    //user 的属性
    public static final String REGIST_USER = "BleDevUserInfo";
    public static final String REGIST_USER_KEY_Phone = "phone";
    public static final String REGIST_USER_KEY_PASSWOED = "password";
    public static final String REGIST_USER_KEY_MONEY = "money";
    public static final String REGIST_USER_KEY_TIMES = "park_times";



    //    public static final String SERVER_IP = "haitao.uicp.io";
    public static final String SERVER_IP = "15bm326540.51mypc.cn";
    //    public static final int PORT = 23712;
    public static final int PORT = 20005;



}
