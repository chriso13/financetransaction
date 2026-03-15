package com.chriso.financetransaction.dto;

import com.chriso.financetransaction.utility.PaymentMethod;
import com.chriso.financetransaction.utility.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.*;


public record FinanceTransactionRequest(

        @NotNull(message = "Transaction type is required")
        TransactionType transactionType,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        // Required if transactionType == TITHE
        Long memberId,

        // Required if transactionType == OFFERING
        Long worshipId,

        // Required if transactionType == TITHE (format: YYYY-MM)
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
                message = "Tithe month must be in format YYYY-MM"
        )
        String titheMonth,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String referenceNumber
) {}