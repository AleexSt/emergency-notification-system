package example.com.notification.dto.request;



import example.com.notification.dto.response.TemplateHistoryResponse;
import example.com.notification.model.NotificationType;
import lombok.Builder;

@Builder
public record NotificationRequest(
        NotificationType type,
        String credential,
        TemplateHistoryResponse template,
        Long clientId,
        Long recipientId) {
}
