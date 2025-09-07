package com.trustai.aggregator.service;

import com.trustai.aggregator.constants.Color;
import com.trustai.aggregator.constants.IconConstants;
import com.trustai.aggregator.dto.CardData;
import com.trustai.aggregator.dto.DashboardResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.trustai.aggregator.constants.CardTitles.*;
import static com.trustai.aggregator.constants.IconConstants.*;

@Service
public class DashboardService {
    public DashboardResponse getDashboard() {

        List<CardData> stats = List.of(
                new CardData(REGISTERED_USER, 1200, USER_ICON, Color.RED.getHex(), "/users"),
                new CardData(ACTIVE_USERS, 850, ACTIVE_USER_ICON, Color.PINK.getHex(), "/active-users"),
                new CardData(SITE_STAFF, 25, STAFF_ICON, Color.PURPLE.getHex(), "/staff"),
                new CardData(TOTAL_DEPOSITS, new BigDecimal("150000.75"), DEPOSIT_ICON, Color.DEEP_PURPLE.getHex(), "/deposits"),
                new CardData(TOTAL_WITHDRAW, new BigDecimal("50000.25"), WITHDRAW_ICON, Color.INDIGO.getHex(), "/withdrawals"),
                new CardData(TOTAL_REFERRAL, 300, REFERRAL_ICON, Color.BLUE.getHex(), "/referrals"),
                new CardData(TOTAL_SEND, 1000, SEND_ICON, Color.TEAL.getHex(), "/send"),
                new CardData(TOTAL_INVESTMENT, new BigDecimal("75000.00"), INVESTMENT_ICON, Color.GREEN.getHex(), "/investments"),
                new CardData(DEPOSIT_BONUS, new BigDecimal("5000.00"), DEPOSIT_BONUS_ICON, Color.ORANGE.getHex(), "/deposit-bonus"),
                new CardData(INVESTMENT_BONUS, new BigDecimal("2500.00"), INVESTMENT_BONUS_ICON, Color.DEEP_ORANGE.getHex(), "/investment-bonus"),
                new CardData(TOTAL_TICKET, 45, IconConstants.TICKET_ICON, Color.BROWN.getHex(), "/tickets")
        );

        return new DashboardResponse(stats);
    }
}
