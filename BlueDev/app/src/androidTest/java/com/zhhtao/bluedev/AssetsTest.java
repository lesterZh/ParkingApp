package com.zhhtao.bluedev;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.orhanobut.logger.Logger;
import com.zhhtao.bluedev.utils.StreamUtils;

import org.junit.Test;

/**
 * @author ZhangHaiTao
 * @ClassName: AssetsTest
 * Description: TODO
 * @date 2016/5/30 11:22
 */
public class AssetsTest {
    @Test
    public void readAssert() {
        Context context = InstrumentationRegistry.getContext();
        String res = StreamUtils.readStringFromAsset(context, "cn.txt");
        Logger.w("len:"+res.length());
    }
}
