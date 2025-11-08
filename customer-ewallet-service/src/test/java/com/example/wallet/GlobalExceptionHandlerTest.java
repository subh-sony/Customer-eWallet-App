package com.example.wallet;

import com.example.wallet.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleExceptionReturnsJson() {
        ResponseEntity<?> response = handler.handle(new RuntimeException("test error"));
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("test error"));
    }
}
