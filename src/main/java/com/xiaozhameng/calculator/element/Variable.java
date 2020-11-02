package com.xiaozhameng.calculator.element;

/**
 * 功能描述：变量，定义表达式中可以用到的变量
 *
 * @author: xiaozhameng
 * @date: 2020/9/24 3:34 下午
 */
public enum Variable {

    /**
     * 应还本金
     */
    DUE_AMOUNT("DUE_AMOUNT", "应还本金"),

    /**
     * 逾期费利率
     */
    OVERDUE_RATE("OVERDUE_RATE", "逾期费利率");

    String code;
    String desc;

    Variable(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 是不是一个表达式变量，这里我们把数字也看做一个变量
     *
     * @param exp 字符序列
     */
    public static boolean variableCheck(String exp) {
        for (Variable value : Variable.values()) {
            if (exp.toUpperCase().equals(value.code)) {
                return true;
            }
        }
        return false;
    }
}
