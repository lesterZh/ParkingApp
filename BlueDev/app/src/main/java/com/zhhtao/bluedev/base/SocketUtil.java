package com.zhhtao.bluedev.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.HomeActivityNew;
import com.zhhtao.bluedev.utils.LogUtil;
import com.zhhtao.bluedev.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by ZhangHaiTao on 2017/1/6.
 */

public class SocketUtil {
    static public Socket mSocket;

    private static SocketUtil mSocketUtil = new SocketUtil();
    volatile private boolean loopReadFlag = true;
    volatile private boolean loopFlag = true;

    private SocketUtil() {};

    public static SocketUtil getInstance() {
        return mSocketUtil;
    }

    public void openSocket() {
        loopReadFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(MyConstant.SERVER_IP, MyConstant.PORT);

                    // 获取Socket的输出流，用来发送数据到服务端
                    PrintStream out = new PrintStream(mSocket.getOutputStream());


                    out.print("userId-" + MyConstant.USER_ID);
                    SystemClock.sleep(500);

//                    Toast.makeText(MyApplication.getAppContext(), "连接成功", Toast.LENGTH_SHORT).show();
                    LogUtil.w("set ok");

                    //在子线程轮询读数据
                    new Thread() {
                        public void run() {
                            while (loopFlag) {
                                try {
                                    // 从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
                                    // String echo = buf.readLine();
                                    // System.out.println(echo);

                                    if (loopReadFlag) {
//                                        readString(mSocket);
                                        readFeedbackBytes(mSocket);
                                    }

                                    sleep(1000);
                                }
//                                    catch (SocketTimeoutException e) {
//                                    LogUtil.e("Time out, No response");
//                                }
                                    catch (Exception e) {
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


    public void sendData(String data) {
        try {

            // 获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(mSocket.getOutputStream());
            out.print(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data) {
        try {
            // 获取Socket的输出流，用来发送数据到服务端
            OutputStream out = mSocket.getOutputStream();
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data, int len) {
        try {
            // 获取Socket的输出流，用来发送数据到服务端
            OutputStream out = mSocket.getOutputStream();
            out.write(data, 0, len);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 阻塞式读取字节
     * @return
     */
    public byte[] readBytes() {
        LogUtil.w("read:");
        byte[] res = null;
        loopReadFlag = false;
        try {
            InputStream inputStream = mSocket.getInputStream();
            byte[] buf = new byte[1024];
            int len = inputStream.read(buf);
            res = new byte[len];
            for (int i=0; i<len; i++) {
                res[i] = buf[i];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loopReadFlag = true;
        return res;
    }

    /**
     * 读取反馈的信息
     * @return
     */
    public byte[] readFeedbackBytes(Socket client)  throws IOException {

        InputStream inputStream = client.getInputStream();
        int len = inputStream.available();

        if (len != 0) {
            byte[] buffer = new byte[len];
            inputStream.read(buffer);
            String msg = new String(buffer);

            if (msg.equals("welcome")) {//连接服务器成功
//                UIUtils.showToast(MyApplication.getAppContext(), "服务器连接成功");
                Toast.makeText(MyApplication.getAppContext(), "服务器连接成功", Toast.LENGTH_SHORT).show();
            }
//            LogUtil.w("socket rec:");
            LogUtil.w("socket rec: "+ Arrays.toString(buffer));
            if (buffer[len-1] == 0x53) {//车辆离开
                notifyLeave();
            }

            return buffer;
        }
        return null;
    }

    public String readString(Socket client) throws IOException {
        InputStream inputStream = client.getInputStream();
        int len = inputStream.available();

        if (len != 0) {
            byte[] buffer = new byte[len];
            inputStream.read(buffer);
            String msg = new String(buffer);

            if (msg.equals("welcome")) {//连接服务器成功
                //UIUtils.showToast(MyApplication.getAppContext(), "服务器连接成功");
            }


            return msg;
        }
        return null;
    }

    public void closeSocket() {
        loopFlag = false;
        SystemClock.sleep(1000);
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isOpen() {
        if (mSocket == null) return false;
        return mSocket.isConnected();
    }

    public void notifyLeave() {
        Context context = MyApplication.getAppContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        PendingIntent contentIntent = PendingIntent.getActivity(
                MyApplication.getAppContext(), 0, new Intent(MyApplication.getAppContext(), HomeActivityNew.class), 0);

        Notification notification = builder
                .setContentTitle("停车提醒")
                .setContentText("您的车辆已经离开，请注意安全，支付本次停车费用！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        context.getResources(), R.mipmap.ic_launcher))
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS)
                .setContentIntent(contentIntent)
                .build();
        manager.notify(1, notification);

        SharedPreferencesUtil.putBoolean(MyApplication.getAppContext(),MyConstant.isUseNow, false);
    }
}
