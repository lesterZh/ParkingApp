package com.zhhtao.bluedev.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.ui.ZhtCustomProgressDialog;
import com.zhhtao.bluedev.utils.UIUtils;
import com.zhhtao.bluedev.utils.ZhtUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 欢迎注册界面，客户需要改变，注册仅需要手机和验证码
 * created by zhangHaiTao at 2016/4/25
 */
public class RegistWelcomeActivity extends BaseActivity {

    private static final int TIME_COUNT = 0;
    private static final int COUNT_END = 1;

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.edit_verification_code)
    EditText editVerificationCode;
    @Bind(R.id.btn_send_verification_code)
    Button btnSendVerificationCode;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.btn_regist)
    Button btnRegist;
    @Bind(R.id.tv_title_title_bar)
    TextView tvTitleToolBar;

    private Activity context;

    private static final int COUNT_SECOND = 60;
    private int time = COUNT_SECOND;//倒计时
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_COUNT:
                    time--;
                    btnSendVerificationCode.setText(time + "S 重新发送");
                    break;
                case COUNT_END:
                    btnSendVerificationCode.setText("发送验证码");
                    btnSendVerificationCode.setEnabled(true);//恢复点击响应
                    break;

            }
        }
    };
    private EditText mEtPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_regist_welcome);
        ButterKnife.bind(this);

        initView();


    }

    String phone;
    String password;

    private void initView() {
        tvTitleToolBar.setText("欢迎注册");
        mEtPassword2 = (EditText) findViewById(R.id.et_password2);

        //发送验证码button
        btnSendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etPhone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    UIUtils.showToast(context, "请输入正确的手机号码");
                    UIUtils.shake(etPhone, 2, 300);
                    return;
                }

                if (phone.matches("^1[3578]\\d{9}")) {
                    //手机号码正确，判断是否注册，未注册则发送验证码
                    isPhoneRegisted(phone);

                    time = COUNT_SECOND;
                    btnSendVerificationCode.setText(time + "S 重新发送");
                    btnSendVerificationCode.setEnabled(false);//禁止点击
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (time <= 0) {
                                handler.removeCallbacks(this);
                                handler.sendEmptyMessage(COUNT_END);
                                return;
                            }
                            handler.postDelayed(this, 1000);
                            handler.sendEmptyMessage(TIME_COUNT);
                        }
                    };
                    handler.postDelayed(runnable, 1000);

                } else {
                    UIUtils.showToast(context, "请输入正确的手机号码");
                    UIUtils.shake(etPhone, 2, 300);
                }
            }
        });

        //注册 流程：判断密码是否合法,校验验证码是否正确，注册，进入主界面
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etPhone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    UIUtils.showToast(context, "请输入正确的手机号码");
                    UIUtils.shake(etPhone, 2, 300);
                    return;
                }

                if (phone.matches("^1[3578]\\d{9}")) {
                    //手机号码正确，判断是否注册，未注册则检查密码
                    isPhoneRegisted(phone);

                } else {
                    UIUtils.showToast(context, "请输入正确的手机号码");
                    UIUtils.shake(etPhone, 2, 300);
                }
//                checkPasswordValid();
                //                registUser();
            }
        });

//        mEtPassword2.setOnClickListener(this);
    }

    /**
     * 判断是否已经注册
     *
     * @param phone
     */
    private void isPhoneRegisted(String phone) {
        AVQuery<AVObject> query = new AVQuery<>(MyConstant.REGIST_USER);
        query.whereMatches("phone", "^" + phone + "$");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        //未注册,检查密码是否合法
//                        sendVerificationCode();
                        checkPasswordValid();
                    } else {
                        //已经注册
                        UIUtils.showToast(context, "该手机号已经注册");
                        time = 0;//倒计时清零
                    }
                }
            }
        });
    }


    /**
     * 判定输入密码是否合法
     * 流程：判断密码是否合法,校验验证码是否正确，注册，进入主界面
     */
    private void checkPasswordValid() {
        password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            UIUtils.showToast(context, "密码输入不能为空");
        } else {
            if (!password.matches("^[a-zA-Z0-9]\\w{5,17}$")) {
                //输入密码不合法
                UIUtils.showToast(context, "请输入6~18位密码");
            } else if (!password.equals(getPassword2())) {
                //输入密码不合法
                UIUtils.showToast(context, "2次密码输入不一致");
            }else {
                //密码输入合法，验证码判断
                //                checkVerificationCode();
                registUser();//跳过验证码，直接注册
            }
        }
    }


    private void registUser() {
        AVObject userObj = new AVObject(MyConstant.REGIST_USER);
        userObj.put(MyConstant.REGIST_USER_KEY_Phone, phone);
        userObj.put(MyConstant.REGIST_USER_KEY_PASSWOED, password);
        userObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    UIUtils.showToast(context, "注册成功");
                    //跳转到登录界面
                    //                    mCustomProgressDialog.dismiss();
                    ZhtUtils.gotoIntent(context, LoginActivity.class);
                } else {
                    UIUtils.showToast(context, "请重试");
                }
            }
        });
    }

    private String getPassword2() {
        // validate
        String password2 = mEtPassword2.getText().toString().trim();
        if (TextUtils.isEmpty(password2)) {
//            Toast.makeText(this, "重复密码不能为空", Toast.LENGTH_SHORT).show();
            return "";
        }

        return password2;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    ZhtCustomProgressDialog mCustomProgressDialog;

    /**
     * 判定验证码是否正确
     */
    private void checkVerificationCode() {

        String code = editVerificationCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            UIUtils.showToast(context, "请输入验证码");
        } else {
            mCustomProgressDialog = ZhtCustomProgressDialog.show(context, "正在注册", true, null);
            verifyCodeInBackground(code);//流程：校验验证码是否正确，判断密码是否合法，注册，进入主界面
        }

    }

    /**
     * @return 从网络中获取验证码，用于用户输入比较
     */
    private void verifyCodeInBackground(String code) {

        AVOSCloud.verifySMSCodeInBackground(code, phone,
                new AVMobilePhoneVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            UIUtils.showToast(context, "验证码正确");
                            //密码和验证码都没问题后进行注册
                            registUser();
                        } else {
                            e.printStackTrace();
                            UIUtils.showToast(context, "验证码不正确");
                        }
                    }
                });

    }


    /**
     * 发送手机验证码
     */
    private void sendVerificationCode() {
        new AsyncTask<Void, Void, Void>() {
            boolean res;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    AVOSCloud.requestSMSCode(phone, MyConstant.APP_NAME, "注册", 10);
                    res = true;
                } catch (AVException e) {
                    e.printStackTrace();
                    res = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (res) {
                    UIUtils.showToast(context, "验证码已发送，请注意查收短信");
                } else {
                    UIUtils.showToast(context, "验证码发送失败");
                }
            }
        }.execute();
    }



}
