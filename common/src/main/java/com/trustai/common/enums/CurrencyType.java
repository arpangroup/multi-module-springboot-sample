package com.trustai.common.enums;

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
}
