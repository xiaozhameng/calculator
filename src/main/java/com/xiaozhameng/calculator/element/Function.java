package com.xiaozhameng.calculator.element;

import com.xiaozhameng.calculator.Calculator;
import com.xiaozhameng.calculator.utils.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 定义内置函数
 *
 * @author xiaozhameng
 * @date 2020/09/26
 */
public enum Function {

    /**
     * 四舍五入
     * <p>
     * 例如：ROUND(100*20/3,2)
     */
    ROUND("ROUND", "ROUND函数", (params -> {
        List<List<Node>> subs = splitNodeListByDot(params);
        if (subs.size() != 2) {
            throw new RuntimeException("参数格式非法，ROUND函数期望两个参数，请检查！");
        }
        List<Node> nodes = subs.get(0);
        BigDecimal value = Calculator.calculate(nodes);
        String scale = subs.get(1).get(0).getExp();
        // 将第一个参数运行计算，然后使用第二个参数取整
        return value.setScale(Integer.parseInt(scale), BigDecimal.ROUND_HALF_UP);
    })),

    /**
     * 直接舍去
     * 例如：ROUND_DOWN(100*20/3,2)
     */
    ROUND_DOWN("ROUND_DOWN", "ROUND_DOWN函数", (params -> {
        List<List<Node>> subs = splitNodeListByDot(params);
        if (subs.size() != 2) {
            throw new RuntimeException("参数格式非法，ROUND_DOWN函数期望两个参数，请检查！");
        }
        List<Node> nodes = subs.get(0);
        BigDecimal value = Calculator.calculate(nodes);
        String scale = subs.get(1).get(0).getExp();
        // 将第一个参数运行计算，然后使用第二个参数取整
        return value.setScale(Integer.parseInt(scale), BigDecimal.ROUND_DOWN);
    })),

    /**
     * 日期操作
     * DATE_OP(20201116000000-20-20200516000000)
     * <p>
     * 仅支持如下的操作
     * 日期 - 日期
     * 日期 - 日期 + 数值
     * 日期 - 日期 - 数值
     * 日期 + 数值 - 日期
     */
    DATE_OP("DATE_OP", "DATE_OP函数", (params -> {
        List<List<Node>> subs = splitNodeListByDot(params);
        // 第一个操作数必须是Date类型，且subs 中仅只能有一个list子集
        if (subs.size() != 1) {
            throw new RuntimeException("参数格式非法，DATE_OP函数期望1个参数，请检查！");
        }
        // 表达式
        List<Node> exps = subs.get(0);
        if (exps.size() != 3 && exps.size() != 5) {
            throw new RuntimeException("非法的日期操作数,请按日期操作模板传值");
        }
        Node node1 = exps.get(0);
        Node node2 = exps.get(2);
        Node opNode1 = exps.get(1);

        // 模板1： 日期 - 日期
        if (exps.size() == 3) {
            if (node1.isDate() && node2.isDate() && String.valueOf(Operator.SUB.code).equals(opNode1.getExp())) {
                return BigDecimal.valueOf(DateUtils.daysBetween(getDate(node2), getDate(node1)));
            }
            throw new RuntimeException(String.format("不支持的日期操作语法： %s %s %s", node1.getNodeTaps(), opNode1.getNodeTaps(), node2.getNodeTaps()));
        }

        Node node3 = exps.get(4);
        Node opNode2 = exps.get(3);

        // 模板2 ：日期 - 日期 + 数值
        if (node1.isDate() && String.valueOf(Operator.SUB.code).equals(opNode1.getExp()) && node2.isDate() && String.valueOf(Operator.ADD.code).equals(opNode2.getExp()) && node3.isNumber()) {
            long temp = DateUtils.daysBetween(getDate(node2), getDate(node1));
            return BigDecimal.valueOf((temp + Integer.parseInt(node3.getExp())));
        }
        // 模板3 ：日期 - 日期 - 数值
        if (node1.isDate() && String.valueOf(Operator.SUB.code).equals(opNode1.getExp()) && node2.isDate() && String.valueOf(Operator.SUB.code).equals(opNode2.getExp()) && node3.isNumber()) {
            long temp = DateUtils.daysBetween(getDate(node2), getDate(node1));
            return BigDecimal.valueOf((temp - Integer.parseInt(node3.getExp())));
        }
        // 模板4 ：日期 + 数值 - 日期
        if (node1.isDate() && String.valueOf(Operator.ADD.code).equals(opNode1.getExp()) && node2.isNumber() && String.valueOf(Operator.SUB.code).equals(opNode2.getExp()) && node3.isDate()) {
            Date temp = DateUtils.getAfterDays(getDate(node2), Integer.parseInt(node2.getExp()));
            return BigDecimal.valueOf(DateUtils.daysBetween(getDate(node3), temp));
        }

        throw new RuntimeException(String.format("不支持的日期操作语法： %s %s %s %s %s",
                node1.getNodeTaps(), opNode1.getNodeTaps(), node2.getNodeTaps(), opNode2.getNodeTaps(), node3.getNodeTaps()));
    })),
    ;

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
    public interface Calculate {
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
        if (!exp.get(0).isFunc()) {
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

    /**
     * 获取日期
     */
    private static Date getDate(Node node) {
        try {
            return DateUtils.getDateFromString(node.exp, DateUtils.DATE_FORMAT_YYYYMMDD);
        } catch (Exception e) {
            try {
                return DateUtils.getDateFromString(node.exp, DateUtils.DATE_FORMAT_YYYYMMDDHHMMSS);
            } catch (Exception e1) {
                throw new RuntimeException("不支持的日期格式:" + node.exp);
            }
        }

    }

}