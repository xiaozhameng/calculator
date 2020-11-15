package com.xiaozhameng.calculator.element;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表达式中某个元素的抽象
 * @author xiaozhameng
 * @date 2020/09/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Node {

    /**
     * 元素字符串表达式
     */
    String exp;
    /**
     * 元素类型
     */
    NodeType type;

    /**
     * 是否是函数
     *
     * @return 判断值
     */
    public boolean isFunc() {
        return NodeType.FUNCTION.equals(this.type);
    }

    /**
     * 是否是变量
     *
     * @return 判断值
     */
    public boolean isVariable() {
        return NodeType.VARIABLE.equals(this.type);
    }

    /**
     * 是否是数值
     *
     * @return 判断值
     */
    public boolean isNumber() {
        return NodeType.NUMERICAL.equals(this.type);
    }

    /**
     * 是否是日期
     *
     * @return 判断值
     */
    public boolean isDate() {
        return NodeType.DATE.equals(this.type);
    }

    /**
     * 是否是操作符
     *
     * @return 判断值
     */
    public boolean isOperator() {
        return NodeType.OPERATOR.equals(this.type);
    }

    /**
     * 是否是符号
     *
     * @return 判断值
     */
    public boolean isMark() {
        return NodeType.MARK.equals(this.type);
    }

    /**
     * 节点描述信息
     *
     * @return str
     */
    public String getNodeTaps() {
        // 空元素
        if (exp == null || "".equals(exp.trim()) || ".".equals(exp)) {
            return "NULL";
        }
        // 符号
        if (isMark()) {
            return String.valueOf(Mark.getByCode(exp).code);
        }
        // 运算符
        if (isOperator()) {
            return String.valueOf(Operator.getByCode(exp).code);
        }
        // 函数
        if (isFunc()) {
            return Function.getByCode(exp).code;
        }

        // 日期
        if (isDate()) {
            return "日期";
        }

        // 数值 或者变量直接返回
        if (isNumber() || isVariable()) {
            return this.exp;
        }

        return "未知元素类型";
    }
}

