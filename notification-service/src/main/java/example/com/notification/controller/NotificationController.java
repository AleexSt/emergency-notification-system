package example.com.notification.controller;

import example.com.notification.dto.kafka.NotificationKafka;
import example.com.notification.dto.response.NotificationHistoryResponse;
import example.com.notification.dto.response.NotificationResponse;
import example.com.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/{id}")
    @Operation(summary = "ded")
    public ResponseEntity<String> notify(
            @RequestHeader Long clientId,
            @PathVariable("id") Long templateId
    ){
        return ResponseEntity.status(OK).body(notificationService.distributeNotifications(clientId, templateId));
    }

    @PostMapping("/{id}/error")
    @Operation(summary = "set Notification status as error")
    public ResponseEntity<NotificationHistoryResponse> setNotificationAsError(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    ) {
        return ResponseEntity.status(OK).body(notificationService.setNotificationAsError(clientId, notificationId));
    }

    @PostMapping("/{id}/corrupt")
    @Operation(summary = "set Notification status as impossible to sent")
    public ResponseEntity<NotificationHistoryResponse> setNotificationAsCorrupt(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    ) {
        return ResponseEntity.status(OK).body(notificationService.setNotificationAsCorrupt(clientId, notificationId));
    }

    @PostMapping("/{id}/resending")
    @Operation(summary = "set Notification status as waiting to be resend")
    public ResponseEntity<NotificationResponse> setNotificationAsResending(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    ){
        return ResponseEntity.status(OK).body(notificationService.setNotificationAsResending(clientId, notificationId));
    }


    @GetMapping("/")
    @Operation(summary = "FOR REBALANCER: get Resending/Pending/New Notifications (set Pending status)")
    public ResponseEntity<List<NotificationKafka>> getNotificationsForRebalancing(
            @RequestParam(name = "pending", required = false, defaultValue = "10") Long pendingSec,
            @RequestParam(name = "new", required = false, defaultValue = "10") Long newSec,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.status(OK).body(
                notificationService.getNotificationsForRebalancing(pendingSec, newSec, size)
        );
    }

}
