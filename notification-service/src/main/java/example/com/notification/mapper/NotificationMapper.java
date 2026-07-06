package example.com.notification.mapper;

import example.com.notification.client.TemplateClient;
import example.com.notification.dto.kafka.NotificationKafka;
import example.com.notification.dto.request.NotificationRequest;
import example.com.notification.dto.response.NotificationHistoryResponse;
import example.com.notification.dto.response.NotificationResponse;
import example.com.notification.entity.Notification;
import example.com.notification.entity.NotificationHistory;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<Notification, NotificationRequest, NotificationResponse> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "retryAttempts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "templateHistoryId", ignore = true)
    Notification mapToEntity(NotificationRequest request);

    @Mapping(target = "template", expression = "java(templateClient.getTemplateHistory(notification.getClientId(), notification.getTemplateHistoryId()).getBody())")
    NotificationResponse mapToResponse(Notification notification, @Context TemplateClient templateClient);

    @Mapping(target = "template", expression = "java(templateClient.getTemplateHistory(notification.getClientId(), notification.getTemplateHistoryId()).getBody())")
    NotificationKafka mapToKafka(Notification notification, @Context TemplateClient templateClient);


    NotificationKafka mapToKafka(NotificationResponse notificationResponse);

    @Mapping(target = "id", ignore = true)
    NotificationHistory mapToHistory(Notification notification);

    NotificationHistoryResponse mapToResponse(NotificationHistory notificationHistory);
}
