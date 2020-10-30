package com.xiaozhameng.calculator.ele;

import java.math.BigDecimal;

/**
 * 元素类型
 * @author qiaofengjun
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
     * 操作符
     */
    OPERATOR,
    /**
     * 符号
     */
    MARK;

    /**
     * 根据字符序列给出类型
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
        if (Function.funcCheck(exp)) {
            return FUNCTION;
        }

        // 变量
        if (Variable.variableCheck(exp)) {
            return VARIABLE;
        }

        // 数值：暴力一点，直接强转Decimal
        try {
            new BigDecimal(exp);
            return NUMERICAL;
        } catch (Exception e) {
            throw new RuntimeException("未知的元素类型，请检查你的输入，当前输入 = " + exp);
        }
    }
}
