
package com.example.wallet.service;

import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, OtpService otpService, JwtService jwtService){
        this.userRepository=userRepository; this.otpService=otpService; this.jwtService=jwtService;
    }

    public void register(User u){
        if(userRepository.findByUsername(u.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username exists");
        }
        u.setPassword(encoder.encode(u.getPassword()));
        userRepository.save(u);
    }

    public String requestOtp(String username, String password){
        var opt = userRepository.findByUsername(username);
        if(opt.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid");
        User u = opt.get();
        if(!encoder.matches(password, u.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid");
        String code = otpService.generateAndSave(username);
        // in real system send via notification service; here return code in response for demo
        return code;
    }

    public String verifyOtpAndIssueToken(String username, String code){
        boolean ok = otpService.verify(username, code);
        if(!ok) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid otp");
        User u = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        return jwtService.generateToken(u.getUsername(), u.getRole());
    }
}
