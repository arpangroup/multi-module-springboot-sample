package com.trustai.common.repository.user;

import com.trustai.common.domain.user.User;
import com.trustai.common.repository.common.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByReferralCode(String referralCode);
    Optional<User> findByReferralCode(String referralCode);
    List<User> findByIdIn(List<Long> ids);
    boolean existsByMobile(String mobile);

    List<User> findByAccountStatus(User.AccountStatus accountStatus);
    Page<User> findByAccountStatus(User.AccountStatus accountStatus, Pageable pageable);

    @Query("SELECT u.walletBalance FROM User u WHERE u.id = :userId")
    Optional<BigDecimal> findWalletBalanceById(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.walletBalance = :balance WHERE u.id = :userId")
    void updateWalletBalance(@Param("userId") Long userId, @Param("balance") BigDecimal balance);

    // Optional: Filter by registration date range
    @Query("""
        SELECT u FROM User u
        WHERE u.id IN :ids
          AND (:startDate IS NULL OR u.createdAt >= :startDate)
          AND (:endDate IS NULL OR u.createdAt <= :endDate)
    """)
    List<User> findByIdsAndDateRange(
            @Param("ids") List<Long> ids,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /*
    @Query("SELECT u.depositBalance FROM User u WHERE u.id = :userId")
    Optional<BigDecimal> findDepositBalanceById(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.depositBalance = :balance WHERE u.id = :userId")
    void updateDepositBalance(@Param("userId") Long userId, @Param("balance") BigDecimal balance);
    */
}
