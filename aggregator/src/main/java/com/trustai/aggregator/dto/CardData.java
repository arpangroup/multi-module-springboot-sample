package com.trustai.aggregator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardData {
    private String title;
    private Object count;  // Object to allow int or BigDecimal
    private String icon;
    private String color;
    private String action;

    // Change constructor to public
    public CardData(String title, Object count, String icon, String color, String action) {
        this.title = title;
        this.count = count;
        this.icon = icon;
        this.color = color;
        this.action = action;
    }

    public CardData(String title, Object count) {
        this(title, count, null, null, null);
    }
}
