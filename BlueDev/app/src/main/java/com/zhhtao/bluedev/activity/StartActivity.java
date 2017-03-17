package com.zhhtao.bluedev.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.SharedPreferencesUtil;
import com.zhhtao.bluedev.utils.ZhtUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.btn_regist)
    Button mBtnRegist;
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    private Button mTestBtn1;
    private Button mTestBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        ButterKnife.bind(this);


        LogUtil.w("StartActivity");
    }

    @OnClick({R.id.btn_regist, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_regist:
//                ZhtUtils.gotoIntent(mContext, HomeActivityNew.class);//测试阶段
                ZhtUtils.gotoIntent(mContext, RegistWelcomeActivity.class);
                break;
            case R.id.btn_login:
                ZhtUtils.gotoIntent(mContext, LoginActivity.class);
                //                ZhtUtils.gotoIntent(mContext, PayActivity.class);
                break;

            case R.id.test_btn1:
//                byte[] cmd = {0x55,0x51,0x2d,0x00,0x01,0x51};
//                SocketUtil.getInstance().sendData(cmd);
                ZhtUtils.gotoIntent(mContext, SocketTestActivity.class);
                break;
            case R.id.test_btn2:
//                byte[] cmd2 = {0x55,0x51,0x2d,0x00,0x01,0x52};
//                SocketUtil.getInstance().sendData(cmd2);
                ZhtUtils.gotoIntent(mContext, com.zhhtao.bluedev.activity.BombPayActivity.class);
                break;
        }
    }


    private void test() {
        SharedPreferencesUtil.putInteger(mContext, "int", 100);
        Logger.i("int:" + SharedPreferencesUtil.getInteger(mContext, "int", 0));
        SharedPreferencesUtil.putLong(mContext, "long", new Long(3333));
        long l = SharedPreferencesUtil.getLong(mContext, "long", 0);
        Logger.i("long:" + l);
        SharedPreferencesUtil.putBoolean(mContext, "bool", true);
        Logger.i("bool:" + SharedPreferencesUtil.getBoolean(mContext, "bool", false));

        SharedPreferencesUtil.putFloat(mContext, "float", 111.11f);
        Logger.i("float:" + SharedPreferencesUtil.getFloat(mContext, "float", 0));

        SharedPreferencesUtil.putString(mContext, "string", "你妹啊");
        Logger.i("string:" + SharedPreferencesUtil.getString(mContext, "string", "fuck"));

        SharedPreferencesUtil.remove(mContext, "string");
        Logger.i("string:" + SharedPreferencesUtil.getString(mContext, "string", "fuck"));

        Logger.i("b:" + SharedPreferencesUtil.contains(mContext, "int"));

        SharedPreferencesUtil.clear(mContext);
        Logger.i("int:" + SharedPreferencesUtil.getInteger(mContext, "int", 0));
    }


    private void initView() {
        mTestBtn1 = (Button) findViewById(R.id.test_btn1);
        mTestBtn2 = (Button) findViewById(R.id.test_btn2);

        mTestBtn1.setOnClickListener(this);
        mTestBtn2.setOnClickListener(this);
    }
}
