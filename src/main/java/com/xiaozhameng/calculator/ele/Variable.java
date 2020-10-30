package com.xiaozhameng.calculator.ele;

import java.math.BigDecimal;

/**
 * 功能描述：变量，定义表达式中可以用到的变量
 *
 * @author: qiaofengjun
 * @date: 2020/9/24 3:34 下午
 */
public enum Variable {

    /**
     * 应还本金
     */
    DUE_AMOUNT("DUE_AMOUNT", "应还本金", (metaData -> {
        return BigDecimal.ZERO;
    })),

    /**
     * 逾期费利率
     */
    OVERDUE_RATE("OVERDUE_RATE", "逾期费利率", (metaData -> {
        return BigDecimal.ZERO;
    }));

    String code;
    String desc;
    Resolver resolver;

    Variable(String code, String desc, Resolver resolver) {
        this.code = code;
        this.desc = desc;
        this.resolver = resolver;
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

    /**
     * 遍历赋值器
     */
    private interface Resolver {
        /**
         * 解析接口
         *
         * @param metaData 元数据
         * @return 返回值
         */
        BigDecimal resolve(SelectorMetaData metaData);
    }

}
