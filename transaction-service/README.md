## Transaction Service

## Interfaces
````java
public interface AdjustmentService {
	Transaction subtract(long userId, @NonNull BigDecimal amount, String reason);
}

public interface BonusTransactionService {
	Transaction applySignupBonus(long userId, @NonNull BigDecimal bonusAmount);
	Transaction applyReferralBonus(long referrerUserId, long referredUserId, @NonNull BigDecimal bonusAmount);
	Transaction applyBonus(long userId, @NonNull BigDecimal bonusAmount, String reason);
	Transaction applyInterest(long userId, @NonNull BigDecimal interestAmount, String periodDescription);
}

public interface DepositService {
	Transaction deposit(@NonNull DepositRequest depositRequest);
	Transaction depositManual(long userId, long depositor, @NonNull BigDecimal amount, String remarks);
	BigDecimal getTotalDeposit(long userId);
	boolean isDepositExistsByTxnRef(String txnRefId);
	Page<Transaction> getDepositHistory(Long userId, Pageable pageable);
	Transaction confirmGatewayDeposit(String txnRefId, String gatewayResponseJson);
}

public interface ExchangeService {
	Transaction exchange(long userId, @NonNull BigDecimal fromAmount, @NonNull String fromCurrency, @NonNull BigDecimal toAmount, @NonNull String toCurrency, String metaInfo);
}

public interface InvestmentTransactionService {
	Transaction invest(long userId, @NonNull BigDecimal amount, String investmentType, String metaInfo);
}

public interface RefundService {
	Transaction refund(long userId, @NonNull BigDecimal amount, String originalTxnRef, String reason);
}


public TransactionQueryService {
	Page<Transaction> getTransactions(Transaction.TransactionStatus status, Integer page, Integer size);
    Page<Transaction> getProfits(Integer page, Integer size);
    Page<Transaction> getTransactionsByUserId(Long userId, Pageable pageable);
    Boolean hasDepositTransaction(Long userId);
	
	Page<Transaction> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
	
    Transaction findByTxnRefId(String txnRefId);
	
    Page<Transaction> findTransfersBySenderId(Long senderId, Pageable pageable);
    Page<Transaction> findTransfersByReceiverId(Long receiverId, Pageable pageable);
	
	
    Page<Transaction> findByUserIdAndStatusAndGateway(Long userId, Transaction.TransactionStatus status, PaymentGateway gateway, Pageable pageable);
    Page<Transaction> searchTransactions(Long userId,
                                         Transaction.TransactionStatus status,
                                         TransactionType type,
                                         PaymentGateway gateway,
                                         LocalDateTime fromDate,
                                         LocalDateTime toDate,
                                         Pageable pageable);
	
	
    Page<Transaction> searchByMetaInfo(String keyword, Pageable pageable);
    Page<Transaction> searchByKeyword(Long userId, String keyword, Pageable pageable);
	
	
    BigDecimal getTotalAmountByUserIdAndTxnType(Long userId, TransactionType txnType);
	
	
    List<Transaction> findTop10ByUserIdOrderByTxnDateDesc(Long userId);
	
	
    List<Transaction> findSuspiciousTransactions(BigDecimal threshold);
}

public interface TransferService {
	Transaction transferMoney(long senderId, long receiverId, @NonNull BigDecimal amount, String message);
}

public interface WalletService {
    BigDecimal getUserBalance(Long userId);
    void updateBalanceFromTransaction(Long userId, BigDecimal delta);
    void updateBalanceFromTransaction(Long userId, BigDecimal delta, TransactionType transactionType);
    void ensureSufficientBalance(Long userId, BigDecimal amount);
}

public interface WithdrawalService {
    Transaction withdraw(long userId, @NonNull BigDecimal amount, String destinationAccount, String remarks);
}





````