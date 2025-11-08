package com.ewallet.merchant.service;

import com.ewallet.merchant.dto.ApiResponse;
import com.ewallet.merchant.dto.CreditRequest;
import com.ewallet.merchant.dto.MerchantResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantService {

    private static final Map<String, MerchantResponse> MERCHANTS = new HashMap<>();

    static {
        MERCHANTS.put("MERCHANT001", new MerchantResponse("MERCHANT001", "MERCHANT_ACC_001", "Amazon"));
        MERCHANTS.put("MERCHANT002", new MerchantResponse("MERCHANT002", "MERCHANT_ACC_002", "Flipkart"));
        MERCHANTS.put("MERCHANT003", new MerchantResponse("MERCHANT003", "MERCHANT_ACC_003", "Paytm"));
    }

    public MerchantResponse getMerchantById(String merchantId) {
        log.info("Fetching merchant details for merchant ID: {}", merchantId);
        MerchantResponse merchant = MERCHANTS.get(merchantId);
        if (merchant == null) {
            log.warn("Merchant not found for merchant ID: {}", merchantId);
            throw new RuntimeException("Merchant not found");
        }
        log.info("Merchant found: {}", merchant.getMerchantName());
        return merchant;
    }

    public ApiResponse creditAmount(CreditRequest request) {
        log.info("Crediting amount {} to merchant account: {}", 
                request.getAmount(), request.getMerchantAccountNumber());
        
        log.info("Amount {} successfully credited to merchant account: {}",
                request.getAmount(), request.getMerchantAccountNumber());
        
        return new ApiResponse("Amount credited successfully to merchant account", true, null);
    }
}

