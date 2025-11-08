package com.ewallet.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundTransferRequest {
    @NotBlank(message = "Customer phone number is required")
    private String customerPhoneNumber;

    @NotBlank(message = "From account number is required")
    private String fromAccountNumber;

    @NotBlank(message = "Merchant ID is required")
    private String merchantId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}

