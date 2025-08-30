//package com.trustai.income_service.income.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//@Entity
//@Table(name = "config_team_income")
//@Data
//@NoArgsConstructor
//public class TeamIncomeConfig {
//    @Id
//    @Column(name = "rank_code")
//    private String rankCode;
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "team_income_percentages", joinColumns = @JoinColumn(name = "rank_type"))
//    @MapKeyColumn(name = "level") // 1 = A, 2 = B, 3 = C
//    @Column(name = "percentage")
//    private Map<Integer, BigDecimal> incomePercentages = new HashMap<>();
//
//    public TeamIncomeConfig(String rankCode, Map<Integer, BigDecimal> incomePercentages) {
//        this.rankCode = rankCode;
//        this.incomePercentages = incomePercentages;
//    }
//}
