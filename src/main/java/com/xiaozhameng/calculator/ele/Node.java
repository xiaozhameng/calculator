package com.xiaozhameng.calculator.ele;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表达式中某个元素的抽象
 * @author qiaofengjun
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
}

