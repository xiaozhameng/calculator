package com.xiaozhameng.calculator.element;

import com.xiaozhameng.calculator.utils.DateUtils;

import java.math.BigDecimal;

/**
 * 元素类型
 * @author xiaozhameng
 * @date 2020/09/26
 */
public enum NodeType {
    /**
     * 函数
     */
    FUNCTION,
    /**
     * 变量
     */
    VARIABLE,
    /**
     * 数值
     */
    NUMERICAL,
    /**
     * 日期
     */
    DATE,
    /**
     * 操作符
     */
    OPERATOR,
    /**
     * 符号
     */
    MARK;

    /**
     * 根据字符序列给出类型
     *
     * @param exp 字符序列
     */
    public static NodeType checkType(String exp) {
        // 空元素
        if (exp == null || "".equals(exp.trim()) || ".".equals(exp)) {
            return null;
        }
        // 符号
        if (exp.trim().length() == 1 && Mark.match(exp.charAt(0))) {
            return MARK;
        }
        // 运算符
        if (exp.trim().length() == 1 && Operator.match(exp.charAt(0))) {
            return OPERATOR;
        }
        // 函数
        if (isFunction(exp)) {
            return FUNCTION;
        }

        // 日期
        if (isDateType(exp)) {
            return DATE;
        }

        // 数值：暴力一点，直接强转Decimal
        try {
            new BigDecimal(exp);
            return NUMERICAL;
        } catch (Exception e) {
            return VARIABLE;
        }
    }

    private static boolean isFunction(String exp) {
        if (exp == null || exp.trim().length() < 1) {
            return false;
        }
        for (Function func : Function.values()) {
            if (exp.toUpperCase().equals(func.code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 日期格式
     * 支持如下两种日期格式
     * yyyyMMddHHmmss
     * yyyyMMdd
     */
    private static boolean isDateType(String exp) {
        try {
            DateUtils.getDateFromString(exp, DateUtils.DATE_FORMAT_YYYYMMDDHHMMSS);
            return true;
        } catch (Exception e) {
            try {
                DateUtils.getDateFromString(exp, DateUtils.DATE_FORMAT_YYYYMMDD);
                return true;
            } catch (Exception ee) {
                return false;
            }
        }
    }
}
