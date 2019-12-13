package com.jet.cloud.deepmind.common.util;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * Copyright 2016 济中节能 All rights reserved.
 *
 * @ClassName: MathUtil.java
 * @CreateTime: 2016年12月2日 上午11:24:05
 * @Description: 负责计算表达式的值
 */
public class MathUtil {
    public static ScriptEngineManager sem = new ScriptEngineManager();
    public static ScriptEngine se = sem.getEngineByExtension("js");
    public static FelEngine fel = new FelEngineImpl();
    private static DecimalFormat format = new DecimalFormat();
    private static NumberFormat fmt = NumberFormat.getPercentInstance();

    /**
     * 计算表达式的值
     *
     * @param expression 算术表达式
     * @return
     */
    public static String cal(String expression) throws ScriptException {
        try {
            Object result = se.eval(expression);
            return result.toString();
        } catch (ScriptException e) {
            throw e;
        }
    }

    /**
     * 计算公式的值，速度比cal方法快
     *
     * @param expression 算术表达式
     * @return
     */
    public static Double calByFel(String expression) {
        Double result = Double.valueOf(fel.eval(expression).toString());
        return result;
    }

    /**
     * 计算返回boolean
     */
    public static boolean calByBoolean(String expression) {
        return (boolean) fel.eval(expression);
    }

    /**
     * 保留小数点后几位有效数字
     *
     * @param source 浮点数值
     * @param size   几位有效数字
     * @return Double 保留小数位后的结果
     */
    public static Double keepDigitsAfterPoint(Double source, int size) {
        Double result = null;
        if (source != null && !source.isInfinite() && !source.isNaN()) {
            String pattern = "#";
            if (size > 0) {
                pattern += ".";
                for (int i = 0; i < size; i++) {
                    pattern += "0";
                }
            }
            format.applyPattern(pattern);
            result = Double.parseDouble(format.format(source));
        }
        return result;
    }

    //判断是否是整数
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param source
     * @return
     * @apiNote 保留2位小数
     */
    public static String double2String(Double source) {
        if (source == null) return null;
        return String.format("%.2f", source);
    }

    /**
     * @param source 数据源
     * @param size   最大保留小数位数
     */
    public static String double2StringPercent(Double source, int size) {
        if (StringUtils.isNullOrEmpty(source)) return null;
        fmt.setMaximumFractionDigits(size);
        return fmt.format(source);
    }

    public static void main(String[] args) {

        Double a = null;
        Double b = null;
        boolean c = calByBoolean(a + "==" + b);
        System.out.println(c);
    }
}
