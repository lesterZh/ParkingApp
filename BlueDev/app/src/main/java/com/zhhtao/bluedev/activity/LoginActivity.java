package com.zhhtao.bluedev.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.base.SocketUtil;
import com.zhhtao.bluedev.ui.ZhtCustomProgressDialog;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.UIUtils;
import com.zhhtao.bluedev.utils.ZhtUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用户登录界面
 * created by zhangHaiTao at 2016/4/25
 */
public class LoginActivity extends BaseActivity {


    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_regist_new)
    TextView tvRegistNew;
    @Bind(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @Bind(R.id.tv_title_title_bar)
    TextView tvTitleToolBar;
//    private Activity mContext;
    private String phone;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvTitleToolBar.setText("登录");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etPhone.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    UIUtils.showToast(mContext, "请输入手机号");
                    UIUtils.shake(etPhone, 2, 300);
                } else if (TextUtils.isEmpty(password)) {
                    UIUtils.showToast(mContext, "请输入密码");
                    UIUtils.shake(etPassword, 2, 300);
                } else {
                    ZhtCustomProgressDialog.show(mContext, "登录中……",true, null);
                    isPhonePasswordValid();
                }
            }
        });

        tvRegistNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZhtUtils.gotoIntent(mContext, RegistWelcomeActivity.class);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ZhtUtils.gotoIntent(mContext, FindPasswordActivity.class);
            }
        });

        etPhone.setText("17729849371");
        etPassword.setText("123456");
    }

    /**
     * 验证输入的用户名和密码是否有效
     *
     */
    private void isPhonePasswordValid() {
        AVQuery<AVObject> query = new AVQuery<>(MyConstant.REGIST_USER);
        query.whereMatches(MyConstant.REGIST_USER_KEY_Phone, "^" + phone + "$");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                    if (list.size() > 0) {
                        MyConstant.LEANCLOUD_SAVE_ID = list.get(0).getObjectId();

                        String ps = (list.get(0).getString(MyConstant.REGIST_USER_KEY_PASSWOED));
                        if (ps.equals(password)) {
                            //
                            MyConstant.USER_ID = phone;
                            SocketUtil.getInstance().openSocket();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(1000);
                                    while (!SocketUtil.getInstance().isOpen()) {//等待socket连接成功
                                        SystemClock.sleep(300);
//                                        SocketUtil.getInstance().openSocket();
                                    }

                                    ZhtCustomProgressDialog.dismiss2();
                                    ZhtUtils.gotoIntent(mContext, HomeActivityNew.class);
                                }
                            });

                        } else {
                            UIUtils.showToast(mContext, "密码错误");
                        }
                    } else {
                        UIUtils.showToast(mContext, "用户不存在");
                    }

                } else {
                    LogUtil.i("err");
                }
            }

        });
    }
}
