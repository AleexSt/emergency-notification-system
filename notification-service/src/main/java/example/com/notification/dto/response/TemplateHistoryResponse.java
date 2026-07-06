package example.com.notification.dto.response;

import lombok.Builder;

@Builder
public record TemplateHistoryResponse(
        Long id,
        String title,
        String content
){
}
