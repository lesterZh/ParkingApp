package com.zhhtao.bluedev.base;

import junit.framework.TestCase;

/**
 * Created by zhangHaiTao on 2016/5/30.
 */
public class CalculatorTest extends TestCase {

    Calculator mCalculator = new Calculator();

    public void setUp() throws Exception {
        super.setUp();

    }



    public void testSum() throws Exception {
        assertEquals(6.0, mCalculator.sum(3,3));
    }

    public void testSubstract() throws Exception {

    }

    public void testDivide() throws Exception {

    }

    public void testMultiply() throws Exception {

    }
}