package com.zhhtao.bluedev.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZhangHaiTao
 * @ClassName: StringVerificationUtil
 * Description: TODO
 * @date 2016/5/29 21:48
 */
public class StringVerificationUtil {
    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    /**
     * 验证手机号 *
     * * @param str
     * * @return
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");
        // p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 验证邮箱 *
     * * @param email
     * * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0) return false;
        return emailer.matcher(email).matches();
    }


    /**
     * 判断输入是否为空
     * @param input
     * @return
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)) return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


}