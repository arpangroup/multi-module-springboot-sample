package com.trustai.aggregator.service;

import com.trustai.aggregator.constants.Color;
import com.trustai.aggregator.constants.DashboardCard;
import com.trustai.aggregator.constants.IconConstants;
import com.trustai.aggregator.dto.CardData;
import com.trustai.aggregator.dto.DashboardResponse;
import com.trustai.aggregator.dto.MonthlyStat;
import com.trustai.aggregator.repository.ChartRepository;
import com.trustai.aggregator.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.trustai.aggregator.constants.CardTitles.*;
import static com.trustai.aggregator.constants.IconConstants.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepo;
    private final ChartRepository chartRepo;

    public DashboardResponse getDashboard() {
        // 1. Cards
        List<CardData> stats = getStats();

        // 2. Chart stats
        List<MonthlyStat> monthlyStats = statSummary(null, null);


        return new DashboardResponse(stats, monthlyStats);
    }

    public List<MonthlyStat> statSummary(LocalDateTime startDate, LocalDateTime endDate) {
        //LocalDateTime start = LocalDateTime.now().minusMonths(12).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        //LocalDateTime end = LocalDateTime.now();

        // Default to last 12 months if no dates provided
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(12)
                    .withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }


        List<MonthlyStat> monthlyStats = chartRepo.getMonthlyStats(startDate, endDate).stream()
                .map(r -> new MonthlyStat(
                        (String) r[0],
                        (BigDecimal) r[1],
                        (BigDecimal) r[2],
                        (BigDecimal) r[3],
                        (BigDecimal) r[4]
                ))
                .toList();

        return monthlyStats;
    }

    public List<CardData> getStats() {
        final long registeredUsers          = dashboardRepo.countRegisteredUsers();
        final long activeUsers              = dashboardRepo.countActiveUsers();
        final long staffCount               = dashboardRepo.countSiteStaff();
        final BigDecimal totalDeposits      = dashboardRepo.getTotalDeposits();
        final BigDecimal totalWithdraw      = dashboardRepo.getTotalWithdrawals();
        final long totalReferrals           = dashboardRepo.getTotalReferrals();
        final BigDecimal totalSend          = dashboardRepo.getTotalSend();
        final BigDecimal totalInvest        = dashboardRepo.getTotalInvestments();
        final BigDecimal depositBonus       = dashboardRepo.getDepositBonus();
        final BigDecimal investBonus        = dashboardRepo.getInvestmentBonus();
        final int totalTickets              = 0;


        /*List<CardData> stats = List.of(
                new CardData(REGISTERED_USER,     registeredUsers,   USER_ICON,              Color.RED.getHex(),         "/users"),
                new CardData(ACTIVE_USERS,        activeUser,        ACTIVE_USER_ICON,       Color.PINK.getHex(),        "/active-users"),
                new CardData(SITE_STAFF,          staffCount,        STAFF_ICON,             Color.PURPLE.getHex(),      "/staff"),
                new CardData(TOTAL_DEPOSITS,      totalDeposits,     DEPOSIT_ICON,           Color.DEEP_PURPLE.getHex(), "/deposits"),
                new CardData(TOTAL_WITHDRAW,      totalWithdrawals,  WITHDRAW_ICON,          Color.INDIGO.getHex(),      "/withdrawals"),
                new CardData(TOTAL_REFERRAL,      totalReferrals,    REFERRAL_ICON,          Color.BLUE.getHex(),        "/referrals"),
                new CardData(TOTAL_SEND,          totalSend,         SEND_ICON,              Color.TEAL.getHex(),        "/send"),
                new CardData(TOTAL_INVESTMENT,    totalInvestments,  INVESTMENT_ICON,        Color.GREEN.getHex(),       "/investments"),
                new CardData(DEPOSIT_BONUS,       depositBonus,      DEPOSIT_BONUS_ICON,     Color.ORANGE.getHex(),      "/deposit-bonus"),
                new CardData(INVESTMENT_BONUS,    investmentBonus,   INVESTMENT_BONUS_ICON,  Color.DEEP_ORANGE.getHex(), "/investment-bonus"),
                new CardData(TOTAL_TICKET,        totalTickets,      TICKET_ICON,            Color.BROWN.getHex(),       "/tickets")
        );*/

        List<CardData> stats = List.of(
                DashboardCard.REGISTERED_USER.create(registeredUsers),
                DashboardCard.ACTIVE_USERS.create(activeUsers),
                DashboardCard.SITE_STAFF.create(staffCount),
                DashboardCard.TOTAL_DEPOSITS.create(totalDeposits),
                DashboardCard.TOTAL_WITHDRAW.create(totalWithdraw),
                DashboardCard.TOTAL_REFERRAL.create(totalReferrals),
                DashboardCard.TOTAL_SEND.create(totalSend),
                DashboardCard.TOTAL_INVESTMENT.create(totalInvest),
                DashboardCard.DEPOSIT_BONUS.create(depositBonus),
                DashboardCard.INVESTMENT_BONUS.create(investBonus),
                DashboardCard.TOTAL_TICKET.create(totalTickets)
        );


        return stats;
    }
}
