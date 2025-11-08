package com.example.notification.Service.controller;

import com.example.notification.Service.dto.NotificationDTO;
import com.example.notification.Service.dto.NotificationRequest;
import com.example.notification.Service.service.NotificationService;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    Tracer tracer;
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = NotificationDTO.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        log.info("Inside createNotification() method....");
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        log.info("Notification Created Successfully....");
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@Valid @PathVariable Long id) {
        log.info("Get Notification By ID method...."+id);
        NotificationDTO notification = notificationService.getNotificationById(id);
        log.info("Notification By ID...."+notification);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByCustomerId(@PathVariable Long customerId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByCustomerId(customerId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByStatus(@PathVariable String status) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    /*@PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Sends OTP to customer via SMS and Email")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody NotificationRequest request) {
        log.info("Received send OTP request for phone: {} and email: {}", request.getPhoneNumber(), request.getEmail());
        try {
            notificationService.sendOtp(request);
            return ResponseEntity.ok(new ApiResponse("OTP sent successfully", true, null));
        } catch (Exception e) {
            log.error("Error sending OTP: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to send OTP: " + e.getMessage(), false, null));
        }
    }*/
}

