package com.chriso.financetransaction.dto;

import com.chriso.financetransaction.utility.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentMethodSummary {

    PaymentMethod getPaymentMethod();
    BigDecimal getTotalAmount();
}
