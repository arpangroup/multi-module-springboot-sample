package com.trustai.transaction_service.dto.response;

import com.trustai.transaction_service.entity.Transaction;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionDTO {
    private Long id;
    private String user;
    private BigDecimal amount;
    private BigDecimal balance;
    private String txnRefId;
    private String remarks;
    private String status;
    private String txnType;
    private String txnTypeDisplayName;
    private boolean isCredit;
    private LocalDateTime createdAt;

    // Constructor
    public TransactionDTO(Transaction txn) {
        this.id = txn.getId();
        this.user = txn.getCreatedBy();
        this.amount = txn.getAmount();
        this.balance = txn.getBalance();
        this.txnRefId = txn.getTxnRefId();
        this.remarks = txn.getRemarks();
        this.status = txn.getStatus().name();
        this.txnType = txn.getTxnType().name();
        this.txnTypeDisplayName = txn.getTxnType().getDisplayName();
        this.isCredit = txn.isCredit();
        this.createdAt = txn.getCreatedAt();
    }


}
