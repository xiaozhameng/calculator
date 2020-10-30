package com.xiaozhameng.calculator.ele;


/**
 * 符号的定义
 * @author qiaofengjun
 * @date 2020/09/26
 */
public enum Mark {

    /**
     * 枚举值
     */
    LEFT_BRACE('(', "左括号"),

    RIGHT_BRACE(')', "右括号"),

    DOT(',', "逗号");

    char code;
    String desc;

    Mark(char code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code 获取枚举值
     */
    public static Mark getByCode(String code) {
        if (code == null || code.length() > 1) {
            return null;
        }
        return getByCode(code.charAt(0));
    }

    /**
     * 根据code 获取枚举值
     */
    public static Mark getByCode(char code) {
        for (Mark value : Mark.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    /**
     * 扫描一遍 ，检查是否匹配
     */
    public static boolean match(char exp) {
        for (Mark mark : Mark.values()) {
            if (exp == mark.code) {
                return true;
            }
        }
        return false;
    }

    public char getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}