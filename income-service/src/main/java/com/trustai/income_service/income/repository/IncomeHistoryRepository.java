package com.trustai.income_service.income.repository;

import com.trustai.common.enums.IncomeType;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IncomeHistoryRepository extends JpaRepository<IncomeHistory, Long> {

    // Generic summary query (optionally filtered by user)
    @Query(value = """
        SELECT ih.income_type AS incomeType,
               SUM(CASE WHEN DATE(ih.created_at) = CURRENT_DATE THEN ih.amount ELSE 0 END) AS todayAmount,
               SUM(CASE WHEN DATE(ih.created_at) = CURRENT_DATE - INTERVAL 1 DAY THEN ih.amount ELSE 0 END) AS yesterdayAmount,
               SUM(CASE WHEN DATE(ih.created_at) >= CURRENT_DATE - INTERVAL 7 DAY THEN ih.amount ELSE 0 END) AS last7DaysAmount,
               SUM(ih.amount) AS totalAmount,
               COUNT(ih.id) AS totalOrders,
               SUM(CASE WHEN ih.income_type = 'DAILY' THEN 1 ELSE 0 END) AS processingOrders
        FROM income_history ih
        WHERE (:userId IS NULL OR ih.user_id = :userId)
        GROUP BY ih.income_type
        """, nativeQuery = true)
    List<IncomeSummaryProjection> getIncomeSummary(@Nullable @Param("userId") Long userId);


    // Generic filter query (optionally filtered by user)
    @Query("""
        SELECT ih
        FROM IncomeHistory ih
        WHERE (:userId IS NULL OR ih.userId = :userId)
          AND (:startDate IS NULL OR ih.createdAt >= :startDate)
          AND (:endDate IS NULL OR ih.createdAt <= :endDate)
          AND (:incomeType IS NULL OR ih.incomeType = :incomeType)
    """)
    Page<IncomeHistory> findByFilters(
            @Nullable @Param("userId") Long userId,
            @Nullable @Param("startDate") LocalDateTime startDate,
            @Nullable @Param("endDate") LocalDateTime endDate,
            @Nullable @Param("incomeType") IncomeType incomeType,
            Pageable pageable
    );

/*    @Query("""
        SELECT COALESCE(SUM(ih.amount), 0)
        FROM IncomeHistory ih
        WHERE ih.userId IN :userIds
          AND ih.incomeType = :incomeType
          AND (:startDate IS NULL OR ih.createdAt >= :startDate)
          AND (:endDate IS NULL OR ih.createdAt <= :endDate)
    """)
    BigDecimal sumShareByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Param("incomeType") IncomeType incomeType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );*/

    @Query("SELECT ih.userId, SUM(ih.amount) " +
            "FROM IncomeHistory ih " +
            "WHERE ih.userId IN :userIds " +
            "AND (:start IS NULL OR ih.createdAt >= :start) " +
            "AND (:end IS NULL OR ih.createdAt <= :end) " +
            "GROUP BY ih.userId")
    List<Object[]> sumSharesByUserIdsAndDateRange(
            @Param("userIds") List<Long> userIds,
            @Nullable @Param("start") LocalDateTime start,
            @Nullable @Param("end") LocalDateTime end
    );
}
