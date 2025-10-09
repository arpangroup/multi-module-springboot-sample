package com.trustai.aggregator.constants;

import com.trustai.aggregator.dto.CardData;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum DashboardCard {
    REGISTERED_USER("Registered User", "FaUser", Color.RED, "/users"),
    ACTIVE_USERS("Active Users", "FaUserCheck", Color.PINK, "/users/active"),
    SITE_STAFF("Site Staff", "FaUsersCog", Color.PURPLE, ""),
    TOTAL_DEPOSITS("Total Deposits", "FaDollarSign", Color.DEEP_PURPLE, "/deposit/history"),
    TOTAL_WITHDRAW("Total Withdraw", "FaMoneyBillWave", Color.INDIGO, "/withdraw/history"),
    TOTAL_REFERRAL("Total Referral", "FaUserFriends", Color.BLUE, ""), // "/referrals"
    TOTAL_SEND("Total Send", "FaPaperPlane", Color.TEAL, ""), // "/send"
    TOTAL_INVESTMENT("Total Investment", "FaChartLine", Color.GREEN, "/investments"),
    DEPOSIT_BONUS("Deposit Bonus", "FaGift", Color.ORANGE, ""), // "/deposit-bonus"
    INVESTMENT_BONUS("Investment Bonus", "FaCoins", Color.DEEP_ORANGE, ""), // "/investment-bonus"
    TOTAL_TICKET("Total Ticket", "FaTicketAlt", Color.BROWN, "/tickets");


    private final String title;
    private final String icon;
    private final Color color;
    private final String actionLink;

    DashboardCard(String title, String icon, Color color, String actionLink) {
        this.title = title;
        this.icon = icon;
        this.color = color;
        this.actionLink = actionLink;
    }

    public String getColorHex() {
        return color.getHex();
    }

    // Factory method for creating CardData
    public CardData create(Number count) {
        BigDecimal decimalCount = (count instanceof BigDecimal)
                ? (BigDecimal) count
                : BigDecimal.valueOf(count.longValue());

        return new CardData(title, decimalCount, icon, color.getHex(), actionLink);
    }
}
