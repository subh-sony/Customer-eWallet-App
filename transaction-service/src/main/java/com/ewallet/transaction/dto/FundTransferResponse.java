package com.ewallet.transaction.dto;

import com.ewallet.transaction.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundTransferResponse {
    private Long transactionId;
    private Long customerId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
