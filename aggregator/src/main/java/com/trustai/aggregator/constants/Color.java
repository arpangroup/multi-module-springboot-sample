package com.trustai.aggregator.constants;

public enum Color {
    RED("#f44336"),
    PINK("#e91e63"),
    PURPLE("#9c27b0"),
    DEEP_PURPLE("#673ab7"),
    INDIGO("#3f51b5"),
    BLUE("#2196f3"),
    TEAL("#009688"),
    GREEN("#4caf50"),
    ORANGE("#ff9800"),
    DEEP_ORANGE("#ff5722"),
    BROWN("#795548"),
    BLUE_GREY("#607d8b"),
    CYAN("#00bcd4"),
    LIME("#cddc39"),
    AMBER("#ffc107"),
    DEEP_ORANGE_DARK("#e64a19"),
    LIGHT_BLUE("#03a9f4");

    private final String hex;

    Color(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }
}
