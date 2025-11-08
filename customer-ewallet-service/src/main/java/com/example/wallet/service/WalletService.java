
package com.example.wallet.service;

import com.example.wallet.model.Wallet;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository){
        this.walletRepository = walletRepository; this.userRepository = userRepository;
    }

    public Map<String,Object> overview(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        Optional<Wallet> w = walletRepository.findByUserId(userId);
        return Map.of("customer", user, "accounts", List.of(Map.of("accountId", user.getId(), "type","WALLET")), "wallet", w.orElse(Wallet.builder().userId(userId).balance(0.0).currency("INR").build()));
    }

    public void createWallet(Wallet w){
        walletRepository.save(w);
    }

    public boolean hasSufficientBalance(Long userId, Double amount){
        return walletRepository.findByUserId(userId).map(w -> w.getBalance()>=amount).orElse(false);
    }
}
