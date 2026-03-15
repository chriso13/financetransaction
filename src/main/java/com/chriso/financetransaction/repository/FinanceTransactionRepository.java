package com.chriso.financetransaction.repository;



import com.chriso.financetransaction.dto.MonthlyTrendSummary;
import com.chriso.financetransaction.dto.PaymentMethodSummary;
import com.chriso.financetransaction.entity.FinanceTransaction;
import com.chriso.financetransaction.utility.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {

    // ================= EXISTING METHODS KEPT =================
    // findByReferenceNumber, existsByReferenceNumber, etc.
    // ... your previous code ...

    // ================= NEW DASHBOARD METHODS =================

    /**
     * 1️⃣ Total amounts for the year
     */
    @Query("""
       SELECT
         COALESCE(SUM(CASE WHEN f.transactionType = 'TITHE' THEN f.amount ELSE 0 END), 0) AS titheTotal,
         COALESCE(SUM(CASE WHEN f.transactionType = 'OFFERING' THEN f.amount ELSE 0 END), 0) AS offeringTotal,
         COALESCE(SUM(CASE WHEN f.transactionType = 'DONATION' THEN f.amount ELSE 0 END), 0) AS donationTotal,
         COALESCE(SUM(f.amount), 0) AS monthlyTotal
       FROM FinanceTransaction f
       WHERE YEAR(f.createdAt) = :year
         AND f.status = 'COMPLETED'
       """)
    Object getTotalsForYear(@Param("year") int year);

    /**
     * 2️⃣ Monthly totals by type for charts
     */
    @Query("""
       SELECT MONTH(f.createdAt) AS month,
              SUM(CASE WHEN f.transactionType = 'TITHE' THEN f.amount ELSE 0 END) AS tithe,
              SUM(CASE WHEN f.transactionType = 'OFFERING' THEN f.amount ELSE 0 END) AS offering,
              SUM(CASE WHEN f.transactionType = 'DONATION' THEN f.amount ELSE 0 END) AS donation,
              SUM(f.amount) AS monthlyIncome
       FROM FinanceTransaction f
       WHERE YEAR(f.createdAt) = :year
         AND f.status = 'COMPLETED'
       GROUP BY MONTH(f.createdAt)
       ORDER BY month
       """)
    List<Object[]> getMonthlyTotalsByType(@Param("year") int year);

    /**
     * 3️⃣ Payment methods summary for the year
     */
    @Query("""
       SELECT f.paymentMethod AS paymentMethod,
              COALESCE(SUM(f.amount), 0) AS total
       FROM FinanceTransaction f
       WHERE YEAR(f.createdAt) = :year
         AND f.status = 'COMPLETED'
       GROUP BY f.paymentMethod
       """)
    List<PaymentMethodSummary> getPaymentMethodTotalsForYear(@Param("year") int year);

    boolean existsByTransactionTypeAndMemberIdAndTitheMonth(
            TransactionType transactionType,
            Long memberId,
            String titheMonth
    );

    Optional<FinanceTransaction> findByReferenceNumber(String referenceNumber);

    @Query("""
       SELECT COALESCE(SUM(f.amount), 0)
       FROM FinanceTransaction f
       WHERE f.createdAt >= :start
       AND f.createdAt < :end
       AND f.status = 'COMPLETED'
       """)
    BigDecimal getTotalAmountForMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("""
       SELECT COALESCE(SUM(f.amount), 0)
       FROM FinanceTransaction f
       WHERE f.createdAt >= :start
       AND f.createdAt < :end
       AND f.transactionType = :type
       AND f.status = 'COMPLETED'
       """)
    BigDecimal getTotalAmountForMonthByType(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("type") TransactionType type
    );

    @Query("""
       SELECT f.paymentMethod as paymentMethod,
              COALESCE(SUM(f.amount), 0) as totalAmount
       FROM FinanceTransaction f
       WHERE f.createdAt >= :start
       AND f.createdAt < :end
       AND f.status = 'COMPLETED'
       GROUP BY f.paymentMethod
       """)
    List<PaymentMethodSummary> groupByPaymentMethod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
       SELECT YEAR(f.createdAt) as year,
              MONTH(f.createdAt) as month,
              COALESCE(SUM(f.amount), 0) as total
       FROM FinanceTransaction f
       WHERE f.createdAt >= :start
       AND f.status = 'COMPLETED'
       GROUP BY YEAR(f.createdAt), MONTH(f.createdAt)
       ORDER BY year, month
       """)
    List<MonthlyTrendSummary> getMonthlyTrend(
            @Param("start") LocalDateTime start
    );

}