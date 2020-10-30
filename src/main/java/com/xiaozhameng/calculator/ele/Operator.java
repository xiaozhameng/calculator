package com.xiaozhameng.calculator.ele;

import java.math.BigDecimal;

/**
 * 功能描述：运算符
 *
 * @author: qiaofengjun
 * @date: 2020/9/24 5:00 下午
 */
public enum Operator {

    /**
     * 枚举值，简单运算符
     */
    ADD('+', "加", 10, BigDecimal::add),

    SUB('-', "减", 10, BigDecimal::subtract),

    MUL('*', "乘", 11, BigDecimal::multiply),

    DIV('/', "除", 11, (p1, p2) -> p1.divide(p2, 14, BigDecimal.ROUND_HALF_UP));

    char code;
    String desc;
    int level;
    Calculate calculate;

    Operator(char code, String desc, int level, Calculate calculate) {
        this.code = code;
        this.desc = desc;
        this.level = level;
        this.calculate = calculate;
    }

    /**
     * 根据code 获取枚举值
     *
     * @param code code 信息
     */
    public static Operator getByCode(String code) {
        if (code == null || code.length() > 1) {
            return null;
        }
        return getByCode(code.charAt(0));
    }

    /**
     * 根据code 获取枚举值
     *
     * @param code code 信息
     */
    public static Operator getByCode(char code) {
        for (Operator value : Operator.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    /**
     * 扫描一遍 ，检查是否匹配
     *
     * @param exp 字符序列
     */
    public static boolean match(char exp) {
        for (Operator val : Operator.values()) {
            if (exp == val.code) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算
     *
     * @param p1 第一个操作数
     * @param p2 第二个操作数
     */
    public BigDecimal calculate(BigDecimal p1, BigDecimal p2) {
        return this.calculate.calculate(p1, p2);
    }

    public char getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 计算接口
     */
    interface Calculate {
        /**
         * 计算接口
         *
         * @param item1 参数1
         * @param item2 参数2
         * @return
         */
        BigDecimal calculate(BigDecimal item1, BigDecimal item2);
    }
}
