package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.enums.CurrencyType;
import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.dto.response.DepositHistoryItem;
import com.trustai.transaction_service.dto.request.DepositRequest;
import com.trustai.transaction_service.dto.request.ManualDepositRequest;
import com.trustai.transaction_service.entity.PendingDeposit;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.TransactionException;
import com.trustai.transaction_service.mapper.TransactionMapper;
import com.trustai.transaction_service.repository.PendingDepositRepository;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.DepositService;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.util.TransactionIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DepositServiceImpl implements DepositService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final UserApi userApi;
    private final PendingDepositRepository pendingDepositRepository;
    private final TransactionMapper mapper;
    private final List<TransactionType> DEPOSIT_TRANSACTIONS = List.of(TransactionType.DEPOSIT, TransactionType.DEPOSIT_MANUAL);

    // Only by ADMIN
    @Override
    @Transactional
    public PendingDeposit depositManual(ManualDepositRequest request, String createdBy) {
        log.info("Processing manual deposit for userId: {}, amount: {}", request.getUserId(), request.getAmount());
        validateManualDepositRequest(request);

        PaymentGateway paymentGateway = PaymentGateway.SYSTEM;
        BigDecimal fee = calculateTxnFee(paymentGateway, request.getAmount());
        BigDecimal netAmount = request.getAmount().subtract(fee);
        log.debug("Calculated fee: {}, netAmount: {}", fee, netAmount);

        String txnRefId = TransactionIdGenerator.generateTransactionId(); // As txnRefId empty for manual

        PendingDeposit deposit = buildPendingDeposit(
                request.getUserId(),
                request.getAmount(),
                txnRefId,
                BigDecimal.ZERO,
                PaymentGateway.SYSTEM,
                request.getRemarks(),
                request.getMetaInfo(),
                CurrencyType.INR.name(), // or make this configurable
                null, // linkedAccountId is required to identify from which account the txn happened (eg: upiID)
                PendingDeposit.DepositStatus.APPROVED, // AS Deposited by ADMIN directly
                createdBy // IMPORTANT for AUDIT
        );
        pendingDepositRepository.save(deposit);
        log.info("Manual PendingDeposit created with ID: {} and status: {}", deposit.getId(), deposit.getStatus());

        Transaction transaction = createAndSaveTransaction(
                request.getUserId(),
                request.getAmount(),
                netAmount,
                paymentGateway,
                TransactionType.DEPOSIT_MANUAL,
                Transaction.TransactionStatus.SUCCESS, // AS Deposited by ADMIN directly
                txnRefId,
                fee,
                null, // no need of linkedTxnId as the txn is direct vis PaymentGateway, we can track via txnRefId and the gateway
                "Deposit via " + paymentGateway.name(),
                request.getMetaInfo(),
                null // no sender for user-initiated
        );
        log.info("Deposit transaction created successfully with ID: {}", transaction.getId());

        return deposit;
    }

    @Override
    @Transactional
    public PendingDeposit deposit(@NonNull DepositRequest request) {
        log.info("Processing deposit for userId: {}, amount: {}", request.getUserId(), request.getAmount());
        validateDepositRequest(request);

        PaymentGateway paymentGateway = PaymentGateway.valueOf(request.getPaymentGateway());
        BigDecimal fee = calculateTxnFee(paymentGateway, request.getAmount());
        BigDecimal netAmount = request.getAmount().subtract(fee);
        log.debug("Calculated fee: {}, netAmount: {}", fee, netAmount);


        PendingDeposit deposit = buildPendingDeposit(
                request.getUserId(),
                request.getAmount(),
                request.getTxnRefId(),
                fee,
                paymentGateway,
                request.getRemarks(),
                request.getMetaInfo(),
                CurrencyType.INR.name(), // or make this configurable
                null, // no linkedTxnId
                PendingDeposit.DepositStatus.PENDING, // AS Deposited by PaymentGateway, and need to verify the payment
                String.valueOf(request.getUserId())
        );
        pendingDepositRepository.save(deposit);
        log.info("PendingDeposit created successfully with ID: {} and status: {}", deposit.getId(), deposit.getStatus());



        return deposit;
    }

    @Override
    @Transactional
    public PendingDeposit approvePendingDeposit(Long depositId, String adminUser) {
        PendingDeposit deposit = pendingDepositRepository.findById(depositId)
                .orElseThrow(() -> new TransactionException("PendingDeposit not found"));

        if (deposit.getStatus() != PendingDeposit.DepositStatus.PENDING) {
            throw new TransactionException("Only pending deposits can be approved.");
        }

        BigDecimal netAmount = deposit.getAmount().subtract(deposit.getTxnFee());

        Transaction transaction = createAndSaveTransaction(
                deposit.getUserId(),
                deposit.getAmount(),
                netAmount,
                deposit.getGateway(),
                TransactionType.DEPOSIT,
                Transaction.TransactionStatus.SUCCESS,
                deposit.getTxnRefId(),
                deposit.getTxnFee(),
                deposit.getLinkedTxnId(),
                "Manual deposit approved",
                deposit.getMetaInfo(),
                null
        );

        deposit.setStatus(PendingDeposit.DepositStatus.APPROVED);
        deposit.setApprovedBy(adminUser);
        deposit.setApprovedAt(LocalDateTime.now());
        deposit.setLinkedTxnId(transaction.getId().toString()); // link to created txn

        return pendingDepositRepository.save(deposit);
    }

    @Override
    @Transactional
    public PendingDeposit rejectPendingDeposit(Long depositId, String adminUser, String reason) {
        PendingDeposit deposit = pendingDepositRepository.findById(depositId)
                .orElseThrow(() -> new TransactionException("PendingDeposit not found")); // IllegalArgumentException

        if (deposit.getStatus() != PendingDeposit.DepositStatus.PENDING) {
            throw new TransactionException("Only pending deposits can be rejected."); // IllegalStateException
        }

        deposit.setStatus(PendingDeposit.DepositStatus.REJECTED);
        deposit.setRejectedBy(adminUser);
        deposit.setRejectedAt(LocalDateTime.now());
        deposit.setRejectionReason(reason);

        return pendingDepositRepository.save(deposit);
    }

    @Override
    public BigDecimal getTotalDeposit(long userId) {
        BigDecimal total = transactionRepository.sumAmountByUserIdAndTxnTypeAndStatusIn(
                userId,
                List.of(TransactionType.DEPOSIT, TransactionType.DEPOSIT_MANUAL),
                List.of(Transaction.TransactionStatus.SUCCESS)
        );
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public boolean isDepositExistsByTxnRef(String txnRefId) {
        return transactionRepository.findByTxnRefId(txnRefId).isPresent();
    }

    @Override
    public Page<DepositHistoryItem> getDepositHistory(Long userId, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Transaction> transactions = transactionRepository.findByUserIdAndTxnType(userId, TransactionType.DEPOSIT, pageable);
        return transactions.map(mapper::mapToDepositHistory);
    }

    @Override
    public Page<DepositHistoryItem> getDepositHistory(PendingDeposit.DepositStatus status, Pageable pageable) {
        log.debug("Fetching deposit history with status: {}, page request: {}", status, pageable);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));

        if (status == PendingDeposit.DepositStatus.PENDING || status == PendingDeposit.DepositStatus.REJECTED) {
            Page<PendingDeposit> pendingDeposits = pendingDepositRepository.findByStatus(status, pageable);
            return pendingDeposits.map(mapper::mapToDepositHistory);
        }
        Page<Transaction> transactions = transactionRepository.findByTxnTypeIn(DEPOSIT_TRANSACTIONS, pageable);
        return transactions.map(mapper::mapToDepositHistory);
        /*else {
            Page<Transaction> transactions = transactionRepository.findByTxnTypeAndStatus(TransactionType.DEPOSIT, status, pageable);
            return transactions.map(mapper::mapToDepositHistory);
        }*/
    }

    @Override
    public Transaction confirmGatewayDeposit(String txnRefId, String gatewayResponseJson) {
        Optional<Transaction> optional = transactionRepository.findByTxnRefId(txnRefId);
        if (optional.isPresent()) {
            Transaction txn = optional.get();
            txn.setStatus(Transaction.TransactionStatus.SUCCESS);
            txn.setMetaInfo(gatewayResponseJson);
            return transactionRepository.save(txn);
        }
        throw new TransactionException("Transaction not found with reference: " + txnRefId); // IllegalArgumentException
    }


    private void validateDepositRequest(DepositRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new TransactionException("Invalid user ID"); // IllegalArgumentException
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Deposit amount must be greater than zero"); // IllegalArgumentException
        }
        /*if (request.getPaymentGateway() == null) {
            throw new IllegalArgumentException("Payment paymentGateway must be provided");
        }
        if (request.getTxnRefId() == null || request.getTxnRefId().isBlank()) {
            throw new IllegalArgumentException("Transaction reference ID is required");
        }*/
    }
    private void validateManualDepositRequest(ManualDepositRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new TransactionException("Invalid user ID"); // IllegalArgumentException
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Deposit amount must be greater than zero"); // IllegalArgumentException
        }
    }

    private BigDecimal calculateTxnFee(PaymentGateway paymentGateway, BigDecimal txnAmount) {
        return BigDecimal.ZERO;
    }

    private Transaction createAndSaveTransaction(
            Long userId,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            PaymentGateway gateway,
            TransactionType txnType,
            Transaction.TransactionStatus status,
            String txnRefId,
            BigDecimal txnFee,
            String linkedTxnId,
            String remarks,
            String metaInfo,
            Long senderId
    ) {
        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal newBalance = currentBalance.add(netAmount);

        Transaction txn = new Transaction(userId, grossAmount, txnType, newBalance, true);

        txn.setTxnFee(txnFee);
        txn.setLinkedTxnId(linkedTxnId);
        txn.setGateway(gateway);
        txn.setStatus(status);
        txn.setRemarks(remarks);
        txn.setMetaInfo(metaInfo);
        txn.setSenderId(senderId);

        if (txnRefId == null) {
            txnRefId = TransactionIdGenerator.generateTransactionId();
            txn.setTxnRefId(txnRefId);
        }

        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, netAmount);

        return txn;
    }

    private PendingDeposit buildPendingDeposit(
            long userId,
            BigDecimal amount,
            String txnRefId,
            BigDecimal txnFee,
            PaymentGateway gateway,
            String remarks,
            String metaInfo,
            String currencyCode,
            String linkedTxnId,
            PendingDeposit.DepositStatus status,
            String createdBy
    ) {
        if (txnRefId == null) txnRefId = TransactionIdGenerator.generateTransactionId();
        return new PendingDeposit(userId, amount)
                .setTxnRefId(txnRefId)
                .setTxnFee(txnFee)
                .setGateway(gateway)
                .setRemarks(remarks)
                .setMetaInfo(metaInfo)
                .setCurrencyCode(currencyCode)
                .setLinkedTxnId(linkedTxnId)
                .setStatus(status)
                .setCreatedBy(createdBy);
    }

}
