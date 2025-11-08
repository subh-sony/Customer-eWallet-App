package com.ewallet.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountResponse {
    private Long id;
    private Long customerId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
}

