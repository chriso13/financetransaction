package com.chriso.financetransaction.dto;

import com.chriso.financetransaction.utility.PaymentMethod;
import com.chriso.financetransaction.utility.TransactionStatus;
import com.chriso.financetransaction.utility.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinanceTransactionResponse(

        Long id,

        TransactionType transactionType,

        BigDecimal amount,

        Long memberId,

        Long worshipId,

        String titheMonth,

        String currency,

        PaymentMethod paymentMethod,

        TransactionStatus status,

        String referenceNumber,

        LocalDateTime createdAt,

        String createdBy
) {
}