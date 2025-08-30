package com.trustai.income_service.income.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamIncomeKey implements Serializable {

    @Column(name = "upline_rank")
    private String uplineRank; // e.g., Level-2, Level-3, Level-4, Level-5

    @Column(name = "downline_depth")
    private int downlineDepth; // 1 = Lv.A, 2 = Lv.B, 3 = Lv.C
}
