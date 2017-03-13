package com.zhhtao.bluedev.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ZhangHaiTao
 * @ClassName: BaseActivity
 * Description: TODO
 * @date 2016/5/29 14:36
 */
public class BaseActivity extends AppCompatActivity {
    public Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        SocketUtil.getInstance().openSocket();
    }
}
