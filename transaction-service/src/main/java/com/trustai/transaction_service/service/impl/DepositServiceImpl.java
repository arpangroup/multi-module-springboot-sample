package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.FileUploadApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.CurrencyType;
import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.event.FirstDepositEvent;
import com.trustai.common.event.NotificationEvent;
import com.trustai.common.event.UserRegisteredEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final FileUploadApi fileUploadApi;
    private final List<TransactionType> DEPOSIT_TRANSACTIONS = List.of(TransactionType.DEPOSIT, TransactionType.DEPOSIT_MANUAL);
    private final ApplicationEventPublisher publisher;

    // Manual Deposit should be in PENDING state until its approved
    @Override
    @Transactional
    //public PendingDeposit depositManual(long userId, ManualDepositRequest request, String createdBy) {
    public PendingDeposit depositManual(long userId, BigDecimal amount, String gateway, String txnId, MultipartFile screenshot) {
        log.info("Starting manual deposit for userId={}, amount={}, txnId={}", userId, amount, txnId);

        validateManualDepositInput(amount, txnId);

        if (pendingDepositRepository.existsByLinkedTxnIdAndStatus(txnId, PendingDeposit.DepositStatus.PENDING)) {
            log.info("Duplicate transaction ID detected: [{}] already exists with PENDING status.", txnId);
            throw new TransactionException("Transaction ID  is already linked to a pending deposit.");
        }

        String imageUrl = fileUploadApi.uploadFile(screenshot);
        PaymentGateway paymentGateway = PaymentGateway.BINANCE; // or SYSTEM
        BigDecimal fee = calculateTxnFee(paymentGateway, amount);
        BigDecimal netAmount = amount.subtract(fee);
        log.debug("Calculated fee: {}, netAmount: {}", fee, netAmount);

        String txnRefId = TransactionIdGenerator.generateTransactionId(); // As txnRefId empty for manual

        PendingDeposit deposit = buildPendingDeposit(
                userId,
                amount,
                imageUrl,
                txnRefId,
                fee,
                paymentGateway,
                "Manual deposit Request for Binance Payment",
                null,
                CurrencyType.INR.name(), // or make this configurable
                txnId, // linkedAccountId is required to identify from which account the txn happened (eg: upiID)
                PendingDeposit.DepositStatus.PENDING, // AS Deposited by ADMIN directly
                String.valueOf(userId) // IMPORTANT for AUDIT
        );
        pendingDepositRepository.save(deposit);
        log.info("Manual PendingDeposit created with ID: {} and status: {}", deposit.getId(), deposit.getStatus());

        return deposit;
    }

    @Override
    @Transactional
    public PendingDeposit deposit(long userId, @NonNull DepositRequest request) {
        log.info("Processing deposit for userId: {}, amount: {}", userId, request.getAmount());
        validateDepositRequest(request);

        PaymentGateway paymentGateway = PaymentGateway.valueOf(request.getPaymentGateway());
        BigDecimal fee = calculateTxnFee(paymentGateway, request.getAmount());
        BigDecimal netAmount = request.getAmount().subtract(fee);
        log.debug("Calculated fee: {}, netAmount: {}", fee, netAmount);


        PendingDeposit deposit = buildPendingDeposit(
                userId,
                request.getAmount(),
                null,
                request.getTxnRefId(),
                fee,
                paymentGateway,
                request.getRemarks(),
                request.getMetaInfo(),
                CurrencyType.INR.name(), // or make this configurable
                null, // no linkedTxnId
                PendingDeposit.DepositStatus.APPROVED, // AS Deposited by PaymentGateway, and need to verify the payment
                String.valueOf(userId)
        );
        pendingDepositRepository.save(deposit);
        log.info("PendingDeposit created successfully with ID: {} and status: {}", deposit.getId(), deposit.getStatus());


        approvePendingDeposit(deposit.getId(), PaymentGateway.SYSTEM.name());
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


        boolean isFirstDeposit = !transactionRepository.existsByUserIdAndTxnType(
                String.valueOf(deposit.getUserId()),
                TransactionType.DEPOSIT
        );

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

        publishDepositApproveEvents(deposit.getUserId(), true, transaction);
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
    public BigDecimal getTotalDeposit(String userId) {
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
    public Page<DepositHistoryItem> getDepositHistory(String userId, Pageable pageable) {
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
            String imageUrl,
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
        return new PendingDeposit(userId, amount, linkedTxnId)
                .setImageUrl(imageUrl)
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

    private void validateManualDepositInput(BigDecimal amount, String txnId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: amount is null or not greater than zero.");
            throw new TransactionException("Deposit amount must be greater than zero.");
        }

        if (txnId == null || txnId.isEmpty()) {
            log.warn("Validation failed: linkedTxnId is null or empty.");
            throw new TransactionException("Transaction ID must not be null or empty.");
        }

        if (txnId.length() < 5) {
            log.warn("Validation failed: linkedTxnId [{}] is shorter than 5 characters.", txnId);
            throw new TransactionException("Transaction ID must be at least 5 characters long.");
        }
    }

    @Async
    private void publishDepositApproveEvents(Long userId, boolean isFirstDeposit, Transaction transaction) {
        log.info("📥 Starting deposit approval event flow | userId={}, isFirstDeposit={}", userId, isFirstDeposit);

        UserInfo userInfo = userApi.getUserById(userId);
        String email = userInfo.getEmail();
        BigDecimal amount = transaction.getAmount();

        try {
            if (isFirstDeposit) {
                // 1. publish FirstDepositEvent: to apply the pending ReferralBonus and make the user ACTIVE
                log.info("🚀 Publishing FirstDepositEvent | userId={}, amount={}", userId, amount);
                publisher.publishEvent(new FirstDepositEvent(userId, amount));
            }

            // 2. Notification content
            String title = "Deposit Success";
            String message = "Thanks for registering TrustAI";


            // 3. Publish In-App Notification
            log.info("📢 Publishing InApp Notification | userId={}, title='{}'", userId, title);
            NotificationRequest inAppRequest = NotificationRequest.forInApp(
                    String.valueOf(userId),
                    title,
                    message
            );
            publisher.publishEvent(new NotificationEvent(this, inAppRequest));


            // 4. Publish Email Notification
            log.info("📧 Publishing Email Notification | email={}, subject='{}'", email, title);
            NotificationRequest emailRequest = NotificationRequest.forEmail(
                    email,
                    title,
                    message
            );
            publisher.publishEvent(new NotificationEvent(this, emailRequest));

            log.info("✅ Deposit approval events completed successfully | userId={}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to publish deposit success events for userId={}", userId, e);
        }
    }

}
