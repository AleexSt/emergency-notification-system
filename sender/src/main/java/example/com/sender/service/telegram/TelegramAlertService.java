package example.com.sender.service.telegram;

import example.com.sender.dto.response.TemplateHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAlertService {

    @Value("${notification.services.telegram.token}")
    private String token;

//    private final TelegramApiClient telegramApiClient;

    public boolean sendMessage(String telegramId, TemplateHistoryResponse template) {
        return true;
    }

//    private boolean send(String telegramId, String message) {
//        try {
//            TelegramResponse telegramResponse = telegramApiClient.sendMessage(telegramId, message, token);
//            return telegramResponse.ok();
//        } catch (BadRequest | Forbidden e) {
//            return false; // TODO: 2 ways: user not found / user blocked bot
//        } catch (TooManyRequests | RetryableException e) {
//            log.warn("TooManyRequests to Telegram API, 10 seconds sleep");
//            try {
//                Thread.sleep(10000L);
//            } catch (InterruptedException ex) {
//                throw new RuntimeException(ex);
//            }
//            return send(telegramId, message);
//        }
//    }
}
