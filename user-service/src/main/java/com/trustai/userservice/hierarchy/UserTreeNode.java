package com.trustai.userservice.hierarchy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTreeNode {
    private Long userId;
    private String username;
    private BigDecimal walletBalance;
    private String userRank;
    private List<UserTreeNode> children = new ArrayList<>();

    public UserTreeNode(Long userId, String username, BigDecimal walletBalance, int rank) {
        this.userId = userId;
        this.username = username;
        this.walletBalance = walletBalance;
        this.userRank = getRank(rank);
    }

    public UserTreeNode(Long userId, String username, BigDecimal walletBalance, String rankCode) {
        this.userId = userId;
        this.username = username;
        this.walletBalance = walletBalance;
        this.userRank = rankCode;
    }

    private String getRank(int rank) {
        if (rank == 0 || rank == 1) return "RANK_1";
        if (rank == 2) return "RANK_2";
        if (rank == 3) return "RANK_3";
        if (rank == 4) return "RANK_4";
        if (rank == 5) return "RANK_5";
        return "RANK_" + rank;
    }

    public void addChild(UserTreeNode child) {
        this.children.add(child);
    }
}
