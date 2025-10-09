package com.trustai.common.enums;

import java.util.Arrays;

public enum CurrencyType {
    BTC("₿"),      // Bitcoin
    ETH("Ξ"),      // Ethereum
    LTC("Ł"),      // Litecoin
    INR("₹"),      // Indian Rupee
    USD("$"),      // US Dollar
    EUR("€"),      // Euro
    USDT("₮");     // Tether (symbol is often ₮ or just use "USDT")

    private final String symbol;

    CurrencyType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }


    public static CurrencyType fromString(String value) {
        return Arrays.stream(CurrencyType.values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid schema type: " + value));
    }
}
