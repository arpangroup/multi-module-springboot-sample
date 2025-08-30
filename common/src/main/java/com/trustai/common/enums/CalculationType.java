package com.trustai.common.enums;

/*
-------------------------------------------------------------
Type        |   Description
------------+------------------------------------------------
FLAT	    |   Fixed value (e.g., 100 units)
PERCENTAGE	|   Percent of a base (e.g., 5% of profit)
SCALE	    |   Tiered scaling (e.g., rate increases by level)
MULTIPLIER	|   Multiplied based on some factor
FORMULA	    |   Custom formula (e.g., (A * B) / C)
------------+------------------------------------------------
*/
public enum CalculationType {
    FLAT,         // Fixed amount (e.g., $50)
    PERCENTAGE,   // Relative amount (e.g., 5% of something)
    SCALE,        // Tiered scaling (e.g., rate increases by level)
    MULTIPLIER,   // Multiplied based on some factor
    FORMULA,      // Custom formula (e.g., (A * B) / C)
    TIERED,       // optional for future
    FIXED_PERCENT,// optional for future
    CUSTOM        // optional for advanced logic
    }
