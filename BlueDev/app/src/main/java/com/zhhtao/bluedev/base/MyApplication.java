package com.zhhtao.bluedev.base;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.zhhtao.bluedev.utils.SharedPreferencesUtil;

/**
 * @author ZhangHaiTao
 * @ClassName: MyApplication
 * Description: TODO
 * @date 2016/5/29 15:59
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        initLeanCloud();
        initLogger();
//        SocketUtil.getInstance().openSocket();

        //设置初始值 只一次
//        SharedPreferencesUtil.putLong(mContext,"CUR_MONEY", 1600);
//        SharedPreferencesUtil.putLong(mContext,"PARK_TIMES", 12);


        //读取本地数据
        long m = SharedPreferencesUtil.getLong(mContext, "CUR_MONEY", 0);
        if (m != 0) {
            MyConstant.CUR_MONEY = m;
        }

        long t = SharedPreferencesUtil.getLong(mContext, "PARK_TIMES", 0);
        if (t != 0) {
            MyConstant.PARK_TIMES = t;
        }
    }



    private void initLeanCloud() {
        AVOSCloud.initialize(this, "I4AiOzuTXH58hlwzmavFRFuY-gzGzoHsz",
                "VNNdwm14AProYqwFyd9DkRb6");
    }

    public static Context getAppContext() {
        return mContext;
    }

    private void initLogger() {
        if (MyConstant.DEBUG)
            Logger.init("ZHT").logLevel(LogLevel.FULL);
    }
}
