package example.com.notification.dto.kafka;


import example.com.notification.dto.response.TemplateHistoryResponse;
import example.com.notification.model.NotificationStatus;
import example.com.notification.model.NotificationType;

import java.util.Map;

public record NotificationKafka(
        Long id,
        NotificationType type,
        String credential,
        NotificationStatus status,
        Integer retryAttempts,
        TemplateHistoryResponse template,
        Long clientId
) {
}
