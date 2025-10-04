package com.trustai.investment_service.scheduler;

import com.trustai.common.api.IncomeApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.event.IncomeCreditedEvent;
import com.trustai.investment_service.entity.UserInvestment;
import com.trustai.investment_service.enums.InvestmentStatus;
import com.trustai.investment_service.repository.UserInvestmentRepository;
import com.trustai.investment_service.service.impl.InvestmentProfitCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentPayoutProcessorV1 {
    private final UserInvestmentRepository investmentRepo;
    private final WalletApi walletApi;
    private final InvestmentProfitCalculator profitCalculator;
    private final IncomeApi incomeApi;
    private final ApplicationEventPublisher publisher;


    public void processProfit(UserInvestment inv) {
        BigDecimal profit = inv.getPerPeriodProfit();
        if (profit == null || profit.compareTo(BigDecimal.ZERO) <= 0) return;

        // ✅ Credit profit to user wallet
        WalletUpdateRequest creditReq = new WalletUpdateRequest(
                profit,
                TransactionType.INTEREST,
                true,
                "investment-profit",
                "Daily profit for investment #" + inv.getId(),
                null
        );
        walletApi.updateWalletBalance(inv.getUserId(), creditReq);

        // ✅ Update investment record
        inv.setReceivedReturnAmount(inv.getReceivedReturnAmount().add(profit));
        inv.setEarnedPeriods(inv.getEarnedPeriods() + 1);
        inv.setLastPayoutAt(LocalDateTime.now());
        inv.setNextPayoutAt(inv.getNextPayoutAt().plusDays(1)); // since DAILY
        investmentRepo.save(inv);

        publisher.publishEvent(new IncomeCreditedEvent(inv.getUserId(), profit, IncomeType.STAKE, "Daily profit for investment #" + inv.getId()));

        log.info("Credited daily profit of {} for investment {}", profit, inv.getId());
    }

    public void settleMaturedInvestment(UserInvestment inv) {
        if (inv.isCapitalReturned() || inv.getStatus() != InvestmentStatus.ACTIVE) return;

        BigDecimal totalToReturn = inv.getInvestedAmount();
        BigDecimal remainingProfit = inv.getExpectedTotalReturnAmount().subtract(inv.getReceivedReturnAmount());
        if (remainingProfit.compareTo(BigDecimal.ZERO) > 0) {
            totalToReturn = totalToReturn.add(remainingProfit);
        }

        WalletUpdateRequest creditReq = new WalletUpdateRequest(
                totalToReturn,
                TransactionType.INVESTMENT_MATURITY,
                true,
                "investment-maturity",
                "Maturity payout for investment #" + inv.getId(),
                null
        );
        walletApi.updateWalletBalance(inv.getUserId(), creditReq);

        inv.setCapitalReturned(true);
        inv.setCapitalAmountReturned(inv.getInvestedAmount());
        inv.setFinalReturnAmount(totalToReturn);
        inv.setStatus(InvestmentStatus.COMPLETED);
        inv.setLastPayoutAt(LocalDateTime.now());
        investmentRepo.save(inv);

        log.info("Investment #{} matured and credited total {}", inv.getId(), totalToReturn);
    }
}
