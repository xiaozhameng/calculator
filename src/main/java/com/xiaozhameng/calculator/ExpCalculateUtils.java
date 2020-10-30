package com.xiaozhameng.calculator;


import com.xiaozhameng.calculator.ele.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * 功能描述：表达式计算工具类
 *
 * @author: qiaofengjun
 * @date: 2020/9/28 5:09 下午
 */
public class ExpCalculateUtils {

    static Logger logger = LoggerFactory.getLogger(ExpCalculateUtils.class);

    @Test
    public void test() {
        String exp = "ROUND(1000*ROUND(0.07/360,6)*1.5*29,2)";
        ArrayList<Node> nodes1 = resolveExp(exp);
        System.out.println(logNodes(nodes1));

        exp = "(20+100/2)-40*(10/2)";
        ArrayList<Node> nodes = resolveExp(exp);
        System.out.println(logNodes(nodes));
    }

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
                        if (!stack.isEmpty() && Function.funcCheck(stack.peek().getExp())) {
                            temp.push(stack.pop());
                        }
                        break;
                    }
                    temp.push(stack.pop());
                }

                logger.info("生成临时栈时，原栈数据 = {}, 临时temp栈 = {}", logNodes(stack), logNodes(temp));

                // 因为temp 里面的元素是按照出栈的顺序入队的，所以
                Node[] sub = new Node[temp.size()];
                while (!temp.isEmpty()) {
                    sub[(sub.length - 1) - (temp.size() - 1)] = temp.pop();
                }

                List<Node> subList = Arrays.asList(sub);
                logger.info("临时sub队列 = {}", logNodes(subList));

                // 开始计算 判断sub 子集是不是一个函数，如果是函数，直接将子集交给对应的函数，如果是一个表达式，则直接计算
                if (Function.funcCheck(subList.get(0).getExp())) {
                    logger.info("子集合是一个函数，交给函数计算，表达式 = {}", logNodes(subList));
                    Function function = Function.getByCode(subList.get(0).getExp());
                    BigDecimal val = function.getCalculate().calculate(subList);
                    // 将计算结果压入到栈中
                    Node tempNode = Node.builder()
                            .exp(val.toString())
                            .type(NodeType.NUMERICAL).build();
                    stack.push(tempNode);
                    logger.info("计算结果入栈 = {}", logNodes(stack));
                } else {
                    logger.info("子集合是一个表达式 = {}， 直接调用计算即可", logNodes(subList));
                    BigDecimal val = calculateBySuffixExp(subList);
                    // 将计算结果压入到栈中
                    Node tempNode = Node.builder()
                            .exp(val.toString())
                            .type(NodeType.NUMERICAL).build();
                    stack.push(tempNode);
                    logger.info("计算结果入栈 = {}", logNodes(stack));
                }
            }
        }

        // 最后需要计算的表达式
        logger.info("最后需要计算的表达式 = {}", logNodes(stack));
        LinkedList<Node> temp = new LinkedList<>();
        while (stack.isEmpty()) {
            temp.add(stack.pop());
        }
        return calculate(temp);
    }

    /**
     * 解析表达式，例如： ROUND(DUE_AMOUNT*ROUND(0.07/360,6)*1.5*29,2)
     */
    public static ArrayList<Node> resolveExp(String exp) {
        // 将表达式中的空元素移除
        exp = exp.trim()
                .replaceAll("\t", "")
                .replaceAll(" ", "");
        ArrayList<Node> nodeList = new ArrayList<>();
        List<Character> temp = new LinkedList<>();
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            boolean match = Mark.match(c) || Operator.match(c);
            if (match) {
                // 如果匹配了，先把队列中的字符拼成一个node，然后紧接着拼接上匹配字符
                if (!temp.isEmpty()) {
                    String subExp = convert2String(temp);
                    // 需要立即将temp 清空
                    temp.clear();
                    NodeType nodeType = NodeType.checkType(subExp);
                    Node node = Node.builder()
                            .exp(subExp)
                            .type(nodeType)
                            .build();
                    nodeList.add(node);
                }

                Node markNode = Node.builder()
                        .exp(String.valueOf(c))
                        .type(NodeType.checkType(String.valueOf(c)))
                        .build();
                nodeList.add(markNode);
                continue;
            }
            temp.add(c);
        }
        if (!temp.isEmpty()) {
            String subExp = convert2String(temp);
            // 需要立即将temp 清空
            temp.clear();
            NodeType nodeType = NodeType.checkType(subExp);
            Node node = Node.builder()
                    .exp(subExp)
                    .type(nodeType)
                    .build();
            nodeList.add(node);
        }
        return nodeList;
    }

    private static String convert2String(List<Character> data) {
        char[] chars = new char[data.size()];
        for (int i = 0; i < data.size(); i++) {
            chars[i] = data.get(i);
        }
        return String.valueOf(chars);
    }

    /**
     * 中缀表达式转后缀表达式
     * <p>
     * ===================================
     * 声明一个操作栈stack，一个结果线性表result
     * 从左到右扫描一遍表中缀表达式
     * 1、遇到操作数时，直接输出到线性表
     * 2、遇到运算符时，比较其余操作栈栈顶元素的优先级
     * 2.1 如果操作栈为空，或者栈顶运算符为左括号"(", 则直接将次运算符入栈
     * 2.2 否则，如果优先级比栈顶运算符的优先级高，则将其也压入到操作栈（不包括优先级相同的情况）
     * 2.3 否则，将操作栈栈顶运算符弹出并输出到线性表。直到遇到优先级大于当前运算符的元素为止
     * 3、遇到括号时
     * 3.1 如果是左括号，则直接压入操作栈
     * 3.2 如果是右括号，则一次弹出操作栈中的运算符，并输出到线性表，直到遇到左括号为止，并将这一对括号丢弃
     * 此时，顺序输出线性表中的序列，即为后缀表达式结果
     * <p>
     * ===================================
     */
    public static List<Node> generateSuffixExp(List<Node> nodes) {
        Stack<Node> opStack = new Stack<>();
        List<Node> suffixList = new LinkedList<>();

        // 遍历表达式
        for (Node node : nodes) {
            if (NodeType.NUMERICAL.equals(node.getType())) {
                // 如果是操作数，将其压入到栈中
                suffixList.add(node);
            } else if (NodeType.OPERATOR.equals(node.getType())) {
                if (opStack.isEmpty()
                        || opStack.peek().getExp().equals(String.valueOf(Mark.LEFT_BRACE.getCode()))) {
                    // 如果运算符栈为空，或者栈顶元素是左括号，则将该运算符入栈
                    opStack.push(node);
                    continue;
                }

                // 如果优先级比栈顶元素优先级高，也将运算符压入到运算符栈
                Node topNode = opStack.peek();
                if (Operator.getByCode(node.getExp()).getLevel() > Operator.getByCode(topNode.getExp()).getLevel()) {
                    opStack.push(node);
                    continue;
                }

                // 弹出栈顶运算符，输出到线性表，并循环判断
                Node pop = opStack.pop();
                suffixList.add(pop);
                while (!opStack.isEmpty() && opStack.peek().getExp().equals(String.valueOf(Mark.LEFT_BRACE.getCode()))) {
                    Node peek = opStack.peek();
                    if (NodeType.OPERATOR.equals(peek.getType()) && Operator.getByCode(node.getExp()).getLevel() <= Operator.getByCode(peek.getExp()).getLevel()) {
                        suffixList.add(opStack.pop());
                    }
                }
                opStack.push(node);
            } else if (NodeType.MARK.equals(node.getType())) {
                if (Mark.LEFT_BRACE.equals(Mark.getByCode(node.getExp()))) {
                    opStack.push(node);
                } else if (Mark.RIGHT_BRACE.equals(Mark.getByCode(node.getExp()))) {
                    while (!opStack.isEmpty()) {
                        // 直到遇到左括号为止
                        if (Mark.LEFT_BRACE.equals(Mark.getByCode(opStack.peek().getExp()))) {
                            opStack.pop();
                            break;
                        }
                        suffixList.add(opStack.pop());
                    }
                }
            } else {
                throw new RuntimeException("未能正确解析的标识 = " + node.getExp());
            }
        }
        // 将栈中的元素顺序出栈到后缀表达式队列中
        while (!opStack.isEmpty()) {
            suffixList.add(opStack.pop());
        }
        return suffixList;
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
            if (NodeType.NUMERICAL.equals(node.getType())) {
                result.push(new BigDecimal(node.getExp()));
            }
            if (NodeType.OPERATOR.equals(node.getType())) {
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
