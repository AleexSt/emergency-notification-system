package example.com.notification.entity;

import example.com.notification.model.NotificationStatus;
import example.com.notification.model.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long clientId;
    private Long recipientId;
    private Long templateId;
    private NotificationType type;

    @Builder.Default
    private NotificationStatus status = NotificationStatus.NEW;

    @Builder.Default
    private Integer retryAttempts = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private Long templateHistoryId;

    public Notification setNotificationStatus(NotificationStatus notificationStatus){
        setStatus(notificationStatus);
        return this;
    }

    public Notification updateCreatedAt(){
        setCreatedAt(LocalDateTime.now());
        return this;
    }

    public Notification incrementRetryAttempts(){
        setRetryAttempts(getRetryAttempts() + 1);
        return this;
    }

    public Notification addTemplateHistory(Long templateHistoryId){
        setTemplateHistoryId(templateHistoryId);
        return this;
    }
}
