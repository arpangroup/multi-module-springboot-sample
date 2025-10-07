package com.trustai.investment_service.service.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.TransactionDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.CalculationType;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.event.IncomeCreditedEvent;
import com.trustai.common.exceptions.ErrorCode;
import com.trustai.common.exceptions.ValidationException;
import com.trustai.common.utils.DateUtils;
import com.trustai.investment_service.dto.InvestmentResponse;
import com.trustai.investment_service.dto.UserInvestmentSummary;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.UserInvestment;
import com.trustai.investment_service.enums.InvestmentStatus;
import com.trustai.investment_service.exception.AccessDeniedException;
import com.trustai.investment_service.exception.InvalidRequestException;
import com.trustai.investment_service.exception.ResourceNotFoundException;
import com.trustai.common.api.WalletApi;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.repository.UserInvestmentRepository;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.service.InvestmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentServiceImpl implements InvestmentService {
    private final SchemaRepository schemaRepo;
    private final UserInvestmentRepository userInvestmentRepo;
    private final InvestmentValidator validator;
    private final UserApi userClient;
    private final WalletApi walletApi;
    private final InvestmentNotificationPublisher notificationPublisher;
    private final InvestmentProfitCalculator profitCalculator;
    private final InvestmentPeriodHelper periodHelper;
    private final ApplicationEventPublisher publisher;


    @Override
    @Transactional
    public InvestmentResponse subscribeToInvestment(Long userId, Long schemaId, BigDecimal investmentAmount) {
        InvestmentSchema schema = schemaRepo.findById(schemaId).orElseThrow(() -> new ResourceNotFoundException("Invalid schemaId"));
        UserInfo user = userClient.getUserById(userId);

        // Step 1. Validate rules
        validator.validateEligibility(user, schema, investmentAmount);

        // Step 2. Calculate deduct amount
        BigDecimal totalDeduct = investmentAmount.add(schema.getHandlingFee());

        // Deduct wallet balance
        String remarks = "Invested in Stake: for scheme: " + schema.getName() +
                " and amount: " + totalDeduct +
                " at " + DateUtils.formatDisplayDate(LocalDateTime.now());
        TransactionDto walletTxn = updateWalletBalance(
                userId,
                investmentAmount,
                false,
                remarks
        );
        log.info("Investment deducted successfully - txnId: {}, userId: {}, amount: {}", walletTxn.getId(), userId, investmentAmount);


        // Step 3. Construct a new UserInvestment entity to record the investment
        // Prepare a temporary UserInvestment object for calculation
        UserInvestment tempInvestment = UserInvestment.builder()
                .userId(userId)
                .schema(schema)
                .investedAmount(investmentAmount)
                .subscribedAt(LocalDateTime.now())
                .build();

        // Calculate all derived fields
        BigDecimal receivedReturn = BigDecimal.ZERO;
        BigDecimal perPeriodProfit = profitCalculator.calculateProfit(tempInvestment);
        BigDecimal expectedReturn = profitCalculator.calculateTotalExpectedReturn(tempInvestment); // returnCalculator.calculate(schema, request.getAmount());
        LocalDateTime nextPayout = periodHelper.calculateNextPayoutDate(tempInvestment);
        LocalDateTime maturity = periodHelper.calculateMaturityDate(tempInvestment);

        // Create and persist final investment
        UserInvestment finalInvestment = UserInvestment.builder()
                .userId(userId)
                .schema(schema)
                .investedAmount(investmentAmount)
                .perPeriodProfit(perPeriodProfit)
                .expectedTotalReturnAmount(expectedReturn)
                .receivedReturnAmount(receivedReturn)
                .profitCalculationType(CalculationType.valueOf(schema.getInterestCalculationMethod().name()))
                .capitalReturned(false)
                .subscribedAt(tempInvestment.getSubscribedAt())
                .nextPayoutAt(nextPayout)
                .maturityAt(maturity)
                .status(InvestmentStatus.ACTIVE)
                .build();

        // Step 6: Save the investment
        userInvestmentRepo.save(finalInvestment);
        log.info("Investment created successfully - investmentId: {}, userId: {}", finalInvestment.getId(), userId);


        notificationPublisher.sendInvestmentSuccessNotification(user, finalInvestment);
        return new InvestmentResponse(finalInvestment.getId(), expectedReturn, maturity);
    }



    @Override
    public Page<UserInvestmentSummary> getAllInvestments(InvestmentStatus status, Pageable pageable) {
        Page<UserInvestment> investments = (status != null)
                ? userInvestmentRepo.findByStatus(status, pageable)
                : userInvestmentRepo.findAll(pageable);

        return investments.map(this::mapToSummary);
    }

    @Override
    public Page<UserInvestmentSummary> getUserInvestments(Long userId, InvestmentStatus status, Pageable pageable) {
        Page<UserInvestment> investments;

        if (status != null) {
            if (status == InvestmentStatus.ACTIVE) {
                investments = userInvestmentRepo.findByUserIdAndStatusIn(userId, List.of(InvestmentStatus.ACTIVE, InvestmentStatus.COMPLETED), pageable);
            } else {
                investments = userInvestmentRepo.findByUserIdAndStatus(userId, status, pageable);
            }
        } else {
            investments = userInvestmentRepo.findByUserId(userId, pageable);
        }

        return investments.map(this::mapToSummary);
    }

    @Override
    public List<UserInvestmentSummary> exportUserInvestments(Long userId) {
        List<UserInvestment> all = userInvestmentRepo.findByUserId(userId);
        return all.stream().map(this::mapToSummary).toList();
    }

    @Override
    public UserInvestmentSummary getInvestmentDetails(Long investmentId) {
        UserInvestment investment = userInvestmentRepo.findById(investmentId).orElseThrow(() -> new ResourceNotFoundException("Invalid schemaId"));
        return mapToSummary(investment);
    }

    @Override
    @Transactional
    public UserInvestmentSummary cancelInvestment(Long userId, Long investmentId) {
        UserInvestment investment = userInvestmentRepo.findById(investmentId).orElseThrow(() -> new ResourceNotFoundException("Investment not found"));

        if (!investment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not allowed to cancel this investment");
        }

        if (investment.isCancelled() || investment.getStatus() != InvestmentStatus.ACTIVE) {
            throw new InvalidRequestException("Investment is already cancelled or completed");
        }

        InvestmentSchema schema = investment.getSchema();
        if (!schema.isCancellable()) {
            throw new InvalidRequestException("This investment schema does not allow cancellation");
        }

        // Grace period check
        long elapsedMinutes = Duration.between(investment.getSubscribedAt(), LocalDateTime.now()).toMinutes();
        if (elapsedMinutes < schema.getCancellationGracePeriodMinutes()) {
            throw new InvalidRequestException("You can't cancel this investment yet. Try after grace period.");
        }

        BigDecimal penalty = schema.getEarlyExitPenalty() != null ? schema.getEarlyExitPenalty() : BigDecimal.ZERO;
        BigDecimal refundAmount = investment.getInvestedAmount().subtract(penalty);
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0) refundAmount = BigDecimal.ZERO;

        // Refund via Wallet
        WalletUpdateRequest refundCreditRequest = new WalletUpdateRequest(
                refundAmount,
                TransactionType.REFUND,
                true,
                "investment-cancel",
                "Investment cancelled: " + schema.getName(),
                null
        );
        TransactionDto refundTxn = walletApi.updateWalletBalance(investment.getUserId(), refundCreditRequest);

        // Mark investment cancelled
        investment.setCancelled(true);
        investment.setStatus(InvestmentStatus.CANCELLED);
        investment.setCancelledAt(LocalDateTime.now());
        investment.setCapitalReturned(true); // Assume capital is returned as refund
        investment.setCapitalAmountReturned(refundAmount);
        investment.setFinalReturnAmount(refundAmount);
        investment.setTxnRefId(refundTxn.getTxnRefId());
        investment.setCancelReason("Cancelled by user");
        userInvestmentRepo.save(investment);

        return mapToSummary(investment); // You already have this mapper in place
    }

    @Override
    @Transactional
    public UserInvestmentSummary redeemStake(Long investmentId) {
        log.info("🔁 Redeeming stake for investment ID: {}", investmentId);
        // 1️⃣ Find the investment
        UserInvestment investment = userInvestmentRepo.findById(investmentId).orElseThrow(() -> new IllegalArgumentException("Investment not found"));
        InvestmentSchema schema = investment.getSchema();

        // 2️⃣ Check already redeemed/cancelled
        if (InvestmentStatus.COMPLETED == investment.getStatus() || InvestmentStatus.CANCELLED == investment.getStatus()) {
            throw new IllegalStateException("Investment already redeemed");
        }
        if (investment.isCancelled()) {
            throw new IllegalStateException("Cancelled investment cannot be redeemed");
        }

        // 3️⃣ Check maturity
        if (investment.getMaturityAt() == null) {
            throw new IllegalStateException("Maturity date not set for this investment");
        }
        if (LocalDateTime.now().isBefore(investment.getMaturityAt())) {
            throw new IllegalStateException("Investment not matured yet — redeem allowed only after " +
                    investment.getMaturityAt());
        }


        // 4️⃣ Calculate total profit (e.g., based on ROI type and duration)
        BigDecimal totalProfit = calculateTotalProfit(investment);
        BigDecimal investmentAmount = investment.getInvestedAmount();
        BigDecimal totalRedeemAmount = investmentAmount.add(totalProfit); //redeem amount = principal + profit
        log.info("💰 Calculated redemption: Invested={}, Profit={}, Total={}", investmentAmount, totalProfit, totalRedeemAmount);


        // 5️⃣ credit profit to user wallet
        WalletUpdateRequest profitCreditReq = new WalletUpdateRequest(
                totalProfit,
                TransactionType.INTEREST,
                true,
                "investment-profit",
                "Total profit for investment " + schema.getName(),
                null
        );
        walletApi.updateWalletBalance(investment.getUserId(), profitCreditReq);
        log.debug("👛 Wallet credited with stake profit: UserID={}, Amount={}", investment.getUserId(), totalProfit);

        // ✅ Record the profit to income History
        publisher.publishEvent(new IncomeCreditedEvent(investment.getUserId(), totalProfit, IncomeType.STAKE, "Profit for Stake " + schema.getName()));


        // ✅ Credit Capital Amount to user wallet
        WalletUpdateRequest creditReq = new WalletUpdateRequest(
                investmentAmount,
                TransactionType.INVESTMENT_MATURITY,
                true,
                "investment-maturity",
                "Maturity payout for investment #" + investment.getId(),
                null
        );
        walletApi.updateWalletBalance(investment.getUserId(), creditReq);
        log.debug("👛 Wallet credited with stake capital: UserID={}, Amount={}", investment.getUserId(), investmentAmount);

        // 6️⃣ Mark investment as redeemed
        investment.setFinalReturnAmount(totalRedeemAmount);
        investment.setReceivedReturnAmount(totalRedeemAmount);
        //investment.setEarnedPeriods(investment.getEarnedPeriods() + 1);
        investment.setStatus(InvestmentStatus.COMPLETED);
        investment.setCapitalReturned(true);
        investment.setCapitalAmountReturned(investment.getInvestedAmount());
        investment.setLastPayoutAt(LocalDateTime.now());
        investment = userInvestmentRepo.save(investment);
        log.info("✅ Investment marked as COMPLETED and saved: ID={}", investment.getId());

        // 7️⃣ Send Notification
        log.info("Sending Investment Redeemed notification....");
        UserInfo user = userClient.getUserById(investment.getUserId());
        notificationPublisher.sendInvestmentMatureNotification(user, investment);

        return mapToSummary(investment);
    }

    private BigDecimal calculateTotalProfit(UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        BigDecimal invested = investment.getInvestedAmount();
        log.debug("Calculating profit for Investment ID: {}, Invested Amount: {}, Return Rate: {}, Subscribed At: {}, Maturity At: {}",
                investment.getId(), invested, schema.getReturnRate(), investment.getSubscribedAt(), investment.getMaturityAt());


        /*if (schema.getRoiType() == RoiType.FIXED) {
            // e.g. returnRate = 12 → 12% total
            return invested.multiply(schema.getReturnRate()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        } else {
            // Daily ROI
            long daysHeld = ...
            if (daysHeld < 0) daysHeld = 0;
        }*/

        long daysHeld = ChronoUnit.DAYS.between(
                investment.getSubscribedAt().toLocalDate(),
                investment.getMaturityAt().toLocalDate()
        );

        // If a user redeems early, the profit should only be for the days held:
        /*long daysHeld = ChronoUnit.DAYS.between(
                investment.getSubscribedAt().toLocalDate(),
                (LocalDateTime.now().isBefore(investment.getMaturityAt()) ? LocalDateTime.now() : investment.getMaturityAt()).toLocalDate()
        );*/
        if (daysHeld < 0) daysHeld = 0;

        BigDecimal dailyRate = schema.getReturnRate().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal profitPerDay = invested.multiply(dailyRate);
        BigDecimal totalProfit = profitPerDay.multiply(BigDecimal.valueOf(daysHeld));
        log.debug("Days Held: {}, Daily Rate: {}, Profit Per Day: {}, Total Profit: {}",
                daysHeld, dailyRate, profitPerDay, totalProfit);

        return totalProfit;
    }


    private UserInvestmentSummary mapToSummary(UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();

        BigDecimal perPeriodProfit = profitCalculator.calculateProfit(investment);
        int completedPeriods = periodHelper.calculateCompletedPeriods(investment);
        int remainingPeriods = periodHelper.calculateRemainingPeriods(investment);
        //LocalDateTime nextPayout = periodHelper.calculateNextPayoutDate(investment);
        //LocalDateTime maturity = periodHelper.calculateMaturityDate(investment);
        LocalDateTime nextPayout = investment.getNextPayoutAt();
        LocalDateTime maturity = investment.getMaturityAt();

        BigDecimal expectedReturn = profitCalculator.calculateTotalExpectedReturn(investment);
        BigDecimal totalEarningPotential = schema.isCapitalReturned()
                ? expectedReturn.add(investment.getInvestedAmount())
                : expectedReturn;

        BigDecimal profit = schema.isCapitalReturned()
                ? investment.getReceivedReturnAmount()
                : investment.getReceivedReturnAmount().subtract(investment.getInvestedAmount());

        return UserInvestmentSummary.builder()
                .investmentId(investment.getId())
                .schemaName(schema.getName())
                .amountRange(formatAmountRange(schema.getMinimumInvestmentAmount(), schema.getMaximumInvestmentAmount()))
                .imageUrl(schema.getImageUrl())
                .investedAmount(investment.getInvestedAmount())
                .roiType(schema.getInterestCalculationMethod().name())
                .roiValue(schema.getReturnRate())
                .perPeriodProfit(perPeriodProfit)
                .capitalBack(schema.isCapitalReturned())
                .capitalReturned(investment.isCapitalReturned())
                .currencyCode(schema.getCurrency().name())
                .totalPeriods(schema.getTotalReturnPeriods())
                .completedPeriods(completedPeriods)
                .remainingPeriods(remainingPeriods)
                .expectedReturn(expectedReturn)
                .receivedReturn(investment.getReceivedReturnAmount())
                .profit(profit)
                .totalEarningPotential(totalEarningPotential)
                .earlyExitPenalty(schema.getEarlyExitPenalty())
                .nextReturnAmount(perPeriodProfit)
                .subscribedAt(investment.getSubscribedAt())
                .nextPayoutDate(nextPayout)
                .maturityAt(maturity)
                .payoutFrequencyLabel(getPayoutLabel(schema))
                .investmentStatus(investment.getStatus().name())
                .canCancelNow(periodHelper.isCancellableNow(investment))
                .isWithdrawableNow(isWithdrawable(investment))
                .daysRemaining((int) Duration.between(LocalDateTime.now(), maturity).toDays())
                .createdBy(investment.getCreatedBy())
                .build();
    }

    private String formatAmountRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null) return "N/A";
        //return "\u20B9" + min.stripTrailingZeros().toPlainString() + " – \u20B9" + max.stripTrailingZeros().toPlainString();
        return "₹" + min.stripTrailingZeros().toPlainString() + " – ₹" + max.stripTrailingZeros().toPlainString();
    }

    private String getPayoutLabel(InvestmentSchema schema) {
        return switch (schema.getPayoutMode()) {
            case DAILY -> "Daily";
            case WEEKLY -> "Weekly";
            case MONTHLY -> "Monthly";
            case CUSTOM -> "Custom";
        };
    }

    private boolean isWithdrawable(UserInvestment investment) {
        return LocalDateTime.now().isAfter(investment.getNextPayoutAt());
    }



    private TransactionDto updateWalletBalance(Long userId, BigDecimal amount, boolean isCredit, String remarks) {
        String operation = isCredit ? "Crediting" : "Debiting";
        log.info("{} wallet balance - userId: {}, amount: {}", operation, userId, amount);

        WalletUpdateRequest walletUpdateRequest = new WalletUpdateRequest(
                amount,
                TransactionType.INVESTMENT,
                isCredit,
                "investment",
                remarks,
                null
        );

        TransactionDto txn = walletApi.updateWalletBalance(userId, walletUpdateRequest);
        if (txn == null || txn.getId() == null) {
            String failOp = isCredit ? "credit" : "debit";
            log.error("Wallet deduction failed - userId: {}, amount: {}", userId, amount);
            throw new ValidationException("Wallet " + failOp + " failed", ErrorCode.WALLET_DEDUCTION_FAILED);
        }
        String successOp = isCredit ? "credit" : "debit";
        log.info("Wallet {} successful - txnId: {}, userId: {}, amount: {}", successOp, txn.getId(), userId, amount);
        return txn;
    }
}
