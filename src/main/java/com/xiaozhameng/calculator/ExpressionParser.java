package com.xiaozhameng.calculator;

import com.xiaozhameng.calculator.element.Mark;
import com.xiaozhameng.calculator.element.Node;
import com.xiaozhameng.calculator.element.NodeType;
import com.xiaozhameng.calculator.element.Operator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 表达式解析器
 */
public class ExpressionParser {

    /**
     * 解析表达式，例如： ROUND(DUE_AMOUNT*ROUND(0.07/360,6)*1.5*29,2)
     */
    public static ArrayList<Node> parseExp(String exp) {
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
                doMatchProcess(nodeList, temp);
                Node markNode = Node.builder()
                        .exp(String.valueOf(c))
                        .type(NodeType.checkType(String.valueOf(c)))
                        .build();
                nodeList.add(markNode);
                continue;
            }
            temp.add(c);
        }
        doMatchProcess(nodeList, temp);
        return nodeList;
    }

    /**
     * 处理匹配节点
     */
    private static void doMatchProcess(ArrayList<Node> nodeList, List<Character> temp) {
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
            doGenerate(node, suffixList, opStack);
        }
        // 将栈中的元素顺序出栈到后缀表达式队列中
        while (!opStack.isEmpty()) {
            suffixList.add(opStack.pop());
        }
        return suffixList;
    }

    /**
     * 中缀表达式转后缀表达式
     */
    public static List<Node> generateSuffixExp(Node[] nodes) {
        Stack<Node> opStack = new Stack<>();
        List<Node> suffixList = new LinkedList<>();

        // 遍历表达式
        for (Node node : nodes) {
            doGenerate(node, suffixList, opStack);
        }
        // 将栈中的元素顺序出栈到后缀表达式队列中
        while (!opStack.isEmpty()) {
            suffixList.add(opStack.pop());
        }
        return suffixList;
    }

    /**
     * 中缀表达式转后缀表达式
     */
    private static void doGenerate(Node node, List<Node> suffixList, Stack<Node> opStack) {
        if (NodeType.NUMERICAL.equals(node.getType())) {
            // 如果是操作数，将其压入到栈中
            suffixList.add(node);
        } else if (NodeType.OPERATOR.equals(node.getType())) {
            if (opStack.isEmpty()
                    || opStack.peek().getExp().equals(String.valueOf(Mark.LEFT_BRACE.getCode()))) {
                // 如果运算符栈为空，或者栈顶元素是左括号，则将该运算符入栈
                opStack.push(node);
                return;
            }

            // 如果优先级比栈顶元素优先级高，也将运算符压入到运算符栈
            Node topNode = opStack.peek();
            if (Operator.getByCode(node.getExp()).getLevel() > Operator.getByCode(topNode.getExp()).getLevel()) {
                opStack.push(node);
                return;
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

    /**
     * 工具方法
     */
    private static String convert2String(List<Character> data) {
        char[] chars = new char[data.size()];
        for (int i = 0; i < data.size(); i++) {
            chars[i] = data.get(i);
        }
        return String.valueOf(chars);
    }
}
