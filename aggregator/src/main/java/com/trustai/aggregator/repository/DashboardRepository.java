package com.trustai.aggregator.repository;

import com.trustai.common.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface DashboardRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u")
    long countRegisteredUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.accountStatus = 'ACTIVE'")
    long countActiveUsers();

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'STAFF'")
    long countSiteStaff();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType IN ('DEPOSIT','DEPOSIT_MANUAL') AND t.status = 'SUCCESS'")
    BigDecimal getTotalDeposits();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType = 'WITHDRAWAL' AND t.status = 'SUCCESS'")
    BigDecimal getTotalWithdrawals();

    @Query("SELECT COUNT(uh) FROM UserHierarchy uh WHERE uh.depth = 1")
    long getTotalReferrals();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType = 'SEND_MONEY' AND t.status = 'SUCCESS'")
    BigDecimal getTotalSend();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType = 'INVESTMENT' AND t.status = 'SUCCESS'")
    BigDecimal getTotalInvestments();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType = 'BONUS' AND t.sourceModule = 'deposit' AND t.status = 'SUCCESS'")
    BigDecimal getDepositBonus();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.txnType = 'BONUS' AND t.sourceModule = 'investment' AND t.status = 'SUCCESS'")
    BigDecimal getInvestmentBonus();
}
