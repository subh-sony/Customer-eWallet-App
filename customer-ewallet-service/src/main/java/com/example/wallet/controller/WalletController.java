
package com.example.wallet.controller;

import com.example.wallet.model.Wallet;
import com.example.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;
    public WalletController(WalletService walletService){ this.walletService = walletService; }

    @GetMapping("/overview/{userId}")
    public ResponseEntity<?> overview(@PathVariable Long userId){
        return ResponseEntity.ok(walletService.overview(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Wallet w){
        walletService.createWallet(w);
        return ResponseEntity.ok(w);
    }
}
