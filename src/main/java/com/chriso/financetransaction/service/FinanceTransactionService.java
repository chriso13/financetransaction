package com.chriso.financetransaction.service;

import com.chriso.financetransaction.adapter.MemberClient;
import com.chriso.financetransaction.adapter.WorshipClient;
import com.chriso.financetransaction.dto.*;
import com.chriso.financetransaction.entity.FinanceTransaction;
import com.chriso.financetransaction.repository.FinanceTransactionRepository;
import com.chriso.financetransaction.utility.FinanceTransactionMapper;
import com.chriso.financetransaction.utility.TransactionStatus;
import com.chriso.financetransaction.utility.TransactionType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FinanceTransactionService {

    private final FinanceTransactionRepository repository;
    private final MemberClient memberClient;
    private final WorshipClient worshipClient;

    public FinanceTransactionService(FinanceTransactionRepository repository,
                                     MemberClient memberClient,
                                     WorshipClient worshipClient) {
        this.repository = repository;
        this.memberClient = memberClient;
        this.worshipClient = worshipClient;
    }

    // =====================================================
    // ✅ SINGLE API DASHBOARD METHOD
    // =====================================================
    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboardData(int year) {

        DashboardResponseDto response = new DashboardResponseDto();

        // ------------------------
        // 1️⃣ Totals
        // ------------------------
        Object totalsRow = repository.getTotalsForYear(year);
        if (totalsRow != null) {
            Object[] totalsArray = (Object[]) totalsRow;

            response.totals.monthlyTotal = ((BigDecimal) totalsArray[3]).doubleValue();
            response.totals.titheTotal = ((BigDecimal) totalsArray[0]).doubleValue();
            response.totals.offeringTotal = ((BigDecimal) totalsArray[1]).doubleValue();
            response.totals.donationTotal = ((BigDecimal) totalsArray[2]).doubleValue();
        }

        // ------------------------
        // 2️⃣ Monthly data (charts)
        // ------------------------
        List<Object[]> monthlyRows = repository.getMonthlyTotalsByType(year);
        for (Object[] row : monthlyRows) {
            int monthIndex = ((Integer) row[0]) - 1; // 0-based index

            response.monthlyData.tithe.add(((BigDecimal) row[1]).doubleValue());
            response.monthlyData.offering.add(((BigDecimal) row[2]).doubleValue());
            response.monthlyData.donation.add(((BigDecimal) row[3]).doubleValue());
            response.monthlyData.monthlyIncome.add(((BigDecimal) row[4]).doubleValue());
        }

        // Fill missing months if repository returned fewer than 12
        while (response.monthlyData.tithe.size() < 12) {
            response.monthlyData.tithe.add(0.0);
            response.monthlyData.offering.add(0.0);
            response.monthlyData.donation.add(0.0);
            response.monthlyData.monthlyIncome.add(0.0);
        }

        // ------------------------
        // 3️⃣ Payment methods
        // ------------------------
        List<PaymentMethodSummary> paymentMethods = repository.getPaymentMethodTotalsForYear(year);
        response.paymentMethods.addAll(paymentMethods);

        return response;
    }

    // =====================================================
    // CREATE TRANSACTION
    // =====================================================
    public FinanceTransactionResponse createTransaction(FinanceTransactionRequest request,
                                                        String currentUser) {

        // 1️⃣ Idempotency check
        if (request.referenceNumber() != null) {
            repository.findByReferenceNumber(request.referenceNumber())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Duplicate transaction request (idempotency violation)");
                    });
        }

        // 2️⃣ Business validation
        validateBusinessRules(request);

        // 3️⃣ Duplicate tithe prevention
        if (request.transactionType() == TransactionType.TITHE) {
            boolean exists = repository.existsByTransactionTypeAndMemberIdAndTitheMonth(
                    TransactionType.TITHE, request.memberId(), request.titheMonth());
            if (exists) {
                throw new RuntimeException("Tithe already paid for this member and month");
            }
        }

        // 4️⃣ Create and save
        FinanceTransaction transaction = FinanceTransaction.builder()
                .transactionType(request.transactionType())
                .amount(request.amount())
                .memberId(request.memberId())
                .worshipId(request.worshipId())
                .titheMonth(request.titheMonth())
                .currency(request.currency())
                .paymentMethod(request.paymentMethod())
                .referenceNumber(request.referenceNumber())
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .createdBy(currentUser)
                .build();

        FinanceTransaction saved = repository.save(transaction);

        return FinanceTransactionMapper.toResponse(saved);
    }

    // =====================================================
    // BUSINESS RULE VALIDATION
    // =====================================================
    @CircuitBreaker(name = "financeTransactionService",
            fallbackMethod = "validateBusinessRulesFallback")
    private void validateBusinessRules(FinanceTransactionRequest request) {

        switch (request.transactionType()) {

            case TITHE -> {
                if (request.memberId() == null)
                    throw new RuntimeException("TITHE must have memberId");
                if (request.titheMonth() == null)
                    throw new RuntimeException("TITHE must have titheMonth");

                memberClient.getMemberById(request.memberId());
            }

            case OFFERING -> {
                if (request.worshipId() == null)
                    throw new RuntimeException("OFFERING must have worshipId");

                worshipClient.getWorshipById(request.worshipId());
            }

            case DONATION -> {
                // Optional validation
            }
        }
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @Transactional(readOnly = true)
    public FinanceTransactionResponse getById(Long id) {
        FinanceTransaction entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return FinanceTransactionMapper.toResponse(entity);
    }

    // =====================================================
    // GET ALL (PAGINATED)
    // =====================================================
    @Transactional(readOnly = true)
    public Page<FinanceTransactionResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(FinanceTransactionMapper::toResponse);
    }

    // =====================================================
    // UPDATE TRANSACTION
    // =====================================================
    public FinanceTransactionResponse update(Long id, FinanceTransactionRequest request) {

        FinanceTransaction entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        validateBusinessRules(request);

        if (request.transactionType() == TransactionType.TITHE) {
            boolean exists = repository.existsByTransactionTypeAndMemberIdAndTitheMonth(
                    TransactionType.TITHE, request.memberId(), request.titheMonth());

            if (exists &&
                    !(entity.getMemberId().equals(request.memberId())
                            && entity.getTitheMonth().equals(request.titheMonth()))) {
                throw new RuntimeException("Tithe already paid for this member and month");
            }
        }

        entity.setTransactionType(request.transactionType());
        entity.setAmount(request.amount());
        entity.setMemberId(request.memberId());
        entity.setWorshipId(request.worshipId());
        entity.setTitheMonth(request.titheMonth());
        entity.setCurrency(request.currency());
        entity.setPaymentMethod(request.paymentMethod());
        entity.setReferenceNumber(request.referenceNumber());
        entity.setStatus(TransactionStatus.COMPLETED);

        FinanceTransaction updated = repository.save(entity);

        return FinanceTransactionMapper.toResponse(updated);
    }

    // =====================================================
    // DELETE TRANSACTION
    // =====================================================
    public void delete(Long id) {
        FinanceTransaction entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        repository.delete(entity);
    }

    // =====================================================
    // MONTHLY TOTALS
    // =====================================================
    @Transactional(readOnly = true)
    public BigDecimal getMonthlyTotal(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return repository.getTotalAmountForMonth(start, end);
    }

    public BigDecimal getMonthlyTotalByType(int year, int month, TransactionType type) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        BigDecimal total = repository.getTotalAmountForMonthByType(start, end, type);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<BigDecimal> getMonthlyTotalByType(int year, TransactionType type) {
        List<BigDecimal> monthlyTotals = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            monthlyTotals.add(getMonthlyTotalByType(year, month, type));
        }
        return monthlyTotals;
    }

    public List<BigDecimal> getMonthlyTotal(int year) {
        List<BigDecimal> monthlyTotals = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal total = repository.getTotalAmountForMonth(
                    LocalDateTime.of(year, month, 1, 0, 0),
                    LocalDateTime.of(year, month, 1, 0, 0).plusMonths(1)
            );
            monthlyTotals.add(total != null ? total : BigDecimal.ZERO);
        }
        return monthlyTotals;
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodSummary> getMonthlyPaymentMethodSummary(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return repository.groupByPaymentMethod(start, end);
    }

    @Transactional(readOnly = true)
    public List<MonthlyTrendSummary> getLast12MonthsTrend() {
        LocalDateTime start = LocalDateTime.now().minusMonths(12)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0);
        return repository.getMonthlyTrend(start);
    }
}