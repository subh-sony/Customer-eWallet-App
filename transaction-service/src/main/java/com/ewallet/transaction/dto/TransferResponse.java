package com.ewallet.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String message;
    private boolean success;
    private FundTransferResponse data;
}

