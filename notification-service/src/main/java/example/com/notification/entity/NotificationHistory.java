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
@Table(name = "notifications_history")
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;
    private Long recipientId;
    private Long templateHistoryId;
    private Long urlId;

    private NotificationType type;
    private String credential;

    private NotificationStatus status;
    private Integer retryAttempts;
    private LocalDateTime createdAt;

    @Builder.Default
    private LocalDateTime executedAt = LocalDateTime.now();

    public NotificationHistory setNotificationStatus(NotificationStatus notificationStatus) {
        setStatus(notificationStatus);
        return this;
    }
}