package com.trustai.common.exceptions;

public interface ErrorCode {
    String ACCESS_DENIED        = "AUTH001"; // Authenticated but not authorized (e.g., missing role)
    String UNAUTHORIZED         = "AUTH002"; // Not authenticated (no token or session)
    String TOKEN_EXPIRED        = "AUTH003"; // Token is expired
    String INVALID_CREDENTIALS  = "AUTH004"; // Wrong username/password or invalid token
    String ACCOUNT_LOCKED       = "AUTH005"; // User account is locked
    String ACCOUNT_DISABLED     = "AUTH006"; // User account is disabled or inactive
    String TOKEN_INVALID        = "AUTH007"; // Token is malformed or cannot be parsed
    String TOKEN_MISSING        = "AUTH008"; // No token provided in request
    String SESSION_EXPIRED      = "AUTH009"; // User session has timed out
    String MULTI_LOGIN_CONFLICT = "AUTH010"; // Multiple concurrent logins not allowed
    String MFA_REQUIRED         = "AUTH011"; // Multi-factor authentication required
    String MFA_FAILED           = "AUTH012"; // Multi-factor authentication failed
    String PASSWORD_EXPIRED     = "AUTH013"; // Password expired (force reset)
    String ROLE_NOT_ASSIGNED    = "AUTH014"; // User has no role assigned

    // Investment
    String INSUFFICIENT_BALANCE = "INV001";
    String INVALID_RANK_CONFIG  = "INV002";
    String MIN_INVESTMENT_NOT_MET = "INV003";
    //String AMOUNT_BELOW_MIN_RANK_THRESHOLD = "";
    //String INSUFFICIENT_INVESTMENT_FOR_RANK = "";
    //String INVESTMENT_BELOW_RANK_REQUIREMENT = "";
    //String INVALID_INVESTMENT_AMOUNT_FOR_RANK = "";
    String USER_RANK_NOT_IN_PARTICIPATION_LEVELS = "INV004";
    //String INELIGIBLE_USER_RANK_FOR_SCHEMA = "";
    //String RANK_NOT_ALLOWED_FOR_SCHEMA = "";

    String USER_RANK_MISMATCH_LINKED_RANK = "INV005";
    //String INVALID_USER_RANK_FOR_LINKED_SCHEMA = "";
    //String SCHEMA_LINKED_RANK_MISMATCH = "";
    //String RANK_CODE_NOT_EQUAL_TO_LINKED_RANK = "";

    // Reservation
    String STAKE_ALREADY_RESERVED           = "RSV001";
    String STAKE_SCHEMA_NOT_FOUND           = "RSV002";
    String INSUFFICIENT_WALLET_BALANCE      = "RSV003";
    String RESERVATION_NOT_FOUND_OR_SOLD    = "RSV004";
    String WALLET_DEDUCTION_FAILED          = "RSV005";
    String RESERVATION_NOT_ALLOWED          = "RSV006";

    String FIXED_AMOUNT_MISMATCH            = "INV006"; // INVALID_FIXED_INVESTMENT_AMOUNT, AMOUNT_NOT_EQUAL_TO_FIXED_MINIMUM
    String INVESTMENT_AMOUNT_OUT_OF_RANGE   = "INV007"; // AMOUNT_NOT_IN_ALLOWED_RANGE, INVALID_RANGE_INVESTMENT_AMOUNT
    String UNKNOWN_SCHEMA_TYPE              = "INV008"; // UNSUPPORTED_SCHEMA_TYPE, INVALID_SCHEMA_TYPE



    String REST_CALL_EXCEPTION = "EXT001";



}
