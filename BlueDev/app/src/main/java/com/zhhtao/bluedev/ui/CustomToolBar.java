package com.zhhtao.bluedev.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhhtao.bluedev.utils.LogUtil;

/**
 * Created by zhangHaiTao on 2016/5/3.
 */
public class CustomToolBar extends RelativeLayout {
    Context mContext;
    public CustomToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //返回图标的功能设定
        getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Activity) {
                    ((Activity)mContext).onBackPressed();
                } else {
                    LogUtil.w("zht", "not activity");
                }
            }
        });
    }
}
