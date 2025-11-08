package com.ewallet.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponse {
    private String merchantId;
    private String merchantAccountNumber;
    private String merchantName;
}

