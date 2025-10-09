package com.trustai.aggregator.repository;

import com.trustai.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Transaction, Long> {
    @Query(value = """
        SELECT DATE_FORMAT(t.created_at, '%Y-%m') as month,
               SUM(CASE WHEN t.txn_type IN ('DEPOSIT','DEPOSIT_MANUAL') AND t.status = 'SUCCESS' THEN t.amount ELSE 0 END) as totalDeposit,
               SUM(CASE WHEN t.txn_type = 'INVESTMENT' AND t.status = 'SUCCESS' THEN t.amount ELSE 0 END) as totalInvestment,
               SUM(CASE WHEN t.txn_type = 'WITHDRAWAL' AND t.status = 'SUCCESS' THEN t.amount ELSE 0 END) as totalWithdraw,
               SUM(CASE WHEN t.txn_type = 'DAILY_INCOME' AND t.status = 'SUCCESS' THEN t.amount ELSE 0 END) as totalProfit
        FROM transactions t
        WHERE t.created_at BETWEEN :startDate AND :endDate
        GROUP BY DATE_FORMAT(t.created_at, '%Y-%m')
        ORDER BY month
    """, nativeQuery = true)
    List<Object[]> getMonthlyStats(LocalDateTime startDate, LocalDateTime endDate);
}
