package com.ewallet.transaction.service;

import com.ewallet.transaction.dto.FundTransferRequest;
import com.ewallet.transaction.dto.TransferResponse;
import com.ewallet.transaction.entity.Transaction;

import java.util.List;

public interface TransactionService {
    public TransferResponse fundTransfer(FundTransferRequest request);

    public List<Transaction> getTransactionsByCustomerId(Long customerId);
}
