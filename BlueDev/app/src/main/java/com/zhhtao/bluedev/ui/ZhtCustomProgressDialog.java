package com.zhhtao.bluedev.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhhtao.bluedev.R;

/*
* 依赖文件和配置：
<!-- 自定义Dialog -->
 1.styles文件下
<style name="ZhtCustomProgressDialog" parent="@android:style/Theme.Dialog">
    <item name="android:windowFrame">@null</item>
    <item name="android:windowIsFloating">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:windowAnimationStyle">@android:style/Animation.Dialog</item>
    <item name="android:windowSoftInputMode">stateUnspecified|adjustPan</item>
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:windowNoTitle">true</item>
</style>

2.布局：layout_custom_progress_dialog.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/bg_custom_progress_dialog"
    android:gravity="center_horizontal"
    android:orientation="vertical"
    android:paddingBottom="20dp"
    android:paddingLeft="30dp"
    android:paddingRight="30dp"
    android:paddingTop="20dp" >

    <!--这里可以放置图片 替换progressBar-->
    <ProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/message"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        android:text="Message"
        android:textColor="#FFFFFF" />

</LinearLayout>

3.背景:bg_custom_progress_dialog.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">

    <solid android:color="#404040" />

    <corners android:radius="8dp" />

</shape>

4.使用说明：
ZhtCustomProgressDialog mCustomProgressDialog;//声明
mCustomProgressDialog = ZhtCustomProgressDialog.show(context,"正在加载",false, null);//新建实例，显示
mCustomProgressDialog.dismiss();//关闭显示
*/

/**
 * Created by zhangHaiTao on 2016/5/14.
 */
public class ZhtCustomProgressDialog extends Dialog{

    public ZhtCustomProgressDialog(Context context) {
        super(context);
    }

    public ZhtCustomProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        //如果自定义了图片动画，在这里开始执行动画
    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    static  private  ZhtCustomProgressDialog  dialog;
    /**
     * 弹出自定义ProgressDialog
     *
     * @param context
     *            上下文
     * @param message
     *            提示
     * @param cancelable
     *            是否按返回键取消
     * @param cancelListener
     *            按下返回键监听
     * @return
     */
    public static ZhtCustomProgressDialog show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        dialog = new ZhtCustomProgressDialog(context, R.style.ZhtCustomProgressDialog);
        dialog.setTitle("");
        dialog.setContentView(R.layout.layout_custom_progress_dialog);
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.message);
            txt.setText(message);
        }
        // 按返回键是否取消
        dialog.setCancelable(cancelable);
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.4f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
        return dialog;
    }

    public static void dismiss2() {
        if (dialog != null)
            dialog.dismiss();
    }
}
