package com.zhhtao.bluedev;

import com.zhhtao.bluedev.bean.UserInfoBean;
import com.zhhtao.bluedev.utils.StreamUtils;

import junit.framework.TestCase;

/**
 * @author ZhangHaiTao
 * @ClassName: TestSomething
 * Description: TODO
 * @date 2016/5/30 14:45
 */
public class TestSomething extends TestCase {

    public void testDivide() throws Exception {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setPhone("123");
        userInfoBean.setPassword("456");
        String res = StreamUtils.objectToString(userInfoBean);
        System.out.println(res);

        assertEquals(1, 1);
    }
}
