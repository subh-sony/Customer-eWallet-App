
package com.example.wallet.controller;

import com.example.wallet.model.User;
import com.example.wallet.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService){ this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User u){
        authService.register(u);
        return ResponseEntity.ok(Map.of("msg","registered"));
    }

    @PostMapping("/login/request")
    public ResponseEntity<?> loginRequest(@RequestBody Map<String,String> body){
        String username = body.get("username"); String password = body.get("password");
        String otp = authService.requestOtp(username, password);
        // returning OTP in response only for demo; in real app send via SMS/email
        return ResponseEntity.ok(Map.of("msg","otp_sent","otp", otp));
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String,String> body){
        String username = body.get("username"); String code = body.get("otp");
        String token = authService.verifyOtpAndIssueToken(username, code);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
