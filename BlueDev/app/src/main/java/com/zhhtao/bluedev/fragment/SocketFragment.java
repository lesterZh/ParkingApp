package com.zhhtao.bluedev.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.base.MyConstant;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.UIUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ZhangHaiTao
 * @ClassName: SocketFragment
 * Description: TODO
 * @date 2016/6/21 15:47
 */
public class SocketFragment extends Fragment {

    private static final int REFRESH_LIST = 1;
    private static final int CON_OK = 2;
    private static final int CON_CLOSE = 3;
    Activity mContext;
    @Bind(R.id.et_user_id)
    EditText mEtUserId;
    @Bind(R.id.et_to_id)
    EditText mEtToId;
    @Bind(R.id.btn_open)
    Button mBtnOpen;
    @Bind(R.id.btn_close)
    Button mBtnClose;
    @Bind(R.id.lv_msg)
    ListView mLvMsg;
    @Bind(R.id.et_input)
    EditText mEtInput;
    @Bind(R.id.btn_send)
    Button mBtnSend;
    @Bind(R.id.btn_open_led)
    Button mBtnOpenLed;
    @Bind(R.id.btn_close_led)
    Button mBtnCloseLed;
    @Bind(R.id.btn_open_beep)
    Button mBtnOpenBeep;
    @Bind(R.id.btn_close_beep)
    Button mBtnCloseBeep;
    @Bind(R.id.tv_net_state)
    TextView mTvNetState;

    volatile private boolean readFlag = true;
    volatile static int times_link = 0;
    volatile static int times_1s = 0;

    private Socket socket;
    ArrayList<String> msgs = new ArrayList<>();
    MyAdapter myAdapter;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_LIST:
                    myAdapter.notifyDataSetChanged();
                    break;

                case CON_OK:
                    mTvNetState.setTextColor(Color.BLUE);
                    mTvNetState.setText("服务器连接成功");
                    mTvNetState.postInvalidate();
                    break;

                case CON_CLOSE:
                    mTvNetState.setTextColor(Color.RED);
                    mTvNetState.setText("服务器连接关闭");
                    mTvNetState.postInvalidate();
                    break;
            }
        }

    };
    private String mUserId = null;
    private String mToId = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_socket, container, false);
        mContext = getActivity();

        ButterKnife.bind(this, view);

        myAdapter = new MyAdapter(mContext, 0, msgs);
        mLvMsg.setAdapter(myAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_open_led, R.id.btn_close_led, R.id.btn_open_beep, R.id.btn_close_beep})
    public void btn_open_led(View view) {
        switch (view.getId()) {
            case R.id.btn_open_led:
                sendDataToDevice("UQ", "A");
                break;
            case R.id.btn_close_led:
                sendDataToDevice("UQ", "B");
                break;
            case R.id.btn_open_beep:
                sendDataToDevice("UQ", "C");
                break;
            case R.id.btn_close_beep:
                sendDataToDevice("UQ", "D");
                break;
        }
    }


    private void alerm() {
        MediaPlayer player = MediaPlayer.create(mContext, R.raw.ylzs);
        player.setVolume(1.0f, 1.0f);
//				player.setLooping(true);
        player.start();
    }

    //RingtoneManager.TYPE_NOTIFICATION;通知声音
    //RingtoneManager.TYPE_ALARM;  警告
    //RingtoneManager.TYPE_RINGTONE; 铃声

    /**
     * 播放铃声
     *
     * @param ctx
     * @param type
     */
    public static void PlayRingTone(Context ctx, int type) {
        MediaPlayer mMediaPlayer = MediaPlayer.create(ctx,
                RingtoneManager.getActualDefaultRingtoneUri(ctx, type));
//        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_text_test,
                        parent, false);

            }

            TextView tv = (TextView) convertView.findViewById(R.id.text);
            tv.setText(getItem(position));

            return convertView;
        }
    }

    @OnClick({R.id.btn_open, R.id.btn_close, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                LogUtil.w("btn open");
                openSocket();
                break;
            case R.id.btn_close:
                closeSocket();

                break;
            case R.id.btn_send:
                sendData();
                break;
        }
    }


    private void sendDataToDevice(String toId, String msg) {
        try {
            // 获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.print(toId + "-" + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        try {
            mToId = mEtToId.getText().toString().trim();

            String msg = mEtInput.getText().toString();
            // 获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.print(mToId + "-" + msg);
            mEtInput.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readString(Socket client) throws IOException {
        InputStream inputStream = client.getInputStream();
        int len = inputStream.available();

        if (len != 0) {
            byte[] buffer = new byte[len];
            inputStream.read(buffer);
            String msg = new String(buffer);

            if (msg.equals("welcome")) {//连接服务器成功
                UIUtils.showToast(mContext, "服务器连接成功");
                mhandler.sendEmptyMessage(CON_OK);
            }

            LogUtil.i(msg);
            msgs.add(msg);
            mhandler.sendEmptyMessage(REFRESH_LIST);

            String[] res = msg.split("-");
            if (res[1].equals("alarm")) {
//                alerm();
                PlayRingTone(mContext, RingtoneManager.TYPE_ALARM);
            }

            return msg;
        }
        return null;
    }

    private void closeSocket() {
        readFlag = false;
        SystemClock.sleep(1000);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mhandler.sendEmptyMessage(CON_CLOSE);
    }

    private void openSocket() {
        readFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(MyConstant.SERVER_IP, MyConstant.PORT);

                    // 获取Socket的输出流，用来发送数据到服务端
                    PrintStream out = new PrintStream(socket.getOutputStream());

                    mUserId = mEtUserId.getText().toString();
                    mToId = mEtToId.getText().toString();

                    out.print("userId-" + mUserId);
                    SystemClock.sleep(500);

                    LogUtil.w("set ok");

                    //在子线程轮询读数据
                    new Thread() {
                        public void run() {
                            while (readFlag) {
                                try {
                                    // 从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
                                    // String echo = buf.readLine();
                                    // System.out.println(echo);
                                    readString(socket);
                                    sleep(100);
                                } catch (SocketTimeoutException e) {
                                    LogUtil.e("Time out, No response");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
