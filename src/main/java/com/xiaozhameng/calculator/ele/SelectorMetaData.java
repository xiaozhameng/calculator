package com.xiaozhameng.calculator.ele;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 功能描述：选择器取数元数据
 *
 * @author: qiaofengjun
 * @date: 2020/9/25 4:04 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class SelectorMetaData {

    /**
     * 逾期利率
     */
    private BigDecimal overdueRate;

    /**
     * 逾期服务费利率
     */
    private BigDecimal overdueServiceRate;

    /**
     * 逾期利息利率
     */
    private BigDecimal overdueInterestRate;

    /**
     * 本金
     */
    private BigDecimal principalAmount;
}
