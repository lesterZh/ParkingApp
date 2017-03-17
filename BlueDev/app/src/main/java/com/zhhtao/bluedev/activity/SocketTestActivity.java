package com.zhhtao.bluedev.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.BaseActivity;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.UIUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class SocketTestActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtIp;
    private EditText mEtPort;
    private EditText mEtUid;
    private Button mBtnCon;
    private LinearLayout mActivitySocketTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);
        initView();
    }

    private void initView() {
        mEtIp = (EditText) findViewById(R.id.et_ip);
        mEtPort = (EditText) findViewById(R.id.et_port);
        mEtUid = (EditText) findViewById(R.id.et_userid);
        mBtnCon = (Button) findViewById(R.id.btn_con);
        mActivitySocketTest = (LinearLayout) findViewById(R.id.activity_socket_test);

        mBtnCon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_con:
                openSocket();
                break;
        }
    }

    private String getIp() {
        // validate
        String ip = mEtIp.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            //            Toast.makeText(this, "ip", Toast.LENGTH_SHORT).show();
            return "";
        }
        return ip;

    }

    private String getPort() {
        // validate
        String ip = mEtPort.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            //            Toast.makeText(this, "ip", Toast.LENGTH_SHORT).show();
            return "";
        }
        return ip;

    }

    private String getUserId() {
        // validate
        String ip = mEtUid.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            //            Toast.makeText(this, "ip", Toast.LENGTH_SHORT).show();
            return "";
        }
        return ip;

    }

    static public Socket mSocket;
    public void openSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ip = MyConstant.SERVER_IP;
                    int port = MyConstant.PORT;

                    if (!getIp().equals("")) {
                        ip = getIp();
                    }

                    if (!getPort().equals("")) {
                        port = Integer.parseInt(getPort());
                    }

                    mSocket = new Socket(ip, port);

                    // 获取Socket的输出流，用来发送数据到服务端
                    PrintStream out = new PrintStream(mSocket.getOutputStream());


                    String userId = "123456789";
                    if (!getUserId().equals("")) {
                        userId = getUserId();
                    }
                    out.print("userId-" + userId);

                    SystemClock.sleep(500);

                    // Toast.makeText(MyApplication.getAppContext(), "连接成功", Toast.LENGTH_SHORT).show();
                    LogUtil.w("set ok");
                    UIUtils.showToast(mContext, "连接成功");
                    //在子线程轮询读数据
                    new Thread() {
                        public void run() {
                            while (true) {
                                try {
                                    // 从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
                                    // String echo = buf.readLine();
                                    // System.out.println(echo);

                                    //                                    if (loopReadFlag) {
                                    //                                        readString(mSocket);
                                    //                                    }

                                    sleep(300);
                                } catch (Exception e) {
                                    LogUtil.e("Time out, No response");
                                }
                            }
                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();
                    UIUtils.showToast(mContext, "连接失败");
                }
            }
        }).start();
    }

}
