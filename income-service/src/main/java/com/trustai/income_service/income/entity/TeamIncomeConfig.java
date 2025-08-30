package com.trustai.income_service.income.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "config_team_income")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamIncomeConfig {

    @EmbeddedId
    private TeamIncomeKey id;

    @Column(name = "payout_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal payoutPercentage;
}
