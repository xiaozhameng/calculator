package com.xiaozhameng.calculator;


import com.xiaozhameng.calculator.element.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import static com.xiaozhameng.calculator.ExpressionParser.*;


/**
 * 功能描述：表达式计算工具类
 *
 * @author: xiaozhameng
 * @date: 2020/9/28 5:09 下午
 */
public class Calculator {

    static Logger logger = LoggerFactory.getLogger(Calculator.class);

    /**
     * 计算
     */
    public static BigDecimal calculate(List<Node> nodes) {
        logger.info("******开始计算******, 表达式 = {}", logNodes(nodes));
        if (nodes == null || nodes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Stack<Node> stack = new Stack<>();
        for (Node node : nodes) {
            stack.push(node);
            // 如果是括号，从栈中弹出元素序列
            if (Mark.RIGHT_BRACE.equals(Mark.getByCode(node.getExp()))) {
                logger.info("******遇到右边括号******，此时，栈元素 ={}", logNodes(stack));
                Stack<Node> temp = new Stack<>();
                while (true) {
                    // 终止条件：弹出的栈顶元素是左括号，或者栈已经为空
                    boolean stopCheck = (stack.isEmpty() || Mark.LEFT_BRACE.equals(Mark.getByCode(stack.peek().getExp())));
                    if (stopCheck) {
                        // 如果是左括号，需要把左括号也加到临时栈
                        if (!stack.isEmpty()) {
                            temp.push(stack.pop());
                        }
                        // 如果是紧邻函数名称的左括号，函数名称也加入到临时栈'
                        if (!stack.isEmpty() && stack.peek().isFunc()) {
                            temp.push(stack.pop());
                        }
                        break;
                    }
                    temp.push(stack.pop());
                }

                logger.info("生成最小计算单元的临时栈时，原栈数据 = {}, \t临时temp栈 = {}", logNodes(stack), logNodes(temp));
                // 因为temp 里面的元素是按照出栈的顺序入队的，所以
                Node[] sub = new Node[temp.size()];
                while (!temp.isEmpty()) {
                    sub[(sub.length - 1) - (temp.size() - 1)] = temp.pop();
                }
                List<Node> subList = Arrays.asList(sub);
                logger.info("生成最小计算单元的临时sub队列 = {}", logNodes(subList));

                // 开始计算 判断sub 子集是不是一个函数，如果是函数，直接将子集交给对应的函数，如果是一个表达式，则直接计算
                if (subList.get(0).isFunc()) {
                    logger.info("子集合是一个函数，交给函数计算，表达式 = {}", logNodes(subList));
                    Function function = Function.getByCode(subList.get(0).getExp());
                    BigDecimal val = function.getCalculate().calculate(subList);
                    // 将计算结果压入到栈中
                    Node tempNode = Node.builder()
                            .exp(val.toString())
                            .type(NodeType.NUMERICAL).build();
                    stack.push(tempNode);
                } else {
                    logger.info("子集合是一个表达式 = {}， 直接调用计算即可", logNodes(subList));
                    BigDecimal val = calculateBySuffixExp(generateSuffixExp(subList));
                    // 将计算结果压入到栈中
                    Node tempNode = Node.builder()
                            .exp(val.toString())
                            .type(NodeType.NUMERICAL).build();
                    stack.push(tempNode);
                }
                logger.info("计算结果入栈 = {}", logNodes(stack));
            }
        }

        // 最后需要计算的表达式
        logger.info("最后需要计算的表达式 = {}", logNodes(stack));
        Node[] lastSubNodes = new Node[stack.size()];
        stack.copyInto(lastSubNodes);
        return calculateBySuffixExp(generateSuffixExp(lastSubNodes));
    }

    /**
     * 根据后缀表达式计算值
     * <p>
     * 从左至右扫描表达式
     * 遇到数字时，将数字压入堆栈
     * 遇到运算符时，弹出栈顶的两个数，用运算符对它们做相应的计算（次顶元素 op 栈顶元素），并将结果入栈；
     * 重复上述过程直到表达式最右端，最后运算得出的值即为表达式的结果
     */
    public static BigDecimal calculateBySuffixExp(List<Node> nodes) {
        Stack<BigDecimal> result = new Stack<>();
        for (Node node : nodes) {
            if (node.isDate()){
                throw new RuntimeException("日期格式的变量只支持嵌套在日期函数中进行计算");
            }
            if (node.isNumber()) {
                result.push(new BigDecimal(node.getExp()));
            }
            if (node.isOperator()) {
                BigDecimal top1 = result.pop();
                BigDecimal top2 = result.pop();

                // 运算次顶的元素和栈顶的元素
                BigDecimal res = Operator.getByCode(node.getExp()).calculate(top2, top1);
                result.push(res);
            }
        }
        return result.pop();
    }

    /**
     * 表达式输出打印
     */
    public static String logNodes(List<Node> list) {
        if (list != null) {
            StringBuilder builder = new StringBuilder();
            for (Node node : list) {
                builder.append(node.getExp()).append("\t");
            }
            return builder.toString();
        }
        return null;
    }

    /**
     * 表达式输出打印
     */
    public static String logNodes(Stack<Node> stack) {
        Node[] nodes = new Node[stack.size()];
        stack.copyInto(nodes);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            builder.append(nodes[i].getExp());
        }
        return builder.toString();
    }

}