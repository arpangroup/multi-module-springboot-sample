package com.trustai.common.dto;

import java.math.BigDecimal;

public record WalletResponse(
        BigDecimal walletBalance,
        BigDecimal profitWallet,
        String currency
) {
}
