package com.chriso.financetransaction.utility;



import com.chriso.financetransaction.dto.FinanceTransactionRequest;
import com.chriso.financetransaction.dto.FinanceTransactionResponse;
import com.chriso.financetransaction.entity.FinanceTransaction;

import java.time.LocalDateTime;

public class FinanceTransactionMapper {

    public static FinanceTransaction toEntity(
            FinanceTransactionRequest request,
            String createdBy
    ) {

        FinanceTransaction entity = new FinanceTransaction();

        entity.setTransactionType(request.transactionType());
        entity.setAmount(request.amount());
        entity.setMemberId(request.memberId());
        entity.setWorshipId(request.worshipId());
        entity.setTitheMonth(request.titheMonth());
        entity.setCurrency(request.currency());
        entity.setPaymentMethod(request.paymentMethod());
        entity.setReferenceNumber(request.referenceNumber());

        entity.setStatus(TransactionStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);

        return entity;
    }

    public static FinanceTransactionResponse toResponse(
            FinanceTransaction entity
    ) {
        return new FinanceTransactionResponse(
                entity.getId(),
                entity.getTransactionType(),
                entity.getAmount(),
                entity.getMemberId(),
                entity.getWorshipId(),
                entity.getTitheMonth(),
                entity.getCurrency(),
                entity.getPaymentMethod(),
                entity.getStatus(),
                entity.getReferenceNumber(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }
}
