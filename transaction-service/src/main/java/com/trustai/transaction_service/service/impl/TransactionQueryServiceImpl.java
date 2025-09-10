package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.service.TransactionQueryService;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
//import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.trustai.common.enums.TransactionType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionQueryServiceImpl implements TransactionQueryService {
    private final TransactionRepository transactionRepository;
//    private final MeterRegistry meterRegistry;
    private final List<TransactionType> PROFIT_TYPES = List.of(SIGNUP_BONUS, REFERRAL, BONUS, INTEREST);


    @Override
//    @Timed("transaction.getTransactions.time")
    public Page<Transaction> getTransactions(Transaction.TransactionStatus status, Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        log.info("Fetching transactions with status: {}, page: {}, size: {}", status, pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Transaction> transactionPage;

        if (status != null) {
            transactionPage = transactionRepository.findByStatus(status, pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }

        log.info("Fetched {} transactions", transactionPage.getNumberOfElements());
        return transactionPage;
    }

    @Override
    public Page<Transaction> getProfits(Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        log.info("Fetching profit transactions, page: {}, size: {}", pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Transaction> transactionPage = transactionRepository.findByTxnTypeIn(PROFIT_TYPES, pageable);

        log.info("Fetched {} profit transactions", transactionPage.getNumberOfElements());
        return transactionPage;
    }


    @Override
    //@Timed(value = "transaction.getTransactionsByUserId.time", description = "Time taken to fetch user transactions")
    public Page<Transaction> getTransactionsByUserId(String userId, Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        log.info("Fetching transactions for userId: {}, page: {}, size: {}", userId, pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Transaction> result = transactionRepository.findByUserId(userId, pageable);

        log.info("Fetched {} transactions for userId: {}", result.getNumberOfElements(), userId);
        return result;
    }

    @Override
    public Boolean hasDepositTransaction(String userId) {
        log.info("Checking if userId: {} has any DEPOSIT transactions", userId);
        boolean exists = transactionRepository.existsByUserIdAndTxnType(userId, TransactionType.DEPOSIT);
        log.info("UserId: {} has DEPOSIT transaction: {}", userId, exists);
        return exists;
    }

    @Override
    public Page<Transaction> getTransactionsByUserIdAndDateRange(String userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        log.info("Fetching transactions for userId: {} between {} and {}", userId, start, end);
        return transactionRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);
    }

    @Override
//    @Timed(value = "transaction.findByTxnRefId.time")
    public Transaction findByTxnRefId(String txnRefId) {
        log.info("Fetching transaction by txnRefId: {}", txnRefId);
        Transaction txn = transactionRepository.findByTxnRefId(txnRefId).orElse(null);
        if (txn != null) {
            log.info("Transaction found for txnRefId: {}", txnRefId);
        } else {
            log.warn("No transaction found for txnRefId: {}", txnRefId);
        }
        return txn;
    }

    @Override
    public Page<Transaction> findTransfersBySenderId(Long senderId, Pageable pageable) {
        log.info("Fetching transfers by senderId: {}", senderId);
        return transactionRepository.findBySenderId(senderId, pageable);
    }

    @Override
    public Page<Transaction> findTransfersByReceiverId(String receiverId, Pageable pageable) {
        log.info("Fetching transfers by receiverId: {}", receiverId);
        return transactionRepository.findByUserId(receiverId, pageable);
    }

    @Override
    public Page<Transaction> findByUserIdAndStatusAndGateway(String userId, Transaction.TransactionStatus status, PaymentGateway gateway, Pageable pageable) {
        log.info("Fetching transactions for userId: {}, status: {}, paymentGateway: {}", userId, status, gateway);
        return transactionRepository.findByUserIdAndStatusAndGateway(userId, status, gateway, pageable);
    }

    @Override
    public Page<Transaction> searchTransactions(String userId, Transaction.TransactionStatus status, TransactionType type,
                                                PaymentGateway gateway,
                                                LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        log.info("Searching transactions with filters - userId: {}, status: {}, type: {}, paymentGateway: {}, from: {}, to: {}",
                userId, status, type, gateway, fromDate, toDate);
        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) predicates.add(cb.equal(root.get("userId"), userId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (type != null) predicates.add(cb.equal(root.get("txnType"), type));
            if (gateway != null) predicates.add(cb.equal(root.get("paymentGateway"), gateway));
            if (fromDate != null) predicates.add(cb.greaterThanOrEqualTo(root.get("txnDate"), fromDate));
            if (toDate != null) predicates.add(cb.lessThanOrEqualTo(root.get("txnDate"), toDate));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Transaction> result = transactionRepository.findAll(spec, pageable);
        log.info("Found {} transactions matching search criteria", result.getNumberOfElements());
        return result;
    }

    @Override
    public Page<Transaction> searchByMetaInfo(String keyword, Pageable pageable) {
        log.info("Searching transactions by metaInfo with keyword: {}", keyword);
        return transactionRepository.findByMetaInfoContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Page<Transaction> searchByKeyword(String userId, String keyword, Pageable pageable) {
        log.info("Searching transactions for userId: {} with keyword: {}", userId, keyword);
        return transactionRepository.searchByUserIdAndKeyword(userId, keyword, pageable);
    }

    @Override
    public BigDecimal getTotalAmountByUserIdAndTxnType(String userId, TransactionType txnType) {
        log.info("Calculating total amount for userId: {}, txnType: {}", userId, txnType);
        BigDecimal total = transactionRepository.sumAmountByUserIdAndTxnType(userId, txnType);
        log.info("Total amount: {} for userId: {}, txnType: {}", total, userId, txnType);
        return total;
    }

    @Override
    public List<Transaction> findTop10ByUserIdOrderByTxnDateDesc(String userId) {
        log.info("Fetching top 10 recent transactions for userId: {}", userId);
        return transactionRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Transaction> findSuspiciousTransactions(BigDecimal threshold) {
        log.info("Fetching suspicious transactions with amount >= {}", threshold);
        List<Transaction> suspicious = transactionRepository.findByAmountGreaterThanEqual(threshold);
        log.info("Found {} suspicious transactions", suspicious.size());
        return suspicious;
    }
}
