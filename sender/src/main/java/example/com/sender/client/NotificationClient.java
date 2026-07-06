package example.com.sender.client;


import example.com.sender.dto.kafka.NotificationKafka;
import example.com.sender.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@FeignClient(name = "${services.notification}")
public interface NotificationClient {

    @PostMapping(value = "/api/v1/notifications/{id}/sent")
    ResponseEntity<NotificationResponse> setNotificationAsSent(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    );

    @PostMapping(value = "/api/v1/notifications/{id}/resending")
    ResponseEntity<NotificationResponse> setNotificationAsResending(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    );

    @PostMapping(value = "/api/v1/notifications/{id}/corrupt")
    ResponseEntity<NotificationResponse> setNotificationAsCorrupt(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    );

    @PostMapping(value = "/api/v1/notifications/{id}/error")
    ResponseEntity<NotificationResponse> setNotificationAsError(
            @RequestHeader Long clientId,
            @PathVariable("id") Long notificationId
    );
}
