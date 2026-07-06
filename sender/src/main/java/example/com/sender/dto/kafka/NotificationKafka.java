package example.com.sender.dto.kafka;


import example.com.sender.dto.response.TemplateHistoryResponse;
import example.com.sender.model.NotificationStatus;
import example.com.sender.model.NotificationType;

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
