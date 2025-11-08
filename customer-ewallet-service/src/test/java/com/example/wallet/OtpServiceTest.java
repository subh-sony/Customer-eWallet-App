package com.example.wallet;

import com.example.wallet.model.Otp;
import com.example.wallet.repository.OtpRepository;
import com.example.wallet.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OtpServiceTest {

    private OtpRepository otpRepository = Mockito.mock(OtpRepository.class);
    private OtpService otpService = new OtpService(otpRepository);

    @BeforeEach
    void setup() {
        Otp otp = Otp.builder()
                .username("cust1")
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .attempts(0)
                .build();
        Mockito.when(otpRepository.findByUsername("cust1")).thenReturn(Optional.of(otp));
    }

    @Test
    void testGenerateOtp() {
        String code = otpService.generateAndSave("cust1");
        assertNotNull(code);
        assertEquals(6, code.length());
    }

    @Test
    void testValidOtpVerification() {
        assertTrue(otpService.verify("cust1", "123456"));
    }

    @Test
    void testInvalidOtpVerification() {
        assertFalse(otpService.verify("cust1", "999999"));
    }
}
