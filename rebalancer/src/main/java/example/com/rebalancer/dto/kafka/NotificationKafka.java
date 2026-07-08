package example.com.rebalancer.dto.kafka;


import example.com.rebalancer.dto.response.TemplateHistoryResponse;
import example.com.rebalancer.model.NotificationStatus;
import example.com.rebalancer.model.NotificationType;

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
