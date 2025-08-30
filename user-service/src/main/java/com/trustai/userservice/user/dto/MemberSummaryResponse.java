package com.trustai.userservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSummaryResponse {

    private int totalUser;         // Total registered members under him (depth ≤ 3)
    private int totalActive;       // Total active members

    private int direct;            // Level 1 total
    private int activeDirect;      // Level 1 active count

    private int indirect;          // Level 2 total
    private int activeIndirect;    // Level 2 active count

    private int third;             // Level 3 total
    private int activeThird;       // Level 3 active count

    private BigDecimal totalShare; // Total share earned from downlines in given range

    private BigDecimal memberA;    // Total share from level 1 users
    private BigDecimal memberB;    // Total share from level 2 users
    private BigDecimal memberC;    // Total share from level 3 users

    private List<ContributionMemberDto> memembers; // List of members with details
}
