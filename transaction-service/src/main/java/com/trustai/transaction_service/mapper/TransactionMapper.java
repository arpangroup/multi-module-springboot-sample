package com.trustai.transaction_service.mapper;

import com.trustai.common.utils.DateUtils;
import com.trustai.transaction_service.dto.response.DepositHistoryItem;
import com.trustai.transaction_service.entity.PendingDeposit;
import com.trustai.transaction_service.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public DepositHistoryItem mapToDepositHistory(Transaction transaction) {
        return DepositHistoryItem.builder()
                .id(transaction.getId())
                .txnRefId(transaction.getTxnRefId())
                .amount(transaction.getAmount())
                .linkedAccountId(transaction.getSenderId() != null ? transaction.getSenderId().toString() : null)
                .paymentGateway(transaction.getGateway().name())
                .txnFee(transaction.getTxnFee())
                .currencyCode(transaction.getCurrencyCode())
                .status(transaction.getStatus().name())
                .remarks(transaction.getRemarks())
                .txnDate(DateUtils.formatDisplayDate(transaction.getCreatedAt()))
                .build();
    }

    public DepositHistoryItem mapToDepositHistory(PendingDeposit deposit) {
        return DepositHistoryItem.builder()
                .id(deposit.getId())
                .txnRefId(deposit.getTxnRefId())
                .amount(deposit.getAmount())
                .linkedAccountId(deposit.getLinkedTxnId())
                .paymentGateway(deposit.getGateway().name())
                .txnFee(deposit.getTxnFee())
                .currencyCode(deposit.getCurrencyCode())
                .status(deposit.getStatus().name())
                .remarks(deposit.getRemarks())
                .txnDate(DateUtils.formatDisplayDate(deposit.getCreatedAt()))
                .build();
    }
}
