package com.example.notification.Service;

import com.example.notification.Service.dto.NotificationDTO;
import com.example.notification.Service.model.Notification;
import com.example.notification.Service.repository.NotificationRepository;
import com.example.notification.Service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class NotificationServiceApplicationTests {


	@Mock
	private NotificationRepository notificationRepository;

	@InjectMocks
	private NotificationService notificationService;

	private NotificationDTO notificationDTO;
	private Notification notification;

	@BeforeEach
	void setUp() {
		notificationDTO = new NotificationDTO();
		notificationDTO.setCustomerId(1L);
		notificationDTO.setMessage("Test notification");
		notificationDTO.setType("EMAIL");

		notification = new Notification();
		notification.setId(1L);
		notification.setCustomerId(1L);
		notification.setMessage("Test notification");
		notification.setType("EMAIL");
		notification.setStatus("PENDING");
	}

	@Test
	void testCreateNotification() {
		when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

		NotificationDTO result = notificationService.createNotification(notificationDTO);

		assertNotNull(result);
		assertEquals("Test notification", result.getMessage());
		verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
	}

	@Test
	void testGetNotificationById() {
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

		NotificationDTO result = notificationService.getNotificationById(1L);

		assertNotNull(result);
		assertEquals("Test notification", result.getMessage());
	}

	@Test
	void testGetNotificationByIdNotFound() {
		when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> notificationService.getNotificationById(1L));
	}

}
