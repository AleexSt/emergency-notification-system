package com.example.template.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TemplateResponse(
        Long id,
        String title,
        String content,
        List<RecipientResponse> recipientIds){
}
