package com.chriso.financetransaction.dto;

import java.math.BigDecimal;

public interface MonthlyTrendSummary {

    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();
}