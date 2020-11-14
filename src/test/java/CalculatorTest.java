import com.xiaozhameng.calculator.element.Node;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaozhameng.calculator.Calculator.*;
import static com.xiaozhameng.calculator.ExpressionParser.*;

/**
 * 功能描述：java 代码解决表达式
 * -- 计划采用逆波兰表达式的方式实现
 *
 * @author: xiaozhameng
 * @date: 2020/9/23 4:11 下午
 */
@Slf4j
public class CalculatorTest {

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
            log.info("***********************");
            log.info("原表达式 = {}", exp);
            ArrayList<Node> nodes = parseExp(exp);
            log.info("原表达式解析之后 = {}", logNodes(nodes));
            List<Node> suffixExp = generateSuffixExp(nodes);
            log.info("生成的后缀表达式 ={}", logNodes(suffixExp));
            BigDecimal val = calculateBySuffixExp(suffixExp);
            log.info("表达式计算值 = {}, 期望值 = {}", val, checkMap.get(exp));
        }
    }

    @Test
    public void checkByTestCase(){
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
            log.info("***********************");
            ArrayList<Node> nodes = parseExp(exp);
            BigDecimal val = calculate(nodes);
            log.info("表达式计算值 = {}, 期望值 = {}", val, checkMap.get(exp));
        }
    }

    @Test
    public void testCalculator() {
        String exp = "ROUND(1000*ROUND(0.7/360,7)*1.5*29,2)";
        log.info("原始表达式 = {}", exp);
        ArrayList<Node> nodes = parseExp(exp);
        log.info("转换后表达式 = {}", logNodes(nodes));
        BigDecimal calculate = calculate(nodes);
        log.info("*****计算结束*****, result = {}", calculate);
    }
}
