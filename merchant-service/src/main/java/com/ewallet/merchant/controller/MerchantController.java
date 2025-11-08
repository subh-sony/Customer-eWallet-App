package com.ewallet.merchant.controller;

import com.ewallet.merchant.dto.ApiResponse;
import com.ewallet.merchant.dto.CreditRequest;
import com.ewallet.merchant.dto.MerchantResponse;
import com.ewallet.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Merchant Controller", description = "APIs for merchant management")
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get merchant by ID", description = "Retrieves merchant details by merchant ID")
    public ResponseEntity<MerchantResponse> getMerchant(@PathVariable String merchantId) {
        log.info("Received get merchant request for merchant ID: {}", merchantId);
        try {
            MerchantResponse merchant = merchantService.getMerchantById(merchantId);
            return ResponseEntity.ok(merchant);
        } catch (Exception e) {
            log.error("Error fetching merchant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/credit")
    @Operation(summary = "Credit amount to merchant", description = "Credits amount to merchant account")
    public ResponseEntity<ApiResponse> creditAmount(@Valid @RequestBody CreditRequest request) {
        log.info("Received credit request for merchant account: {}", request.getMerchantAccountNumber());
        ApiResponse response = merchantService.creditAmount(request);
        return ResponseEntity.ok(response);
    }
}

