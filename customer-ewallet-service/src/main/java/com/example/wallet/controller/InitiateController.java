
package com.example.wallet.controller;

import com.example.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
public class InitiateController {

    private final WalletService walletService;
    private final List<String> supported;
    public InitiateController(WalletService walletService, @Value("${app.supported.currencies}") String currencies){
        this.walletService = walletService;
        this.supported = Arrays.asList(currencies.split(","));
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(@RequestBody Map<String,Object> body){
        // body: productId, productName, productCost, currency, merchantId, buyerId
        Double cost = Double.valueOf(body.get("productCost").toString());
        String currency = body.get("currency").toString();
        Long buyerId = Long.valueOf(body.get("buyerId").toString());
        if(!supported.contains(currency)) return ResponseEntity.status(400).body(Map.of("error","unsupported currency"));
        if(!walletService.hasSufficientBalance(buyerId, cost)) return ResponseEntity.status(402).body(Map.of("error","insufficient funds"));
        String txId = UUID.randomUUID().toString();
        // For demo: we only validate and return txId; payment-service would be invoked here in full system
        return ResponseEntity.ok(Map.of("transactionId", txId, "status","VALIDATED"));
    }
}
