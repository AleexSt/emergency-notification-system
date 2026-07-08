package example.com.notification.util;

import example.com.notification.model.NotificationType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTypeConverter extends BaseEnumConverter<NotificationType> {

    public NotificationTypeConverter() {
        super(NotificationType.class);
    }
}
