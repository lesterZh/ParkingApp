package com.zhhtao.bluedev.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author ZhangHaiTao
 * @ClassName: BaseFragment
 * Description: TODO
 * @date 2016/8/5 20:27
 */
public class BaseFragment extends android.app.Fragment {
    protected Activity mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
}
