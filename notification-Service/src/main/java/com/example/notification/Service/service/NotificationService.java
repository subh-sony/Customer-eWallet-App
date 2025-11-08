package com.example.notification.Service.service;

import com.example.notification.Service.dto.NotificationDTO;
import com.example.notification.Service.dto.NotificationRequest;
import com.example.notification.Service.model.Notification;
import com.example.notification.Service.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }
    private final JavaMailSender mailSender;

    //@Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtp(NotificationRequest request) {
        log.info("Sending OTP notification to phone: {} and email: {}", request.getPhoneNumber(), request.getEmail());
        log.info("OTP: {}", request.getOtp());
        sendSms(request);
        sendEmail(request);
    }

    private void sendSms(NotificationRequest request) {
        log.info("SMS sent to {}: OTP has been shared to your mobile number. OTP: {}",
                request.getPhoneNumber(), request.getOtp());
        log.info("OTP for phone number {}: {}", request.getPhoneNumber(), request.getOtp());
    }

    private void sendEmail(NotificationRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(request.getEmail());
            message.setSubject("E-Wallet OTP Verification");
            message.setText("Your OTP for e-Wallet registration is: " + request.getOtp() +
                    "\n\nThis OTP is valid for 5 minutes.\n\nThank you for using E-Wallet.");
            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.getEmail());
            log.info("OTP sent to email {}: {}", request.getEmail(), request.getOtp());
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        log.info("Inside createNotification() method in service Layer....");
        Notification notification = new Notification();
        notification.setCustomerId(notificationDTO.getCustomerId());
        notification.setMessage(notificationDTO.getMessage());
        notification.setType(notificationDTO.getType());
        notification.setStatus("PENDING");

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification Created with PENDING Status....");
        // Simulate sending notification
        sendNotification(savedNotification);
        
        return convertToDTO(savedNotification);
    }

    private void sendNotification(Notification notification) {
        // Simulate notification sending
        try {
            // In production, integrate with email/SMS/push notification services
            Thread.sleep(100); // Simulate network delay
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Notification SENT....");
        } catch (InterruptedException e) {
            notification.setStatus("FAILED");
            log.error("Notification FAILED....");
            notificationRepository.save(notification);
            Thread.currentThread().interrupt();
        }
    }

    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return convertToDTO(notification);
    }

    public List<NotificationDTO> getNotificationsByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setCustomerId(notification.getCustomerId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        return dto;
    }
}

