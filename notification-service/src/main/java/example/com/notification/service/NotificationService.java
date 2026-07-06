package example.com.notification.service;

import example.com.notification.client.RecipientClient;
import example.com.notification.client.TemplateClient;
import example.com.notification.dto.kafka.NotificationKafka;
import example.com.notification.dto.kafka.RecipientListKafka;
import example.com.notification.dto.request.NotificationRequest;
import example.com.notification.dto.response.*;
import example.com.notification.entity.Notification;
import example.com.notification.entity.NotificationHistoryRepository;
import example.com.notification.mapper.NotificationMapper;
import example.com.notification.model.NotificationStatus;
import example.com.notification.repository.NotificationRepository;
import example.com.notification.util.CollectionUtils;
import example.com.notification.util.NodeChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static example.com.notification.model.NotificationStatus.*;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TemplateClient templateClient;
    private final RecipientClient recipientClient;
    private final KafkaTemplate<String, RecipientListKafka> kafkaTemplate;
    private final NodeChecker nodeChecker;
    private final NotificationMapper mapper;
    private final NotificationRepository notificationRepository;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.kafka.topics.splitter}")
    private String recipientListDistributionTopic;

    private final NotificationHistoryRepository notificationHistoryRepository;

    public String distributeNotifications(Long clientId, Long templateId){
        TemplateResponse templateResponse = templateClient.getTemplateByClientIdAndTemplateId(clientId, templateId).getBody();

        if(templateResponse == null){
            return "TODO";
        }

        List<Long> recipientIds = templateResponse.recipientIds()
                .stream()
                .map(RecipientResponse::id)
                .toList();

        if(recipientIds.isEmpty()){
            throw new RuntimeException("template-recipients not found");
        }

        TemplateHistoryResponse templateHistoryResponse = templateClient.createTemplateHistory(clientId, templateId).getBody();

        for(List<Long> recipients: splitRecipientIds(recipientIds)){
            RecipientListKafka recipientListKafka = new RecipientListKafka(recipients, templateHistoryResponse, clientId);
            kafkaTemplate.send(recipientListDistributionTopic, recipientListKafka);
        }

        return "Notification's been successfully sent!";
    }

    public List<NotificationKafka> getNotificationsForRebalancing(Long pendingSec, Long newSec, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findNotificationsByStatusAndCreatedAt(
                        now.minus(pendingSec, SECONDS), now.minus(newSec, SECONDS), Pageable.ofSize(size)
                ).stream()
                .map(notification -> notification.setNotificationStatus(PENDING))
                .map(Notification::updateCreatedAt)
                .map(notificationRepository::saveAndFlush)
                .map(notification -> mapper.mapToKafka(notification, templateClient))
                .toList();
    }

    public NotificationResponse createNotification(NotificationRequest request){
        return Optional.of(request)
                .map(mapper::mapToEntity)
                .map(notification -> notification.addTemplateHistory(request.template().id()))
                .map(notificationRepository::saveAndFlush)
                .map(notification -> mapper.mapToResponse(notification, templateClient))
                .orElseThrow();
    }


    public NotificationResponse setNotificationAsPending(Long clientId, Long notificationId){
        return notificationRepository.findByIdAndClientId(notificationId, clientId)
                .map(notification -> notification.setNotificationStatus(PENDING))
                .map(notificationRepository::saveAndFlush)
                .map(notification -> mapper.mapToResponse(notification, templateClient))
                .orElseThrow(() -> new RuntimeException("notification.not_found"));
    }

    public NotificationHistoryResponse setNotificationAsSent(Long clientId, Long notificationId) {
        return setNotificationAsExecutedWithGivenStatus(clientId, notificationId, SENT);
    }

    public NotificationHistoryResponse setNotificationAsError(Long clientId, Long notificationId) {
        return setNotificationAsExecutedWithGivenStatus(clientId, notificationId, ERROR);
    }

    public NotificationHistoryResponse setNotificationAsCorrupt(Long clientId, Long notificationId) {
        return setNotificationAsExecutedWithGivenStatus(clientId, notificationId, CORRUPT);
    }


    public NotificationResponse setNotificationAsResending(Long clientId, Long notificationId){
        return notificationRepository.findByIdAndClientId(notificationId, clientId)
                .map(Notification::incrementRetryAttempts)
                .map(notification -> notification.setNotificationStatus(RESENDING))
                .map(notificationRepository::saveAndFlush)
                .map(notification -> mapper.mapToResponse(notification, templateClient))
                .orElseThrow(() -> new RuntimeException("notification.not_found"));
    }

    private NotificationHistoryResponse setNotificationAsExecutedWithGivenStatus(
            Long clientId, Long notificationId,
            NotificationStatus status
    ){
        return notificationRepository.findByIdAndClientId(notificationId, clientId)
                .map(notification -> {
                    notificationRepository.delete(notification);
                    return notification;
                })
                .map(mapper::mapToHistory)
                .map(notificationHistory -> notificationHistory.setNotificationStatus(status))
                .map(notificationHistoryRepository::saveAndFlush)
                .map(mapper::mapToResponse)
                .orElseThrow(() -> new RuntimeException("notification.not_found"));
    }


    private List<List<Long>> splitRecipientIds(List<Long> list){
        return CollectionUtils.splitList(list, nodeChecker.getAmountOfRunningNodes(applicationName));
    }

}
