package example.com.notification.dto.response;

import example.com.notification.model.NotificationStatus;
import example.com.notification.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationHistoryResponse(
        Long id,
        NotificationType type,
        String credential,
        NotificationStatus status,
        Integer retryAttempts,
        LocalDateTime createdAt,
        LocalDateTime executedAt,
        TemplateHistoryResponse template,
        Long clientId
) {
}