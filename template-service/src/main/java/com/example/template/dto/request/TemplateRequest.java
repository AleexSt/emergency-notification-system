package com.example.template.dto.request;

import jakarta.validation.constraints.NotNull;

public record TemplateRequest(
        @NotNull
        String title,
        @NotNull
        String content){
}
