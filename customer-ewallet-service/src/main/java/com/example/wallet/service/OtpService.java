
package com.example.wallet.service;

import com.example.wallet.model.Otp;
import com.example.wallet.repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    public OtpService(OtpRepository otpRepository){ this.otpRepository = otpRepository; }

    public String generateAndSave(String username){
        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime exp = LocalDateTime.now().plusMinutes(2);
        Otp otp = otpRepository.findByUsername(username).orElse(Otp.builder().username(username).build());
        otp.setCode(code);
        otp.setExpiresAt(exp);
        otp.setAttempts(0);
        otpRepository.save(otp);
        return code;
    }

    public boolean verify(String username, String code){
        var maybe = otpRepository.findByUsername(username);
        if(maybe.isEmpty()) return false;
        Otp otp = maybe.get();
        if(otp.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if(otp.getAttempts()>=3) return false;
        if(!otp.getCode().equals(code)){
            otp.setAttempts(otp.getAttempts()+1);
            otpRepository.save(otp);
            return false;
        }
        otpRepository.delete(otp);
        return true;
    }
}
