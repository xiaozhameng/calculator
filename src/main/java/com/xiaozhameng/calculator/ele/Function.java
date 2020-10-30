package com.xiaozhameng.calculator.ele;

import com.xiaozhameng.calculator.ExpCalculateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 定义内置函数
 *
 * @author qiaofengjun
 * @date 2020/09/26
 */
public enum Function {

    /**
     * 四舍五入
     */
    ROUND("ROUND", "ROUND函数", (params -> {
        List<List<Node>> subs = splitNodeListByDot(params);
        if (subs.size() != 2) {
            throw new RuntimeException("参数格式非法，期望两个参数，请检查！");
        }
        List<Node> nodes = subs.get(0);
        BigDecimal value = ExpCalculateUtils.calculate(nodes);
        String scale = subs.get(1).get(0).getExp();
        // 将第一个参数运行计算，然后使用第二个参数取整
        return value.setScale(Integer.parseInt(scale), BigDecimal.ROUND_HALF_UP);
    })),

    /**
     * 直接舍去
     */
    ROUND_DOWN("ROUND_DOWN", "ROUND_DOWN函数", (params -> {
        List<List<Node>> subs = splitNodeListByDot(params);
        if (subs.size() != 2) {
            throw new RuntimeException("参数格式非法，期望两个参数，请检查！");
        }
        List<Node> nodes = subs.get(0);
        BigDecimal value = ExpCalculateUtils.calculate(nodes);
        String scale = subs.get(1).get(0).getExp();
        // 将第一个参数运行计算，然后使用第二个参数取整
        return value.setScale(Integer.parseInt(scale), BigDecimal.ROUND_DOWN);
    }));

    String code;
    String desc;
    Calculate calculate;

    /**
     * 构造函数
     */
    Function(String code, String desc, Calculate calculate) {
        this.code = code;
        this.desc = desc;
        this.calculate = calculate;
    }

    /**
     * 根据code 获取函数枚举
     */
    public static Function getByCode(String exp) {
        if (exp == null || exp.trim().length() < 1) {
            return null;
        }
        for (Function func : Function.values()) {
            if (exp.toUpperCase().equals(func.code)) {
                return func;
            }
        }
        return null;
    }

    /**
     * 判断是不是一个函数
     */
    public static boolean funcCheck(String exp) {
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

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Calculate getCalculate() {
        return calculate;
    }

    /**
     * 计算接口
     */
    public static interface Calculate {
        /**
         * 计算接口
         *
         * @param nodes 节点集
         * @return 计算值
         */
        BigDecimal calculate(List<Node> nodes);
    }

    /**
     * 从函数式表达式中获取最小表达式集合
     */
    private static List<List<Node>> splitNodeListByDot(List<Node> exp) {
        if (!Function.funcCheck(exp.get(0).getExp())) {
            throw new RuntimeException("参数表达式不合法，请检查！");
        }
        List<List<Node>> params = defaultFuncParam();
        // 忽略函数名称，忽略函数名称后面的第一个括号，忽略最后一个括号
        for (int i = 2; i < exp.size() - 1; i++) {
            Node node = exp.get(i);
            if (String.valueOf(Mark.DOT.getCode()).equals(node.getExp())) {
                params.add(new ArrayList<>());
                continue;
            }
            List<Node> nodes = params.get(params.size() - 1);
            nodes.add(node);
        }
        return params;
    }

    /**
     * 获取函数的默认参数集合
     */
    private static List<List<Node>> defaultFuncParam() {
        List<List<Node>> params = new LinkedList<>();
        params.add(new LinkedList<>());
        return params;
    }

}