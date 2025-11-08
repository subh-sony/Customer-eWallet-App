package com.example.wallet;

import com.example.wallet.security.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testTokenGenerationAndValidation() {
        String token = jwtService.generateToken("cust1", "ROLE_CUSTOMER");
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("cust1", username);

        Claims claims = jwtService.extractClaim(token, c -> c);
        assertEquals("ROLE_CUSTOMER", claims.get("role"));
    }
}
