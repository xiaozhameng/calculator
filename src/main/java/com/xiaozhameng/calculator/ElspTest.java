package com.xiaozhameng.calculator;



import com.xiaozhameng.calculator.ele.Node;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.xiaozhameng.calculator.ExpCalculateUtils.*;

/**
 * 功能描述：java 代码解决表达式
 * <p>
 * 可以支持的表达式格式
 * <p>
 * 函数定义
 * 1、ROUND、ROUNDUP、ROUNDDOWN
 * <p>
 * <p>
 * -- 计划采用逆波兰表达式的方式实现
 *
 * @author: qiaofengjun
 * @date: 2020/9/23 4:11 下午
 */
public class ElspTest {

    @Test
    public void testSuffixCheck() {
        Map<String, BigDecimal> checkMap = new HashMap<>(16);

        // 带括号，括号开头，括号结尾
        checkMap.put("(20+100/2)-40*(10/2)", new BigDecimal("-130"));
        // 带括号，开头是数字
        checkMap.put("100-20+5*4 + (20+10) / 5", new BigDecimal("106"));
        // 没有括号
        checkMap.put("100+10-20*3/6", new BigDecimal("100"));

        // 不断完善添加的测试用例
        checkMap.put("100/2", new BigDecimal("50"));

        for (String exp : checkMap.keySet()) {
            logger.info("***********************");
            logger.info("原表达式 = {}", exp);
            ArrayList<Node> nodes = resolveExp(exp);
            logger.info("原表达式解析之后 = {}", logNodes(nodes));
            List<Node> suffixExp = generateSuffixExp(nodes);
            logger.info("生成的后缀表达式 ={}", logNodes(suffixExp));
            BigDecimal val = calculateBySuffixExp(suffixExp);
            logger.info("表达式计算值 = {}, 期望值 = {}", val, checkMap.get(exp));
        }
    }

    @Test
    public void testStack() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.peek());
        }
    }

    @Test
    public void testCommon() {
        BigDecimal val = new BigDecimal("0E-7");
        System.out.println(val.longValue());
    }

    public static void main(String[] args) {
        String exp = "ROUND(1000*ROUND(0.7/360,7)*1.5*29,2)";
        logger.info("原始表达式 = {}", exp);
        ArrayList<Node> nodes = resolveExp(exp);
        logger.info("转换后表达式 = {}", logNodes(nodes));
        BigDecimal calculate = calculate(nodes);
        logger.info("*****计算结束*****, result = {}", calculate);
    }

}
