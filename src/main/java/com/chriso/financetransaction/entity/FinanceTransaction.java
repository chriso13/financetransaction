package com.chriso.financetransaction.entity;

import com.chriso.financetransaction.utility.PaymentMethod;
import com.chriso.financetransaction.utility.TransactionStatus;
import com.chriso.financetransaction.utility.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "finances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    private Long memberId; // Required for TITHE

    private Long worshipId; // Required for OFFERING

    private String titheMonth; // Format YYYY-MM

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String referenceNumber;

    private LocalDateTime createdAt;

    private String createdBy;
}
