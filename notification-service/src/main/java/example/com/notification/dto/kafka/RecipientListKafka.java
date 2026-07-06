package example.com.notification.dto.kafka;

import example.com.notification.dto.response.TemplateHistoryResponse;

import java.util.List;

public record RecipientListKafka(
        List<Long> recipientIds,
        TemplateHistoryResponse templateHistoryResponse,
        Long clientId
) {
}
