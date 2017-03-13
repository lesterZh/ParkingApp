package com.zhhtao.bluedev.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhhtao.bluedev.base.MyApplication;

/**
 * Created by zhangHaiTao on 2016/4/25.
 */
public class UIUtils {

    /**
     * 可以在子线程弹出吐司，默认为短时间显示
     * @param context
     * @param message
     */
    public static void showToast(final Activity context, final String message) {
        if (Thread.currentThread().getName().equals("main")) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * @param context
     * @param message
     * @param showTime 设置吐司显示时间 0：短时间  1：长时间
     */
    public static void showToast(final Activity context, final String message, int showTime) {
        final int duration ;
        if (showTime == 0)
            duration = Toast.LENGTH_SHORT;
        else
            duration = Toast.LENGTH_LONG;

        if (Thread.currentThread().getName().equals("main")) {
            Toast.makeText(context, message, duration).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, duration).show();
                }
            });
        }
    }

    /**
     * 设置控价的抖动动画效果
     * @param view   要设置抖动的控价
     * @param counts 抖动次数
     * @param timeMs 抖动持续时间
     */
    public static void shake(View view, int counts, int timeMs) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        translateAnimation.setInterpolator(new CycleInterpolator(2));
        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);
    }

    public static boolean isInputEmpty(Activity activity, String text) {
        if (TextUtils.isEmpty(text)) {
            UIUtils.showToast(activity, "输入不能为空");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 通过弹出全局对话框显示文本内容，按返回键关闭窗口
     * @param text
     */
    public static void showText(String text) {
        int padding = 10;
        Context context = MyApplication.getAppContext();
        Dialog dialog = new Dialog(MyApplication.getAppContext());
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(padding,padding,padding,padding);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(textView);

        dialog.setContentView(scrollView);
        dialog.setCancelable(true);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
