package com.trustai.transaction_service.dto.response;

import java.math.BigDecimal;

public record WalletResponse(BigDecimal walletBalance, String currency) {
}
