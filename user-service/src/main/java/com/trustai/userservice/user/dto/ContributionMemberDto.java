package com.trustai.userservice.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributionMemberDto {
    private Long userId;      // User ID
    private String username;  // User's account username
    private String name;      // Full name
    private String related;   // A=direct, B=indirect, C=third-level
    private BigDecimal share; // Share contribution in given date range
}
